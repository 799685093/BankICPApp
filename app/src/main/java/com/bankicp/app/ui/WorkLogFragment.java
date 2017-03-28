/**
 * @Description: TODO(“关于”界面，展现应用相关信息)
 * @author 广东省电信工程有限公司信息技术研发中心
 * @date 2014-4-24 上午9:53:48
 */
package com.bankicp.app.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bankicp.app.R;
import com.bankicp.app.adapter.CommonAdapter;
import com.bankicp.app.adapter.ViewHolder;
import com.bankicp.app.model.Result;
import com.bankicp.app.model.SimpleUser;
import com.bankicp.app.model.TransferInfo;
import com.bankicp.app.model.URLs;
import com.bankicp.app.model.UserInfo;
import com.bankicp.app.model.request.AsyncTaskRequest;
import com.bankicp.app.model.request.ProgressView;
import com.bankicp.app.model.request.util.HttpRequestResult;
import com.bankicp.app.utils.AlertUtils;
import com.bankicp.app.utils.HttpService;
import com.bankicp.app.utils.JsonUtil;
import com.bankicp.app.utils.ToastUtils;
import com.bankicp.app.utils.Util;
import com.bankicp.app.view.MultiListView;
import com.bankicp.app.view.MultiListView.OnRefreshListener;
import com.bankicp.app.view.dialog.MyDialogMenber;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 换班
 */

public class WorkLogFragment extends BaseFragment implements ProgressView {

    // 当前页
    private Button send_btn, header_close;
    private EditText remark;
    private EditText work_log;
    private TextView to_user;
    private MultiListView pull_refresh_list;
    private List<TransferInfo> dataList;
    private CommonAdapter adapter;

    private List<SimpleUser> userlist = new ArrayList<>();
    private int updatePosition = -1;
    private UserInfo user;
    private Set<Long> userIds = new HashSet<>();

    private LinearLayout loading;

    // list
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.work_log_frament);
        user = app.getUserInfo();
    }

    @Override
    public void back() {
        FragmentManager fm = getFragmentManager();
        fm.popBackStack();
    }

    @Override
    protected void initViews() {

        header_close = $(R.id.header_close);
        send_btn = $(R.id.send_btn);
        to_user = $(R.id.to_user);
        work_log = $(R.id.work_log);
        remark = $(R.id.remark);
        loading = $(R.id.loading);
        pull_refresh_list = $(R.id.pull_refresh_list);
        dataList = new ArrayList<>();

        adapter = new CommonAdapter<TransferInfo>(mContext, dataList, R.layout.worklog_item) {
            @Override
            public void convert(final ViewHolder viewHolder, TransferInfo item) {
                final long id = item.getId();
                viewHolder.setOnClickListener(R.id.isRead, new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        updatePosition = viewHolder.getPosition();
                        new PostDataTask(mContext, null, URLs.URL_UPDATE_TTANSFER).execute(id + "");
                    }
                });
                viewHolder.setText(R.id.text, item.getTransferContent());
                viewHolder.setText(R.id.from_user, "" + item.getPreUserName());
                viewHolder.setText(R.id.to_user, "发给：" + item.getNextUserName());
                viewHolder.setText(R.id.status, "状态：" + (item.getTransferStatus() == 1 ? "已读" : "未读"));
                viewHolder.setText(R.id.time, Util.friendly_time(item.getCreateTime()));
                viewHolder.setVisible(R.id.isRead, false);
                if (item.getTransferStatus() == 0 && (item.getNextUserId() == Long.parseLong(user.getUserId()))) {
                    viewHolder.setVisible(R.id.isRead, true);
                }
            }
        };
        pull_refresh_list.setAdapter(adapter);

        pull_refresh_list.setOnRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh() {
                GetDataTask();
            }
        });

        header_close.setOnClickListener(listener);
        send_btn.setOnClickListener(listener);
        to_user.setOnClickListener(listener);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        GetDataTask();
    }

    private OnClickListener listener = new OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.header_close:
                    back();
                    break;
                case R.id.send_btn:
                    send();
                    break;
                case R.id.to_user:
                    final MyDialogMenber dialog = new MyDialogMenber(getActivity());
                    dialog.setNegativeButton("取消", new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                    dialog.setPositiveButton("确定", new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                            userlist = dialog.getUserInfo();

                            StringBuilder sb = new StringBuilder();
                            userIds.clear();
                            for (SimpleUser user : userlist) {
                                if (user.isChecked()) {
                                    sb.append(user.getUserName() + ",");
                                    userIds.add(user.getUserID());
                                }
                            }
                            if (sb.length() > 2) {
                                sb.deleteCharAt(sb.lastIndexOf(","));
                            }
                            to_user.setText(sb.toString());

                        }
                    });
                    break;

            }

        }
    };

    private void send() {
        String content = remark.getText().toString();
        String copyContent = work_log.getText().toString();
        if (TextUtils.isEmpty(content) && TextUtils.isEmpty(copyContent)) {
            ToastUtils.showToast(mContext, "内容不能为空", Toast.LENGTH_LONG);
            return;
        }
        long[] ids = new long[userIds.size()];
        int i = 0;
        for (long id : userIds) {
            ids[i++] = id;
        }
        TransferInfo transferInfo = new TransferInfo();
        transferInfo.setNextUseIds(ids);
        transferInfo.setTransferContent(content);
        transferInfo.setCopyContent(copyContent);
        transferInfo.setPreUserId(Long.parseLong(user.getUserId()));
        new PostDataTask(mContext, transferInfo, URLs.URL_ADDTTANSFER).execute();

    }

    /***
     * 联网获取事件列表
     */
    private void GetDataTask() {
        HashMap<String, String> map = new HashMap<>();
        map.put("userId", user.getUserId());
        map.put("url", URLs.URL_GET_TRANSFER_BYUSERID);
        AsyncTaskRequest taskRequest = new AsyncTaskRequest(this, requestResult);
        taskRequest.execute(map);
    }


    HttpRequestResult<String> requestResult = new HttpRequestResult<String>() {
        @Override
        public void onHttpSuccess(int code, String result) {
            List<TransferInfo> list = new ArrayList<>();
            Result res = Util.processResult(result);
            if (res != null) {
                list.addAll(processData(res.getData().toString()));
                if (dataList != null || dataList.size() > 0) {
                    dataList.clear();
                }
                dataList.addAll(list);
                adapter.notifyDataSetChanged();
                pull_refresh_list.onRefreshComplete();
            }
        }

        @Override
        public void onHttpFailure(int code) {
            Toast.makeText(getActivity(), "数据请求失败", Toast.LENGTH_SHORT).show();
        }
    };


    private List<TransferInfo> processData(String json) {
        if (TextUtils.isEmpty(json))
            return Collections.EMPTY_LIST;

        try {
            JSONArray tasks = new JSONArray(json);
            if (tasks == null || tasks.length() == 0) {
                return Collections.EMPTY_LIST;
            }

            List<TransferInfo> infos = JsonUtil.jsonToList(json, TransferInfo.class);
            return infos;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.EMPTY_LIST;
    }

    /**
     * 提交数据
     */
//    private void PostDataTask(TransferInfo model) {
//
//        try {
//
//            Map<String, String> map = new HashMap<>();
//            if (model != null) {
//                map.put("json", JsonUtil.objectToJson(model));
//            } else {
//                map.put("id", userIds);
//            }
//            Map<String, String> files = new HashMap<>();
//            String res = HttpService.doPost(this.url, map, files,
//                    new HttpService.ProgressCallback() {
//                        @Override
//                        public void progressUpdate(Integer... progress) {
//                            publishProgress(progress);
//                        }
//                    });
//            Result result = Util.processResult(res);
//            if (result.getCode() == 1) {
//                return "提交成功";
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        HashMap<String, String> map = new HashMap<>();
//        map.put("")
//
//        AsyncTaskRequest asyncTaskRequest = new AsyncTaskRequest(this, httpPostTask);
//        asyncTaskRequest.execute(map);
//
//    }

    HttpRequestResult<String> httpPostTask = new HttpRequestResult<String>() {
        @Override
        public void onHttpSuccess(int code, String result) {

        }

        @Override
        public void onHttpFailure(int code) {

        }
    };


    private class PostDataTask extends AsyncTask<String, Integer, String> {
        private Context context;
        private Object model;
        private String url;
        private TextView progress_text;
        private boolean showProgressDialog = true;
        private Handler mHandler = new Handler();

        public PostDataTask(Context context, Object model, String url) {
            this.context = context;
            this.model = model;
            this.url = url;
        }

        @Override
        protected void onPreExecute() {
            if (showProgressDialog) {
                try {
                    showLoading();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (showProgressDialog) {
                dismissLoading();
            }
            if (!TextUtils.isEmpty(result)) {
                remark.setText("");
                work_log.setText("");
                to_user.setText("");
                ToastUtils.showToast(mContext, result, 0);
                if (updatePosition >= 0) {
                    dataList.get(updatePosition).setTransferStatus(1);
                    adapter.notifyDataSetChanged();
                    updatePosition = -1;
                }
                GetDataTask();


            } else {
                ToastUtils.showToast(mContext, "提交失败", 0);
            }

            super.onPostExecute(result);
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {

            if (showProgressDialog) {
                if (progress_text != null) {
                    progress_text.setText(progress[0] + "%");
                }
            }

        }

        @Override
        protected String doInBackground(String... params) {

            try {

                Map<String, String> map = new HashMap<>();
                if (model != null) {
                    map.put("json", JsonUtil.objectToJson(model));
                } else {
                    map.put("id", params[0]);
                }

                Map<String, String> files = new HashMap<>();

                String res = HttpService.doPost(this.url, map, files,
                        new HttpService.ProgressCallback() {
                            @Override
                            public void progressUpdate(Integer... progress) {
                                publishProgress(progress);
                            }
                        });
                Result result = Util.processResult(res);
                if (result.getCode() == 1) {
                    return "提交成功";
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
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

    @Override
    public void showProgress() {
        loading.setVisibility(View.VISIBLE);
    }

    @Override
    public void closeProgress() {
        loading.setVisibility(View.GONE);
    }
}
