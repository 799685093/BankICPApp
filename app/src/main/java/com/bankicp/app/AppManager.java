package com.bankicp.app;

import java.util.Stack;

import android.app.ActivityManager;
import android.app.Application;
import android.app.Activity;
import android.content.Context;

public class AppManager extends Application {

    private static Stack<Activity> activityStack;
    private static AppManager instance;

    private AppManager() {
    }

    // 单例模式中获取唯一的MyApplication实例
    public static AppManager getInstance() {
        if (null == instance) {
            instance = new AppManager();
        }
        return instance;
    }

    /**
     * 添加Activity到堆栈
     */
    public void addActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<>();
        }

        activityStack.add(activity);
        for (Activity a : activityStack) {
            System.out.println("+++>" + a.getClass().getName());
        }
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
            activity.finish();
        }
        for (Activity a : activityStack) {
            System.out.println("--->" + a.getClass().getName());
        }
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        for (Activity a : activityStack) {
            System.out.println("===>" + a.getClass().getName());
        }
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (null != activityStack.get(i)) {
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
    }

    /**
     * 退出应用程序
     */
    @SuppressWarnings("deprecation")
    public void AppExit(Context context) {
        try {
            finishAllActivity();
            ActivityManager activityMgr = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            activityMgr.restartPackage(context.getPackageName());
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

