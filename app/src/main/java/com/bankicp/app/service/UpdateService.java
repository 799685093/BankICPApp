package com.bankicp.app.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.widget.RemoteViews;

import com.bankicp.app.R;
import com.bankicp.app.ui.MainFragmentActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;

public class UpdateService extends Service {

    private static final int NOTIFY_ID = 102;
    private static final int CANCEL_DOWNLOAD = 0;
    private Context mContext = this;
    private NotificationManager mNotificationManager;
    private Notification mNotification;
    private long time;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {

        @Override
        @SuppressWarnings("deprecation")
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    // int rate = msg.arg1;
                    if (progress < 100) {
                        if ((System.currentTimeMillis() - time) > 150) {
                            // 设置进度条位置
                            setNotify(progress);
                            time = System.currentTimeMillis();
                        }
                    } else {
                        closeNitify();
                        stopSelf();// 停掉服务自身
                    }
                    // 最后别忘了通知一下,否则不会更新

                    break;
                case CANCEL_DOWNLOAD:
                    // 取消通知
                    mNotification.flags = Notification.FLAG_AUTO_CANCEL;
                    mNotification.contentView = null;
                    PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, new Intent(),
                            PendingIntent.FLAG_UPDATE_CURRENT);

                    mNotification = new Notification.Builder(mContext)
                            .setAutoCancel(true)
                            .setContentTitle("下载未完成")
                            .setContentText("下载未完成")
                            .setContentIntent(contentIntent)
                            .setSmallIcon(R.mipmap.notify_icon)
                            .setWhen(System.currentTimeMillis())
                            .build();
                    mNotificationManager.notify(NOTIFY_ID, mNotification);
                    stopSelf();// 停掉服务自身
                    break;
            }
        }

    };

    /**
     * 设置下载进度
     */
    public void setNotify(int progress) {

        RemoteViews mRemoteViews = mNotification.contentView;
        mRemoteViews
                .setTextViewText(R.id.tv_custom_progress_title, "正在下载，点击取消");
        mRemoteViews.setTextViewText(R.id.tv_custom_progress_status, progress
                + "%");
        mRemoteViews.setProgressBar(R.id.custom_progressbar, 100, progress,
                false);
        // mBuilder.setProgress(100, progress, false); // 这个方法是显示进度条
        mNotificationManager.notify(NOTIFY_ID, mNotification);
    }

    /***
     * 关闭nitify
     */
    @SuppressWarnings("deprecation")
    private void closeNitify() {
        // 下载完毕后变换通知形式
        mNotification.flags = Notification.FLAG_AUTO_CANCEL;
        mNotification.contentView = null;
        // Intent intent = new Intent(mContext, MainTabActivity.class);

        // 点击<strong>安装</strong>PendingIntent
        Uri uri = Uri.fromFile(apkFile);
        Intent installIntent = new Intent(Intent.ACTION_VIEW);
        installIntent.setDataAndType(uri,
                "application/vnd.android.package-archive");

        // 更新参数,注意flags要使用FLAG_UPDATE_CURRENT
        PendingIntent contentIntent = PendingIntent.getActivity(mContext, 3,
                installIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        mNotification = new Notification.Builder(mContext)
                .setAutoCancel(true)
                .setContentTitle("下载完成")
                .setContentText("下载完成,点击安装。")
                .setContentIntent(contentIntent)
                .setSmallIcon(R.mipmap.notify_icon)
                .setWhen(System.currentTimeMillis())
                .build();

        mNotificationManager.notify(NOTIFY_ID, mNotification);
    }

    ;

    // 是否取消（未处理）
    private boolean cancelled = false;
    private int progress;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        try {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("CANCEL_SERVICE");
            registerReceiver(broadcastReceiver, intentFilter);
        } catch (Exception e) {
            // LogUtil.recordExceptionLog(this, e, "LoadFolderOfPhotoActivity",
            // true);
            e.printStackTrace();
        }
        super.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId) {
    }

    /**
     * 初始化通知栏
     */
    @SuppressWarnings("deprecation")
    private void showNotify() {
        mNotificationManager = (NotificationManager) mContext
                .getSystemService(Context.NOTIFICATION_SERVICE);

        int icon = R.mipmap.icon;
        CharSequence tickerText = "开始下载";
        long when = System.currentTimeMillis();
        mNotification = new Notification(icon, tickerText, when);

        // 放置在"正在运行"栏目中
        mNotification.flags = Notification.FLAG_ONGOING_EVENT;
        RemoteViews contentView = new RemoteViews(mContext.getPackageName(),
                R.layout.view_custom_progress);
        contentView.setTextViewText(R.id.tv_custom_progress_title, name);
        // 指定个性化视图
        mNotification.contentView = contentView;

        Intent intent = new Intent(mContext, MainFragmentActivity.class);
        // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0,
                intent, 0);
        // 指定内容意图
        mNotification.contentIntent = contentIntent;
        mNotificationManager.notify(NOTIFY_ID, mNotification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 将进度归零
        progress = 0;
        if (intent != null) {
            Url = intent.getStringExtra("url");
            Version = intent.getStringExtra("version");
            name = "gdtec_" + Version + ".apk";
        }
        // 创建通知
        showNotify();
        startDownload();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }
        super.onDestroy();
    }

    /**
     * 获取进度
     *
     * @return
     */
    public int getProgress() {
        return progress;
    }

    /**
     * 是否已被取消
     *
     * @return
     */
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * 下载模块
     */
    private void startDownload() {
        cancelled = false;
        new downloadApkThread().start();

    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            cancelled = true;
            // if(intent.getAction().equals("CANCEL_SERVICE")){
            // cancelled = true;
            // }
            // finish();
        }
    };

    private String name;
    private static String Version = null;
    private String mSavePath;
    private static String Url = null;
    @SuppressWarnings("unused")
    private String apkFileSize;
    @SuppressWarnings("unused")
    private String tmpFileSize;
    private File apkFile;

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
                // 判断SD卡是否存在，并且是否具有读写权限
                if (Environment.getExternalStorageState().equals(
                        Environment.MEDIA_MOUNTED)) {

                    // 获得存储卡的路径
                    String sdpath = Environment.getExternalStorageDirectory()
                            + "/";
                    mSavePath = sdpath + "gdtec";
                    URL url = new URL(Url);
                    // 创建连接
                    HttpURLConnection conn = (HttpURLConnection) url
                            .openConnection();
                    conn.connect();
                    // 获取文件大小
                    int length = conn.getContentLength();
                    // 创建输入流
                    InputStream is = conn.getInputStream();

                    // 显示文件大小格式：2个小数点显示
                    DecimalFormat df = new DecimalFormat("0.00");
                    // 进度条下面显示的总文件大小
                    apkFileSize = df.format((float) length / 1024 / 1024)
                            + "MB";

                    File file = new File(mSavePath);
                    // 判断文件目录是否存在
                    if (!file.exists()) {
                        file.mkdir();
                    }
                    apkFile = new File(mSavePath, name);

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
                        mHandler.sendEmptyMessage(1);
                        if (numread <= 0) {
                            // 下载完成
                            // mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
                            break;
                        }
                        // 写入文件
                        fos.write(buf, 0, numread);
                    } while (!cancelled);// 点击取消就停止下载.
                    fos.close();
                    is.close();
                    if (cancelled)
                        mHandler.sendEmptyMessage(CANCEL_DOWNLOAD);
                } else {
                    mHandler.sendEmptyMessage(CANCEL_DOWNLOAD);
                }
            } catch (Exception e) {
                mHandler.sendEmptyMessage(CANCEL_DOWNLOAD);
            }
        }
    }

}
