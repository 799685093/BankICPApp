package com.bankicp.app.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bankicp.app.MyApp;
import com.bankicp.app.R;

/**
 * 
 */
public abstract class BaseFragment extends Fragment {
	protected Context mContext;
	protected Bundle bundle;
	protected MyApp app;
	private View rootView;
	private int viewId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		mContext = getActivity();
		app = (MyApp) mContext.getApplicationContext();
		bundle = getArguments();

	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		
		rootView = inflater.inflate(this.viewId, container, false);
		initViews();
		return rootView == null ? super.onCreateView(inflater, container,
				savedInstanceState) : rootView;
	}

	/**
	 * 
	 * @param rid
	 */
	protected void setContentView(int rid) {
		this.viewId = rid;
	};

	protected abstract void initViews();

	@SuppressWarnings("unchecked")
	protected <T extends View> T $(int resId) {
		return (T) rootView.findViewById(resId);
	}

	/**
	 * 返回列表页
	 */
	protected void back() {
		FragmentManager fm = getFragmentManager();
		fm.popBackStack();
	}
	public void toOutFm(Fragment fragment) {
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();// 注意。一个transaction
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		// 只能commit一次，所以不要定义成全局变量
		ft.add(R.id.main_content, fragment, fragment.getClass().getSimpleName());
		ft.addToBackStack(null);
		ft.commit();
	}
}
