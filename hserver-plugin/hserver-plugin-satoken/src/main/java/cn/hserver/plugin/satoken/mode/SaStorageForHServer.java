package cn.hserver.plugin.satoken.mode;

import cn.dev33.satoken.context.model.SaStorage;
import cn.hserver.plugin.web.interfaces.HttpRequest;

public class SaStorageForHServer implements SaStorage {

	/**
	 * 底层Request对象
	 */
	protected HttpRequest request;

	/**
	 * 实例化
	 * @param request request对象
	 */
	public SaStorageForHServer(HttpRequest request) {
		this.request = request;
	}

	/**
	 * 获取底层源对象
	 */
	@Override
	public Object getSource() {
		return request;
	}

	/**
	 * 在 [Request作用域] 里写入一个值
	 */
	@Override
	public SaStorageForHServer set(String key, Object value) {
		request.setAttribute(key, value);
		return this;
	}

	/**
	 * 在 [Request作用域] 里获取一个值
	 */
	@Override
	public Object get(String key) {
		return request.getAttribute(key);
	}

	/**
	 * 在 [Request作用域] 里删除一个值
	 */
	@Override
	public SaStorageForHServer delete(String key) {
		request.removeAttribute(key);
		return this;
	}

}
