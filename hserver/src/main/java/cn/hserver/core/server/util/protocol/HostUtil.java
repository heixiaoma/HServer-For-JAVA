package cn.hserver.core.server.util.protocol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

/**
 * 用于https提取SNI协议Host，方便在没有解ssl时就进行穿透处理
 * 或者明文http提取HOST
 */
public class HostUtil {
    private static final Logger log = LoggerFactory.getLogger(HostUtil.class);

    private static final String LINE_END_REGEX = "\\r\\n";

    /**
     * 提取HOST
     *
     * @param buffer
     * @return
     */
    public static String getHost(ByteBuffer buffer) {
        String domain;
        if (isHttp(buffer)) {
            domain = parseHttpHost(buffer.array(), buffer.position(), buffer.remaining());
        } else {
            domain = parseHttpsHost(buffer.array(), buffer.position(), buffer.remaining());
        }
        return domain;
    }

    private static boolean isHttp(ByteBuffer buffer) {
        switch (buffer.get(buffer.position())) {
            // HTTP methods.
            case 'G':
            case 'H':
            case 'P':
            case 'D':
            case 'O':
            case 'T':
            case 'C':
                return true;
            default:
                // Unknown first byte data.
                break;
        }
        return false;
    }

    private static String parseHttpsHost(byte[] buffer, int offset, int size) {
        int limit = offset + size;
        // Client Hello
        if (size <= 43 || buffer[offset] != 0x16) {
            log.warn("Failed to get host from SNI: Bad ssl packet.");
            return null;
        }
        // Skip 43 byte header
        offset += 43;

        // Read sessionID
        if (offset + 1 > limit) {
            log.warn("Failed to get host from SNI: No session id.");
            return null;
        }
        int sessionIDLength = buffer[offset++] & 0xFF;
        offset += sessionIDLength;

        // Read cipher suites
        if (offset + 2 > limit) {
            log.warn("Failed to get host from SNI: No cipher suites.");
            return null;
        }

        int cipherSuitesLength = readShort(buffer, offset) & 0xFFFF;
        offset += 2;
        offset += cipherSuitesLength;

        // Read Compression method.
        if (offset + 1 > limit) {
            log.warn("Failed to get host from SNI: No compression method.");
            return null;
        }
        int compressionMethodLength = buffer[offset++] & 0xFF;
        offset += compressionMethodLength;

        // Read Extensions
        if (offset + 2 > limit) {
            log.warn("Failed to get host from SNI: no extensions.");
            return null;
        }
        int extensionsLength = readShort(buffer, offset) & 0xFFFF;
        offset += 2;

        if (offset + extensionsLength > limit) {
            log.warn("Failed to get host from SNI: no sni.");
            return null;
        }

        while (offset + 4 <= limit) {
            int type0 = buffer[offset++] & 0xFF;
            int type1 = buffer[offset++] & 0xFF;
            int length = readShort(buffer, offset) & 0xFFFF;
            offset += 2;
            // Got the SNI info
            if (type0 == 0x00 && type1 == 0x00 && length > 5) {
                offset += 5;
                length -= 5;
                if (offset + length > limit) {
                    return null;
                }
                return new String(buffer, offset, length);
            } else {
                offset += length;
            }

        }
        log.warn("Failed to get host from SNI: no host.");
        return null;
    }


    private static String parseHttpHost(byte[] buffer, int offset, int size) {
        String header = new String(buffer, offset, size);
        String[] headers = header.split(LINE_END_REGEX);
        if (headers.length <= 1) {
            return null;
        }
        for (int i = 1; i < headers.length; i++) {
            String requestHeader = headers[i];
            // Reach the header end
            if (requestHeader.isEmpty()) {
                return null;
            }
            String[] nameValue = requestHeader.split(":");
            if (nameValue.length < 2) {
                return null;
            }
            String name = nameValue[0].trim();
            String value = requestHeader.replaceFirst(nameValue[0] + ": ", "").trim();
            if (name.toLowerCase().equals("host")) {
                return value;
            }
        }
        return null;
    }

    private static short readShort(byte[] data, int offset) {
        int r = ((data[offset] & 0xFF) << 8) | (data[offset + 1] & 0xFF);
        return (short) r;
    }

}
