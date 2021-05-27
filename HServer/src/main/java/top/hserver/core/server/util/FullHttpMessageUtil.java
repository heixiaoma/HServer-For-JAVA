package top.hserver.core.server.util;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.Map;

public class FullHttpMessageUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(FullHttpMessageUtil.class);
    private static final String LINE_SEPARATOR = "\r\n";
    private static final Charset UTF_8 = Charset.forName("UTF-8");
    
    public static MessageLogger log(FullHttpRequest request) {
        return new MessageLogger(LOGGER, request);
    }
    
    public static MessageLogger log(FullHttpResponse response) {
        return new MessageLogger(LOGGER, response);
    }
    
    public static final class MessageLogger {
        private Logger logger;
        private String line;
        private Object[] firstLine;
        private HttpHeaders headers;
        private String content;
        
        
        public MessageLogger(Logger logger, FullHttpRequest request) {
            this.logger = logger;
            this.line = "------------------ request "+ request.method()+"  ------------------";
            this.firstLine = new Object[]{request.getMethod(), request.getUri(), request.getProtocolVersion()};
            this.headers = request.headers();
            this.content = request.content().toString(UTF_8);
        }


        public MessageLogger(Logger logger, FullHttpResponse response) {
            this.logger = logger;
            this.line = "------------------ response ------------------";
            this.firstLine = new Object[]{response.getProtocolVersion(), response.getStatus().code(), response.getStatus().reasonPhrase()};
            this.headers = response.headers();
            this.content = response.content().toString(UTF_8);
        }


        public void debug() {
            logger.debug(line);
            logger.debug("{} {} {}", firstLine);
            for (Map.Entry<String, String> entry : headers) {
                logger.debug("{}:{}", entry.getKey(), entry.getValue());
            }
            logger.debug("");
            logger.debug("{}", ( content == null || content.length() == 0) ? "[no-content]":content);
            logger.debug("<<<");
        }
        
        @Override
        public String toString() {
            StringBuffer buf = new StringBuffer();
            for (int i = 0; i < firstLine.length; i++) {
                if (0 != i) {
                    buf.append(" ");
                }
                buf.append(firstLine[i]);
            }
            buf.append(LINE_SEPARATOR);
            for (Map.Entry<String, String> entry : headers) {
                buf.append(entry.getKey()).append(":").append(entry.getValue()).append(LINE_SEPARATOR);
            }
            buf.append(LINE_SEPARATOR);
            buf.append(( content == null || content.length() == 0)  ? "[no-content]":content);
            buf.append(LINE_SEPARATOR).append("<<<<<<<<<<.");
            return buf.toString();
        }
    }
}