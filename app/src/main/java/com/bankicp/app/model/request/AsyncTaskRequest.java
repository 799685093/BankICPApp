package com.bankicp.app.model.request;

import android.os.AsyncTask;

import com.bankicp.app.model.request.util.HttpRequestResult;
import com.bankicp.app.model.request.util.StringUtil;

import java.util.HashMap;


/**
 * 异步请求
 * Created by admin on 2015/12/1.
 */
public class AsyncTaskRequest extends AsyncTask<HashMap<String, String>, Void, String> {
    private ProgressView mProgressView;
    private HttpRequestResult<String> mHttpRequestResult;

    public AsyncTaskRequest(ProgressView progressView, HttpRequestResult<String> httpRequestResult) {
        this.mProgressView = progressView;
        this.mHttpRequestResult = httpRequestResult;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (mProgressView != null)
            mProgressView.showProgress();
    }

    @Override
    protected String doInBackground(HashMap<String, String>... params) {
        if (params.length > 0) {
            String url = params[0].get("url");
            params[0].remove("url");
            String result = HttpRequest.ResultByPost(url, params[0]);
            return result;
        } else {
            return "请求数据失败";
        }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (mProgressView != null)
            mProgressView.closeProgress();
        if (!StringUtil.isEmpty(result))
            mHttpRequestResult.onHttpSuccess(0, result);
        else
            mHttpRequestResult.onHttpFailure(-1);
    }

}
