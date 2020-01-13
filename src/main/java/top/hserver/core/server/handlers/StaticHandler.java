package top.hserver.core.server.handlers;


import top.hserver.core.interfaces.GlobalException;
import top.hserver.core.ioc.IocUtil;
import top.hserver.core.server.context.StaticFile;
import top.hserver.core.server.context.WebContext;
import top.hserver.core.server.exception.BusinessException;
import top.hserver.core.server.util.ExceptionUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;

import java.io.*;

/**
 * 静态文件的处理，包括文件缓存效果等
 */

@Slf4j
public class StaticHandler {

    private String basePath = "/static";

    public StaticFile handler(String uri,WebContext webContext) {
        //判断一次文件是否有/index.html文件
        if ("/".equals(uri)) {
            uri = "/index.html";
        }
        InputStream input = getResourceStreamFromJar(basePath + uri);
        if (input != null) {
            return buildStaticFile(input, uri,webContext);
        }
        return null;
    }


    private InputStream getResourceStreamFromJar(String uri) {
        return StaticHandler.class.getResourceAsStream(uri);
    }

    /**
     * 构建一个静态文件对象
     *
     * @param input
     * @param url
     * @return
     */
    private StaticFile buildStaticFile(InputStream input, String url, WebContext webContext) {
        StaticFile staticFile = new StaticFile();
        try {
            //获取文件大小
            int available = input.available();
            staticFile.setSize(available);
            //获取文件名
            int i = url.lastIndexOf("/");
            int i1 = url.lastIndexOf(".");
            if (i > -1 && i1 > 0) {
                String fileName = url.substring(i + 1, url.length());
                String[] split = fileName.split("\\.");
                staticFile.setFileName(fileName);
                //设置文件是下载还
                staticFile.setFileType(split[split.length-1]);
            } else {
                return null;
            }
            //input转ByteBuf
            ByteBuf byteBuf = Unpooled.buffer();
            byteBuf.writeBytes(input, input.available());
            staticFile.setByteBuf(byteBuf);
            input.close();
        } catch (Exception e) {
            GlobalException bean1 = IocUtil.getBean(GlobalException.class);
            if (bean1 != null) {
                bean1.handler(e, webContext.getWebkit());
            } else {
            log.error("获取文件大小异常:{}" , e.getMessage());
            try {
                input.close();
            } catch (Exception e1) {
                GlobalException bean2 = IocUtil.getBean(GlobalException.class);
                if (bean2 != null) {
                    bean2.handler(e1, webContext.getWebkit());
                } else {
                    log.error("关闭文件流异常:{}" , e.getMessage());
                    throw new BusinessException(503, "关闭文件流异常" + ExceptionUtil.getMessage(e));
                }
            }
            throw new BusinessException(503, "获取文件大小异常" + ExceptionUtil.getMessage(e));
            }
        }
        return staticFile;
    }
}
