package cn.hserver.plugin.web.handlers.check;

import cn.hserver.core.ioc.IocUtil;
import cn.hserver.plugin.web.context.HServerContext;
import cn.hserver.plugin.web.context.HServerContextHolder;
import cn.hserver.plugin.web.exception.BusinessException;
import cn.hserver.plugin.web.interfaces.FilterAdapter;
import cn.hserver.plugin.web.interfaces.LimitAdapter;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Limit implements DispatcherHandler{
    private static final Logger log = LoggerFactory.getLogger(Limit.class);
    private boolean hasLimit = true;
    List<LimitAdapter> listBean = IocUtil.getListBean(LimitAdapter.class);

    @Override
    public HServerContext dispatcher(HServerContext hServerContext) {
        /**
         * 否则就去执行控制器的方法
         */
        if (!hasLimit) {
            return hServerContext;
        }
        /**
         * 检查限流操作
         */
        if (listBean != null) {
            try {
                for (LimitAdapter limitAdapter : listBean) {
                    limitAdapter.doLimit(hServerContext.getWebkit());
                    if (hServerContext.getWebkit().httpResponse.hasData()) {
                        break;
                    }
                }
            } catch (Exception e) {
                throw new BusinessException(HttpResponseStatus.INTERNAL_SERVER_ERROR.code(), "拦截器异常", e, hServerContext.getWebkit());
            }
        }else {
            hasLimit=false;
        }
        return hServerContext;
    }
}
