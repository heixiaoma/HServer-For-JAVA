package com.hserver.core.interfaces;

import java.io.File;
import java.io.InputStream;
import java.util.Map;

public interface HttpResponse {

    void setHeader(String key, String value);

    void setDownloadFile(File file);

    void setDownloadFile(InputStream inputStream, String fileName);

    void sendJson(Object object);

    void sendHtml(String html);

    void sendTemplate(String htmlPath, Map<String,Object> obj);

}
