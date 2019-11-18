package com.hserver.core.interfaces;

import java.io.File;
import java.io.InputStream;

public interface HttpResponse {

    void setHeader(String key, String value);

    void setDownloadFile(File file);

    void setDownloadFile(InputStream inputStream, String fileName);
}
