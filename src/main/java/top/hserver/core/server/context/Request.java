package top.hserver.core.server.context;

import io.netty.channel.ChannelHandlerContext;
import top.hserver.core.interfaces.HttpRequest;
import top.hserver.core.server.handlers.FileItem;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.multipart.*;
import io.netty.util.CharsetUtil;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author hxm
 */
@Getter
@Setter
public class Request implements HttpRequest {
    private String uri;
    private String nettyUri;
    private HttpMethod requestType;
    private String ip;
    private int port;
    private ChannelHandlerContext ctx;
    private Map<String, String> requestParams = new ConcurrentHashMap<>();
    private Map<String, String> headers = new ConcurrentHashMap<>();

    /**
     * 文件处理
     */
    private static final ByteBuf EMPTY_BUF = Unpooled.copiedBuffer("", CharsetUtil.UTF_8);
    private byte[] body = null;
    private Map<String, FileItem> fileItems = new HashMap<>(8);
    private static final String tempPath = System.getProperty("java.io.tmpdir") + File.separator;

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
    public int getPort() {
        return port;
    }

    @Override
    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    @Override
    public String getNettyUri() {
        return nettyUri;
    }

    @Override
    public String getHeader(String headName) {
        return headers.get(headName);
    }

    @Override
    public String getRawData() {
        try {
            return new String(this.body, "UTF-8");
        } catch (Exception e) {
            return null;
        }
    }


    @Override
    public byte[] getBody() {
        return this.body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    /**
     * 判断数据类型进行转换
     *
     * @param data
     */
    public void writeHttpData(InterfaceHttpData data) {
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

    /**
     * httpContent 转化为 Key-Value
     *
     * @param attribute
     * @throws IOException
     */
    private void parseAttribute(Attribute attribute) throws IOException {
        this.requestParams.put(attribute.getName(), attribute.getValue());
    }

    /**
     * httpContent 转化为文件
     *
     * @param fileUpload
     * @throws IOException
     */
    private void parseFileUpload(FileUpload fileUpload) throws IOException {
        if (!fileUpload.isCompleted()) {
            return;
        }
        FileItem fileItem = new FileItem();
        fileItem.setFormName(fileUpload.getName());
        fileItem.setFileName(fileUpload.getFilename());
        File file = new File(tempPath + "h_server_" + UUID.randomUUID() + "_upload");
        fileUpload.renameTo(file);
        fileItem.setFile(file);
        fileItem.setFilePath(file.getPath());
        fileItem.setContentType(fileUpload.getContentType());
        fileItem.setLength(fileUpload.length());
        fileItems.put(fileItem.getFormName(), fileItem);
    }

}
