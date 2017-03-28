package com.bankicp.app;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.bankicp.app.model.URLs;
import com.bankicp.app.ui.MainFragmentActivity;
import com.bankicp.app.utils.AlertUtils;
import com.bankicp.app.utils.HttpMultipartPost;
import com.bankicp.app.utils.HttpMultipartPost.HttpConnectionCallback;
import com.bankicp.app.utils.ToastUtils;
import com.bankicp.app.view.NumberProgressBar;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.HashMap;
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
 * 项目名称：GDTEC_Spot 类名称：UpdateManager 类描述： 版本更新的方法 创建人：huangchunfei
 * 修改人：Administrator 创建时间：2013年10月24日 上午4:35:42 修改时间：2013年10月24日 上午4:35:42
 */
public class UpdateManager {
    MyApp app = MyApp.getInstance();

    private static final int CHECKE_UPDATE = 0;
    /* 下载中 */
    private static final int DOWNLOAD_UPDATE = 1;
    /* 下载结束 */
    private static final int DOWNLOAD_FINISH = 2;
    private static final int DOWN_ERR = 3;
    private static final int GET_UPDATE_URL = 4;

    // /* 保存解析的XML信息 */
    // HashMap<String, String> mHashMap;
    /* 下载保存路径 */
    private String mSavePath;
    /* 记录进度条数量 */
    private int progress;
    /* 是否取消更新 */
    private boolean cancelUpdate = false;

    private Context mContext;
    /* 更新进度条 */
    private NumberProgressBar mProgress;
    private Dialog mDownloadDialog;

    private String Url;
    private String Version;
    private String name;
    private String updateinfo;

    private Dialog noticeDialog;
    //	private TextView mProgressText;
    @SuppressWarnings("unused")
    private String apkFileSize;
    @SuppressWarnings("unused")
    private String tmpFileSize;
    private long time = 0;
    private boolean p = false;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case GET_UPDATE_URL:
                    if (loading != null) {
                        loading.dismiss();
                        loading = null;
                    }
                    if (msg.obj != null) {
                        try {
                            JSONObject j = new JSONObject((String) msg.obj);
                            //
                            if ("1".equals(j.optString("Code"))) {
                                String data = j.optString("Data");
                                JSONObject d = new JSONObject(data);
                                updateinfo = d.optString("AndroidUpdateContent");
                                if (!TextUtils.isEmpty(d.optString("APKPath"))) {
                                    Url = URLs.URL_BASE_APP + d.optString("APKPath");
                                    showNoticeDialog();
                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case CHECKE_UPDATE:
                    if (msg.obj != null) {
                        try {
                            JSONObject j = new JSONObject((String) msg.obj);
                            if ("1".equals(j.optString("Code"))) {
                                String data = j.optString("Data");
                                JSONObject d = new JSONObject(data);
                                Version = d.optString("AndroidVersion");
                                app.setProps("version", Version);
                                boolean isUpdate = isUpdate(Version);
                                if (isUpdate) {
                                    getUpdateUrl();
                                } else if (p) {
                                    if (loading != null) {
                                        loading.dismiss();
                                        loading = null;
                                    }
                                    ToastUtils.showToast(mContext, "当前为最新版本", Toast.LENGTH_SHORT);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                // 正在下载
                case DOWNLOAD_UPDATE:
                    if ((System.currentTimeMillis() - time) > 100) {
                        // 设置进度条位置
                        mProgress.setProgress(progress);
                        time = System.currentTimeMillis();
                    }

                    break;
                case DOWNLOAD_FINISH:
                    // mNotificationManager.cancel(notification.flags);
                    // 安装文件
                    if (mDownloadDialog != null) {
                        mDownloadDialog.dismiss();
                    }
                    // 下载完毕后变换通知形式
                    // closeNitify();
                    // // Removes the progress bar
                    // .setProgress(0, 0, false);
                    // NotifyBarClose();
                    installApk();

                    break;
                case DOWN_ERR:
                    // mNotificationManager.cancel(notification.flags);
                    // 安装文件
                    // NotifyBarClose();
                    // installApk();
                    if (mDownloadDialog != null) {
                        mDownloadDialog.dismiss();
                        Toast.makeText(mContext, "请检查你的内存卡是否挂载", Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    break;
            }
        }

    };

    public UpdateManager(Context context) {
        this.mContext = context;
    }

    private Dialog loading;

    public void checkUpdate(boolean b) {
        try {
            p = b;
            if (b) {
                loading = AlertUtils.showLoading(mContext);
                loading.show();
            }
            Map<String, String> paras = new HashMap<String, String>();
            paras.put("enterprise", URLs.ENTERPRISE);
            HttpMultipartPost httpMultipartPost = new HttpMultipartPost(
                    mContext, URLs.URL_UPDATE, paras, false);
            httpMultipartPost.setCallback(new HttpConnectionCallback() {

                @Override
                public void result(String result) {
                    Message message = new Message();
                    message.what = CHECKE_UPDATE;
                    message.obj = result;
                    mHandler.sendMessage(message);
                }

                @Override
                public void progressUpdate(Integer... progress) {

                }
            });
            httpMultipartPost.execute();
        } catch (Exception e) {

        }
    }

    public void getUpdateUrl() {
        try {
            Map<String, String> paras = new HashMap<>();
            paras.put("enterprise", URLs.ENTERPRISE);
            HttpMultipartPost httpMultipartPost = new HttpMultipartPost(
                    mContext, URLs.URL_UPDATE_CLIENT, paras, false);
            httpMultipartPost.setCallback(new HttpConnectionCallback() {

                @Override
                public void result(String result) {
                    Message message = new Message();
                    message.what = GET_UPDATE_URL;
                    message.obj = result;
                    mHandler.sendMessage(message);
                }

                @Override
                public void progressUpdate(Integer... progress) {

                }
            });
            httpMultipartPost.execute();
        } catch (Exception e) {

        }
    }

    /**
     * 检查软件是否有更新版本
     *
     * @return
     */
    public boolean isUpdate(String version_new) {

        // 获取当前软件版本
        int versionCode = getVersionCode(mContext);
        if (null != version_new) {
            try {
                int serviceCode = Integer.parseInt(version_new);
                // 版本判断
                if (serviceCode > versionCode) {
                    app.setNeedToUpdate(true);
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 获取软件版本号
     *
     * @param context
     * @return
     */
    private int getVersionCode(Context context) {
        int versionCode = 0;
        try {
            // 获取软件版本号，对应AndroidManifest.xml下android:versionCode
            versionCode = context.getPackageManager().getPackageInfo(
                    mContext.getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * 显示软件下载对话框
     */
    @SuppressLint("InflateParams")
    private void showDownloadDialog() {

        final LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.update, null);
        mProgress = (NumberProgressBar) v.findViewById(R.id.update_progress);
        mDownloadDialog = AlertUtils.showAlertBtn(mContext, v, "正在下载", "取消", null, new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mDownloadDialog != null) {
                    mDownloadDialog.dismiss();
                    mDownloadDialog = null;
                }
                cancelUpdate = true;
            }
        }, null);
        mDownloadDialog.setOnCancelListener(new OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
                cancelUpdate = true;
            }
        });

        downloadApk();
    }

    /**
     * Notification管理
     */
    public NotificationManager mNotificationManager;
    private Notification nitify;

    /**
     * 初始化通知栏
     */
    @SuppressWarnings({"deprecation", "unused"})
    private void showNotify() {
        mNotificationManager = (NotificationManager) mContext
                .getSystemService(Context.NOTIFICATION_SERVICE);

        int icon = R.mipmap.notify_icon;
        CharSequence tickerText = "开始下载";
        long when = System.currentTimeMillis();
        nitify = new Notification(icon, tickerText, when);

        // 放置在"正在运行"栏目中
        nitify.flags = Notification.FLAG_ONGOING_EVENT;
        RemoteViews contentView = new RemoteViews(mContext.getPackageName(),
                R.layout.view_custom_progress);
        contentView.setTextViewText(R.id.tv_custom_progress_title, name);
        // 指定个性化视图
        nitify.contentView = contentView;

        Intent intent = new Intent(mContext, MainFragmentActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0,
                intent, 0);
        // 指定内容意图
        nitify.contentIntent = contentIntent;
        mNotificationManager.notify(notifyId, nitify);
    }

    /**
     * 关闭Nitify
     */
    @SuppressWarnings({"unused", "deprecation"})
    private void closeNitify() {

        nitify.flags = Notification.FLAG_AUTO_CANCEL;
        nitify.contentView = null;
        Intent intent = new Intent(mContext, MainFragmentActivity.class);
        // 告知已完成
        intent.putExtra("completed", "yes");
        // 更新参数,注意flags要使用FLAG_UPDATE_CURRENT
        PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        nitify = new Notification.Builder(mContext)
                .setAutoCancel(true)
                .setContentTitle("下载完成")
                .setContentText("文件已下载完毕")
                .setContentIntent(contentIntent)
                .setSmallIcon(R.mipmap.notify_icon)
                .setWhen(System.currentTimeMillis())
                .build();
        mNotificationManager.notify(notifyId, nitify);
    }

    /**
     * Notification的ID
     */
    int notifyId = 102;

    /**
     * 设置下载进度
     */
    public void setNotify(int progress) {

        RemoteViews mRemoteViews = nitify.contentView;
        mRemoteViews.setTextViewText(R.id.tv_custom_progress_title, "下载");
        mRemoteViews.setTextViewText(R.id.tv_custom_progress_status, progress
                + "%");
        mRemoteViews.setProgressBar(R.id.custom_progressbar, 100, progress,
                false);
        // mBuilder.setProgress(100, progress, false); // 这个方法是显示进度条
        mNotificationManager.notify(notifyId, nitify);
    }

    /**
     * 取消下载
     */
    public void stopDownloadNotify() {
    }

    /**
     * 下载apk文件
     */
    private void downloadApk() {
        // 启动新线程下载软件
        // showNotify();
        new downloadApkThread().start();
    }

    /**
     * 下载文件线程
     *
     * @author gdtec
     * @date 2012-4-26
     * @blog http://blog.92coding.com
     */
    private class downloadApkThread extends Thread {

        @Override
        public void run() {
            try {
                String storageState = Environment.getExternalStorageState();
                if (storageState.equals(Environment.MEDIA_MOUNTED)) {
                    mSavePath = Environment.getExternalStorageDirectory()
                            .getAbsolutePath() + "/GPDI/";// 瀛樻斁鐓х墖鐨勬枃浠跺す

                    File savedir = new File(mSavePath);
                    if (!savedir.exists()) {
                        savedir.mkdirs();
                    }
                }
                name = "gpdi_" + Version + ".apk";
                URL url = new URL(Url);
                // 创建连接
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.connect();
                // 获取文件大小
                int length = conn.getContentLength();
                // 创建输入流
                InputStream is = conn.getInputStream();

                // 显示文件大小格式：2个小数点显示
                DecimalFormat df = new DecimalFormat("0.00");
                // 进度条下面显示的总文件大小
                apkFileSize = df.format((float) length / 1024 / 1024) + "MB";

                File apkFile = new File(mSavePath, name);

                FileOutputStream fos = new FileOutputStream(apkFile);
                int count = 0;
                // 缓存
                byte buf[] = new byte[1024];
                // 写入到文件中
                do {
                    int numread = is.read(buf);
                    count += numread;

                    // 进度条下面显示的当前下载文件大小
                    tmpFileSize = df.format((float) count / 1024 / 1024)
                            + "MB";
                    // 计算进度条位置
                    progress = (int) (((float) count / length) * 100);
                    // 更新进度
                    mHandler.sendEmptyMessage(DOWNLOAD_UPDATE);
                    if (numread <= 0) {
                        // 下载完成
                        mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
                        break;
                    }
                    // 写入文件
                    fos.write(buf, 0, numread);
                } while (!cancelUpdate);// 点击取消就停止下载.
                fos.close();
                is.close();
            } catch (Exception e) {
                mHandler.sendEmptyMessage(DOWN_ERR);
                e.printStackTrace();
            }
        }
    }

    ;

    /**
     * 安装APK文件
     */
    private void installApk() {
        File apkfile = new File(mSavePath, name);
        if (!apkfile.exists()) {
            return;
        }
        // 通过Intent安装APK文件
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
                "application/vnd.android.package-archive");
        mContext.startActivity(i);
    }

    /**
     * 显示版本更新通知对话框
     */
    private void showNoticeDialog() {

        updateinfo = updateinfo.replaceAll("#", "<br/>");
        final TextView text = new TextView(mContext);//内容
        text.setGravity(Gravity.LEFT | Gravity.CENTER);
        text.setTextSize(16);
        text.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (noticeDialog != null) {
                    noticeDialog.dismiss();
                    noticeDialog = null;
                }
                Intent intent = new Intent();
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(Url));

                mContext.startActivity(intent);
            }
        });
        text.setText(Html.fromHtml("<a href=" + Url + ">" + updateinfo + "</a>"));
        noticeDialog = AlertUtils.showAlertBtn(mContext, text, "提示", "立即更新", "以后再说",
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        showDownloadDialog();
                        noticeDialog.dismiss();
                        noticeDialog = null;
                    }
                }, new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        noticeDialog.dismiss();
                        noticeDialog = null;
                    }
                });
    }

    /**
     * 清除当前创建的通知栏
     */
    public void clearNotify(int notifyId) {
        if (mNotificationManager != null) {
            mNotificationManager.cancel(notifyId);// 删除一个特定的通知ID对应的通知
        }
    }

    public int getNotifyId() {
        return notifyId;
    }

    public void setNotifyId(int notifyId) {
        this.notifyId = notifyId;
    }

}
