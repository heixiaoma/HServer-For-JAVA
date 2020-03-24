package top.hserver.core.eventx;

import java.lang.reflect.Method;

/**
 * 事件处理方法
 *
 */
public class EventHandleMethod {

	private Object handler;
	private Method method;
	private String uri;
	private int priority;

	public EventHandleMethod() {
	}

	public EventHandleMethod(Object handler, Method method, String uri, int priority) {
		this.handler = handler;
		this.method = method;
		this.uri = uri;
		this.priority = priority;
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

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}
}
