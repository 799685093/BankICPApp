package com.bankicp.app.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bankicp.app.MyApp;
import com.bankicp.app.R;
import com.bankicp.app.adapter.GridViewAdapter;
import com.bankicp.app.model.SimpleUser;
import com.bankicp.app.model.URLs;
import com.bankicp.app.model.UserInfo;
import com.bankicp.app.model.request.AsyncTaskRequest;
import com.bankicp.app.model.request.util.HttpRequestResult;
import com.bankicp.app.utils.JsonUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


/**
 * 弹出窗体
 * Created by admin on 2015/12/2.
 */
public class MyDialogMenber {
    private View MyDialogView;
    private Button btnCancel;
    private Button btnOK;
    private GridView gridView;

    private Activity mActivity;
    private Dialog dialog;

    private GridViewAdapter mAdapter;

    private MyApp app;
    private UserInfo user;

    private List<SimpleUser> list = new ArrayList<>();
    private ProgressBar progressBar;

    public MyDialogMenber(Activity activity) {
        this.mActivity = activity;
        app = (MyApp) mActivity.getApplicationContext();
        user = app.getUserInfo();
        initDialog();
        show();
        showMenber();
    }

    /**
     * 确定或者取消
     */
    private void initDialog() {

        dialog = new Dialog(mActivity, R.style.Dialog);
        dialog.setCanceledOnTouchOutside(false);
        LayoutInflater inflater = (mActivity.getLayoutInflater());
        MyDialogView = inflater.inflate(R.layout.dialog_select_menber, null);
        btnCancel = (Button) MyDialogView.findViewById(R.id.btnCancel);
        gridView = (GridView) MyDialogView.findViewById(R.id.gridView);
        btnOK = (Button) MyDialogView.findViewById(R.id.btnOK);
        progressBar = (ProgressBar) MyDialogView.findViewById(R.id.progress);

        gridView.setOnItemClickListener(listener);
    }

    private void show() {
        progressBar.setVisibility(View.VISIBLE);
        dialog.setContentView(MyDialogView);
        dialog.show();
    }

    public void dismiss() {
        dialog.dismiss();
    }

    /* 确认*/
    public void setPositiveButton(String text, View.OnClickListener clickListener) {
        btnOK.setText(text);
        btnOK.setOnClickListener(clickListener);
    }

    /* 取消*/
    public void setNegativeButton(String text, View.OnClickListener clickListener) {
        btnCancel.setText(text);
        btnCancel.setOnClickListener(clickListener);
    }

    private AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            try {
                if (list.get(position).isChecked()) {
                    list.get(position).setChecked(false);
                } else {
                    list.get(position).setChecked(true);
                }
                mAdapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    /*显示用户*/
    private void showMenber() {
        HashMap<String, String> map = new HashMap<>();
        map.put("userId", String.valueOf(user.getUserId()));
        map.put("url", URLs.URL_NEXT_ONDUTY_USERS);
        AsyncTaskRequest taskRequest = new AsyncTaskRequest(null, request);
        taskRequest.execute(map);
    }

    /*获取用户信息*/
    public List<SimpleUser> getUserInfo() {
        if (list != null) {
            return list;
        } else {
            return null;
        }
    }

    HttpRequestResult<String> request = new HttpRequestResult<String>() {
        @Override
        public void onHttpSuccess(int code, String result) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                code = jsonObject.optInt("Code");
                String msg = jsonObject.optString("Msg");
                if (code == 1) {
                    list.addAll(processSimpleUser(jsonObject.optString("Data")));
                    if (list.size() > 0) {
                        mAdapter = new GridViewAdapter(mActivity, list);
                        gridView.setAdapter(mAdapter);
                    } else {
                        Toast.makeText(mActivity, "暂无人员供选择", Toast.LENGTH_SHORT).show();
                    }
                    mAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException IO) {
                IO.printStackTrace();
            }
            progressBar.setVisibility(View.GONE);

        }

        @Override
        public void onHttpFailure(int code) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(mActivity, "数据请求失败", Toast.LENGTH_SHORT).show();
        }
    };

    private List<SimpleUser> processSimpleUser(String json) {
        if (TextUtils.isEmpty(json))
            return Collections.EMPTY_LIST;

        try {
            JSONArray tasks = new JSONArray(json);
            if (tasks == null || tasks.length() == 0) {
                return Collections.EMPTY_LIST;
            }

            List<SimpleUser> infos = JsonUtil.jsonToList(json, SimpleUser.class);
            return infos;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.EMPTY_LIST;
    }

}
