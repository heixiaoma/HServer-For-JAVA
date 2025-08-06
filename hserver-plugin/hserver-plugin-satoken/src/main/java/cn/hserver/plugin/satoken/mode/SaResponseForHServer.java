package cn.hserver.plugin.satoken.mode;

import cn.dev33.satoken.context.model.SaResponse;
import cn.dev33.satoken.exception.SaTokenException;
import cn.hserver.mvc.constants.HttpResponseStatus;
import cn.hserver.mvc.response.Response;

public class SaResponseForHServer implements SaResponse {
	/**
	 * 底层Request对象
	 */
	protected Response response;

	/**
	 * 实例化
	 * @param response response对象
	 */
	public SaResponseForHServer(Response response) {
		this.response = response;
	}

	/**
	 * 获取底层源对象
	 */
	@Override
	public Object getSource() {
		return response;
	}

	/**
	 * 设置响应状态码
	 */
	@Override
	public SaResponse setStatus(int sc) {
		response.setStatus(HttpResponseStatus.value(sc));
		return this;
	}

	/**
	 * 在响应头里写入一个值
	 */
	@Override
	public SaResponse setHeader(String name, String value) {
		response.addHeader(name, value);
		return this;
	}

	/**
	 * 在响应头里添加一个值
	 * @param name 名字
	 * @param value 值
	 * @return 对象自身
	 */
	public SaResponse addHeader(String name, String value) {
		response.addHeader(name, value);
		return this;
	}

	/**
	 * 重定向
	 */
	@Override
	public Object redirect(String url) {
		try {
			response.redirect(url);
		} catch (Exception e) {
			throw new SaTokenException(e);
		}
		return null;
	}


}
