/**
 * @Description: TODO(“关于”界面，展现应用相关信息)
 * @author 广东省电信工程有限公司信息技术研发中心
 * @date 2014-4-24 上午9:53:48
 */
package com.bankicp.app.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bankicp.app.Constants;
import com.bankicp.app.R;
import com.bankicp.app.adapter.CreateTaskGridViewAdapter;
import com.bankicp.app.model.EventInfo;
import com.bankicp.app.model.EventLog;
import com.bankicp.app.model.FileInfo;
import com.bankicp.app.model.Result;
import com.bankicp.app.model.URLs;
import com.bankicp.app.model.UserInfo;
import com.bankicp.app.utils.AlertUtils;
import com.bankicp.app.utils.HttpService;
import com.bankicp.app.utils.JsonUtil;
import com.bankicp.app.utils.ToastUtils;
import com.bankicp.app.utils.Util;
import com.bankicp.app.view.MGridView;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CreateReportFragment extends BaseFragment {

    Button send_btn, status_btn, header_close;
    EditText remark;
    EditText title;
    // 图片上传
    private MGridView grid_image;
    List<FileInfo> imageList = new ArrayList<>();
    private CreateTaskGridViewAdapter imageGridViewAdapter;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 1) {
                grid_image.setAdapter(imageGridViewAdapter);
            }
        }

        ;
    };
    private UserInfo user;
    private EventInfo eventInfo;

    private int status = 0;

    // list
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_report_right);
        user = app.getUserInfo();
        if (bundle != null) {
            eventInfo = (EventInfo) bundle.get("eventInfo");
            status = bundle.getInt("form");
        }
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
        status_btn = $(R.id.status);
        title = $(R.id.title);
        remark = $(R.id.remark);

        header_close.setOnClickListener(listener);
        send_btn.setOnClickListener(listener);
        status_btn.setOnClickListener(listener);
        initImageView();
        initStatusBtn();
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

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
                case R.id.status:
                    showItemDig(v);
                    break;

            }

        }
    };

    private void send() {
        String content = remark.getText().toString();
        String t = title.getText().toString();
        if (TextUtils.isEmpty(content)) {
            ToastUtils.showToast(mContext, "内容不能为空", Toast.LENGTH_LONG);
            return;
        }
        if (isCreateEventInfo()) {

            EventInfo info = new EventInfo();
            info.setContent(content);
            info.setTitle(t);
            info.setStatus(status);
            info.setCreateUserId(Long.parseLong(user.getUserId()));
            List<String> images = new ArrayList<>();
            for (int i = 0; i < imageList.size(); i++) {
                if (!TextUtils.isEmpty(Util.getNotNullString(imageList.get(i).getPath()))) {
                    images.add(imageList.get(i).getPath());
                }
            }
            info.setImages(images);
            new PostDataTask(mContext, info, URLs.URL_ADD_EVENT_REPORT).execute();
        } else {

            if (eventInfo.getStatus() == 2) {
                ToastUtils.showToast(mContext, "已经关闭！", Toast.LENGTH_LONG);
                return;
            }
            EventLog log = new EventLog();
            log.setContent(content);
            log.setTitle(t);
            log.setStatus(status);
            log.setCreateUserId(Long.parseLong(user.getUserId()));
            log.setEventInfoId(eventInfo.getId());
            List<String> images = new ArrayList<String>();
            for (int i = 0; i < imageList.size(); i++) {
                if (!TextUtils.isEmpty(Util.getNotNullString(imageList.get(i).getPath()))) {
                    images.add(imageList.get(i).getPath());
                }
            }
            log.setImages(images);
            new PostDataTask(mContext, log, URLs.URL_ADD_EVENT_LOG).execute();
        }

    }

    private boolean isCreateEventInfo() {
        if (eventInfo == null || status == 0) {
            return true;
        } else {
            return false;
        }
    }

    // 初始化上传图片视图
    private void initImageView() {
        grid_image = $(R.id.grid_image);
        imageGridViewAdapter = new CreateTaskGridViewAdapter(this, imageList);
        grid_image.setAdapter(imageGridViewAdapter);
        updateImageGrid();

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
//        if (imageList != null && imageList != null) {
//            for (String item : imageList) {
//                if ("photo".equals(MediaUtils.getContentType(MediaUtils
//                        .getEndType(item)))) {
//                    FileInfo fileInfo = new FileInfo();
//                    fileInfo.setType(2);
//                    fileInfo.setPath(item);
//                    addFile(fileInfo);
//                }
//
//            }
//        }
        grid_image.setAdapter(imageGridViewAdapter);
        //grid_image.setAdapter(imageGridViewAdapter);
        // imageGridViewAdapter.notifyDataSetChanged();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constants.REQUEST_CODE_SCAN:
                if (data != null) {
                    Bundle extras = data.getExtras();
                    if (null != extras) {
                        // int width = extras.getInt("width");
                        // int height = extras.getInt("height");

                        // LinearLayout.LayoutParams lps = new
                        // LinearLayout.LayoutParams(width, height);
                        // lps.topMargin = (int)
                        // TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                        // 30, getResources().getDisplayMetrics());
                        // lps.leftMargin = (int)
                        // TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                        // 20, getResources().getDisplayMetrics());
                        // lps.rightMargin = (int)
                        // TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                        // 20, getResources().getDisplayMetrics());
                        //
                        // right_top_iv_qrcode.setLayoutParams(lps);

                        String result = extras.getString("result");
                        // mResultText.setText(result);

                        // Bitmap barcode = null;
                        // byte[] compressedBitmap = extras
                        // .getByteArray(DecodeThread.BARCODE_BITMAP);
                        // if (compressedBitmap != null) {
                        // barcode = BitmapFactory.decodeByteArray(
                        // compressedBitmap, 0, compressedBitmap.length,
                        // null);
                        // // Mutable copy:
                        // barcode = barcode.copy(Bitmap.Config.RGB_565, true);
                        // }

                        // right_top_iv_qrcode.setImageBitmap(barcode);

                        // initGridView()
                        // 提示是否保存

                        //updateView(result);
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
                            if (info != null
                                    && new File(info.getPath()).exists()) {
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
            // String name = currentMachineInfo.getEquipmentName()+"_"+size+"_";

            String name = "拍照";
            info.setType(2);
            info.setName(Util.StringFilter(name));
            if (size > 0)
                imageList.add(size - 1, info);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private class PostDataTask extends AsyncTask<String, Integer, String> {
        private Context context;
        private Serializable model;
        private String url;
        private TextView progress_text;
        private long totalSize;
        private boolean showProgressDialog = true;
        private Handler mHandler = new Handler();

        public PostDataTask(Context context, Serializable model, String url) {
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

                }

            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (showProgressDialog) {
                dismissLoading();
            }
            if (!TextUtils.isEmpty(result)) {
                imageList.clear();
                updateImageGrid();
                remark.setText("");
                ToastUtils.showToast(mContext, result, 0);
//                new GetDataTask().execute();
                if (isCreateEventInfo()) {
                    Intent intent = new Intent("com.bankicp.app.ACTION_ONREFRESH_REPORT");
                    intent.putExtra("EventInfo", model);
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                } else {
                    Intent intent = new Intent("com.bankicp.app.ACTION_ONREFRESH_LOG");
                    intent.putExtra("EventLog", model);
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                }

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

                Map<String, String> map = new HashMap<String, String>();
                map.put("json", JsonUtil.objectToJson(model));
                Map<String, String> files = new HashMap<String, String>();
                for (int i = 0; i < imageList.size(); i++) {
                    if (!TextUtils.isEmpty(Util.getNotNullString(imageList.get(i).getPath()))) {
                        files.put(i + "", imageList.get(i).getPath());
                    }
                }
                String res = HttpService.doPost(this.url, map,
                        files, new HttpService.ProgressCallback() {

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
                        progress_text = (TextView) dialog
                                .findViewById(R.id.progress_text);
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

    private void initStatusBtn() {
        String[] arr = getResources().getStringArray(R.array.status);
        status_btn.setText(arr[status]);
    }

    private Dialog dig;

    protected void showItemDig(final View v) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contectView = inflater.inflate(R.layout.dig_listview, null);
        LinkedList mListItems = new LinkedList<>();
        mListItems.addAll(new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.status))));

        ListView lv = (ListView) contectView.findViewById(R.id.lv);
        lv.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.item_text, mListItems));
        lv.setOnItemClickListener(new OnItemClickListener() {


            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long id) {
                status = position;
                initStatusBtn();
                dig.dismiss();
            }

        });
        dig = AlertUtils.showAlertBtn(mContext, contectView, true, null, null, null, null, null, null);
    }
}
