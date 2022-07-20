package cn.hserver.plugin.web.interfaces;

public interface ProgressStatus {

    void operationComplete(String path);


    void downloading(long progress, long total);

}
