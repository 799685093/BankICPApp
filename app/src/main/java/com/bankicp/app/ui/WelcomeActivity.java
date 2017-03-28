package com.bankicp.app.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.RelativeLayout;

import com.bankicp.app.MyApp;
import com.bankicp.app.R;
import com.bankicp.app.model.UserInfo;

/***
 * 欢迎界面
 */
public class WelcomeActivity extends Activity {

    private MyApp app;
    /**
     * 用户信息
     */
    private UserInfo user;
    /**
     * 首次使用
     */
    private boolean isLogin = false;
    /**
     * 是否自动登录
     */
    private boolean isMemory = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        initData();
        startAnimation();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        app = (MyApp) getApplication();
        user = app.getUserInfo();
        isMemory = app.getProps("isMemory", false);
        if (isMemory && !TextUtils.isEmpty(user.getUserno()) && !TextUtils.isEmpty(user.getUserpwd())) {
            isLogin = true;
        }
    }

    /**
     * 启动动画
     */
    private void startAnimation() {
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.logo_bg);
        AlphaAnimation aa = new AlphaAnimation(0.1f, 1.0f);
        aa.setDuration(3000);
        relativeLayout.startAnimation(aa);
        aa.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationEnd(Animation arg0) {
                if (isLogin) { // 如果存在用户并且保存了默认的用户,跳往默认主界面
                    startActivity(new Intent(WelcomeActivity.this, MainFragmentActivity.class));
                } else { // 如果没有存在一个用户的话那么跳往授权界面，添加用户
                    startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
                }
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationStart(Animation animation) {
            }

        });
    }

}
