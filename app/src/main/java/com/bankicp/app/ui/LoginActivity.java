package com.bankicp.app.ui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bankicp.app.MyApp;
import com.bankicp.app.R;
import com.bankicp.app.model.Result;
import com.bankicp.app.model.RoleInfo;
import com.bankicp.app.model.URLs;
import com.bankicp.app.model.UserInfo;
import com.bankicp.app.utils.HttpMultipartPost;
import com.bankicp.app.utils.HttpMultipartPost.HttpConnectionCallback;
import com.bankicp.app.utils.ToastUtils;
import com.bankicp.app.utils.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity extends BaseFragmentActivity {
    private EditText usernameEt;
    private EditText passwordEt;
    private Button loginBtn;
    private Dialog loading;
    private Intent intent;
    private MyApp app;
    /**
     * @ClassName: 1
     * @Description: TODO(消息处理对象，处理用户信息验证成功和失败时的消息)
     * @author 广东省电信工程有限公司信息技术研发中心
     * @date 2014-4-24 下午4:28:07
     */
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (loading != null) {
                loading.dismiss();
                loading = null;
            }
            switch (msg.what) {

                case 1:
                    intent = new Intent(LoginActivity.this, MainFragmentActivity.class);
                    // 启动Activity
                    startActivity(intent);
                    LoginActivity.this.finish();
                    break;
                case 2:
                    ToastUtils.showToast(LoginActivity.this, getString(R.string.LoginActivity_notuser), Toast.LENGTH_LONG);
                    break;
                case 3:
                    ToastUtils.showToast(LoginActivity.this, getString(R.string.LoginActivity_invaliduser), Toast.LENGTH_LONG);
                    break;
                case 4:
                    ToastUtils.showToast(LoginActivity.this, getString(R.string.LoginActivity_loginfail), Toast.LENGTH_LONG);
                    break;
                case 0:
                case -1:
                    if (msg.obj != null) {
                        Toast.makeText(LoginActivity.this, msg.obj.toString(), Toast.LENGTH_LONG).show();
                    }
                    break;
            }
        }
    };

    /*
     * (非 Javadoc) <p>Title: onCreate</p> <p>Description: 判断是否已登录过 如果不是则呈现登录界面
     * 初始化相关界面控件 </p>
     *
     * @param savedInstanceState
     *
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_land);
        app = (MyApp) getApplication();
        initView();
    }

    private void initView() {
        // 得到用户名和密码的编辑框
        usernameEt = (EditText) findViewById(R.id.gdtec_account);
        passwordEt = (EditText) findViewById(R.id.et_pwd);
        // 得到登录按钮对象
        loginBtn = (Button) findViewById(R.id.btn_signin);
        loginBtn.setOnClickListener(listener);

    }

    /**
     * 点击事件
     */
    private OnClickListener listener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_signin:
                    try {
                        app.setProps("AUTO", usernameEt.getText().toString());
                        if (!TextUtils.isEmpty(usernameEt.getText().toString())
                                && !TextUtils.isEmpty(passwordEt.getText().toString())) {
                            doLogin(usernameEt.getText().toString(), passwordEt.getText().toString());
                        } else {
                            ToastUtils.showToast(getApplicationContext(),
                                    getString(R.string.LoginActivity_notnull), Toast.LENGTH_LONG);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

    /***
     * 登录
     */
    private void doLogin(String userNo, String pwd) {
        showLoading();
        Map<String, String> paras = new HashMap<>();
        String r = Util.getRandomStr();
        paras.put("login", userNo);
        paras.put("pwd", Util.MD5(userNo + "_" + Util.MD5(pwd) + "_" + r));
        paras.put("rand", r);
        HttpMultipartPost httpMultipartPost = new HttpMultipartPost(this, URLs.URL_LOGIN, paras, false);
        httpMultipartPost.setCallback(new HttpConnectionCallback() {
            @Override
            public void result(String result) {
                Message message = new Message();
                try {
                    Result res = Util.processResult(result);
                    if (res != null) {
                        message.what = res.getCode();
                        message.obj = res.getMsg();
                        try {
                            processData(res.getData().toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        message.what = 0;
                        message.obj = "链接超时";
                    }
                } catch (Exception e) {
                    message.what = 0;
                    message.obj = "数据解析错误";
                    e.printStackTrace();
                }
                handler.sendMessage(message);
            }

            @Override
            public void progressUpdate(Integer... progress) {

            }
        });
        httpMultipartPost.execute();
    }

    /***
     * 登陆进度进度条
     */
    private void showLoading() {
        loading = new Dialog(this, R.style.loadingStyle);
        loading.setContentView(R.layout.loginloading);
        Window dialogWindow = loading.getWindow();
        dialogWindow.setGravity(Gravity.CENTER);
        loading.setCanceledOnTouchOutside(false);
        loading.setCancelable(true);
        loading.show();
    }

    /**
     * 处理json
     *
     * @param json
     * @throws JSONException
     */
    private void processData(String json) throws JSONException {
        if (json == null)
            return;
        JSONObject user_info = new JSONObject(json);
        UserInfo user = new UserInfo();// 生成一个user对象保存到数据库
        user.setUserId(user_info.optString("id"));
        user.setUserno(user_info.optString("login"));
        user.setUserName(user_info.optString("name"));
        user.setUuid(user_info.optString("uuid"));
        user.setUserIcon(URLs.URL_BASE_PIC + user_info.optString("avatar"));
        user.setUserpwd(passwordEt.getText().toString());
        user.setMobile(user_info.optString("userMobile"));
        user.setEmail(user_info.optString("userEmail"));
        user.setUserstatus(user_info.optString("userStatus"));
        user.setCompanyid(user_info.optString("organization_id"));
        user.setDepartmentid(user_info.optString("enterprise_id"));
        user.setCompanyname(user_info.optString("firstOrganization"));
        user.setDepartmentname(user_info.optString("secondOrganization"));
        user.setChildDepartmentname(user_info.optString("thirdOrganization"));
        if (user_info.optString("Roles") != null) {
            JSONArray roles = new JSONArray(user_info.optString("Roles"));
            List<RoleInfo> roleInfos = new ArrayList<>();
            for (int i = 0; i < roles.length(); i++) {
                JSONObject obj = roles.getJSONObject(i);
                RoleInfo roleInfo = new RoleInfo();
                roleInfo.setRoleId(obj.optString("id"));
                roleInfo.setRoleLevel(obj.optString("rolelevel"));
                roleInfo.setRoleName(obj.optString("permissionRoleName"));
                roleInfos.add(roleInfo);
            }
            user.setRoles(roleInfos);
        }
        app.saveUserInfo(user);
        app.setProps("isMemory", true);
    }
}
