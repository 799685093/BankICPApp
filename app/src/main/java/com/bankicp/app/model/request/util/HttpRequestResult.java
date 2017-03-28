package com.bankicp.app.model.request.util;

/**
 * 图片上传返回信息状态
 * Created by liuyongfeng on 2015/11/13.
 */
public interface HttpRequestResult<T> {
    /**
     * @param code   http请求返回的代码
     * @param result http请求返回数据对象。即是data一层转换成的
     */
    public void onHttpSuccess(int code, T result);

    /**
     * @param code http请求返回码
     */
    public void onHttpFailure(int code);
}
