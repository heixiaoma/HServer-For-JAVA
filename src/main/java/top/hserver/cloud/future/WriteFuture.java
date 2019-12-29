package top.hserver.cloud.future;

import top.hserver.cloud.bean.ResultData;

import java.util.concurrent.Future;

public interface WriteFuture<T> extends Future<T> {

    Throwable cause();

    void setCause(Throwable cause);

    boolean isWriteSuccess();

    void setWriteResult(boolean result);

    String requestId();

    T resultData();

    void setResultData(ResultData resultData);

    boolean isTimeout();


}
