package com.bankicp.app.ui;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bankicp.app.AppManager;
import com.bankicp.app.Constants;
import com.bankicp.app.MyApp;
import com.bankicp.app.R;
import com.bankicp.app.db.DataHelper;
import com.bankicp.app.model.TaskInfo;
import com.bankicp.app.model.UserInfo;
import com.bankicp.app.utils.ThreadManager;
import com.bankicp.app.utils.ToastUtils;
import com.bankicp.app.utils.UIUtils;
import com.bankicp.app.utils.Util;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/***
 * 巡检
 *
 * @author yzw
 */
public class MainFragmentActivity extends FragmentActivity {


    private ImageView home_icon;
    private ImageView patrol_icon;
    private ImageView inspection_icon;

    private TextView home_tx;
    private TextView patrol_tx;
    private TextView inspection_tx;

    private ImageView top_user_icon;
    private TextView top_tab_name;
    private TextView top_tab_room_name;
    private TextView top_date_text;
    private TextView top_user_org;

    public TextView left_new_count;
    public TextView left_finished_count;

    public TextView left_feedback_count;

    private Button left_feedback_btn;
    // 主页tab切换的集合
    private List<Class> clazzList = new ArrayList<>();
    private Fragment currentFragment;
    private int currentIndex;

    private MyApp app;

    private UserInfo user;

    private DataHelper dataHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getInstance().addActivity(this);
        setContentView(R.layout.main_tab_activity_land);

        initValue();
        initView();
        initDefaultValue();
        findTabView();
        initFragment(currentIndex, null);

        try {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("updateCount");
            registerReceiver(broadcastReceiver, intentFilter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        getPermission();
    }


    private void initView() {
        top_tab_name = $(R.id.top_tab_name);
        top_tab_room_name = $(R.id.top_tab_room_name);
        top_user_icon = $(R.id.top_user_icon);
        top_date_text = $(R.id.top_date_text);
        top_user_org = $(R.id.top_user_org);

        left_new_count = $(R.id.left_new_count);
        left_finished_count = $(R.id.left_finished_count);
        left_feedback_count = $(R.id.left_feedback_count);
        left_feedback_btn = $(R.id.left_feedback_btn);
        top_user_icon.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                toOutFm(new DetailsFragment());

            }
        });
        left_feedback_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toOutFm(new ReportFragment());
            }
        });
    }


    private void initDefaultValue() {
        setTabTitle(getResources().getString(R.string.tab_home), "");
        setUserInfo();
    }

    /**
     * 设置用户头像用户信息
     */
    private void setUserInfo() {
        if (user.getUserIcon() != null) {
            try {
                String url = "";
                if (!user.getUserIcon().contains("http://")) {
                    url = "http://" + user.getUserIcon();
                } else {
                    url = user.getUserIcon();
                }
                ImageLoader.getInstance().displayImage(url, top_user_icon);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        top_date_text.setText(Util.getNowDateTime() + " " + Util.getWeek());
        top_user_org.setText(user.getCompanyname() + "-" + user.getDepartmentname() + "-" + user.getUserName());
    }

    public void setTabTitle(String title, String roomName) {
        top_tab_name.setText(title);
        top_tab_room_name.setTag(roomName);
    }

    private void updateCount() {
        ThreadManager.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                ArrayList<TaskInfo> list = dataHelper.getAllTask(null, null);
                int new_count = 0, finished_count = 0, feedback_count = 0;

                for (TaskInfo taskInfo : list) {
                    if (taskInfo.getTaskState() == 2 && !taskInfo.isException()) {
                        finished_count++;
                    }
                    if (taskInfo.getTaskState() == 2 && taskInfo.isException()) {
                        feedback_count++;
                    }
                    if (taskInfo.getTaskState() < 2) {
                        new_count++;
                    }
                }
                final int finalFinished_count = finished_count;
                final int finalFeedback_count = feedback_count;
                final int finalNew_count = new_count;
                UIUtils.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        left_finished_count.setText(String.valueOf(finalFinished_count));
                        left_feedback_count.setText(String.valueOf(finalFeedback_count));
                        left_new_count.setText(String.valueOf(finalNew_count));
                    }
                });

            }
        });

    }

    /***
     * 初始化变量
     */
    private void initValue() {
        app = (MyApp) getApplicationContext();
        user = app.getUserInfo();
        dataHelper = new DataHelper(this);
        currentIndex = getIntent().getIntExtra(Constants.Extra.FRAGMENT_INDEX, 0);
        // 添加Fragment
        clazzList.add(HomeFragment.class);
        clazzList.add(PatrolFragment.class);
        clazzList.add(InspectionFragment.class);

    }

    /**
     * 获得底部工具栏控件
     */
    private void findTabView() {

        home_icon = $(R.id.home_icon);
        patrol_icon = $(R.id.patrol_icon);
        inspection_icon = $(R.id.inspection_icon);

        home_tx = $(R.id.home_tx);
        patrol_tx = $(R.id.patrol_tx);
        inspection_tx = $(R.id.inspection_tx);

    }

    private void setContentCheck(ImageView icon, TextView tx, int rId) {
        try {
            icon.setImageResource(rId);
            tx.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.list_item_map_text));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Tab点击监听
    public void onMainTab(View v) {

        switch (v.getId()) {
            case R.id.main_tab_home:
                initFragment(HomeFragment.INDEX, null);
                break;
            case R.id.main_tab_patrol:
                initFragment(PatrolFragment.INDEX, null);
                break;
            case R.id.main_tab_inspection:
                initFragment(InspectionFragment.INDEX, null);
                break;
            default:
                break;
        }
    }

    private void initCurrentTab() {
        initTab();
        try {
            switch (currentIndex) {
                case 0:
                    setTabTitle(getResources().getString(R.string.tab_home), "");
                    setContentCheck(home_icon, home_tx, R.mipmap.home_btn_p);
                    break;
                case 1:
                    setTabTitle(getResources().getString(R.string.patrol), "");
                    setContentCheck(patrol_icon, patrol_tx, R.mipmap.patrol_btn_p);
                    break;
                case 2:
                    setTabTitle(getResources().getString(R.string.inspection), "");
                    setContentCheck(inspection_icon, inspection_tx, R.mipmap.inspection_btn_p);
                    break;
                default:
                    setContentCheck(home_icon, home_tx, R.mipmap.home_btn_p);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initTab() {
        home_icon.setImageResource(R.mipmap.home_btn_n);

        patrol_icon.setImageResource(R.mipmap.patrol_btn_n);
        inspection_icon.setImageResource(R.mipmap.inspection_btn_n);
        home_tx.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.activity_title_text));
        patrol_tx.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.activity_title_text));
        inspection_tx.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.activity_title_text));

    }

    /***
     * 初始化Fragment
     *
     * @param frIndex
     */
    private void initFragment(int frIndex, Bundle bundle) {

        int size = clazzList.size();
        if (frIndex > size || frIndex < 0) {
            frIndex = 0;
        }
        this.currentIndex = frIndex;
        initCurrentTab();
        FragmentManager fm = getSupportFragmentManager();
        Class clazz = clazzList.get(frIndex);
        Fragment fragment = fm.findFragmentByTag(clazz.getSimpleName());

        FragmentTransaction ft = fm.beginTransaction();
        if (bundle != null && fragment != null) {
            ft.remove(fragment);
            fragment = null;
        }
        if (fragment == null) {
            try {
                fragment = (Fragment) clazz.newInstance();
                if (bundle != null) {
                    fragment.setArguments(bundle);
                }
                ft.add(R.id.content, fragment, fragment.getClass().getSimpleName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        ft.commit();
        currentFragment = fragment;

        List<Fragment> fragments = fm.getFragments();
        if (fragments != null) {
            FragmentTransaction ft2 = fm.beginTransaction();
            for (Fragment f : fragments) {
                if (f == null) {
                    continue;
                }
                if (currentFragment == f) {
                    f.onResume();
                    ft2.show(f);
                } else {
                    f.onPause();
                    ft2.hide(f);
                }
            }
            ft2.commit();
        }
    }

    public void toOutFm(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();// 注意。一个transaction
        // 只能commit一次，所以不要定义成全局变量
        ft.add(R.id.main_content, fragment, fragment.getClass().getSimpleName());
        ft.addToBackStack(null);
        ft.commit();
    }

    public void toTabFm(int frIndex, Bundle bundle) {
        initFragment(frIndex, bundle);
    }

    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {

            if (backFragment()) {
                return true;
            }
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                ToastUtils.showToast(getApplicationContext(), "再按一次退出应用", Toast.LENGTH_SHORT);
                exitTime = System.currentTimeMillis();
            } else {
                AppManager.getInstance().AppExit(this);
            }
            return true;
        } else {
            return false;
        }

    }

    private boolean backFragment() {
        DetailsFragment detailsFm = (DetailsFragment) getSupportFragmentManager()
                .findFragmentByTag(DetailsFragment.class.getSimpleName());
        if (detailsFm != null) {
            detailsFm.back();
            return true;
        }
        ReportFragment reportFragment = (ReportFragment) getSupportFragmentManager()
                .findFragmentByTag(ReportFragment.class.getSimpleName());
        if (reportFragment != null) {
            reportFragment.back();
            return true;
        }
        InspectionFragment inspectionFragment = (InspectionFragment) getSupportFragmentManager()
                .findFragmentByTag(InspectionFragment.class.getSimpleName());
        if (inspectionFragment != null) {
            inspectionFragment.back();
            return true;
        }

        PatrolFragment patrolFragment = (PatrolFragment) getSupportFragmentManager()
                .findFragmentByTag(PatrolFragment.class.getSimpleName());
        if (patrolFragment != null) {
            patrolFragment.back();
            return true;
        }
        WorkLogFragment workLogFragment = (WorkLogFragment) getSupportFragmentManager()
                .findFragmentByTag(WorkLogFragment.class.getSimpleName());
        if (workLogFragment != null) {
            workLogFragment.back();
            return true;
        }
        CreateReportFragment createReportFragment = (CreateReportFragment) getSupportFragmentManager()
                .findFragmentByTag(CreateReportFragment.class.getSimpleName());
        if (createReportFragment != null) {
            createReportFragment.back();
            return true;
        }
        LogFragment logFragment = (LogFragment) getSupportFragmentManager()
                .findFragmentByTag(LogFragment.class.getSimpleName());
        if (logFragment != null) {
            logFragment.back();
            return true;
        }
        return false;
    }

    public String getTabTitle() {
        return top_tab_name.getText().toString();
    }


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateCount();
        }
    };

    @Override
    protected void onDestroy() {
        if (dataHelper != null) {
            dataHelper.Close();
        }
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }
        super.onDestroy();
    }

    @SuppressWarnings("unchecked")
    protected <T extends View> T $(int resId) {
        return (T) super.findViewById(resId);
    }

    private static final int REQUEST_CODE = 1;

    private void getPermission() {
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(MainFragmentActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(MainFragmentActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(MainFragmentActivity.this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.CAMERA);
        }
        if (ContextCompat.checkSelfPermission(MainFragmentActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MainFragmentActivity.this, permissions, REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(MainFragmentActivity.this, "必须同意所有权限才可以使用app", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                } else {
                    Toast.makeText(MainFragmentActivity.this, "必须同意所有权限才可以使用app", Toast.LENGTH_SHORT).show();
                }
                break;

            default:
        }

    }
}
