package cn.hserver.plugin.web.handlers.check;

import cn.hserver.core.ioc.IocUtil;
import cn.hserver.plugin.web.context.HServerContext;
import cn.hserver.plugin.web.exception.BusinessException;
import cn.hserver.plugin.web.interfaces.FilterAdapter;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class Filter implements DispatcherHandler{
    private static final Logger log = LoggerFactory.getLogger(Filter.class);
    private boolean hasFilter = true;
    List<FilterAdapter> listBean = IocUtil.getListBean(FilterAdapter.class);

    @Override
    public HServerContext dispatcher(HServerContext hServerContext) {
        /**
         * 如果静态文件就跳过当前的处理，否则就去执行控制器的方法
         */
        if (!hasFilter) {
            return hServerContext;
        }

        if (hServerContext.isStaticFile()) {
            return hServerContext;
        }

        /**
         * 检查限流操作是否设置了数据
         */
        if (hServerContext.getWebkit().httpResponse.hasData()) {
            return hServerContext;
        }

        /**
         * 检测下Filter的过滤哈哈
         */
        if (listBean != null) {
            try {
                for (FilterAdapter filterAdapter : listBean) {
                    filterAdapter.doFilter(hServerContext.getWebkit());
                    if (hServerContext.getWebkit().httpResponse.hasData()) {
                        break;
                    }
                }
            } catch (Exception e) {
                throw new BusinessException(HttpResponseStatus.INTERNAL_SERVER_ERROR.code(), "拦截器异常", e, hServerContext.getWebkit());
            }
        }else {
            hasFilter=false;
        }
        return hServerContext;
    }
}
