package cn.hserver.netty.web.context;

import cn.hserver.mvc.multipart.PartFile;
import cn.hserver.mvc.request.HeadMap;
import cn.hserver.mvc.request.Request;
import cn.hserver.mvc.session.HttpSession;
import cn.hserver.mvc.util.IpUtil;
import cn.hserver.netty.web.util.NettyIpUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class HttpRequest implements Request {

    private static final Logger log = LoggerFactory.getLogger(Request.class);
    private String requestId;
    private String uri;
    private String uriWithParams;
    private HttpSession httpSession;
    private HttpMethod requestType;
    private ChannelHandlerContext ctx;
    private Map<String, List<String>> requestParams = new ConcurrentHashMap<>();
    private Map<String, List<String>> urlParams = new ConcurrentHashMap<>();
    private HeadMap headers;
    private final long createTime = System.currentTimeMillis();
    private byte[] body = null;
    private Map<String, PartFile> multipartFile;
    private static final String TEMP_PATH = System.getProperty("java.io.tmpdir") + File.separator;
    private Map<String, Object> attributes;

    @Override
    public void setAttribute(String key, Object value) {
        if (attributes == null) {
            attributes = new ConcurrentHashMap<>();
        }
        attributes.put(key, value);
    }

    @Override
    public Object getAttribute(String key) {
        if (attributes == null) {
            return null;
        }
        return attributes.get(key);
    }

    @Override
    public void removeAttribute(String key) {
        if (attributes != null){
            attributes.remove(key);
        }
    }

    @Override
    public HttpSession getHttpSession() {
        if (httpSession == null) {
            log.warn("如果需要使用httpSession 请在配置中打开该功能: web.openSession=true");
        }
        return httpSession;
    }

    public HttpSession getInnerHttpSession() {
        return httpSession;
    }

    public void setHttpSession(HttpSession httpSession) {
        this.httpSession = httpSession;
    }

    @Override
    public String getRequestId() {
        return requestId;
    }

    @Override
    public String getIpAddress() {
        return IpUtil.getIpAddr(this);
    }

    @Override
    public long getCreateTime() {
        return createTime;
    }

    @Override
    public Set<cn.hserver.mvc.request.Cookie> getCookies() {
        String cookieString = headers.get("Cookie");
        if (cookieString != null) {
            Set<Cookie> decode = ServerCookieDecoder.LAX.decode(cookieString);
             return decode.stream().map(DefaultCookie::new).collect(Collectors.toSet());
        }
        return null;
    }

    @Override
    public String query(String name) {
        List<String> strings = requestParams.get(name);
        if (strings != null&&!strings.isEmpty()) {
            String s = strings.get(0);
            try {
                return URLDecoder.decode(s, StandardCharsets.UTF_8.name());
            }catch (Exception e){
                return s;
            }
        }
        return null;
    }

    @Override
    public String queryUrl(String name) {
        List<String> strings = urlParams.get(name);
        if (strings != null&&!strings.isEmpty()) {
            String s = strings.get(0);
            try {
                return URLDecoder.decode(s, StandardCharsets.UTF_8.name());
            }catch (Exception e){
                return s;
            }
        }
        return null;
    }

    @Override
    public PartFile queryFile(String name) {
        if (multipartFile == null) {
            return null;
        }
        return multipartFile.get(name);
    }

    @Override
    public String getIp() {
        return NettyIpUtil.getClientIp(ctx);
    }

    @Override
    public int getPort() {
        return NettyIpUtil.getClientPort(ctx);
    }

    @Override
    public String getUriWithParams() {
        return uriWithParams;
    }

    @Override
    public cn.hserver.mvc.constants.HttpMethod getRequestMethod() {
        return cn.hserver.mvc.constants.HttpMethod.valueOf(requestType.name());
    }


    @Override
    public String getHeader(String headName) {
        return headers.get(headName);
    }

    @Override
    public String getRawData() {
        try {
            return new String(this.body, StandardCharsets.UTF_8);
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
        } catch (IOException ignored) {
        }
    }

    /**
     * httpContent 转化为 Key-Value
     *
     * @param attribute
     * @throws IOException
     */
    private void parseAttribute(Attribute attribute) throws IOException {
        addReqParams(attribute.getName(), attribute.getValue());
    }

    public void addReqParams(String key, String value) {
        if (requestParams.containsKey(key)) {
            requestParams.get(key).add(value);
        } else {
            List<String> data = new ArrayList<>();
            data.add(value);
            requestParams.put(key, data);
        }
    }

    public void addReqUrlParams(String key, String value) {
        if (urlParams.containsKey(key)) {
            urlParams.get(key).add(value);
        } else {
            urlParams.put(key, Collections.singletonList(value));
        }
    }


    /**
     * httpContent 转化为文件
     *
     * @param fileUpload
     * @throws IOException
     */
    private void parseFileUpload(FileUpload fileUpload) throws IOException {
        if (fileUpload.isCompleted()) {
            PartFile partFile = new PartFile();
            partFile.setFormName(fileUpload.getName());
            partFile.setFileName(fileUpload.getFilename());
            String s = TEMP_PATH + "h_server_" + UUID.randomUUID() + "_upload";
            if (s.contains("../")) {
                fileUpload.delete();
                return;
            }
            File file = new File(s);
            fileUpload.renameTo(file);
            partFile.setFile(file);
            partFile.setFilePath(file.getPath());
            partFile.setContentType(fileUpload.getContentType());
            partFile.setLength(fileUpload.length());
            if (multipartFile == null) {
                multipartFile = new ConcurrentHashMap<>();
            }
            multipartFile.put(partFile.getFormName(), partFile);
        }
    }

    @Override
    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void setUriWithParams(String uriWithParams) {
        this.uriWithParams = uriWithParams;
    }

    public void setRequestType(HttpMethod requestType) {
        this.requestType = requestType;
    }

    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public Map<String, List<String>> getRequestParams() {
        return requestParams;
    }

    public void setRequestParams(Map<String, List<String>> requestParams) {
        this.requestParams = requestParams;
    }

    @Override
    public Map<String, List<String>> getUrlParams() {
        return urlParams;
    }

    @Override
    public void addUrlParams(String key, String value) {
        List<String> strings = urlParams.get(key);
        if (strings == null) {
            strings = new ArrayList<>();
            strings.add(value);
            urlParams.put(key, strings);
        }else {
            strings.add(value);
        }
    }

    public void setUrlParams(Map<String, List<String>> urlParams) {
        this.urlParams = urlParams;
    }

    @Override
    public HeadMap getHeaders() {
        return headers;
    }

    public void setHeaders(HeadMap headers) {
        this.headers = headers;
    }


    @Override
    public Map<String, PartFile> getMultipartFile() {
        return multipartFile;
    }


    public static String getTempPath() {
        return TEMP_PATH;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}
