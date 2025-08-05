package cn.hserver.mvc.pipeline;

import cn.hserver.core.context.IocApplicationContext;
import cn.hserver.mvc.context.WebContext;
import cn.hserver.mvc.exception.GlobalExceptionHandler;
import cn.hserver.mvc.exception.NotFoundException;
import cn.hserver.mvc.filter.FilterAdapter;
import cn.hserver.mvc.filter.GlobalFilterAdapter;
import cn.hserver.mvc.server.WebServer;
import cn.hserver.mvc.staticfile.StaticFileHandler;

import java.util.ArrayList;
import java.util.List;

public class PipelineExecutor {

    private final static StaticFileHandler staticFileHandler = new StaticFileHandler();

    private static final List<GlobalFilterAdapter> globalFilters = IocApplicationContext.getBeansOfTypeSorted(GlobalFilterAdapter.class);

    private static final List<FilterAdapter> filters = IocApplicationContext.getBeansOfTypeSorted(FilterAdapter.class);
    private static final List<GlobalExceptionHandler> globalExceptionHandlers = IocApplicationContext.getBeansOfTypeSorted(GlobalExceptionHandler.class);

    public static void executor(WebContext webContext) throws Throwable {
        try {
            //全局拦截器
            for (GlobalFilterAdapter globalFilter : globalFilters) {
                globalFilter.doFilter(webContext);
                if (webContext.response.hasData()) {
                    return;
                }
            }
            //静态文件
            staticFileHandler.handlerStatic(webContext);
            if (webContext.response.hasData()) {
                return;
            }
            //普通拦截器
            for (FilterAdapter filterAdapter : filters) {
                filterAdapter.doFilter(webContext);
                if (webContext.response.hasData()) {
                    return;
                }
            }
            //控制器
            WebServer.router.matchAndHandle(webContext);
            if (!webContext.response.hasData()) {
                throw new NotFoundException();
            }
        } catch (Throwable e) {
            for (GlobalExceptionHandler globalExceptionHandler : globalExceptionHandlers) {
                globalExceptionHandler.handlerException(e,webContext);
                if (webContext.response.hasData()) {
                    return;
                }
            }
            throw e;
        }
    }
}
