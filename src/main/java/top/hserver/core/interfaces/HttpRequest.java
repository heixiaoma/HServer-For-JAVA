package top.hserver.core.interfaces;


import io.netty.channel.ChannelHandlerContext;
import top.hserver.core.server.handlers.FileItem;
import io.netty.handler.codec.http.HttpMethod;

import java.util.Map;

public interface HttpRequest {

    String getUri();

    String getNettyUri();

    HttpMethod getRequestType();

    Map<String, String> getRequestParams();

    String query(String name);

    Map<String, FileItem> getFileItems();

    FileItem queryFile(String name);

    String getHeader(String headName);

    Map<String, String> getHeaders();

    String getRawData();

    String getIp();

    int getPort();

    ChannelHandlerContext getCtx();


}
