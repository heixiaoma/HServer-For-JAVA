package top.hserver.core.event;

import java.lang.reflect.Method;

/**
 * 事件处理方法
 *
 */
public class EventHandleMethod {

	private Object handler;
	private Method method;
	private String uri;

	public EventHandleMethod() {
	}

	public EventHandleMethod(Object handler, Method method, String uri) {
		this.handler = handler;
		this.method = method;
		this.uri = uri;
	}

	public Object getHandler() {
		return handler;
	}

	public void setHandler(Object handler) {
		this.handler = handler;
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
