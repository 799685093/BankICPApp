package com.bankicp.app.ui;

import com.bankicp.app.AppManager;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

/**
 * 应用程序Activity的基类
 */
public class BaseFragmentActivity extends FragmentActivity {
	protected Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// 添加Activity到堆栈
		AppManager.getInstance().addActivity(this);
		mContext = this;

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		// 结束Activity&从堆栈中移除
		AppManager.getInstance().finishActivity(this);
	}

	@SuppressWarnings("unchecked")
	protected <T extends View> T $(int resId) {
		return (T) super.findViewById(resId);
	}
}
