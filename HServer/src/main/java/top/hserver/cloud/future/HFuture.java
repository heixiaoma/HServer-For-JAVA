package top.hserver.cloud.future;

import top.hserver.cloud.bean.ResultData;

import java.util.concurrent.*;

/**
 * @author hxm
 */
public class HFuture implements Future<ResultData> {

    private CountDownLatch latch = new CountDownLatch(1);

    private ResultData data;

    public void setData(ResultData data) {
        this.data = data;
        latch.countDown();
    }

    @Override
    public ResultData get() throws InterruptedException, ExecutionException {
        latch.wait();
        return data;
    }

    @Override
    public ResultData get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        if (latch.await(timeout, unit)) {
            return data;
        }
        throw new TimeoutException("远程调用超时");
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return false;
    }


}
