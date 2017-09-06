package com.bankicp.app.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.TextView;

import com.bankicp.app.MyApp;
import com.bankicp.app.db.DataHelper;
import com.bankicp.app.model.Result;
import com.bankicp.app.model.TaskInfo;
import com.bankicp.app.model.URLs;
import com.bankicp.app.model.UserInfo;
import com.bankicp.app.utils.HttpMultipartPost.HttpConnectionCallback;
import com.bankicp.app.utils.HttpService;
import com.bankicp.app.utils.HttpService.ProgressCallback;
import com.bankicp.app.utils.JsonUtil;
import com.bankicp.app.utils.LogUtil;
import com.bankicp.app.utils.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NetstateReceiver extends BroadcastReceiver {
    public static String TAG = "NetstateReceiver";

    private UserInfo user;
    private MyApp app;

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo gprs = manager
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifi = manager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        app = (MyApp) context.getApplicationContext();
        user = app.getUserInfo();

        if (!gprs.isConnected() && !wifi.isConnected()) {
            //new PostDataTask(context).execute();
            LogUtil.i(TAG, "network closed");
        } else {
            LogUtil.i(TAG, "network opend");
            // network opend  
            new PostDataTask(context).execute();
        }
    }

    private class PostDataTask extends AsyncTask<String, Integer, Boolean> {
        private DataHelper dataHelper;
        private Context context;
        private ArrayList<TaskInfo> tasks;
        private TextView progress_text;
        private long totalSize;
        private boolean showProgressDialog = true;
        private HttpConnectionCallback callback;
        private Handler mHandler = new Handler();

        public PostDataTask(Context context) {
            this.context = context;
            this.dataHelper = new DataHelper(context);
        }

        @Override
        protected void onPreExecute() {
            ArrayList<TaskInfo> tasks = new ArrayList<TaskInfo>();
            ArrayList<TaskInfo> taskInfos = dataHelper.getAllTask(null, null);
            for (TaskInfo taskInfo : taskInfos) {

                if (taskInfo.getTaskState() == 1 && !taskInfo.isProblem()) {
                    tasks.add(taskInfo);
                }
            }
            this.tasks = tasks;
        }


        @Override
        protected void onPostExecute(Boolean result) {
            if (showProgressDialog) {
//				dismissLoading();
            }
            if (dataHelper != null) {
                dataHelper.Close();
            }
            if (result) {
                context.sendBroadcast(new Intent("updateCount"));
            }
            super.onPostExecute(result);
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {

        }

        @Override
        protected Boolean doInBackground(String... params) {


            try {
                int sum = 0;
                for (TaskInfo taskInfo : tasks) {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("taskJson", JsonUtil.objectToJson(taskInfo));
                    map.put("userId", user.getUserId());
                    Map<String, String> files = new HashMap<String, String>();
                    for (int i = 0; i < taskInfo.getImageList().size(); i++) {

                        files.put(i + "", taskInfo.getImageList().get(i));
                    }
                    String res = HttpService.doPost(URLs.URL_SAVE_TASK, map, files, new ProgressCallback() {

                        @Override
                        public void progressUpdate(Integer... progress) {
                            publishProgress(progress);
                        }
                    });
                    Result result = Util.processResult(res);
                    if (result.getCode() == 1) {
                        taskInfo.setTaskState(2);
                        dataHelper.saveTask(taskInfo);
                        sum++;
                    }
                }
                if (sum == tasks.size()) {
                    return true;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
    }

}  

