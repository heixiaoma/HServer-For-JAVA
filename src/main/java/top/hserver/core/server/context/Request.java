package top.hserver.core.server.context;

import top.hserver.core.interfaces.HttpRequest;
import top.hserver.core.server.handlers.FileItem;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.multipart.*;
import io.netty.util.CharsetUtil;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
public class Request implements HttpRequest {
    private String uri;
    private HttpMethod requestType;
    private String ip;
    private Map<String, String> requestParams = new ConcurrentHashMap<>();
    private Map<String, String> headers = new ConcurrentHashMap<>();

    /**
     * 文件处理
     */
    private static final ByteBuf EMPTY_BUF = Unpooled.copiedBuffer("", CharsetUtil.UTF_8);
    private ByteBuf body = EMPTY_BUF;
    private Map<String, FileItem> fileItems = new HashMap<>(8);
    private HttpData partialContent;

    public ByteBuf getBody() {
        return body;
    }

    public void setBody(ByteBuf body) {
        this.body = body;
    }


    @Override
    public String query(String name) {
        return requestParams.get(name);
    }

    @Override
    public FileItem queryFile(String name) {
        return fileItems.get(name);
    }

    @Override
    public String getIp() {
        return ip;
    }

    @Override
    public String getHeader(String headName) {
        return headers.get(headName);
    }

    public void readHttpDataChunkByChunk(HttpPostRequestDecoder decoder) {
        try {
            while (decoder.hasNext()) {
                InterfaceHttpData data = decoder.next();
                if (data != null) {
                    if (partialContent == data) {
                        partialContent = null;
                    }
                    try {
                        writeHttpData(data);
                    } finally {
                        data.release();
                    }
                }
            }
            InterfaceHttpData data = decoder.currentPartialHttpData();
            if (data != null) {
                if (partialContent == null) {
                    partialContent = (HttpData) data;
                }
            }
        } catch (HttpPostRequestDecoder.EndOfDataDecoderException e1) {
        }
    }

    private void writeHttpData(InterfaceHttpData data) {
        try {
            InterfaceHttpData.HttpDataType dataType = data.getHttpDataType();
            if (dataType == InterfaceHttpData.HttpDataType.Attribute) {
                parseAttribute((Attribute) data);
            } else if (dataType == InterfaceHttpData.HttpDataType.FileUpload) {
                parseFileUpload((FileUpload) data);
            }
        } catch (IOException e) {
        }
    }

    private void parseAttribute(Attribute attribute) throws IOException {
        this.requestParams.put(attribute.getName(), attribute.getValue());
    }

    private void parseFileUpload(FileUpload fileUpload) throws IOException {
        if (!fileUpload.isCompleted()) {
            return;
        }
        FileItem fileItem = new FileItem();
        fileItem.setName(fileUpload.getName());
        fileItem.setFileName(fileUpload.getFilename());
        Path tmpFile = Files.createTempFile(
                Paths.get(fileUpload.getFile().getParent()), "h_server_", "_upload");

        Path fileUploadPath = Paths.get(fileUpload.getFile().getPath());
        Files.move(fileUploadPath, tmpFile, StandardCopyOption.REPLACE_EXISTING);

        fileItem.setFile(tmpFile.toFile());
        fileItem.setPath(tmpFile.toFile().getPath());
        fileItem.setContentType(fileUpload.getContentType());
        fileItem.setLength(fileUpload.length());

        fileItems.put(fileItem.getName(), fileItem);
    }

}
