package com.bankicp.app.utils;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.TextView;

import com.bankicp.app.R;
import com.bankicp.app.model.CustomMultipartEntity;
import com.bankicp.app.model.CustomMultipartEntity.ProgressListener;


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Map;

public class HttpMultipartPost extends AsyncTask<String, Integer, String> {

    private Context context;
    private TextView progress_text;
    private long totalSize;
    private String URL;
    private Map<String, String> paras;
    private Map<String, String> files;
    private boolean showProgressDialog = false;
    private HttpConnectionCallback callback;
    private Handler mHandler = new Handler();

    public HttpMultipartPost(Context context, String url, Map<String, String> paras, boolean showProgressDialog) {
        this.context = context;
        this.URL = url;
        this.paras = paras;
        this.showProgressDialog = showProgressDialog;
    }

    public HttpMultipartPost(Context context, String url,
                             Map<String, String> paras, Map<String, String> files, boolean showProgressDialog) {
        this.context = context;
        this.URL = url;
        this.paras = paras;
        this.files = files;
        this.showProgressDialog = showProgressDialog;
    }

    @Override
    protected void onPreExecute() {
        if (showProgressDialog) {
            try {
                showLoading();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Override
    protected String doInBackground(String... params) {
        String serverResponse = null;
        try {
            HttpClient httpClient = new DefaultHttpClient();
            httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 15000);
            HttpContext httpContext = new BasicHttpContext();
            HttpPost httpPost = new HttpPost(URL);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
            CustomMultipartEntity multipartContent = new CustomMultipartEntity(
                    new ProgressListener() {
                        @Override
                        public void transferred(long num) {
                            publishProgress((int) ((num / (float) totalSize) * 100));
                        }
                    });
            // 添加参数
            if (paras != null && !paras.isEmpty()) {
                for (Map.Entry<String, String> item : paras.entrySet()) {
                    multipartContent.addPart(item.getKey(), new StringBody(item.getValue().toString(), Charset.forName("UTF-8")));
                }
            }
            if (files != null && !files.isEmpty()) {
                for (Map.Entry<String, String> item : files.entrySet()) {
                    multipartContent.addPart("files", new FileBody(new File(item.getValue())));
                }
            }
            totalSize = multipartContent.getContentLength();
            httpPost.setEntity(multipartContent);
            HttpResponse response = httpClient.execute(httpPost, httpContext);
            int code = response.getStatusLine().getStatusCode();
            if (code == 200) {
                serverResponse = EntityUtils.toString(response.getEntity());
            } else {
                serverResponse = "{code:0}";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return serverResponse;
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        if (callback != null) {
            callback.progressUpdate(progress);
        }
        if (showProgressDialog) {
            if (progress_text != null) {
                progress_text.setText(progress[0] + "%");
            }
        }

    }

    @Override
    protected void onPostExecute(String result) {// //---------
        if (showProgressDialog) {
            dismissLoading();
        }
        if (callback != null) {
            callback.result(result);
        }

    }

    @Override
    protected void onCancelled() {
        System.out.println("cancle");
    }

    public void setCallback(HttpConnectionCallback callback) {
        this.callback = callback;
    }

    public interface HttpConnectionCallback {
        /**
         * 进度跟新 100为总度
         **/
        void progressUpdate(Integer... progress);

        /***
         * 结果集
         ***/
        void result(String result);
    }

    private Dialog dialog;

    private void showLoading() {


        mHandler.post(new Runnable() {


            @Override
            public void run() {
                try {
                    dialog = AlertUtils.showLoading(context);
                    progress_text = (TextView) dialog.findViewById(R.id.progress_text);
                    dialog.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void dismissLoading() {

        mHandler.post(new Runnable() {

            @Override
            public void run() {
                if (dialog != null) {
                    dialog.dismiss();
                    dialog = null;

                }
            }
        });

    }
}
