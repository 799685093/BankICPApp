/*
 *  Copyright 2010 Emmanuel Astier & Kevin Gaudin
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package acra;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import org.apache.http.conn.ssl.AllowAllHostnameVerifier;

import android.util.Log;

/**
 * Helper class to send POST data over HTTP/HTTPS.
 */
class HttpUtils {
    private static final String LOG_TAG = CrashReport.LOG_TAG;

    private static final TrustManager[] TRUST_MANAGER = {new NaiveTrustManager()};

    private static final AllowAllHostnameVerifier HOSTNAME_VERIFIER = new AllowAllHostnameVerifier();

    private static final int SOCKET_TIMEOUT = 3000;

    /**
     * Send an HTTP(s) request with POST parameters.
     *
     * @param parameters
     * @param url
     * @throws UnsupportedEncodingException
     * @throws IOException
     * @throws KeyManagementException
     * @throws NoSuchAlgorithmException
     */
    static void doPost(Map<?, ?> parameters, URL url) throws UnsupportedEncodingException,
            IOException, KeyManagementException, NoSuchAlgorithmException {

        URLConnection cnx = getConnection(url);

        // Construct data
        StringBuilder dataBfr = new StringBuilder();
        Iterator<?> iKeys = parameters.keySet().iterator();
        while (iKeys.hasNext()) {
            if (dataBfr.length() != 0) {
                dataBfr.append('&');
            }
            String key = (String) iKeys.next();
            dataBfr.append(URLEncoder.encode(key, "UTF-8")).append('=')
                    .append(URLEncoder.encode((String) parameters.get(key), "UTF-8"));
        }
        // POST data
        cnx.setDoOutput(true);

        OutputStreamWriter wr = new OutputStreamWriter(cnx.getOutputStream());
        Log.d(LOG_TAG, "Posting crash report data");
        wr.write(dataBfr.toString());
        wr.flush();
        wr.close();

        Log.d(LOG_TAG, "Reading response");
        BufferedReader rd = new BufferedReader(new InputStreamReader(cnx.getInputStream()));

        String line;
        while ((line = rd.readLine()) != null) {
            Log.d(LOG_TAG, line);
        }
        rd.close();
    }

    /**
     * Open an URL connection. If HTTPS, accepts any certificate even if not
     * valid, and connects to any host name.
     *
     * @param url The destination URL, HTTP or HTTPS.
     * @return The URLConnection.
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws KeyManagementException
     */
    private static URLConnection getConnection(URL url) throws IOException,
            NoSuchAlgorithmException, KeyManagementException {
        URLConnection conn = url.openConnection();
        if (conn instanceof HttpsURLConnection) {
            // Trust all certificates
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(new KeyManager[0], TRUST_MANAGER, new SecureRandom());
            SSLSocketFactory socketFactory = context.getSocketFactory();
            ((HttpsURLConnection) conn).setSSLSocketFactory(socketFactory);

            // Allow all hostnames
            ((HttpsURLConnection) conn).setHostnameVerifier(HOSTNAME_VERIFIER);

        }
        conn.setConnectTimeout(SOCKET_TIMEOUT);
        conn.setReadTimeout(SOCKET_TIMEOUT);
        return conn;
    }


    public static String post(String actionUrl, Map<String, String> params,
                              Map<String, File> files) throws IOException {

        String BOUNDARY = java.util.UUID.randomUUID().toString();
        String PREFIX = "--", LINEND = "\r\n";
        String MULTIPART_FROM_DATA = "multipart/form-data";
        String CHARSET = "UTF-8";
        URL uri = new URL(actionUrl);
        HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
        conn.setReadTimeout(5 * 1000);
        conn.setDoInput(true);// 允许输入
        conn.setDoOutput(true);// 允许输出
        conn.setUseCaches(false);
        conn.setRequestMethod("POST"); // Post方式
        conn.setRequestProperty("connection", "keep-alive");
        conn.setRequestProperty("Charsert", "UTF-8");
        conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA
                + ";boundary=" + BOUNDARY);

        // 首先组拼文本类型的参数
        StringBuilder sb = new StringBuilder();
        DataOutputStream outStream = new DataOutputStream(conn
                .getOutputStream());
        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                sb.append(PREFIX);
                sb.append(BOUNDARY);
                sb.append(LINEND);
                sb.append("Content-Disposition: form-data; name=\""
                        + entry.getKey() + "\"" + LINEND);
                sb.append("Content-Type: text/plain; charset=" + CHARSET + LINEND);
                sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
                sb.append(LINEND);
                sb.append(entry.getValue());
                sb.append(LINEND);
            }
            outStream.write(sb.toString().getBytes());
        }

        // 发送文件数据
        if (files != null)
            for (Map.Entry<String, File> file : files.entrySet()) {
                StringBuilder sb1 = new StringBuilder();
                sb1.append(PREFIX);
                sb1.append(BOUNDARY);
                sb1.append(LINEND);
                sb1.append("Content-Disposition: form-data; name=\"file\"; filename=\""
                        + file.getKey() + "\"" + LINEND);
                sb1.append("Content-Type: application/octet-stream; charset="
                        + CHARSET + LINEND);
                sb1.append(LINEND);
                outStream.write(sb1.toString().getBytes());
                InputStream is = new FileInputStream(file.getValue());
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = is.read(buffer)) != -1) {
                    outStream.write(buffer, 0, len);
                }

                is.close();
                outStream.write(LINEND.getBytes());
            }

        // 请求结束标志
        byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
        outStream.write(end_data);
        outStream.flush();

        // 得到响应码
        int res = conn.getResponseCode();
        InputStream in = conn.getInputStream();
        InputStreamReader isReader = new InputStreamReader(in);
        BufferedReader bufReader = new BufferedReader(isReader);
        String line = null;
        @SuppressWarnings("unused")
        String data = "OK";

        while ((line = bufReader.readLine()) == null)
            data += line;

        if (res == 200) {
            int ch;
            StringBuilder sb2 = new StringBuilder();
            while ((ch = in.read()) != -1) {
                sb2.append((char) ch);
            }
        }
        outStream.close();
        conn.disconnect();
        return in.toString();
    }

}
