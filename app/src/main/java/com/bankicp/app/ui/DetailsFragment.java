package com.bankicp.app.ui;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.bankicp.app.AppManager;
import com.bankicp.app.R;
import com.bankicp.app.model.URLs;
import com.bankicp.app.model.UserInfo;
import com.bankicp.app.utils.AlertUtils;
import com.bankicp.app.utils.DataCleanManager;
import com.bankicp.app.utils.HttpMultipartPost;
import com.bankicp.app.utils.ToastUtils;
import com.bankicp.app.utils.Util;
import com.bankicp.app.utils.HttpMultipartPost.HttpConnectionCallback;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DetailsFragment extends BaseFragment {
    TextView usertitletext, info_name_tv1_top, info_account_tv1_top,
            info_sex_tv1_top, info_company_tv1_top, info_department_tv1_top,
            info_contract_tv1_top, info_createtime_tv1_top;
    Button setting;
    private UserInfo user;
    private Button header_close;
    private RelativeLayout set_pwd;
    private Dialog dig;
    protected EditText et_pwd;
    protected EditText et_pwd_new_c;
    protected EditText et_pwd_new;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.information_details);
        user = app.getUserInfo();
    }

    private void changePwd(final String newPwd) {
        Map<String, String> paras = new HashMap<String, String>();
        String r = Util.getRandomStr();
        paras.put("login", user.getUserno());
        paras.put(
                "pwd",
                Util.MD5(user.getUserno() + "_" + Util.MD5(user.getUserpwd())
                        + "_" + r));
        paras.put("rand", r);
        paras.put("newPwd", Util.MD5(newPwd));
        HttpMultipartPost httpMultipartPost = new HttpMultipartPost(mContext,
                URLs.URL_APP_CHANGEPASSWORD, paras, true);
        httpMultipartPost.setCallback(new HttpConnectionCallback() {

            @Override
            public void result(String result) {

                try {
                    if (result != null) {
                        JSONObject jsonObject = new JSONObject(result);
                        String code = jsonObject.optString("Code");

                        if (code != null && code.equals("1")) {
                            user.setUserId(newPwd);
                            app.saveUserInfo(user);
                            app.setProps("isMemory", false);
                            ToastUtils.showToast(mContext, "修改成功", 0);

                        } else {
                            String Msg = jsonObject.optString("Msg");
                            ToastUtils.showToast(mContext, "修改失败：" + Msg, 0);
                        }
                    } else {
                        ToastUtils.showToast(mContext, "修改失败:没有响应", 0);
                    }
                } catch (JSONException e) {
                    ToastUtils.showToast(mContext, "修改失败：数据解析出错", 0);
                    e.printStackTrace();
                }

            }

            @Override
            public void progressUpdate(Integer... progress) {

            }
        });
        httpMultipartPost.execute();
    }

    @Override
    public void back() {

        FragmentManager fm = getFragmentManager();
        fm.popBackStack();

    }

    @Override
    protected void initViews() {
        header_close = $(R.id.header_close);
        setting = $(R.id.setting);
        usertitletext = $(R.id.usertitletext);
        info_name_tv1_top = $(R.id.info_name_tv1_top);
        info_account_tv1_top = $(R.id.info_account_tv1_top);
        info_sex_tv1_top = $(R.id.info_sex_tv1_top);
        info_company_tv1_top = $(R.id.info_company_tv1_top);
        info_department_tv1_top = $(R.id.info_department_tv1_top);
        info_contract_tv1_top = $(R.id.info_contract_tv1_top);
        info_createtime_tv1_top = $(R.id.info_createtime_tv1_top);
        set_pwd = $(R.id.set_pwd);

        usertitletext.setText("个人信息");
        info_name_tv1_top.setText(user.getUserName());
        info_account_tv1_top.setText(user.getUserno());
        info_sex_tv1_top.setText(user.getSex());
        info_company_tv1_top.setText(user.getCompanyname());
        info_department_tv1_top.setText(user.getDepartmentname());
        info_contract_tv1_top.setText(user.getMobile());
        info_createtime_tv1_top.setText(user.getUserstatus());
        setting.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                app.clearUserProps();

                DataCleanManager.cleanFiles(mContext);
                AppManager.getInstance().finishAllActivity();
                Intent intent = new Intent(mContext, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                app.setOnCancellation(true);
                startActivity(intent);
            }
        });
        header_close.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                back();

            }
        });
        set_pwd.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.dig_edit_pwd, null);
                et_pwd = (EditText) layout.findViewById(R.id.et_pwd);
                et_pwd_new = (EditText) layout.findViewById(R.id.et_pwd_new);
                et_pwd_new_c = (EditText) layout.findViewById(R.id.et_pwd_new_c);
                dig = AlertUtils.showAlertBtn(mContext, layout, true, "修改密码",
                        null, "确认", "取消", new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                if (TextUtils.isEmpty(et_pwd.getText()
                                        .toString())
                                        || !et_pwd.getText().toString()
                                        .equals(user.getUserpwd())) {
                                    ToastUtils.showToast(mContext, "密码错误", 0);
                                    return;

                                }
                                if (TextUtils.isEmpty(et_pwd_new.getText()
                                        .toString())) {
                                    ToastUtils
                                            .showToast(mContext, "新密码不能为空", 0);
                                    return;
                                }
                                if (TextUtils.isEmpty(et_pwd_new.getText()
                                        .toString())
                                        || !et_pwd_new
                                        .getText()
                                        .toString()
                                        .equals(et_pwd_new_c.getText()
                                                .toString())) {
                                    ToastUtils.showToast(mContext,
                                            "两次输入的密码不一致", 0);
                                    return;
                                }
                                changePwd(et_pwd_new.getText().toString());
                                dig.dismiss();
                            }

                        }, new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                dig.dismiss();
                            }
                        });
            }
        });

    }
}
