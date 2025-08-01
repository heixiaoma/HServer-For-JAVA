package cn.hserver.mvc.response;

public interface ProgressStatus {

    void operationComplete(String path);


    void downloading(long progress, long total);

}
