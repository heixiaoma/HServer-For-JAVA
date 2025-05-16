/*
 * Copyright 2024-2024 the original author or authors.
 */

package cn.hserver.modelcontextprotocol.spec;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Based on the <a href="http://www.jsonrpc.org/specification">JSON-RPC 2.0
 * specification</a> and the <a href=
 * "https://github.com/modelcontextprotocol/specification/blob/main/schema/schema.ts">Model
 * Context Protocol Schema</a>.
 *
 * @author Christian Tzolov
 */
public final class McpSchema {

	private static final Logger logger = LoggerFactory.getLogger(McpSchema.class);

	private McpSchema() {
	}

	public static final String LATEST_PROTOCOL_VERSION = "2024-11-05";

	public static final String JSONRPC_VERSION = "2.0";

	// ---------------------------
	// Method Names
	// ---------------------------

	// Lifecycle Methods
	public static final String METHOD_INITIALIZE = "initialize";

	public static final String METHOD_NOTIFICATION_INITIALIZED = "notifications/initialized";

	public static final String METHOD_PING = "ping";

	// Tool Methods
	public static final String METHOD_TOOLS_LIST = "tools/list";

	public static final String METHOD_TOOLS_CALL = "tools/call";

	public static final String METHOD_NOTIFICATION_TOOLS_LIST_CHANGED = "notifications/tools/list_changed";

	// Resources Methods
	public static final String METHOD_RESOURCES_LIST = "resources/list";

	public static final String METHOD_RESOURCES_READ = "resources/read";

	public static final String METHOD_NOTIFICATION_RESOURCES_LIST_CHANGED = "notifications/resources/list_changed";

	public static final String METHOD_RESOURCES_TEMPLATES_LIST = "resources/templates/list";

	public static final String METHOD_RESOURCES_SUBSCRIBE = "resources/subscribe";

	public static final String METHOD_RESOURCES_UNSUBSCRIBE = "resources/unsubscribe";

	// Prompt Methods
	public static final String METHOD_PROMPT_LIST = "prompts/list";

	public static final String METHOD_PROMPT_GET = "prompts/get";

	public static final String METHOD_NOTIFICATION_PROMPTS_LIST_CHANGED = "notifications/prompts/list_changed";

	// Logging Methods
	public static final String METHOD_LOGGING_SET_LEVEL = "logging/setLevel";

	public static final String METHOD_NOTIFICATION_MESSAGE = "notifications/message";

	// Roots Methods
	public static final String METHOD_ROOTS_LIST = "roots/list";

	public static final String METHOD_NOTIFICATION_ROOTS_LIST_CHANGED = "notifications/roots/list_changed";

	// Sampling Methods
	public static final String METHOD_SAMPLING_CREATE_MESSAGE = "sampling/createMessage";

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	// ---------------------------
	// JSON-RPC Error Codes
	// ---------------------------
	/**
	 * Standard error codes used in MCP JSON-RPC responses.
	 */
	public static final class ErrorCodes {

		/**
		 * Invalid JSON was received by the server.
		 */
		public static final int PARSE_ERROR = -32700;

		/**
		 * The JSON sent is not a valid Request object.
		 */
		public static final int INVALID_REQUEST = -32600;

		/**
		 * The method does not exist / is not available.
		 */
		public static final int METHOD_NOT_FOUND = -32601;

		/**
		 * Invalid method parameter(s).
		 */
		public static final int INVALID_PARAMS = -32602;

		/**
		 * Internal JSON-RPC error.
		 */
		public static final int INTERNAL_ERROR = -32603;

	}

	public interface Request {
	}

	private static final TypeReference<HashMap<String, Object>> MAP_TYPE_REF = new TypeReference<HashMap<String, Object>>() {
	};

	/**
	 * Deserializes a JSON string into a JSONRPCMessage object.
	 * @param objectMapper The ObjectMapper instance to use for deserialization
	 * @param jsonText The JSON string to deserialize
	 * @return A JSONRPCMessage instance using either the {@link JSONRPCRequest},
	 * {@link JSONRPCNotification}, or {@link JSONRPCResponse} classes.
	 * @throws IOException If there's an error during deserialization
	 * @throws IllegalArgumentException If the JSON structure doesn't match any known
	 * message type
	 */
	public static JSONRPCMessage deserializeJsonRpcMessage(ObjectMapper objectMapper, String jsonText)
			throws IOException {

		logger.debug("Received JSON message: {}", jsonText);

		var map = objectMapper.readValue(jsonText, MAP_TYPE_REF);

		// Determine message type based on specific JSON structure
		if (map.containsKey("method") && map.containsKey("id")) {
			return objectMapper.convertValue(map, JSONRPCRequest.class);
		}
		else if (map.containsKey("method") && !map.containsKey("id")) {
			return objectMapper.convertValue(map, JSONRPCNotification.class);
		}
		else if (map.containsKey("result") || map.containsKey("error")) {
			return objectMapper.convertValue(map, JSONRPCResponse.class);
		}

		throw new IllegalArgumentException("Cannot deserialize JSONRPCMessage: " + jsonText);
	}

	// ---------------------------
	// JSON-RPC Message Types
	// ---------------------------
	public interface JSONRPCMessage {
		String getJsonrpc();
	}

	@JsonInclude(JsonInclude.Include.NON_ABSENT)
	@JsonIgnoreProperties(ignoreUnknown = true)
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class JSONRPCRequest implements JSONRPCMessage {
		@JsonProperty("jsonrpc") String jsonrpc;
		@JsonProperty("method") String method;
		@JsonProperty("id") Object id;
		@JsonProperty("params") Object params;
	} // @formatter:on

	@JsonInclude(JsonInclude.Include.NON_ABSENT)
	@JsonIgnoreProperties(ignoreUnknown = true)
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class JSONRPCNotification implements JSONRPCMessage {
		@JsonProperty("jsonrpc") String jsonrpc;
		@JsonProperty("method") String method;
		@JsonProperty("params") Map<String, Object> params;
	} // @formatter:on

	@JsonInclude(JsonInclude.Include.NON_ABSENT)
	@JsonIgnoreProperties(ignoreUnknown = true)
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class JSONRPCResponse implements JSONRPCMessage {
		@JsonProperty("jsonrpc") String jsonrpc;
		@JsonProperty("id") Object id;
		@JsonProperty("result") Object result;
		@JsonProperty("error") JSONRPCError error;

		@JsonInclude(JsonInclude.Include.NON_ABSENT)
		@JsonIgnoreProperties(ignoreUnknown = true)
		@Data
		@AllArgsConstructor
		@NoArgsConstructor
		public static class JSONRPCError {
			@JsonProperty("code") int code;
			@JsonProperty("message") String message;
			@JsonProperty("data") Object data;
		}
	}// @formatter:on

	// ---------------------------
	// Initialization
	// ---------------------------
	@JsonInclude(JsonInclude.Include.NON_ABSENT)
	@JsonIgnoreProperties(ignoreUnknown = true)
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class InitializeRequest implements Request {
		@JsonProperty("protocolVersion") String protocolVersion;
		@JsonProperty("capabilities") ClientCapabilities capabilities;
		@JsonProperty("clientInfo") Implementation clientInfo;
	} // @formatter:on

	@JsonInclude(JsonInclude.Include.NON_ABSENT)
	@JsonIgnoreProperties(ignoreUnknown = true)
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class InitializeResult {
		@JsonProperty("protocolVersion") String protocolVersion;
		@JsonProperty("capabilities") ServerCapabilities capabilities;
		@JsonProperty("serverInfo") Implementation serverInfo;
		@JsonProperty("instructions") String instructions;
	} // @formatter:on

	/**
	 * Clients can implement additional features to enrich connected MCP servers with
	 * additional capabilities. These capabilities can be used to extend the functionality
	 * of the server, or to provide additional information to the server about the
	 * client's capabilities.
	 *
	 * @param experimental WIP
	 * @param roots define the boundaries of where servers can operate within the
	 * filesystem, allowing them to understand which directories and files they have
	 * access to.
	 * @param sampling Provides a standardized way for servers to request LLM sampling
	 * (“completions” or “generations”) from language models via clients.
	 *
	 */
	@JsonInclude(JsonInclude.Include.NON_ABSENT)
	@JsonIgnoreProperties(ignoreUnknown = true)
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class ClientCapabilities {
		@JsonProperty("experimental") Map<String, Object> experimental;
		@JsonProperty("roots") RootCapabilities roots;
		@JsonProperty("sampling") Sampling sampling;

		/**
		 * Roots define the boundaries of where servers can operate within the filesystem,
		 * allowing them to understand which directories and files they have access to.
		 * Servers can request the list of roots from supporting clients and
		 * receive notifications when that list changes.
		 *
		 * @param listChanged Whether the client would send notification about roots
		 * 		  has changed since the last time the server checked.
		 */
		@JsonInclude(JsonInclude.Include.NON_ABSENT)
		@JsonIgnoreProperties(ignoreUnknown = true)
		@Data
		@AllArgsConstructor
		@NoArgsConstructor
		public static class RootCapabilities {
			@JsonProperty("listChanged") Boolean listChanged;
		}

		/**
		 * Provides a standardized way for servers to request LLM
	 	 * sampling ("completions" or "generations") from language
		 * models via clients. This flow allows clients to maintain
		 * control over model access, selection, and permissions
		 * while enabling servers to leverage AI capabilities—with
		 * no server API keys necessary. Servers can request text or
		 * image-based interactions and optionally include context
		 * from MCP servers in their prompts.
		 */
		@JsonInclude(JsonInclude.Include.NON_ABSENT)
		@Data
		@AllArgsConstructor
		public static class Sampling {
		}

		public static Builder builder() {
			return new Builder();
		}

		public static class Builder {
			private Map<String, Object> experimental;
			private RootCapabilities roots;
			private Sampling sampling;

			public Builder experimental(Map<String, Object> experimental) {
				this.experimental = experimental;
				return this;
			}

			public Builder roots(Boolean listChanged) {
				this.roots = new RootCapabilities(listChanged);
				return this;
			}

			public Builder sampling() {
				this.sampling = new Sampling();
				return this;
			}

			public ClientCapabilities build() {
				return new ClientCapabilities(experimental, roots, sampling);
			}
		}
	}// @formatter:on

	@JsonInclude(JsonInclude.Include.NON_ABSENT)
	@JsonIgnoreProperties(ignoreUnknown = true)
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class ServerCapabilities {
		@JsonProperty("experimental") Map<String, Object> experimental;
		@JsonProperty("logging") LoggingCapabilities logging;
		@JsonProperty("prompts") PromptCapabilities prompts;
		@JsonProperty("resources") ResourceCapabilities resources;
		@JsonProperty("tools") ToolCapabilities tools;

			
		@JsonInclude(JsonInclude.Include.NON_ABSENT)
		public static class LoggingCapabilities {
		}
	
		@JsonInclude(JsonInclude.Include.NON_ABSENT)
		@Data
		@AllArgsConstructor
		@NoArgsConstructor
		public static class PromptCapabilities {
			@JsonProperty("listChanged") Boolean listChanged;
		}

		@JsonInclude(JsonInclude.Include.NON_ABSENT)
		@Data
		@AllArgsConstructor
		@NoArgsConstructor
		public static class ResourceCapabilities {
			@JsonProperty("subscribe") Boolean subscribe;
			@JsonProperty("listChanged") Boolean listChanged;
		}

		@JsonInclude(JsonInclude.Include.NON_ABSENT)
		@Data
		@AllArgsConstructor
		@NoArgsConstructor
		public static class ToolCapabilities {
			@JsonProperty("listChanged") Boolean listChanged;
		}

		public static Builder builder() {
			return new Builder();
		}

		public static class Builder {

			private Map<String, Object> experimental;
			private LoggingCapabilities logging = new LoggingCapabilities();
			private PromptCapabilities prompts;
			private ResourceCapabilities resources;
			private ToolCapabilities tools;

			public Builder experimental(Map<String, Object> experimental) {
				this.experimental = experimental;
				return this;
			}

			public Builder logging() {
				this.logging = new LoggingCapabilities();
				return this;
			}

			public Builder prompts(Boolean listChanged) {
				this.prompts = new PromptCapabilities(listChanged);
				return this;
			}

			public Builder resources(Boolean subscribe, Boolean listChanged) {
				this.resources = new ResourceCapabilities(subscribe, listChanged);
				return this;
			}

			public Builder tools(Boolean listChanged) {
				this.tools = new ToolCapabilities(listChanged);
				return this;
			}

			public ServerCapabilities build() {
				return new ServerCapabilities(experimental, logging, prompts, resources, tools);
			}
		}
	} // @formatter:on

	@JsonInclude(JsonInclude.Include.NON_ABSENT)
	@JsonIgnoreProperties(ignoreUnknown = true)
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Implementation {
		@JsonProperty("name") String name;
		@JsonProperty("version") String version;
	} // @formatter:on

	// Existing Enums and Base Types (from previous implementation)
	public enum Role {// @formatter:off

		@JsonProperty("user") USER,
		@JsonProperty("assistant") ASSISTANT
	}// @formatter:on

	// ---------------------------
	// Resource Interfaces
	// ---------------------------
	/**
	 * Base for objects that include optional annotations for the client. The client can
	 * use annotations to inform how objects are used or displayed
	 */
	public interface Annotated {

		Annotations getAnnotations();

	}

	/**
	 * Optional annotations for the client. The client can use annotations to inform how
	 * objects are used or displayed.
	 *
	 * @param audience Describes who the intended customer of this object or data is. It
	 * can include multiple entries to indicate content useful for multiple audiences
	 * (e.g., `["user", "assistant"]`).
	 * @param priority Describes how important this data is for operating the server. A
	 * value of 1 means "most important," and indicates that the data is effectively
	 * required, while 0 means "least important," and indicates that the data is entirely
	 * optional. It is a number between 0 and 1.
	 */
	@JsonInclude(JsonInclude.Include.NON_ABSENT)
	@JsonIgnoreProperties(ignoreUnknown = true)
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Annotations {
		@JsonProperty("audience") List<Role> audience;
		@JsonProperty("priority") Double priority;
	} // @formatter:on

	/**
	 * A known resource that the server is capable of reading.
	 *
	 * @param uri the URI of the resource.
	 * @param name A human-readable name for this resource. This can be used by clients to
	 * populate UI elements.
	 * @param description A description of what this resource represents. This can be used
	 * by clients to improve the LLM's understanding of available resources. It can be
	 * thought of like a "hint" to the model.
	 * @param mimeType The MIME type of this resource, if known.
	 * @param annotations Optional annotations for the client. The client can use
	 * annotations to inform how objects are used or displayed.
	 */
	@JsonInclude(JsonInclude.Include.NON_ABSENT)
	@JsonIgnoreProperties(ignoreUnknown = true)
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Resource implements Annotated {
		@JsonProperty("uri") String uri;
		@JsonProperty("name") String name;
		@JsonProperty("description") String description;
		@JsonProperty("mimeType") String mimeType;
		@JsonProperty("annotations") Annotations annotations;
	} // @formatter:on

	/**
	 * Resource templates allow servers to expose parameterized resources using URI
	 * templates.
	 *
	 * @param uriTemplate A URI template that can be used to generate URIs for this
	 * resource.
	 * @param name A human-readable name for this resource. This can be used by clients to
	 * populate UI elements.
	 * @param description A description of what this resource represents. This can be used
	 * by clients to improve the LLM's understanding of available resources. It can be
	 * thought of like a "hint" to the model.
	 * @param mimeType The MIME type of this resource, if known.
	 * @param annotations Optional annotations for the client. The client can use
	 * annotations to inform how objects are used or displayed.
	 * @see <a href="https://datatracker.ietf.org/doc/html/rfc6570">RFC 6570</a>
	 */
	@JsonInclude(JsonInclude.Include.NON_ABSENT)
	@JsonIgnoreProperties(ignoreUnknown = true)
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class ResourceTemplate implements Annotated {
		@JsonProperty("uriTemplate") String uriTemplate;
		@JsonProperty("name") String name;
		@JsonProperty("description") String description;
		@JsonProperty("mimeType") String mimeType;
		@JsonProperty("annotations") Annotations annotations;
	} // @formatter:on

	@JsonInclude(JsonInclude.Include.NON_ABSENT)
	@JsonIgnoreProperties(ignoreUnknown = true)
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class ListResourcesResult {
		@JsonProperty("resources") List<Resource> resources;
		@JsonProperty("nextCursor") String nextCursor;
	} // @formatter:on

	@JsonInclude(JsonInclude.Include.NON_ABSENT)
	@JsonIgnoreProperties(ignoreUnknown = true)
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class ListResourceTemplatesResult {
		@JsonProperty("resourceTemplates") List<ResourceTemplate> resourceTemplates;
		@JsonProperty("nextCursor") String nextCursor;
	} // @formatter:on

	@JsonInclude(JsonInclude.Include.NON_ABSENT)
	@JsonIgnoreProperties(ignoreUnknown = true)
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class ReadResourceRequest {
		@JsonProperty("uri") String uri;
	} // @formatter:on

	@JsonInclude(JsonInclude.Include.NON_ABSENT)
	@JsonIgnoreProperties(ignoreUnknown = true)
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class ReadResourceResult{
		@JsonProperty("contents") List<ResourceContents> contents;
	} // @formatter:on

	/**
	 * Sent from the client to request resources/updated notifications from the server
	 * whenever a particular resource changes.
	 *
	 * @param uri the URI of the resource to subscribe to. The URI can use any protocol;
	 * it is up to the server how to interpret it.
	 */
	@JsonInclude(JsonInclude.Include.NON_ABSENT)
	@JsonIgnoreProperties(ignoreUnknown = true)
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class SubscribeRequest{
		@JsonProperty("uri") String uri;
	} // @formatter:on

	@JsonInclude(JsonInclude.Include.NON_ABSENT)
	@JsonIgnoreProperties(ignoreUnknown = true)
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class UnsubscribeRequest{
		@JsonProperty("uri") String uri;
	} // @formatter:on

	/**
	 * The contents of a specific resource or sub-resource.
	 */
	@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION, include = As.PROPERTY)
	@JsonSubTypes({ @JsonSubTypes.Type(value = TextResourceContents.class, name = "text"),
			@JsonSubTypes.Type(value = BlobResourceContents.class, name = "blob") })
	public interface ResourceContents {

		/**
		 * The URI of this resource.
		 * @return the URI of this resource.
		 */
		String getUri();

		/**
		 * The MIME type of this resource.
		 * @return the MIME type of this resource.
		 */
		String getMimeType();

	}

	/**
	 * Text contents of a resource.
	 *
	 * @param uri the URI of this resource.
	 * @param mimeType the MIME type of this resource.
	 * @param text the text of the resource. This must only be set if the resource can
	 * actually be represented as text (not binary data).
	 */
	@JsonInclude(JsonInclude.Include.NON_ABSENT)
	@JsonIgnoreProperties(ignoreUnknown = true)
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class TextResourceContents implements ResourceContents {
		@JsonProperty("uri") String uri;
		@JsonProperty("mimeType") String mimeType;
		@JsonProperty("text") String text;
	} // @formatter:on

	/**
	 * Binary contents of a resource.
	 *
	 * @param uri the URI of this resource.
	 * @param mimeType the MIME type of this resource.
	 * @param blob a base64-encoded string representing the binary data of the resource.
	 * This must only be set if the resource can actually be represented as binary data
	 * (not text).
	 */
	@JsonInclude(JsonInclude.Include.NON_ABSENT)
	@JsonIgnoreProperties(ignoreUnknown = true)
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class BlobResourceContents implements ResourceContents {
		@JsonProperty("uri") String uri;
		@JsonProperty("mimeType") String mimeType;
		@JsonProperty("blob") String blob;
	} // @formatter:on

	// ---------------------------
	// Prompt Interfaces
	// ---------------------------
	/**
	 * A prompt or prompt template that the server offers.
	 *
	 * @param name The name of the prompt or prompt template.
	 * @param description An optional description of what this prompt provides.
	 * @param arguments A list of arguments to use for templating the prompt.
	 */
	@JsonInclude(JsonInclude.Include.NON_ABSENT)
	@JsonIgnoreProperties(ignoreUnknown = true)
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Prompt {
		@JsonProperty("name") String name;
		@JsonProperty("description") String description;
		@JsonProperty("arguments") List<PromptArgument> arguments;
	} // @formatter:on

	/**
	 * Describes an argument that a prompt can accept.
	 *
	 * @param name The name of the argument.
	 * @param description A human-readable description of the argument.
	 * @param required Whether this argument must be provided.
	 */
	@JsonInclude(JsonInclude.Include.NON_ABSENT)
	@JsonIgnoreProperties(ignoreUnknown = true)
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class PromptArgument {
		@JsonProperty("name") String name;
		@JsonProperty("description") String description;
		@JsonProperty("required") Boolean required;
	}// @formatter:on

	/**
	 * Describes a message returned as part of a prompt.
	 *
	 * This is similar to `SamplingMessage`, but also supports the embedding of resources
	 * from the MCP server.
	 *
	 * @param role The sender or recipient of messages and data in a conversation.
	 * @param content The content of the message of type {@link Content}.
	 */
	@JsonInclude(JsonInclude.Include.NON_ABSENT)
	@JsonIgnoreProperties(ignoreUnknown = true)
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class PromptMessage {
		@JsonProperty("role") Role role;
		@JsonProperty("content") Content content;
	} // @formatter:on

	/**
	 * The server's response to a prompts/list request from the client.
	 *
	 * @param prompts A list of prompts that the server provides.
	 * @param nextCursor An optional cursor for pagination. If present, indicates there
	 * are more prompts available.
	 */
	@JsonInclude(JsonInclude.Include.NON_ABSENT)
	@JsonIgnoreProperties(ignoreUnknown = true)
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class ListPromptsResult {
		@JsonProperty("prompts") List<Prompt> prompts;
		@JsonProperty("nextCursor") String nextCursor;
	}// @formatter:on

	/**
	 * Used by the client to get a prompt provided by the server.
	 *
	 * @param name The name of the prompt or prompt template.
	 * @param arguments Arguments to use for templating the prompt.
	 */
	@JsonInclude(JsonInclude.Include.NON_ABSENT)
	@JsonIgnoreProperties(ignoreUnknown = true)
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class GetPromptRequest implements Request {
		@JsonProperty("name") String name;
		@JsonProperty("arguments") Map<String, Object> arguments;
	}// @formatter:off

	/**
	 * The server's response to a prompts/get request from the client.
	 *
	 * @param description An optional description for the prompt.
	 * @param messages A list of messages to display as part of the prompt.
	 */
	@JsonInclude(JsonInclude.Include.NON_ABSENT)
	@JsonIgnoreProperties(ignoreUnknown = true)
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class GetPromptResult {
		@JsonProperty("description") String description;
		@JsonProperty("messages") List<PromptMessage> messages;
	} // @formatter:on

	// ---------------------------
	// Tool Interfaces
	// ---------------------------
	/**
	 * The server's response to a tools/list request from the client.
	 *
	 * @param tools A list of tools that the server provides.
	 * @param nextCursor An optional cursor for pagination. If present, indicates there
	 * are more tools available.
	 */
	@JsonInclude(JsonInclude.Include.NON_ABSENT)
	@JsonIgnoreProperties(ignoreUnknown = true)
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class ListToolsResult {
		@JsonProperty("tools") List<Tool> tools;
		@JsonProperty("nextCursor") String nextCursor;
	}// @formatter:on

	@JsonInclude(JsonInclude.Include.NON_ABSENT)
	@JsonIgnoreProperties(ignoreUnknown = true)
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class JsonSchema {
		@JsonProperty("type") String type;
		@JsonProperty("properties") Map<String, Object> properties;
		@JsonProperty("required") List<String> required;
		@JsonProperty("additionalProperties") Boolean additionalProperties;
	} // @formatter:on

	/**
	 * Represents a tool that the server provides. Tools enable servers to expose
	 * executable functionality to the system. Through these tools, you can interact with
	 * external systems, perform computations, and take actions in the real world.
	 *
	 * @param name A unique identifier for the tool. This name is used when calling the
	 * tool.
	 * @param description A human-readable description of what the tool does. This can be
	 * used by clients to improve the LLM's understanding of available tools.
	 * @param inputSchema A JSON Schema object that describes the expected structure of
	 * the arguments when calling this tool. This allows clients to validate tool
	 * arguments before sending them to the server.
	 */
	@JsonInclude(JsonInclude.Include.NON_ABSENT)
	@JsonIgnoreProperties(ignoreUnknown = true)
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Tool {
		@JsonProperty("name") String name;
		@JsonProperty("description") String description;
		@JsonProperty("inputSchema") JsonSchema inputSchema;
	
		public Tool(String name, String description, String schema) {
			this(name, description, parseSchema(schema));
		}
			
	} // @formatter:on

	private static JsonSchema parseSchema(String schema) {
		try {
			return OBJECT_MAPPER.readValue(schema, JsonSchema.class);
		}
		catch (IOException e) {
			throw new IllegalArgumentException("Invalid schema: " + schema, e);
		}
	}

	/**
	 * Used by the client to call a tool provided by the server.
	 *
	 * @param name The name of the tool to call. This must match a tool name from
	 * tools/list.
	 * @param arguments Arguments to pass to the tool. These must conform to the tool's
	 * input schema.
	 */
	@JsonInclude(JsonInclude.Include.NON_ABSENT)
	@JsonIgnoreProperties(ignoreUnknown = true)
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class CallToolRequest implements Request {
		@JsonProperty("name") String name;
		@JsonProperty("arguments") Map<String, Object> arguments;
	}// @formatter:off

	/**
	 * The server's response to a tools/call request from the client.
	 *
	 * @param content A list of content items representing the tool's output. Each item can be text, an image,
	 *                or an embedded resource.
	 * @param isError If true, indicates that the tool execution failed and the content contains error information.
	 *                If false or absent, indicates successful execution.
	 */
	@JsonInclude(JsonInclude.Include.NON_ABSENT)
	@JsonIgnoreProperties(ignoreUnknown = true)
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class CallToolResult {
		@JsonProperty("content") List<Content> content;
		@JsonProperty("isError") Boolean isError;
	} // @formatter:on

	// ---------------------------
	// Sampling Interfaces
	// ---------------------------
	@JsonInclude(JsonInclude.Include.NON_ABSENT)
	@JsonIgnoreProperties(ignoreUnknown = true)
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class ModelPreferences {
		@JsonProperty("hints") List<ModelHint> hints;
		@JsonProperty("costPriority") Double costPriority;
		@JsonProperty("speedPriority") Double speedPriority;
		@JsonProperty("intelligencePriority") Double intelligencePriority;
	
	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private List<ModelHint> hints;
		private Double costPriority;
		private Double speedPriority;
		private Double intelligencePriority;

		public Builder hints(List<ModelHint> hints) {
			this.hints = hints;
			return this;
		}

		public Builder addHint(String name) {
			if (this.hints == null) {
				this.hints = new ArrayList<>();
			}
			this.hints.add(new ModelHint(name));
			return this;
		}

		public Builder costPriority(Double costPriority) {
			this.costPriority = costPriority;
			return this;
		}

		public Builder speedPriority(Double speedPriority) {
			this.speedPriority = speedPriority;
			return this;
		}

		public Builder intelligencePriority(Double intelligencePriority) {
			this.intelligencePriority = intelligencePriority;
			return this;
		}

		public ModelPreferences build() {
			return new ModelPreferences(hints, costPriority, speedPriority, intelligencePriority);
		}
	}
} // @formatter:on

	@JsonInclude(JsonInclude.Include.NON_ABSENT)
	@JsonIgnoreProperties(ignoreUnknown = true)
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class ModelHint {
		@JsonProperty("name") String name;

		public static ModelHint of(String name) {
			return new ModelHint(name);
		}
	}

	@JsonInclude(JsonInclude.Include.NON_ABSENT)
	@JsonIgnoreProperties(ignoreUnknown = true)
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class SamplingMessage {
		@JsonProperty("role") Role role;
		@JsonProperty("content") Content content;
	} // @formatter:on

	// Sampling and Message Creation
	@JsonInclude(JsonInclude.Include.NON_ABSENT)
	@JsonIgnoreProperties(ignoreUnknown = true)
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class CreateMessageRequest implements Request {
		@JsonProperty("messages") List<SamplingMessage> messages;
		@JsonProperty("modelPreferences") ModelPreferences modelPreferences;
		@JsonProperty("systemPrompt") String systemPrompt;
		@JsonProperty("includeContext") ContextInclusionStrategy includeContext;
		@JsonProperty("temperature") Double temperature;
		@JsonProperty("maxTokens") int maxTokens;
		@JsonProperty("stopSequences") List<String> stopSequences;
		@JsonProperty("metadata") Map<String, Object> metadata;

		public enum ContextInclusionStrategy {
			@JsonProperty("none") NONE,
			@JsonProperty("thisServer") THIS_SERVER,
			@JsonProperty("allServers") ALL_SERVERS
		}
		
		public static Builder builder() {
			return new Builder();
		}

		public static class Builder {
			private List<SamplingMessage> messages;
			private ModelPreferences modelPreferences;
			private String systemPrompt;
			private ContextInclusionStrategy includeContext;
			private Double temperature;
			private int maxTokens;
			private List<String> stopSequences;
			private Map<String, Object> metadata;

			public Builder messages(List<SamplingMessage> messages) {
				this.messages = messages;
				return this;
			}

			public Builder modelPreferences(ModelPreferences modelPreferences) {
				this.modelPreferences = modelPreferences;
				return this;
			}

			public Builder systemPrompt(String systemPrompt) {
				this.systemPrompt = systemPrompt;
				return this;
			}

			public Builder includeContext(ContextInclusionStrategy includeContext) {
				this.includeContext = includeContext;
				return this;
			}

			public Builder temperature(Double temperature) {
				this.temperature = temperature;
				return this;
			}

			public Builder maxTokens(int maxTokens) {
				this.maxTokens = maxTokens;
				return this;
			}

			public Builder stopSequences(List<String> stopSequences) {
				this.stopSequences = stopSequences;
				return this;
			}

			public Builder metadata(Map<String, Object> metadata) {
				this.metadata = metadata;
				return this;
			}

			public CreateMessageRequest build() {
				return new CreateMessageRequest(messages, modelPreferences, systemPrompt,
					includeContext, temperature, maxTokens, stopSequences, metadata);
			}
		}
	}// @formatter:on

	@JsonInclude(JsonInclude.Include.NON_ABSENT)
	@JsonIgnoreProperties(ignoreUnknown = true)
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class CreateMessageResult {
		@JsonProperty("role") Role role;
		@JsonProperty("content") Content content;
		@JsonProperty("model") String model;
		@JsonProperty("stopReason") StopReason stopReason;
		
		public enum StopReason {
			@JsonProperty("endTurn") END_TURN,
			@JsonProperty("stopSequence") STOP_SEQUENCE,
			@JsonProperty("maxTokens") MAX_TOKENS
		}

		public static Builder builder() {
			return new Builder();
		}

		public static class Builder {
			private Role role = Role.ASSISTANT;
			private Content content;
			private String model;
			private StopReason stopReason = StopReason.END_TURN;

			public Builder role(Role role) {
				this.role = role;
				return this;
			}

			public Builder content(Content content) {
				this.content = content;
				return this;
			}

			public Builder model(String model) {
				this.model = model;
				return this;
			}

			public Builder stopReason(StopReason stopReason) {
				this.stopReason = stopReason;
				return this;
			}

			public Builder message(String message) {
				this.content = new TextContent(message);
				return this;
			}

			public CreateMessageResult build() {
				return new CreateMessageResult(role, content, model, stopReason);
			}
		}
	}// @formatter:on

	// ---------------------------
	// Pagination Interfaces
	// ---------------------------
	@JsonInclude(JsonInclude.Include.NON_ABSENT)
	@JsonIgnoreProperties(ignoreUnknown = true)
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class PaginatedRequest {
		@JsonProperty("cursor") String cursor;
	}

	@JsonInclude(JsonInclude.Include.NON_ABSENT)
	@JsonIgnoreProperties(ignoreUnknown = true)
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class PaginatedResult {
		@JsonProperty("nextCursor") String nextCursor;
	}

	// ---------------------------
	// Progress and Logging
	// ---------------------------
	@JsonIgnoreProperties(ignoreUnknown = true)
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class ProgressNotification {
		@JsonProperty("progressToken") String progressToken;
		@JsonProperty("progress") double progress;
		@JsonProperty("total") Double total;
	}// @formatter:on

	/**
	 * The Model Context Protocol (MCP) provides a standardized way for servers to send
	 * structured log messages to clients. Clients can control logging verbosity by
	 * setting minimum log levels, with servers sending notifications containing severity
	 * levels, optional logger names, and arbitrary JSON-serializable data.
	 *
	 * @param level The severity levels. The mimimum log level is set by the client.
	 * @param logger The logger that generated the message.
	 * @param data JSON-serializable logging data.
	 */
	@JsonIgnoreProperties(ignoreUnknown = true)
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class LoggingMessageNotification {
		@JsonProperty("level") LoggingLevel level;
		@JsonProperty("logger") String logger;
		@JsonProperty("data") String data;

		public static Builder builder() {
			return new Builder();
		}

		public static class Builder {
			private LoggingLevel level = LoggingLevel.INFO;
			private String logger = "server";
			private String data;

			public Builder level(LoggingLevel level) {
				this.level = level;
				return this;
			}

			public Builder logger(String logger) {
				this.logger = logger;
				return this;
			}

			public Builder data(String data) {
				this.data = data;
				return this;
			}

			public LoggingMessageNotification build() {
				return new LoggingMessageNotification(level, logger, data);
			}
		}
	}// @formatter:on

	public enum LoggingLevel {// @formatter:off
		@JsonProperty("debug") DEBUG(0),
		@JsonProperty("info") INFO(1),
		@JsonProperty("notice") NOTICE(2),
		@JsonProperty("warning") WARNING(3),
		@JsonProperty("error") ERROR(4),
		@JsonProperty("critical") CRITICAL(5),
		@JsonProperty("alert") ALERT(6),
		@JsonProperty("emergency") EMERGENCY(7);

		private final int level;

		LoggingLevel(int level) {
			this.level = level;
		}

		public int level() {
			return level;
		}

	} // @formatter:on

	// ---------------------------
	// Autocomplete
	// ---------------------------
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class CompleteRequest implements Request {
		PromptOrResourceReference ref;
		CompleteArgument argument;

		public interface PromptOrResourceReference {
			String getType();
		}

		@Data
		@AllArgsConstructor
		@NoArgsConstructor
		public static class PromptReference implements PromptOrResourceReference {
			@JsonProperty("type") String type;
			@JsonProperty("name") String name;
		}// @formatter:on

		@Data
		@AllArgsConstructor
		@NoArgsConstructor
		public static class ResourceReference implements PromptOrResourceReference {
			@JsonProperty("type") String type;
			@JsonProperty("uri") String uri;
		}// @formatter:on

		@Data
		@AllArgsConstructor
		@NoArgsConstructor
		public static class CompleteArgument {
			@JsonProperty("name") String name;
			@JsonProperty("value") String value;
		}// @formatter:on
	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class CompleteResult {
		CompleteCompletion completion;

		@Data
		@AllArgsConstructor
		@NoArgsConstructor
		public static class CompleteCompletion {
			@JsonProperty("values") List<String> values;
			@JsonProperty("total") Integer total;
			@JsonProperty("hasMore") Boolean hasMore;
		}// @formatter:on
	}

	// ---------------------------
	// Content Types
	// ---------------------------
	@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = As.PROPERTY, property = "type")
	@JsonSubTypes({ @JsonSubTypes.Type(value = TextContent.class, name = "text"),
			@JsonSubTypes.Type(value = ImageContent.class, name = "image"),
			@JsonSubTypes.Type(value = EmbeddedResource.class, name = "resource") })
	public interface Content {

		default String type() {
			if (this instanceof TextContent) {
				return "text";
			}
			else if (this instanceof ImageContent) {
				return "image";
			}
			else if (this instanceof EmbeddedResource) {
				return "resource";
			}
			throw new IllegalArgumentException("Unknown content type: " + this);
		}

	}

	@JsonInclude(JsonInclude.Include.NON_ABSENT)
	@JsonIgnoreProperties(ignoreUnknown = true)
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class TextContent implements Content { // @formatter:on
		@JsonProperty("audience") List<Role> audience;
		@JsonProperty("priority") Double priority;
		@JsonProperty("text") String text;

		public TextContent(String content) {
			this(null, null, content);
		}
	}

	@JsonInclude(JsonInclude.Include.NON_ABSENT)
	@JsonIgnoreProperties(ignoreUnknown = true)
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class ImageContent implements Content { // @formatter:on
		@JsonProperty("audience") List<Role> audience;
		@JsonProperty("priority") Double priority;
		@JsonProperty("data") String data;
		@JsonProperty("mimeType") String mimeType;
	}

	@JsonInclude(JsonInclude.Include.NON_ABSENT)
	@JsonIgnoreProperties(ignoreUnknown = true)
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class EmbeddedResource implements Content { // @formatter:on
		@JsonProperty("audience") List<Role> audience;
		@JsonProperty("priority") Double priority;
		@JsonProperty("resource") ResourceContents resource;
	}

	// ---------------------------
	// Roots
	// ---------------------------
	/**
	 * Represents a root directory or file that the server can operate on.
	 *
	 * @param uri The URI identifying the root. This *must* start with file:// for now.
	 * This restriction may be relaxed in future versions of the protocol to allow other
	 * URI schemes.
	 * @param name An optional name for the root. This can be used to provide a
	 * human-readable identifier for the root, which may be useful for display purposes or
	 * for referencing the root in other parts of the application.
	 */
	@JsonInclude(JsonInclude.Include.NON_ABSENT)
	@JsonIgnoreProperties(ignoreUnknown = true)
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Root {
		@JsonProperty("uri") String uri;
		@JsonProperty("name") String name;
	} // @formatter:on

	/**
	 * The client's response to a roots/list request from the server. This result contains
	 * an array of Root objects, each representing a root directory or file that the
	 * server can operate on.
	 *
	 * @param roots An array of Root objects, each representing a root directory or file
	 * that the server can operate on.
	 */
	@JsonInclude(JsonInclude.Include.NON_ABSENT)
	@JsonIgnoreProperties(ignoreUnknown = true)
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class ListRootsResult {
		@JsonProperty("roots") List<Root> roots;
	} // @formatter:on

}
