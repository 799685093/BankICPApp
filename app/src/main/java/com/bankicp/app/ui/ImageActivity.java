package com.bankicp.app.ui;

import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;

import com.bankicp.app.MyApp;
import com.bankicp.app.R;
import com.bankicp.app.adapter.ChooseImageAdapter;
import com.bankicp.app.model.FileFolder;
import com.bankicp.app.model.FileInfo;

import java.util.ArrayList;

/**
 * @author 广东省电信工程有限公司信息技术研发中心
 * @ClassName: MyDevicePhotoActivity
 * @Description: TODO(实现获取本机中指定目录下的所有图片)
 * @date 2014-4-24 下午4:45:43
 */
public class ImageActivity extends BaseFragmentActivity {
    private Bitmap bitmap = null;
    private Button btn_finish;
    private Button btn_camera_filter_back;
    private GridView gridview = null;
    private ChooseImageAdapter chooseImageAdapter;

    private Button btn1;

    private ArrayList<FileInfo> imageInfos = new ArrayList<>();
    private MyApp app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_add_ablum);
        app = (MyApp) getApplication();
        Intent intent = getIntent();
        FileFolder folder = (FileFolder) intent.getSerializableExtra("images");

        if (folder != null) {
            imageInfos = folder.getFileInfos();
            app.getChooseImages().clear();
            app.getMapIsChoosed().clear();
        }
        initViews();

    }

    /**
     * @author 广东省电信工程有限公司信息技术研发中心
     * @ClassName: ShowItemImageOnClickListener
     * @Description: TODO(单击项显示图片事件监听器)
     * @date 2014-4-24 下午5:10:57
     */

    private class ShowItemImageOnClickListener implements OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            try {
                btn1 = (Button) view.findViewById(R.id.btn_upload_ablum_choose);
                FileInfo imageInfo = (FileInfo) btn1.getTag();

                btn1.setBackgroundResource(R.mipmap.btn_unchoose);
                if (app.getMapIsChoosed().get(imageInfo.getId()) == false) {
                    app.getMapIsChoosed().put(imageInfo.getId(), true);
                    btn1.setBackgroundResource(R.mipmap.btn_choose);
                    app.getChooseImages().add(imageInfo);
                } else if (app.getMapIsChoosed().get(imageInfo.getId()) == true) {
                    app.getMapIsChoosed().put(imageInfo.getId(), false);
                    btn1.setBackgroundResource(R.mipmap.btn_unchoose);
                    app.getChooseImages().remove(imageInfo);
                }
            } catch (NotFoundException e) {

                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (bitmap != null) {
            bitmap.recycle();
        }
    }

    // @Override
    // public boolean onKeyDown(int keyCode, KeyEvent event) {
    // try {
    // if (keyCode == KeyEvent.KEYCODE_BACK
    // && event.getAction() == KeyEvent.ACTION_DOWN) {
    // if (bitmap != null) {
    // bitmap.recycle();
    // }
    // finish();
    // return true;
    // }
    // } catch (Exception e) {
    // LogUtil.recordExceptionLog(MyDevicePhotoActivity.this, e,
    // "MyDevicePhotoActivity", true);
    // e.printStackTrace();
    // }
    // return super.onKeyDown(keyCode, event);
    // }

    /**
     * @author 广东省电信工程有限公司信息技术研发中心
     * @ClassName: BcakListener
     * @Description: TODO(点击返回上一层 清空所选的图片)
     * @date 2014-4-24 下午5:11:12
     */
    class BcakListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            try {
                finish();
                // 回滚在此页面所有选择图片的操作
                app.getMapIsChoosed().clear();
                app.getChooseImages().clear();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    protected void initViews() {

        gridview = $(R.id.upload_ablum_check_gridview);
        btn_finish = $(R.id.btn_camera_filter_choose);
        btn_camera_filter_back = $(R.id.btn_camera_filter_back);
        chooseImageAdapter = new ChooseImageAdapter(mContext, imageInfos,
                gridview);
        gridview.setNumColumns(6);
        gridview.setAdapter(chooseImageAdapter);
        btn_camera_filter_back.setOnClickListener(new BcakListener());
        // 注意此处是getSupportLoaderManager()，而不是getLoaderManager()方法。
        // getSupportLoaderManager().initLoader(0, null, this);
        // 单击显示图片
        gridview.setOnItemClickListener(new ShowItemImageOnClickListener());
        // “完成”按钮点击事件
        btn_finish.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                try {
                    sendBroadcast(new Intent("finish"));
                    app.getMapIsChoosed().clear();
                    finish();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        try {
            if (keyCode == KeyEvent.KEYCODE_BACK
                    && event.getAction() == KeyEvent.ACTION_DOWN) {
                if (bitmap != null) {
                    bitmap.recycle();
                }
                finish();
                return true;
            }
        } catch (Exception e) {
        }
        return super.onKeyDown(keyCode, event);
    }
}