package cn.hserver.plugin.web.exception;

/**
 * @author hxm
 */
public class QpsException extends Exception {

    private final Integer qps;

    private final Double rate;

    public QpsException(Integer qps, Double rate) {
        super("QPS异常");
        this.qps = qps;
        this.rate = rate;
    }

    public Integer getQps() {
        return qps;
    }

    public Double getRate() {
        return rate;
    }
}
