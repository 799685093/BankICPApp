package com.bankicp.app.ui;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.bankicp.app.Constants;
import com.bankicp.app.R;
import com.bankicp.app.adapter.CreateTaskGridViewAdapter;
import com.bankicp.app.adapter.MachineGridViewAdapter;
import com.bankicp.app.adapter.MachineItemAdapter;
import com.bankicp.app.adapter.TaskListAdapter;
import com.bankicp.app.db.DataHelper;
import com.bankicp.app.model.FileInfo;
import com.bankicp.app.model.ItemFiled;
import com.bankicp.app.model.MachineBoxInfo;
import com.bankicp.app.model.MachineInfo;
import com.bankicp.app.model.MachineItemInfo;
import com.bankicp.app.model.Result;
import com.bankicp.app.model.RoomInfo;
import com.bankicp.app.model.TaskInfo;
import com.bankicp.app.model.TaskSort;
import com.bankicp.app.model.URLs;
import com.bankicp.app.model.UserInfo;
import com.bankicp.app.utils.AlertUtils;
import com.bankicp.app.utils.HttpRequest;
import com.bankicp.app.utils.MediaUtils;
import com.bankicp.app.utils.ToastUtils;
import com.bankicp.app.utils.Util;
import com.bankicp.app.view.MGridView;
import com.bankicp.app.view.MultiListView;
import com.bankicp.app.view.MultiListView.OnRefreshListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zbar.lib.CaptureActivity;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 巡查/巡检
 */
public class TaskBaseFragment extends BaseFragment {
    private MultiListView mPullRefreshListView;
    private TaskListAdapter adapter;
    private List<TaskInfo> dataList;
    private Button left_top_btn_scan;
    private Button left_top_btn_sign;

    // 当前页
    private int pageIndex = Constants.PAGEINDEX;
    private int pageSize = Constants.PAGESIZE;

    private DataHelper dataHelper;

    private UserInfo user;
    private String taskType;
    private long roomId;

    private GridView grid_view;
    private List<MachineInfo> machineInfos;
    private MachineGridViewAdapter gridAdapter;

    private ListView listView;
    private BaseAdapter machineItemInfoAdapter;
    private List<MachineItemInfo> machineItemInfos;
    private Button isExceptionBtn, isWarnBtn;
    private Button save, save_all, back, reset;
    // 当前选择任务
    protected TaskInfo currentTaskInfo;
    protected int currentIndex = -1;
    // 当前设备
    private MachineInfo currentMachineInfo;

    protected boolean isException;
    protected boolean isWarn;
    private ImageView left_top_iv_img;
    private String image_url;
    private EditText remarkEt;
    // 图片上传
    private MGridView grid_image;
    private List<FileInfo> imageList = new ArrayList<>();
    private CreateTaskGridViewAdapter imageGridViewAdapter;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 1) {
                grid_image.setAdapter(imageGridViewAdapter);
            }
        }
    };
    private int tempIndex;
    private boolean isOpen = false;
    private View right_list_content;
    private View right_top_content;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_patrol);
        taskType = ((MainFragmentActivity) getActivity()).getTabTitle();
        user = app.getUserInfo();
        roomId = app.getRoomId();
        dataHelper = new DataHelper(mContext);

        dataList = new ArrayList<>();
        machineInfos = new ArrayList<>();
        machineItemInfos = new ArrayList<>();

        if (bundle != null) {
            RoomInfo machineAreaInfo = (RoomInfo) bundle.get("group");
            if (machineAreaInfo != null) {
                roomId = machineAreaInfo.getId();

            }
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        new GetDataTask().execute();
    }

    private void initListView() {
        right_top_content = $(R.id.right_top_content);
        right_list_content = $(R.id.right_list_content);
        mPullRefreshListView = $(R.id.pull_refresh_list);//
        adapter = new TaskListAdapter(this, dataList);
        mPullRefreshListView.setAdapter(adapter);
        mPullRefreshListView.setOnRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh() {
                pageIndex = 1;
                new GetDataTask().execute();
            }
        });

        mPullRefreshListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int p, long id) {
                /**
                 * 客户演示需要。默认为true
                 * 正常情况下是要扫描二维码才可以显示添加图片区域
                 */
                isOpen = true;
                currentTaskInputView(p - 1);
            }

        });
    }

    // 初始化当前选择任务的视图
    private void currentTaskInputView(final int p) {

        if (dataList.get(p).getTaskState() == 0) {
            tempIndex = p;
            new CheckTask().execute(dataList.get(p).getId());
        } else {

            initCurrentTaskView(p);
        }
    }

    /***
     * 初始化当前任务视图信息
     *
     * @param p
     */
    private void initCurrentTaskView(final int p) {
        adapter.setSelectItem(p);
        adapter.notifyDataSetChanged();
        if (p == currentIndex)
            return;

        if (currentTaskInfo != null) {
            saveCurrentTask();
        }
        currentIndex = p;
        currentTaskInfo = dataList.get(p);
        if (!isOpen) {
            save_all.setEnabled(false);
            save.setEnabled(false);
            reset.setEnabled(false);
            isExceptionBtn.setEnabled(false);
            isWarnBtn.setEnabled(false);
            right_top_content.setVisibility(View.INVISIBLE);
            right_list_content.setVisibility(View.INVISIBLE);

        } else {
            save_all.setEnabled(true);
            save.setEnabled(true);
            reset.setEnabled(true);
            isExceptionBtn.setEnabled(true);
            isWarnBtn.setEnabled(true);
            right_top_content.setVisibility(View.VISIBLE);
            right_list_content.setVisibility(View.VISIBLE);
            // 刷新填报项视图
            updateInputItem();
            // 刷新图片列表
            updateImageGrid();
            // 更新按钮视图
            updateBtnText();

        }

    }

    /**
     * 更新按钮视图
     */

    private void updateBtnText() {
        isException = currentTaskInfo.isException();
        isWarn = currentTaskInfo.isWarn();
        if (isException) {
            isExceptionBtn.setText("异常");
        } else {
            isExceptionBtn.setText("正常");
        }
        if (isWarn) {
            isWarnBtn.setText("红灯");
        } else {
            isWarnBtn.setText("绿灯");
        }
        remarkEt.setText(currentTaskInfo.getProblemDescription());
    }

    /**
     * 保存当前任务
     */
    private void saveCurrentTask() {
        List<String> list = new ArrayList<>();
        for (FileInfo item : imageList) {
            list.add(item.getPath());
        }
        currentTaskInfo.setImageList(list);
        currentTaskInfo.setException(isException);
        currentTaskInfo.setWarn(isWarn);
        currentTaskInfo.setProblemDescription(remarkEt.getText().toString());
        dataList.set(currentIndex, currentTaskInfo);
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
                if (tempIndex == -1) {
                    adapter.notifyDataSetChanged();
                } else {
                    initCurrentTaskView(tempIndex);
                }


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
                StringBuilder sb = new StringBuilder();
                for (Long long1 : ids) {
                    sb.append(long1).append(",");
                }
                if (sb.length() > 1) {
                    Map<String, String> map = new HashMap<>();

                    map.put("ids", sb.substring(0, sb.length() - 1));
                    map.put("userId", user.getUserId() + "");
                    Result res = Util.processResult(HttpRequest.sendPostRequest(URLs.URL_SIGN_TASK, map));
                    if (res != null) {
                        isOK = res.getCode();
                        if (res.getCode() == 1) {

                            List<TaskInfo> taskInfos = new ArrayList<>();
                            for (int i = 0; i < dataList.size(); i++) {
                                TaskInfo m = dataList.get(i);
                                for (int j = 0; j < ids.length; j++) {
                                    if (m.getId() == ids[j]) {
                                        m.setTaskState(1);
                                        taskInfos.add(m);
                                    }
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

    private class GetDataTask extends AsyncTask<Void, Void, List> {
        @Override
        protected void onPostExecute(List result) {
            if (result != null && !result.isEmpty()) {
                if (pageIndex == 1) {
                    dataList.clear();
                }
                dataList.addAll(result);
            }

            adapter.notifyDataSetChanged();
            mPullRefreshListView.onRefreshComplete();

            super.onPostExecute(result);
        }

        @Override
        protected List doInBackground(Void... params) {

            List<TaskInfo> list = new ArrayList<>();

            try {
                List<TaskInfo> li = dataHelper.getAllTask(String.valueOf(roomId), taskType);

                if (li.size() > 0) {
                    list = TaskSort.getSingleTaskList(li);
                    for (TaskInfo task : list) {
                        if (TextUtils.isEmpty(task.getActualStartTime())) {

                            task.setActualStartTime(Util.getNowDateTime());
                        } else {
                            if ("0001-01-01 00:00:00".equals(task.getActualStartTime())) {
                                task.setActualStartTime(Util.getNowDateTime());
                            }
                        }
                    }
                    dataHelper.saveTaskList(list);
                    return list;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return list;
        }

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
                        updateGridView(result);
                    }
                }

                break;
            case Constants.PIC_REQUEST_CODE:// 拍照
                addImage();
                break;
            case Constants.IMAGE_REQUEST_CODE:// 相册
                addImage();
                break;

            default:
                break;
        }
    }

    // 扫描后直接跳转至
    private void updateGridView(String result) {
        long id = Long.parseLong(result);
        if (currentTaskInfo == null || currentTaskInfo.getCabinet() == null) {
            ToastUtils.showToast(mContext, "请先选择机柜", 0);
            return;
        }
        if (currentTaskInfo.getCabinet().getCabinetId() == id) {
            isOpen = true;
            currentTaskInputView(currentIndex);
        } else {
            ToastUtils.showToast(mContext, "与所选项不一致", 0);
        }
    }

    @Override
    protected void initViews() {
        initListView();
        initScanView();
        initGridView();
        initItemView();
        initBtnView();
        initImageView();
    }

    /***
     * 设备列表视图
     */
    private void initGridView() {

        grid_view = $(R.id.grid_view);
        gridAdapter = new MachineGridViewAdapter(mContext, machineInfos);
        grid_view.setAdapter(gridAdapter);
        grid_view.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View arg1, int p, long id) {
                selectItem(p);
            }

        });

    }

    /**
     * 扫描视图
     */
    private void initScanView() {
        left_top_btn_sign = $(R.id.left_top_btn_sign);
        left_top_btn_sign.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                ArrayList<Long> ids = new ArrayList<>();
                int len = 0;
                for (TaskInfo task : dataList) {
                    if (task.getTaskState() == 0) {
                        ids.add(task.getId());
                    }
                }
                if (ids.size() > 0) {
                    Long[] params = new Long[ids.size()];
                    for (int i = 0; i < ids.size(); i++) {
                        params[i] = ids.get(i);
                    }
                    tempIndex = -1;
                    new CheckTask().execute(params);
                } else {
                    Toast.makeText(mContext, "没有可领取的任务", Toast.LENGTH_SHORT).show();
                }

            }
        });
        left_top_btn_scan = $(R.id.left_top_btn_scan);
        left_top_btn_scan.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CaptureActivity.class);
                TaskBaseFragment.this.startActivityForResult(intent, Constants.REQUEST_CODE_SCAN);
            }
        });
    }

    /**
     * 填报项的视图
     */
    private void initItemView() {
        listView = $(R.id.list_view);//
        machineItemInfoAdapter = new MachineItemAdapter(this, machineItemInfos);
        listView.setAdapter(machineItemInfoAdapter);

    }

    /***
     * 初始化上传图片视图
     */
    private void initImageView() {
        grid_image = $(R.id.grid_image);
        imageGridViewAdapter = new CreateTaskGridViewAdapter(this, imageList);
        grid_image.setAdapter(imageGridViewAdapter);
    }

    /***
     * 底部视图
     */
    private void initBtnView() {

        left_top_iv_img = $(R.id.left_top_iv_img);
        image_url = "http://imgs0.soufunimg.com/news/2015_07/06/1436162486481.jpg";
        left_top_iv_img.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                List<FileInfo> infos = new ArrayList<FileInfo>();
                infos.add(new FileInfo(image_url));
                Intent intent = new Intent(mContext, GalleryFileActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("imageInfos", (Serializable) infos);
                bundle.putInt("position", 0);
                bundle.putInt("from", InspectionFragment.INDEX);
                intent.putExtras(bundle);
                TaskBaseFragment.this.startActivityForResult(intent, 3);
            }
        });
        try {
            ImageLoader.getInstance().displayImage(image_url, left_top_iv_img);
        } catch (Exception e) {
            e.printStackTrace();
        }
        remarkEt = $(R.id.remark);
        isWarnBtn = $(R.id.isWarn);
        isExceptionBtn = $(R.id.isException);
        isWarnBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                isWarn = !isWarn;
                if (isWarn) {
                    isWarnBtn.setText("红灯");
                } else {
                    isWarnBtn.setText("绿灯");
                }
            }
        });
        isExceptionBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                isException = !isException;
                if (isException) {
                    isExceptionBtn.setText("异常");
                } else {
                    isExceptionBtn.setText("正常");
                }
            }
        });
        save = $(R.id.save);
        save_all = $(R.id.save_all);
        reset = $(R.id.reset);
        back = $(R.id.back);

        if ("巡检".equals(taskType)) {
            save_all.setVisibility(View.GONE);
        }

        save.setOnClickListener(l);
        save_all.setOnClickListener(l);
        reset.setOnClickListener(l);
        back.setOnClickListener(l);
    }

    private OnClickListener l = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.save:
                    saveTask();
                    break;
                case R.id.save_all:
                    saveAll();
                    break;
                case R.id.reset:
                    reset();
                    break;
                case R.id.back:
                    ((MainFragmentActivity) mContext).toTabFm(HomeFragment.INDEX, null);
                    break;
                default:
                    break;
            }

        }
    };

    private void selectItem(int p) {
        gridAdapter.setSelectItem(p);
        currentMachineInfo = machineInfos.get(p);
        if ("巡检".equals(taskType)) {
            machineItemInfos.clear();
            machineItemInfos.addAll(currentMachineInfo.getInspectionItems());
            for (MachineItemInfo item : machineItemInfos) {
                item.setInputValue(item.getStandardValue());
            }
        }
        machineItemInfoAdapter.notifyDataSetChanged();
        gridAdapter.notifyDataSetChanged();
    }

    protected void reset() {
        if (currentTaskInfo == null) {
            ToastUtils.showToast(mContext, "请先选择任务", 0);
            return;
        }
        if (currentMachineInfo != null) {
            for (MachineItemInfo item : currentMachineInfo.getInspectionItems()) {
                item.setInputValue(item.getStandardValue());
            }

            machineItemInfoAdapter.notifyDataSetChanged();
        }

        dataHelper.saveTask(currentTaskInfo);
        isExceptionBtn.setText("正常");
        isWarnBtn.setText("绿灯");
        isException = false;
        isWarn = false;

    }

    protected void saveAll() {
        int sum = 0;
        for (TaskInfo info : dataList) {
            if (info.getTaskState() == 0) {
                sum++;
            }
        }

        dig = AlertUtils.showAlertBtn(mContext, "确认完成其余" + sum + "任务？", "确定", "取消", new OnClickListener() {

            @Override
            public void onClick(View v) {
                for (TaskInfo info : dataList) {
                    if (info.getTaskState() < 2) {
                        info.setTaskState(2);
                        info.setActualEndTime(Util.getNowDateTime());
                    }
                }

                int i = dataHelper.saveTaskList(dataList);
                if (i == dataList.size()) {
                    ((MainFragmentActivity) mContext).toTabFm(HomeFragment.INDEX, null);
                    ToastUtils.showToast(mContext, "保存成功", 0);
                    getActivity().sendBroadcast(new Intent("updateCount"));
                } else {
                    ToastUtils.showToast(mContext, "保存失败", 0);
                }
                dig.dismiss();
            }
        }, new OnClickListener() {

            @Override
            public void onClick(View v) {
                dig.dismiss();
            }
        });

    }

    protected void saveTask() {
        if (currentTaskInfo == null) {
            ToastUtils.showToast(mContext, "请先选择任务", 0);
            return;
        }
        if (currentTaskInfo.getTaskState() == 3) {
            ToastUtils.showToast(mContext, "该任务已处理了", 0);
            return;
        }

        if ("巡检".equals(taskType)) {
            if (currentMachineInfo != null) {
                List<MachineItemInfo> infos = currentMachineInfo.getInspectionItems();
                if (infos != null) {
                    for (MachineItemInfo machineItemInfo : infos) {
                        List<ItemFiled> itemFilds = machineItemInfo.getInspectionItemFileds();
                        if (itemFilds != null) {
                            for (ItemFiled itemFiled : itemFilds) {
                                if (TextUtils.isEmpty(itemFiled.getInputValue())) {
                                    ToastUtils.showToast(mContext, "请填写完整信息:" + itemFiled.getName() + "不能为空", 0);
                                    return;
                                }
                            }
                        }

                    }
                }
            }
        }

        List<String> list = new ArrayList<>();

        for (FileInfo item : imageList) {
            list.add(item.getPath());
        }
        currentTaskInfo.setImageList(list);
        currentTaskInfo.setException(isException);
        currentTaskInfo.setWarn(isWarn);
        currentTaskInfo.setActualEndTime(Util.getNowDateTime());
        currentTaskInfo.setProblemDescription(remarkEt.getText().toString());
        currentTaskInfo.setTaskState(2);
        long i = dataHelper.saveTask(currentTaskInfo);
        if (i > 0) {
            Toast.makeText(mContext, "保存成功", Toast.LENGTH_SHORT).show();
            getActivity().sendBroadcast(new Intent("updateCount"));
        } else {

            Toast.makeText(mContext, "保存失败", Toast.LENGTH_SHORT).show();
        }
    }

    private Dialog dig;

    /**
     * 相册选择返回
     */
    private void addImage() {

        handler.post(new Runnable() {

            @Override
            public void run() {
                if (app.getChooseImages().size() > 0) {

                    for (FileInfo info : app.getChooseImages()) {
                        try {
                            if (info != null && new File(info.getPath()).exists()) {
                                addFile(info);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    app.getChooseImages().clear();
                    handler.sendEmptyMessage(1);
                }
            }
        });
    }

    /***
     * 将文件、图片加入到列表中
     *
     * @param info
     */
    private void addFile(FileInfo info) {
        try {
            // 命名：每项标题_位置_备注(如：勘察草图_1_备注)
            int size = imageList.size();
            String name = "拍照";
            info.setType(2);
            info.setName(Util.StringFilter(name));
            if (size > 0)
                imageList.add(size - 1, info);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        if (dataHelper != null) {
            dataHelper.Close();
        }
        super.onDestroy();
    }

    private void updateInputItem() {
        MachineBoxInfo machineBoxInfo = currentTaskInfo.getCabinet();
        if ("巡查".equals(taskType)) {
            return;
        }
        if (machineBoxInfo == null)
            return;
        machineInfos.clear();
        machineInfos.addAll(machineBoxInfo.getEquipmentList());
        if (machineInfos.size() > 0) {
            selectItem(0);
        } else {
            machineItemInfos.clear();
            machineItemInfoAdapter.notifyDataSetChanged();
            gridAdapter.notifyDataSetChanged();
        }
    }

    private void updateImageGrid() {
        imageList.clear();
        if (imageList != null && imageList.size() > 0) {
            FileInfo fileInfo = imageList.get(imageList.size() - 1);
            if (fileInfo.getType() != -1) {
                FileInfo fileInfo1 = new FileInfo();
                fileInfo1.setType(-1);
                imageList.add(fileInfo1);
            }
        } else {
            FileInfo fileInfo = new FileInfo();
            fileInfo.setType(-1);
            imageList.add(fileInfo);
        }
        if (currentTaskInfo != null && currentTaskInfo.getImageList() != null) {
            for (String item : currentTaskInfo.getImageList()) {
                if ("photo".equals(MediaUtils.getContentType(MediaUtils.getEndType(item)))) {
                    FileInfo fileInfo = new FileInfo();
                    fileInfo.setType(2);
                    fileInfo.setPath(item);
                    addFile(fileInfo);
                }

            }
        }
        grid_image.setAdapter(imageGridViewAdapter);
    }

    @Override
    protected void back() {
        ((MainFragmentActivity) mContext).toTabFm(HomeFragment.INDEX, null);
    }
}
