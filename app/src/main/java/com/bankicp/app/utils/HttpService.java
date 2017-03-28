package com.bankicp.app.utils;


import com.bankicp.app.model.CustomMultipartEntity;
import com.bankicp.app.model.CustomMultipartEntity.ProgressListener;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
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
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * * Copyright (C) 2010 The Android Open Source Project
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License. --------------------------------------- PLEASE READ
 * --------------------------------------- CopyRight (c) 广东电信工程有限公司 信息技术研发中心
 * 项目名称：GDTEC_Spot 类名称：HttpService 类描述：用于检测登陆网络的地点 创建人：Huangchunfei
 * 修改人：Huangchunfei 创建时间：2013年10月28日 下午3:09:12 修改时间：2013年10月28日 下午3:09:12
 */
public class HttpService {
    public interface ProgressCallback {
        /**
         * 进度跟新 100为总度
         **/
        void progressUpdate(Integer... progress);
    }

    private static long totalSize;
    private static String url;

    public HttpService() {
        super();
    }

    public static String doPost(String url, Map<String, String> paras, Map<String, String> files, final ProgressCallback callback) {
        String serverResponse = null;
        HttpClient httpClient = new DefaultHttpClient();
        httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 15000);
        HttpContext httpContext = new BasicHttpContext();
        HttpPost httpPost = new HttpPost(url);

        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        registry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));

        try {
            CustomMultipartEntity multipartContent = new CustomMultipartEntity(new ProgressListener() {
                @Override
                public void transferred(long num) {
                    if (callback != null) {
                        callback.progressUpdate((int) ((num / (float) totalSize) * 100));
                    }

                }
            });
            // 添加参数
            if (paras != null && !paras.isEmpty()) {
                for (Map.Entry<String, String> item : paras.entrySet()) {
                    System.out.println(item.getKey() + "=" + item.getValue().toString());
                    multipartContent.addPart(item.getKey(), new StringBody(item.getValue().toString(), Charset.forName("UTF-8")));
                }
            }
            if (files != null && !files.isEmpty()) {
                for (Map.Entry<String, String> item : files.entrySet()) {
                    if (null != item.getValue() && !"".equals(item.getValue())) {
                        File file = null;
                        try {
                            file = new File(item.getValue());
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        if (file != null && file.exists()) {
                            multipartContent.addPart(item.getKey(), new FileBody(file));
                        }
                    }


                }
            }
            // We use FileBody to transfer an image
//				multipartContent.addPart("data", new FileBody(new File(filePath)));
            totalSize = multipartContent.getContentLength();
            // Send it
            httpPost.setEntity(multipartContent);
            HttpResponse response = httpClient.execute(httpPost, httpContext);
            int code = response.getStatusLine().getStatusCode();
            if (code == 200) {
                serverResponse = EntityUtils.toString(response.getEntity());
            } else {
                serverResponse = "{code:0}";
            }
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return serverResponse;
    }
}
