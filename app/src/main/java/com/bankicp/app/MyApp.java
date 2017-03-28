package com.bankicp.app;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.StrictMode;

import com.baidu.frontia.FrontiaApplication;
import com.bankicp.app.model.FileInfo;
import com.bankicp.app.model.URLs;
import com.bankicp.app.model.UserInfo;
import com.bankicp.app.service.BaiduPush;
import com.bankicp.app.utils.CyptoUtils;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import acra.CrashReport;
import acra.ErrorReporter;

public class MyApp extends FrontiaApplication {
    private static final String FILE = "config_" + URLs.ENTERPRISE;
    public String path;
    public boolean onCancellation = false;
    public boolean isNeedToUpdate = false;
    @SuppressWarnings("unused")
    private UserInfo userInfo;
    // 记录拍照添加时，保存的路径
    private String capturePath;
    // 记录拍照添加时，保存的uri
    private Uri captureUri;
    // 选择图片时列表位置
    private int choosePosition;
    private BaiduPush mBaiduPushServer;
    private NotificationManager mNotificationManager;
    private long roomId;

    private static Context context;
    private static int mainThreadId;
    private static Handler handler;

    @SuppressWarnings("unused")
    @Override
    public void onCreate() {
        app = this;
        // SDKInitializer.initialize(this);

        if (Constants.Config.DEVELOPER_MODE
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyDialog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectAll().penaltyDeath().build());
        }
        // 异常处理，不需要处理时注释掉这两句即可！
        CrashReport crashReport = new CrashReport();
        crashReport.start(this);
        ErrorReporter errorReporter = ErrorReporter.getInstance();
        errorReporter.checkReportsOnApplicationStart();
        super.onCreate();
        initImageLoader(getApplicationContext());
        context = getApplicationContext();
        mainThreadId = android.os.Process.myTid();// 获取当前主线程id
        handler = new Handler();
    }

    public static Context getContext() {
        return context;
    }

    public static int getMainThreadId() {
        return mainThreadId;
    }

    public static Handler getHandler() {
        return handler;
    }

    public static void initImageLoader(Context context) {

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                context).threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCacheSize(50 * 1024 * 1024)// 50 Mb
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs()
                .build();
        ImageLoader.getInstance().init(config);
    }

    public static MyApp getInstance() {
        if (app == null) {
            app = new MyApp();
        }
        return app;
    }

    public NotificationManager getNotificationManager() {
        if (mNotificationManager == null)
            mNotificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
        return mNotificationManager;
    }

    public synchronized BaiduPush getBaiduPush() {
        if (mBaiduPushServer == null)
            mBaiduPushServer = new BaiduPush(BaiduPush.HTTP_METHOD_POST,
                    Constants.SECRIT_KEY, Constants.API_KEY);
        return mBaiduPushServer;
    }


    /**
     * 获取Preference设置
     */
    public static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(FILE, MODE_PRIVATE);
    }

    /**
     * 设置SharedPreferences 的值
     *
     * @param key
     * @param value
     */
    public void setProps(String key, String value) {
        Editor edit = getSharedPreferences(this).edit();
        edit.putString(key, value);
        edit.commit();
    }

    public void setProps(String key, Boolean value) {
        Editor edit = getSharedPreferences(this).edit();
        edit.putBoolean(key, value);
        edit.commit();
    }

    public String getProps(String key, String value) {
        return getSharedPreferences(this).getString(key, value);
    }

    public boolean getProps(String key, boolean value) {
        return getSharedPreferences(this).getBoolean(key, value);
    }

    /**
     * 根据键删除 SharedPreferences
     *
     * @param key
     */
    public void removeProps(String... key) {
        Editor edit = getSharedPreferences(this).edit();
        for (String k : key)
            edit.remove(k);
        edit.commit();
    }


    /***
     * 清除用户信息
     */
    public void clearUserProps() {
        removeProps(UserInfo.ID, UserInfo.USERID, UserInfo.USERNO,
                UserInfo.USERNAME, UserInfo.USERPWD, UserInfo.SEX,
                UserInfo.MOBILE, UserInfo.EMAIL, UserInfo.USERSTATUS,
                UserInfo.COMPANYID, UserInfo.DEPARTMENTID, UserInfo.EMPLOYEEID,
                UserInfo.UUID, UserInfo.COMPANYNAME, UserInfo.DEPARTMENTNAME,
                UserInfo.CHILDDEPARTMENTNAME);
    }

    /**
     * @param @param  user 将要添加的用户实体
     * @param @return
     * @throws
     * @Title: SaveUserInfo
     * @Description: TODO(添加users表的记录)
     */
    public void saveUserInfo(UserInfo user) {

        try {
            Editor editor = getSharedPreferences(this).edit();
            editor.putString(UserInfo.UUID, user.getUuid());
            editor.putString(UserInfo.USERID, user.getUserId());
            editor.putString(UserInfo.USERNO, user.getUserno());
            editor.putString(UserInfo.USERNAME, user.getUserName());
            editor.putString(UserInfo.USERPWD,
                    CyptoUtils.encode(UserInfo.USERNAME, user.getUserpwd()));
            editor.putString(UserInfo.SEX, user.getSex());
            editor.putString(UserInfo.MOBILE, user.getMobile());
            editor.putString(UserInfo.EMAIL, user.getEmail());
            editor.putString(UserInfo.USERSTATUS, user.getUserstatus());
            editor.putString(UserInfo.COMPANYID, user.getCompanyid());
            editor.putString(UserInfo.DEPARTMENTID, user.getDepartmentid());
            editor.putString(UserInfo.EMPLOYEEID, user.getEmployeeid());
            editor.putString(UserInfo.COMPANYNAME, user.getCompanyname());
            editor.putString(UserInfo.DEPARTMENTNAME, user.getDepartmentname());
            editor.putString(UserInfo.CHILDDEPARTMENTNAME,
                    user.getChildDepartmentname());
            editor.putString(UserInfo.USERICON, user.getUserIcon());

            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @return
     */
    public UserInfo getUserInfo() {
        UserInfo user = new UserInfo();
        try {
            SharedPreferences p = getSharedPreferences(this);
            user.setUuid(p.getString(UserInfo.UUID, "0"));
            user.setUserId(p.getString(UserInfo.USERID, "0"));
            user.setUserno(p.getString(UserInfo.USERNO, ""));
            user.setUserpwd(CyptoUtils.decode(UserInfo.USERNAME,
                    p.getString(UserInfo.USERPWD, "")));
            user.setUserName(p.getString(UserInfo.USERNAME, ""));
            user.setSex(p.getString(UserInfo.SEX, ""));
            user.setMobile(p.getString(UserInfo.MOBILE, ""));
            user.setEmail(p.getString(UserInfo.EMAIL, ""));
            user.setUserstatus(p.getString(UserInfo.USERSTATUS, ""));
            user.setCompanyid(p.getString(UserInfo.COMPANYID, ""));
            user.setCompanyname(p.getString(UserInfo.COMPANYNAME, ""));
            user.setDepartmentid(p.getString(UserInfo.DEPARTMENTID, ""));
            user.setDepartmentname(p.getString(UserInfo.DEPARTMENTNAME, ""));
            user.setChildDepartmentname(p.getString(
                    UserInfo.CHILDDEPARTMENTNAME, ""));
            user.setEmployeeid(p.getString(UserInfo.EMPLOYEEID, ""));
            user.setUserIcon(p.getString(UserInfo.USERICON, ""));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    public int width = 0;


    public Map<String, Boolean> mapIsChoosed = new HashMap<>();

    public Map<String, Boolean> getMapIsChoosed() {
        return mapIsChoosed;
    }


    public ArrayList<String> strChooseAblum = new ArrayList<>();


    public int getWidth() {
        return width;
    }

    private ArrayList<FileInfo> chooseImages = new ArrayList<FileInfo>();


    public ArrayList<FileInfo> getChooseImages() {
        return chooseImages;
    }

    public void setChooseImages(ArrayList<FileInfo> chooseImages) {
        this.chooseImages = chooseImages;
    }

    public void setWidth(int width) {
        this.width = width;
    }


    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }


    private static MyApp app;


    public void setOnCancellation(boolean onCancellation) {
        this.onCancellation = onCancellation;
    }


    public void setNeedToUpdate(boolean isNeedToUpdate) {
        this.isNeedToUpdate = isNeedToUpdate;
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }


    public void setCapturePath(String capturePath) {
        this.capturePath = capturePath;
    }


    public int getChoosePosition() {
        return choosePosition;
    }

    public void setChoosePosition(int choosePosition) {
        this.choosePosition = choosePosition;
    }

    public boolean isTopActivity() {
        try {
            String packageName = getPackageName();
            ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(1);
            if (tasksInfo.size() > 0) {
                System.out.println("---------------包名-----------"
                        + tasksInfo.get(0).topActivity.getPackageName());
                // 应用程序位于堆栈的顶层
                if (packageName.equals(tasksInfo.get(0).topActivity
                        .getPackageName())) {
                    return true;
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    public long getRoomId() {
        return roomId;
    }

    public void setRoomId(long roomId) {
        this.roomId = roomId;
    }

}