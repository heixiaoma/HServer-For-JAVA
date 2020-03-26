package top.hserver.core.event;

import top.hserver.core.ioc.IocUtil;

import java.lang.reflect.Method;

/**
 * 事件处理方法
 *
 */
public class EventHandleMethod {

	private Method method;
	private String uri;
  private String className;

	public EventHandleMethod(String className,Method method, String uri) {
		this.method = method;
		this.uri = uri;
		this.className=className;
	}

	public Object getHandler() {
    return IocUtil.getBean(this.className);
	}


	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}
	
	public String getUri() {
		return uri;
	}
	
	public void setUri(String uri) {
		this.uri = uri;
	}

}
