package com.bankicp.app.ui;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bankicp.app.Constants;
import com.bankicp.app.R;
import com.bankicp.app.adapter.MyExpandableListAdapter;
import com.bankicp.app.db.DataHelper;
import com.bankicp.app.model.Result;
import com.bankicp.app.model.RoomInfo;
import com.bankicp.app.model.TaskInfo;
import com.bankicp.app.model.TimeGroupInfo;
import com.bankicp.app.model.URLs;
import com.bankicp.app.model.UserInfo;
import com.bankicp.app.utils.HttpRequest;
import com.bankicp.app.utils.HttpService;
import com.bankicp.app.utils.HttpService.ProgressCallback;
import com.bankicp.app.utils.JsonUtil;
import com.bankicp.app.utils.ThreadManager;
import com.bankicp.app.utils.ToastUtils;
import com.bankicp.app.utils.UIUtils;
import com.bankicp.app.utils.Util;
import com.zbar.lib.CaptureActivity;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/***
 * 主页
 * 1. 数据获取-- 时间段列表数据 --时间段内任务数据获取
 * 2. 提交数据 -- 提交任务
 */
public class HomeFragment extends BaseFragment {

    public static Map<Integer, Map> mMap = new HashMap<>();

    public static final int TIME_GROUP_TASK = 1;
    public static final int ROOM_LIST_TASK = 2;
    public static final int INDEX = 0;
    private List<RoomInfo> currentRoomList = new ArrayList<>();
    /***
     * 当前选择的时间段项
     */
    private int currentGroupPosition = -1;

    /***
     * 主页扫描按钮
     */
    private Button home_left_btn_scan;// 扫描按钮
    /**
     * 主页提交任务按钮
     */
    private Button home_left_btn_update;
    /***
     * 跳转至反馈页面的按钮
     */
    private Button home_left_btn_feekback;
    /***
     * 跳转至工作日志页面的按钮
     */
    private Button home_left_btn_log;
    /***
     * 显示选中的当前机房id
     */
    private TextView room_id;
    /***
     * 显示选中的当前机房名称
     */
    private TextView room_name;
    /***
     * 显示选中的当前机房类型
     */
    private TextView room_type;
    // 显示当前主页所有任务状态基本数据
    private TextView home_left_iv_info;
    private TextView home_left_iv_address;
    private TextView home_left_iv_type;
    private TextView home_left_iv_total;
    private TextView home_start_time;
    private TextView home_end_time;
    private TextView time_list_size_val;
    private TextView room_list_size_val;
    private Button btn_load;

    private UserInfo user;
    private DataHelper dataHelper;
    public boolean isRefresh = false;


    /***
     * 适配器
     */
    private MyExpandableListAdapter adapter;

    private ExpandableListView listView;
    //右侧圆形进度条
    private ProgressBar progressBar;

    /**
     * 列表数据
     */
    public static ArrayList<TimeGroupInfo> groupList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_home);
        user = app.getUserInfo();
        groupList = new ArrayList<>();
        dataHelper = new DataHelper(mContext);
        for (int i = 0; i < 7; i++) {
            HashMap map = new HashMap();
            mMap.put(i, map);
        }
    }

    @Override
    protected void initViews() {

        room_id = $(R.id.room_id);
        room_name = $(R.id.room_name);
        room_type = $(R.id.room_type);
        home_left_iv_info = $(R.id.info);
        home_left_iv_address = $(R.id.address);
        home_left_iv_type = $(R.id.type);
        home_left_iv_total = $(R.id.total);
        home_start_time = $(R.id.start_time);
        home_end_time = $(R.id.end_time);
        home_left_btn_scan = $(R.id.home_left_btn_scan);
        home_left_btn_log = $(R.id.home_left_btn_log);
        home_left_btn_update = $(R.id.home_left_btn_update);
        home_left_btn_feekback = $(R.id.home_left_btn_feekback);
        time_list_size_val = $(R.id.time_list_size_val);
        room_list_size_val = $(R.id.room_list_size_val);
        btn_load = $(R.id.btn_load);
        listView = $(R.id.eListView);
        progressBar = $(R.id.pb1);

        listView.setOnGroupClickListener(onGroupClickListener);
        listView.setOnChildClickListener(childClickListener);

        listView.setEmptyView(new TextView(mContext));
        adapter = new MyExpandableListAdapter(mContext, groupList);
        adapter.setSignBtnListener(new MyExpandableListAdapter.SignBtnListener() {

            @Override
            public void onClick(TimeGroupInfo groupInfo) {

                ArrayList<Long> ids = getTaskIds(groupInfo);
                if (ids.size() > 1) {
                    Long[] params = new Long[ids.size()];
                    for (int i = 0; i < ids.size(); i++) {
                        params[i] = ids.get(i);
                    }
                    new CheckTask().execute(params);
                } else {
                    ToastUtils.showToast(mContext, "无可领取项", 0);
                }
            }

        });

        listView.setAdapter(adapter);

        home_left_btn_feekback.setOnClickListener(listener);
        home_left_btn_scan.setOnClickListener(listener);
        home_left_btn_update.setOnClickListener(listener);
        home_left_btn_log.setOnClickListener(listener);
        btn_load.setOnClickListener(listener);
    }

    /**
     * 点击事件
     */
    private OnClickListener listener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.home_left_btn_scan://扫描
                    Intent intent = new Intent(mContext, CaptureActivity.class);
                    startActivityForResult(intent, Constants.REQUEST_CODE_SCAN);
                    break;
                case R.id.home_left_btn_log://换班
                    ((MainFragmentActivity) mContext).toOutFm(new WorkLogFragment());
                    break;
                case R.id.home_left_btn_update://同步
                    new PostDataTask(mContext, 1).execute();
                    break;
                case R.id.home_left_btn_feekback://上报
                    new PostDataTask(mContext, 2).execute();
                    break;
                case R.id.btn_load://重新加载
                    isRefresh = true;
                    getTimeGroup();
                    break;
                default:
                    break;
            }
        }
    };

    protected boolean isExist(TaskInfo taskInfo) {

        for (TimeGroupInfo groupInfo : groupList) {
            for (RoomInfo machineAreaInfo : groupInfo.getRoomList()) {
                for (TaskInfo info : machineAreaInfo.getTaskList()) {
                    if (info.getId() == taskInfo.getId()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /***
     * 获取任务未领取的的id 集合
     *
     * @param groupInfo
     * @return
     */
    private ArrayList<Long> getTaskIds(TimeGroupInfo groupInfo) {
        ArrayList<Long> ids = new ArrayList<>();
        int size = groupInfo.getRoomList().size();
        for (int i = 0; i < size; i++) {
            RoomInfo areaInfo = groupInfo.getRoomList().get(i);
            for (TaskInfo task : areaInfo.getTaskList()) {
                if (task.getTaskState() == 0) {
                    ids.add(task.getId());
                }
            }
        }
        return ids;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getTimeGroup();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constants.REQUEST_CODE_SCAN:
                if (data != null) {
                    Bundle extras = data.getExtras();
                    if (null != extras) {
                        String result = extras.getString("result");
                        initRoom(result);
                    }
                }
                break;
            default:
                break;
        }
    }

    /***
     * @param result
     */
    private void initRoom(String result) {
        long id = Long.parseLong(result);
        RoomInfo info = null;
        for (RoomInfo item : currentRoomList) {
            if (item.getId() == id) {
                info = item;
                break;
            }
        }
        if (info == null) {
            ToastUtils.showToast(mContext, "没有适配项", 0);
            return;
        }
        showRoomInfo(info);
    }

    /**
     * 显示机房信息
     *
     * @param info
     */
    private void showRoomInfo(RoomInfo info) {
        saveRoom(info.getId());
        room_id.setText(String.valueOf("ID:" + info.getId()));
        room_name.setText(String.valueOf("机房：" + info.getName()));
        room_type.setText(String.valueOf("编号：" + info.getNo()));

        Bundle bundle = new Bundle();
        bundle.putSerializable("group", info);
        if (info.getTaskList().size() > 0 && "巡检".equals(info.getTaskList().get(0).getTaskType())) {
            ((MainFragmentActivity) mContext).toTabFm(InspectionFragment.INDEX, bundle);
        } else {
            ((MainFragmentActivity) mContext).toTabFm(PatrolFragment.INDEX, bundle);
        }

    }

    /***
     * 提交任务信息
     */
    private class PostDataTask extends AsyncTask<String, Integer, String> {
        private Context context;
        private ArrayList<TaskInfo> tasks;
        private boolean showProgressDialog = true;
        private int type = 0;

        public PostDataTask(Context context, int type) {
            this.context = context;
            this.type = type;
        }

        @Override
        protected void onPreExecute() {
            if (showProgressDialog) {
                try {
                    progressBar.setVisibility(View.VISIBLE);
                    //                    showLoading();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (showProgressDialog) {
                progressBar.setVisibility(View.GONE);
                //                dismissLoading();
            }
            if (!TextUtils.isEmpty(result)) {

                ToastUtils.showToast(mContext, result, 0);
                getActivity().sendBroadcast(new Intent("updateCount"));
            } else {
                ToastUtils.showToast(mContext, "提交失败", 0);
            }
            super.onPostExecute(result);
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {

            if (showProgressDialog) {
                if (progress_text != null) {
                    progress_text.setText(String.valueOf(progress[0] + "%"));
                }
            }
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                tasks = new ArrayList<>();
                ArrayList<TaskInfo> taskInfos = dataHelper.getAllTask(null, null);
                for (TaskInfo taskInfo : taskInfos) {
                    if (type == 1 && taskInfo.getTaskState() == 2 && !taskInfo.isProblem() && isExist(taskInfo)) {
                        tasks.add(taskInfo);
                    }
                    if (type == 2 && taskInfo.getTaskState() == 2 && taskInfo.isProblem() && isExist(taskInfo)) {
                        tasks.add(taskInfo);
                    }
                }
                if (tasks.size() == 0) {
                    return "无可提交项";
                }
                int sum = 0;
                for (TaskInfo taskInfo : tasks) {
                    Map<String, String> map = new HashMap<>();
                    map.put("taskJson", JsonUtil.objectToJson(taskInfo));
                    map.put("userId", user.getUserId());
                    Map<String, String> files = new HashMap<>();

                    for (int i = 0; i < taskInfo.getImageList().size(); i++) {
                        if (!TextUtils.isEmpty(Util.getNotNullString(taskInfo.getImageList().get(i)))) {
                            files.put(i + "", taskInfo.getImageList().get(i));
                        }
                    }
                    String res = HttpService.doPost(URLs.URL_SAVE_TASK, map, files, new ProgressCallback() {

                        @Override
                        public void progressUpdate(Integer... progress) {
                            publishProgress(progress);
                        }
                    });
                    Result result = Util.processResult(res);
                    if (result.getCode() == 1) {
                        taskInfo.setTaskState(3);
                        dataHelper.saveTask(taskInfo);
                        sum++;
                    }
                }
                if (sum == tasks.size()) {
                    return "提交成功";
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return "提交失败";
        }
    }

    private Dialog dialog;
    private TextView progress_text;

    //    private void showLoading() {
    //        try {
    //            dialog = AlertUtils.showLoading(mContext);
    //            progress_text = (TextView) dialog.findViewById(R.id.progress_text);
    //            dialog.show();
    //        } catch (Exception e) {
    //            e.printStackTrace();
    //        }
    //    }
    //
    //    private void dismissLoading() {
    //
    //        if (dialog != null) {
    //            dialog.dismiss();
    //            dialog = null;
    //        }
    //    }

    /***
     * 获取时间段列表数据
     */

    private void getTimeGroup() {
        progressBar.setVisibility(View.VISIBLE);

        //        new GetTimeGroupTask().start();
        ThreadManager.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                List<TimeGroupInfo> list = getTimeGroupList();

                //                //                    dismissLoading();
                //
                //                groupList.clear();
                //                if (msg.obj != null) {
                //                    try {
                //                        ArrayList<TimeGroupInfo> list = (ArrayList<TimeGroupInfo>) msg.obj;
                //                        if (list.size() > 0) {
                //                            groupList.addAll(list);
                //                        } else {
                //                            ToastUtils.showToast(mContext, "数据为空", 0);
                //                        }
                //                    } catch (Exception e) {
                //                        e.printStackTrace();
                //                    }
                //                } else {
                //                    ToastUtils.showToast(mContext, "数据为空", 0);
                //                }
                //
                //                adapter.notifyDataSetChanged();
                //                initCount();
                groupList.clear();
                if (list.size() > 0) {
                    groupList.addAll(list);
                } else {
                    UIUtils.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.showToast(mContext, "数据为空", 0);
                        }
                    });
                }
                UIUtils.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                    }
                });
                initCount();
            }
        });
    }

    //    private class GetTimeGroupTask extends Thread {
    //
    //        @Override
    //        public void run() {
    //            List<TimeGroupInfo> list = getTimeGroupList();
    //            Message msg = new Message();
    //            msg.obj = list;
    //            msg.what = TIME_GROUP_TASK;
    //            mHandler.sendMessage(msg);
    //        }
    //    }

    /***
     * 获取主页单一时间段的机房列表数据
     */

    private void getRoomData(final Long taskTimeId) {
        progressBar.setVisibility(View.VISIBLE);
        //        new GetRoomTask(taskTimeId).start();
        ThreadManager.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                //                GetRoomTask(taskTimeId);
                getRoomList(taskTimeId);
            }
        });

        //        showLoading();
    }

    //    private class GetRoomTask extends Thread {
    //        Long taskTimeId;
    //
    //        public GetRoomTask(Long taskTimeId) {
    //            this.taskTimeId = taskTimeId;
    //        }
    //
    //        @Override
    //        public void run() {
    //            getRoomList(taskTimeId);
    //        }
    //
    //    }
    //    private void GetRoomTask(Long taskTimeId){
    //        getRoomList(taskTimeId);
    //    }

    /**
     * 获取子项数据
     *
     * @param taskTimeId
     * @return
     */
    private List<RoomInfo> getRoomList(final Long taskTimeId) {
        final List<RoomInfo> list = new ArrayList<>();
        ThreadManager.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Map<String, String> map = new HashMap<>();
                    map.put("taskTimeId", String.valueOf(taskTimeId));
                    map.put("userId", user.getUserId());

                    Result res = Util.processResult(HttpRequest.sendPostRequest(URLs.URL_GET_HOME_LIST, map));
                    if (res != null) {
                        list.addAll(processRoomData(res.getData().toString()));

                        if (list.size() > 0) {
                            groupList.get(currentGroupPosition).getRoomList().clear();
                            groupList.get(currentGroupPosition).getRoomList().addAll(list);
                            initCount();
                            loadHomeTaskInfo(groupList.get(currentGroupPosition));
                            getActivity().sendBroadcast(new Intent("updateCount"));

                        } else {
                            UIUtils.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setVisibility(View.GONE);
                                    ToastUtils.showToast(mContext, "数据为空", 0);
                                }
                            });

                        }
                        UIUtils.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                                progressBar.setVisibility(View.GONE);
                            }
                        });

                    }
                    final List<RoomInfo> list2 = new ArrayList<>();
                    list2.addAll(list);
                    //            new Thread() {
                    //                @Override
                    //                public void run() {
                    for (RoomInfo info : list2) {
                        dataHelper.deleteByRoomId(info.getId() + "");
                        dataHelper.saveTaskList(info.getTaskList());
                    }
                    //                }
                    //            }.start();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return list;
    }

    private List<TimeGroupInfo> processGroupData(String json) {
        if (TextUtils.isEmpty(json))
            return Collections.EMPTY_LIST;
        try {
            JSONArray tasks = new JSONArray(json);
            if (tasks == null || tasks.length() == 0) {
                return Collections.EMPTY_LIST;
            }
            List<TimeGroupInfo> groups = JsonUtil.jsonToList(json, TimeGroupInfo.class);
            return groups;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.EMPTY_LIST;
    }

    private List<RoomInfo> processRoomData(String json) {
        if (TextUtils.isEmpty(json))
            return Collections.EMPTY_LIST;
        try {
            JSONArray tasks = new JSONArray(json);
            if (tasks == null || tasks.length() == 0) {
                return Collections.EMPTY_LIST;
            }
            List<RoomInfo> groups = JsonUtil.jsonToList(json, RoomInfo.class);
            return groups;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.EMPTY_LIST;
    }

    /**
     * 统计时间段与机房数量
     */
    private void initCount() {
        final int groupList_size = groupList.size();
        int roomList_size = 0;
        for (TimeGroupInfo group : groupList) {
            if (group.getRoomList() != null)
                roomList_size += group.getRoomList().size();
        }
        final int finalRoomList_size = roomList_size;
        UIUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                time_list_size_val.setText(String.valueOf(groupList_size + "个"));
                room_list_size_val.setText(String.valueOf(finalRoomList_size + "个"));
            }
        });

    }

    private void loadHomeTaskInfo(final TimeGroupInfo group) {
        progressBar.setVisibility(View.VISIBLE);
        ThreadManager.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                currentRoomList.clear();
                if (group == null || group.getRoomList() == null) {
                    return;
                }
                currentRoomList.addAll(group.getRoomList());
                //        if (currentRoomList == null)
                //            return;

                final int areaCount = currentRoomList.size();
                String type = "";
                String time = "";

                int taskCount = 0;
                Set<String> types = new HashSet<>();
                String startTime = "", endTime = "";

                if (areaCount == 0) {
                    return;
                }

                for (RoomInfo areaInfo : currentRoomList) {
                    if (areaInfo.getTaskList() != null && areaInfo.getTaskList().size() > 0) {
                        startTime = areaInfo.getTaskList().get(0).getTaskTime();
                        endTime = areaInfo.getTaskList().get(0).getTaskEndTime();
                        break;
                    }
                }

                for (RoomInfo areaInfo : currentRoomList) {
                    if (TextUtils.isEmpty(time)) {
                        time = areaInfo.getCreateDate();
                    }
                    if (areaInfo.getTaskList() != null && areaInfo.getTaskList().size() > 0) {
                        taskCount += areaInfo.getTaskList().size();

                        for (TaskInfo task : areaInfo.getTaskList()) {
                            try {
                                types.add(task.getTaskType());
                                if (Util.toDate(task.getTaskTime()).before(Util.toDate(startTime))) {
                                    startTime = task.getTaskTime();
                                }
                                if (Util.toDate(task.getTaskEndTime()).before(Util.toDate(endTime))) {
                                    endTime = task.getTaskEndTime();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                }
                for (String string : types) {
                    type += string + " ";
                }
                final String finalEndTime = endTime;
                final String finalType = type;
                final int finalTaskCount = taskCount;
                final String address = user.getDepartmentname() + " " + user.getChildDepartmentname();
                UIUtils.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        home_start_time.setText("时间段：" + group.getStartTime() + "~" + group.getEndTime());
                        home_end_time.setText("结束时间：" + finalEndTime);
                        home_left_iv_address.setText("地址：" + address);
                        home_left_iv_type.setText("类型：" + finalType);
                        home_left_iv_info.setText("机房：" + areaCount + "个");
                        home_left_iv_total.setText("巡检：" + finalTaskCount + "个");
                        adapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                        getActivity().sendBroadcast(new Intent("updateCount"));
                    }
                });
            }
        });
    }

    @Override
    public void onDestroy() {
        if (dataHelper != null) {
            dataHelper.Close();
        }
        super.onDestroy();
    }

    private void saveRoom(long roomId) {
        app.setRoomId(roomId);
    }

    /**
     * 二级列表父项点击事件
     *
     * @param position
     */

    private ExpandableListView.OnGroupClickListener onGroupClickListener = new ExpandableListView.OnGroupClickListener() {
        @Override
        public boolean onGroupClick(ExpandableListView expandableListView, View view, int position, long l) {

            if (currentGroupPosition == -1) {
                expandableListView.expandGroup(position);
                currentGroupPosition = position;
                expandableListView.setSelectedGroup(position);
            } else {// 至少有一个打开
                if (currentGroupPosition == position) {//点击是自己
                    // 关闭自己
                    expandableListView.collapseGroup(currentGroupPosition);
                    // 重置mCurrentPosistion
                    currentGroupPosition = -1;
                    progressBar.setVisibility(View.GONE);
                    return true;
                } else {//点击不是自己
                    expandableListView.collapseGroup(currentGroupPosition);
                    // 打开groupPosition
                    expandableListView.expandGroup(position);
                    // 更新mCurrentPosistion
                    currentGroupPosition = position;
                    // 选中
                    expandableListView.setSelectedGroup(position);
                }
            }

            if (groupList.get(position).getRoomList().size() == 0) {
                //                        || groupList.get(position).getRoomList().size() == 0)) {
                //子项没数据走这里
                //                ToastUtils.showToast(mContext, "还没有数据", 0);
                getRoomData(groupList.get(position).getId());
                //                Log.i("tag","还没有数据");

            } else if (groupList.get(position).getRoomList().size() > 0) {
                //子项有数据走这里
                loadHomeTaskInfo(groupList.get(position));
                //                ToastUtils.showToast(mContext, "这里有数据了", 0);
                //                Log.i("tag","有数据");
            }

            //            for (int i = 0, count = expandableListView.getCount(); i < count; i++) {
            //                if (i == position)
            //                    continue;
            //                expandableListView.collapseGroup(i);
            //            }


            //                if (groupList.get(position) != null && (groupList.get(position).getRoomList() == null
            //                        || groupList.get(position).getRoomList().size() == 0)) {
            //
            //                    getRoomData(groupList.get(position).getId());
            //
            //                } else {
            //                    loadHomeTaskInfo(groupList.get(position));
            //                }

            return true;
        }
    };

    /**
     * 二级列表子项点击事件
     */
    private ExpandableListView.OnChildClickListener childClickListener = new ExpandableListView.OnChildClickListener() {
        @Override
        public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long l) {
            showRoomInfo(adapter.getChild(groupPosition, childPosition));
            return false;
        }
    };

    private List<TimeGroupInfo> getTimeGroupList() {
        List<TimeGroupInfo> list = new ArrayList<>();
        try {
            List<TimeGroupInfo> li = processGroupData(Util.getCacheJson(mContext, URLs.URL_GET_TIME_LIST + "_" + user.getUserId()));
            if (!isRefresh && li != null && li.size() > 0) {
                list.addAll(li);
                return list;
            }
            Map<String, String> map = new HashMap<>();
            map.put("userId", user.getUserId());
            Result res = Util.processResult(HttpRequest.sendPostRequest(URLs.URL_GET_TIME_LIST, map));
            if (res != null) {
                list.addAll(processGroupData(res.getData().toString()));
            }
            dataHelper.delAllTask();
            Util.weiteCache(mContext, URLs.URL_GET_TIME_LIST + "_" + user.getUserId(), list);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private class CheckTask extends AsyncTask<Long, Void, Integer> {

        private ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(mContext);
            pd.setCanceledOnTouchOutside(false);
            pd.setMessage("正在加载...");
            pd.show();
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (pd != null) {
                pd.dismiss();
                pd = null;
            }
            if (result == 1) {
                ToastUtils.showToast(mContext, "认领成功", 0);
            } else {
                ToastUtils.showToast(mContext, "认领失败", 0);
            }
            super.onPostExecute(result);
        }

        @Override
        protected Integer doInBackground(Long... ids) {
            Integer isOK = 0;
            try {
                Map<String, String> map = new HashMap<>();
                StringBuilder sb = new StringBuilder();
                for (Long long1 : ids) {
                    sb.append(long1).append(",");
                }
                if (sb.length() > 1) {
                    map.put("ids", sb.substring(0, sb.length() - 1));
                    map.put("userId", user.getUserId() + "");
                    Result res = Util.processResult(HttpRequest.sendPostRequest(URLs.URL_SIGN_TASK, map));
                    if (res != null) {
                        isOK = res.getCode();
                        if (res.getCode() == 1) {
                            TimeGroupInfo g = groupList.get(currentGroupPosition);
                            List<TaskInfo> taskInfos = new ArrayList<>();
                            for (int i = 0; i < g.getRoomList().size(); i++) {
                                RoomInfo m = g.getRoomList().get(i);
                                for (TaskInfo task : m.getTaskList()) {
                                    task.setTaskState(1);
                                    taskInfos.add(task);
                                }
                            }
                            dataHelper.saveTaskList(taskInfos);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return isOK;
        }
    }
}
