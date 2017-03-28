package com.bankicp.app.model.request;

import android.util.Log;

import com.bankicp.app.model.request.util.HasMapToString;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

/**
 * Post网络请求
 * Created by admin on 2015/11/9.
 */
public class HttpRequest {

    public static String ResultByPost(String strUrl, String params) {
        try {
            // / 根据地址创建URL对象
            URL url = new URL(strUrl);
            // 传递数据换成字节
            byte[] data = params.getBytes();
            // 根据URL对象打开链接
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            // 设置请求时间
            urlConnection.setConnectTimeout(6000);
            // 这是请求方式为POST
            urlConnection.setRequestMethod("POST");
            // 设置post请求必要的请求头
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");// 请求头,
            // 必须设置
            urlConnection.setRequestProperty("Content-Length", data.length + "");// 注意是字节长度
            // 不是字符长度
            urlConnection.setDoOutput(true);// 准备写出
            urlConnection.getOutputStream().write(data);// 写出数据
            if (urlConnection.getResponseCode() == 200) {
                // 获取响应的输入流对象
                InputStream is = urlConnection.getInputStream();
                // 创建字节输出流对象
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                // 定义读取的长度
                int len = 0;
                // 定义缓冲区
                byte buffer[] = new byte[data.length];
                // 按照缓冲区的大小，循环读取
                while ((len = is.read(buffer)) != -1) {
                    // 根据读取的长度写入到os对象中
                    baos.write(buffer, 0, len);
                }
                // 释放资源
                is.close();
                baos.close();
                // 返回字符串
                final String result = new String(baos.toByteArray());
                return result;
            } else {
                return "网络链接不可用，请检查网络设置。";
            }
        } catch (Exception e) {
            return "网络链接不可用，请检查网络设置。";
        }

    }

    public static String ResultByPost(String strUrl, HashMap<String, String> map) {
        try {
            // / 根据地址创建URL对象
            URL url = new URL(strUrl);
            // 传递数据换成字节
            HasMapToString hasMapToString = new HasMapToString(map);
            String params = hasMapToString.getString("&");
            byte[] data = params.getBytes();
            // 根据URL对象打开链接
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            // 设置请求时间
            urlConnection.setConnectTimeout(6000);
            // 这是请求方式为POST
            urlConnection.setRequestMethod("POST");
            // 设置post请求必要的请求头
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");// 请求头,
            // 必须设置
            urlConnection.setRequestProperty("Content-Length", data.length + "");// 注意是字节长度
            // 不是字符长度
            urlConnection.setDoOutput(true);// 准备写出
            urlConnection.getOutputStream().write(data);// 写出数据
            if (urlConnection.getResponseCode() == 200) {
                // 获取响应的输入流对象
                InputStream is = urlConnection.getInputStream();
                // 创建字节输出流对象
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                // 定义读取的长度
                int len = 0;
                // 定义缓冲区
                byte buffer[] = new byte[data.length];
                // 按照缓冲区的大小，循环读取
                while ((len = is.read(buffer)) != -1) {
                    // 根据读取的长度写入到os对象中
                    baos.write(buffer, 0, len);
                }
                // 释放资源
                is.close();
                baos.close();
                // 返回字符串
                String result = new String(baos.toByteArray());
                return result;
            } else {
                return "网络链接不可用，请检查网络设置。";
            }
        } catch (Exception e) {
            return "网络链接不可用，请检查网络设置。" + e.toString();
        }

    }

    /**
     * 调试用
     *
     * @param strUrl
     * @param map
     * @return
     */
    public static String ResultByTest(String strUrl, HashMap<String, String> map) {
        try {
            // / 根据地址创建URL对象
            URL url = new URL(strUrl);
            // 传递数据换成字节
            HasMapToString hasMapToString = new HasMapToString(map);
            String params = hasMapToString.getString("&");
            byte[] data = params.getBytes();
            // 根据URL对象打开链接
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            // 设置请求时间
            urlConnection.setConnectTimeout(6000);
            // 这是请求方式为POST
            urlConnection.setRequestMethod("POST");
            // 设置post请求必要的请求头
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");// 请求头,
            // 必须设置
            urlConnection.setRequestProperty("Content-Length", data.length + "");// 注意是字节长度
            // 不是字符长度
            urlConnection.setDoOutput(true);// 准备写出
            urlConnection.getOutputStream().write(data);// 写出数据
            if (urlConnection.getResponseCode() == 200) {
                // 获取响应的输入流对象
                InputStream is = urlConnection.getInputStream();
                // 创建字节输出流对象
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                // 定义读取的长度
                int len = 0;
                // 定义缓冲区
                byte buffer[] = new byte[data.length];
                // 按照缓冲区的大小，循环读取
                while ((len = is.read(buffer)) != -1) {
                    // 根据读取的长度写入到os对象中
                    baos.write(buffer, 0, len);
                }
                // 释放资源
                is.close();
                baos.close();
                // 返回字符串
                final String result = new String(baos.toByteArray());
                Log.i("TAG", "+++++++++++++++++++" + result.toString());
                return result;
            } else {
                return "网络链接不可用，请检查网络设置。";
            }
        } catch (Exception e) {
            Log.i("TAG", "网络链接不可用，请检查网络设置。" + e.toString());
            return "网络链接不可用，请检查网络设置。" + e.toString();
        }

    }
}
