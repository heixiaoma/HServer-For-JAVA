package net.hserver.core.interfaces;

public interface ProgressStatus {

    void operationComplete(String path);


    void downloading(long progress, long total);

}
