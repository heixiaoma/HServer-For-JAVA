/*
 * Copyright 2024 - 2024 the original author or authors.
 */
package cn.hserver.modelcontextprotocol.server.transport;

import cn.hserver.core.ioc.annotation.Bean;
import cn.hserver.modelcontextprotocol.spec.*;
import cn.hserver.plugin.web.context.WebConstConfig;
import cn.hserver.plugin.web.context.Webkit;
import cn.hserver.plugin.web.context.sse.SSeEvent;
import cn.hserver.plugin.web.context.sse.SSeStream;
import cn.hserver.plugin.web.interfaces.FilterAdapter;
import cn.hserver.plugin.web.interfaces.HttpRequest;
import cn.hserver.plugin.web.interfaces.HttpResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HServerSseServerTransportProvider  implements McpServerTransportProvider {

	/** Logger for this class */
	private static final Logger logger = LoggerFactory.getLogger(HServerSseServerTransportProvider.class);

	/** Default endpoint path for SSE connections */

	/** Event type for regular messages */
	public static final String MESSAGE_EVENT_TYPE = "message";

	/** Event type for endpoint information */
	public static final String ENDPOINT_EVENT_TYPE = "endpoint";

	/** JSON object mapper for serialization/deserialization */
	private final ObjectMapper objectMapper= WebConstConfig.JSON;

	/** The endpoint path for handling client messages */
	private final String messageEndpoint;

	/** The endpoint path for handling SSE connections */
	private final String sseEndpoint;

	/** Map of active client sessions, keyed by session ID */
	private final Map<String, McpServerSession> sessions = new ConcurrentHashMap<>();

	/** Session factory for creating new sessions */
	private McpServerSession.Factory sessionFactory;


	public HServerSseServerTransportProvider(String sseEndpoint){
		this.sseEndpoint=sseEndpoint;
		this.messageEndpoint=sseEndpoint+"/message";
	}


	/**
	 * Sets the session factory for creating new sessions.
	 * @param sessionFactory The session factory to use
	 */
	@Override
	public void setSessionFactory(McpServerSession.Factory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	/**
	 * Broadcasts a notification to all connected clients.
	 * @param method The method name for the notification
	 * @param params The parameters for the notification
	 * @return A Mono that completes when the broadcast attempt is finished
	 */
	@Override
	public Mono<Void> notifyClients(String method, Map<String, Object> params) {
		if (sessions.isEmpty()) {
			logger.debug("No active sessions to broadcast message to");
			return Mono.empty();
		}

		logger.debug("Attempting to broadcast message to {} active sessions", sessions.size());

		return Flux.fromIterable(sessions.values())
			.flatMap(session -> session.sendNotification(method, params)
				.doOnError(
						e -> logger.error("Failed to send message to session {}: {}", session.getId(), e.getMessage()))
				.onErrorComplete())
			.then();
	}


	public void doGet(HttpRequest request, HttpResponse response) {
		String pathInfo = request.getUri();
		if (!sseEndpoint.equals(pathInfo)) {
			return;
		}
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Connection", "keep-alive");
		response.setHeader("Access-Control-Allow-Origin", "*");

		String sessionId = UUID.randomUUID().toString();

		SSeStream sSeStream = response.getSSeStream();


		// Create a new session transport
		HServerMcpSessionTransport sessionTransport = new HServerMcpSessionTransport(sessionId,
				sSeStream);
		sSeStream.addCloseListener(sessionTransport::close);
		// Create a new session using the session factory
		McpServerSession session = sessionFactory.create(sessionTransport);
		this.sessions.put(sessionId, session);
		// Send initial endpoint event
		this.sendEvent(sSeStream, ENDPOINT_EVENT_TYPE, this.messageEndpoint + "?sessionId=" + sessionId);
	}

	public void doPost(HttpRequest request, HttpResponse response){

		String pathInfo = request.getUri();
		if (!messageEndpoint.equals(pathInfo)) {
			return;
		}

		// Get the session ID from the request parameter
		String sessionId = request.query("sessionId");
		if (sessionId == null) {
			response.sendStatusCode(HttpResponseStatus.BAD_REQUEST);
			response.sendJson(new McpError("Session ID missing in message endpoint"));
			return;
		}

		// Get the session from the sessions map
		McpServerSession session = sessions.get(sessionId);
		if (session == null) {
			response.sendStatusCode(HttpResponseStatus.NOT_FOUND);
			response.sendJson(new McpError("Session not found: " + sessionId));
			return;
		}
		try {
			logger.info("收到消息:"+request.getRawData());
			McpSchema.JSONRPCMessage message = McpSchema.deserializeJsonRpcMessage(objectMapper,request.getRawData());
			session.handle(message).block(); // Block for Servlet compatibility
			response.sendStatusCode(HttpResponseStatus.OK);
			response.sendText("");
		}
		catch (Exception e) {
			logger.error("Error processing message: {}", e.getMessage());
            McpError mcpError = new McpError(e.getMessage());
            response.sendStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR);
            response.sendJson(mcpError);
        }
	}

	/**
	 * Initiates a graceful shutdown of the transport.
	 * <p>
	 * This method marks the transport as closing and closes all active client sessions.
	 * New connection attempts will be rejected during shutdown.
	 * @return A Mono that completes when all sessions have been closed
	 */
	@Override
	public Mono<Void> closeGracefully() {
		logger.debug("Initiating graceful shutdown with {} active sessions", sessions.size());
		return Flux.fromIterable(sessions.values()).flatMap(McpServerSession::closeGracefully).then();
	}


	private void sendEvent(SSeStream sSeStream, String eventType, String data)  {
		SSeEvent sSeEvent = new SSeEvent.Builder().event(eventType).data(data).build();
		sSeStream.sendSseEvent(sSeEvent);
	}



	private class HServerMcpSessionTransport implements McpServerTransport {

		private final String sessionId;

		private final SSeStream writer;

		HServerMcpSessionTransport(String sessionId, SSeStream writer) {
			this.sessionId = sessionId;
			this.writer = writer;
			logger.debug("Session transport {} initialized with SSE writer", sessionId);
		}


		@Override
		public Mono<Void> sendMessage(McpSchema.JSONRPCMessage message) {
			return Mono.fromRunnable(() -> {
				try {
					String jsonText = objectMapper.writeValueAsString(message);
					sendEvent(writer, MESSAGE_EVENT_TYPE, jsonText);
					logger.debug("Message sent to session {}", sessionId);
				}
				catch (Exception e) {
					logger.error("Failed to send message to session {}: {}", sessionId, e.getMessage());
					sessions.remove(sessionId);
				}
			});
		}

		@Override
		public <T> T unmarshalFrom(Object data, TypeReference<T> typeRef) {
			return objectMapper.convertValue(data, typeRef);
		}

		@Override
		public Mono<Void> closeGracefully() {
			return Mono.fromRunnable(() -> {
				logger.debug("Closing session transport: {}", sessionId);
				try {
					sessions.remove(sessionId);
					logger.debug("Successfully completed async context for session {}", sessionId);
				}
				catch (Exception e) {
					logger.warn("Failed to complete async context for session {}: {}", sessionId, e.getMessage());
				}
			});
		}

		@Override
		public void close() {
			try {
				sessions.remove(sessionId);
				logger.debug("Successfully completed async context for session {}", sessionId);
			}
			catch (Exception e) {
				logger.warn("Failed to complete async context for session {}: {}", sessionId, e.getMessage());
			}
		}

	}

}
