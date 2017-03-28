package com.bankicp.app.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import android.widget.Toast;

import com.bankicp.app.Constants;
import com.bankicp.app.ImageOptions;
import com.bankicp.app.MyApp;
import com.bankicp.app.R;
import com.bankicp.app.model.FileInfo;
import com.bankicp.app.ui.AblumActivity;
import com.bankicp.app.ui.BaseFragment;
import com.bankicp.app.ui.GalleryFileActivity;
import com.bankicp.app.ui.InspectionFragment;
import com.bankicp.app.utils.AlertUtils;
import com.bankicp.app.utils.AlertUtils.OnAlertSelectId;
import com.bankicp.app.utils.MediaUtils;
import com.bankicp.app.utils.ToastUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CreateTaskGridViewAdapter extends BaseAdapter {
    // 定义Context
    private Context mContext;
    private BaseFragment fm;
    // 定义整型数组 即图片源
    private List<FileInfo> infos;
    private LayoutInflater mInflater;
    private DisplayImageOptions options;
    private MyApp app;

    public CreateTaskGridViewAdapter(BaseFragment fm, List<FileInfo> infos) {
        this.fm = fm;
        this.mContext = fm.getActivity();
        this.infos = infos;
        this.app = (MyApp) mContext.getApplicationContext();
        this.mInflater = LayoutInflater.from(mContext);

        options = ImageOptions.getImageOptions();
    }

    // 获取图片的个
    @Override
    public int getCount() {
        return infos == null ? 0 : infos.size();
    }

    // 获取图片在库中的位置
    @Override
    public Object getItem(int position) {
        return infos == null ? null : infos.get(position);
    }

    // 获取图片ID
    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        // 异步加载图片
        final FileInfo info = infos.get(position);
        if (convertView == null) {
            // 给ImageView设置资源
            convertView = mInflater.inflate(R.layout.image_upload, null);
            holder = new ViewHolder();
            assert convertView != null;
            holder.iv = (ImageView) convertView.findViewById(R.id.image_upload_im);
            holder.imgDelete = (ImageView) convertView.findViewById(R.id.image_upload_btn);
            holder.tv = (TextView) convertView.findViewById(R.id.image_upload_tv);
            holder.type = (TextView) convertView.findViewById(R.id.image_upload_type);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (position == getCount() - 1) {
            holder.iv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    app.setChoosePosition(position);
                    addImage();
                }
            });
            holder.imgDelete.setVisibility(View.GONE);
            holder.tv.setVisibility(View.GONE);
            holder.type.setVisibility(View.GONE);

            holder.iv.setImageResource(R.mipmap.icon_addpic);

        } else {
            holder.imgDelete.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    infos.remove(position);
                    notifyDataSetChanged();
                }
            });
            if (info.getType() == Constants.DATA_TYPE_IMAGE_2) {
                holder.iv.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            app.setChoosePosition(position);
                            Intent intent = new Intent(mContext, GalleryFileActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("imageInfos", (Serializable) infos);
                            bundle.putInt("position", position);
                            bundle.putInt("from", InspectionFragment.INDEX);
                            intent.putExtras(bundle);
                            fm.startActivityForResult(intent, 3);// (intent);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
            if (info.getType() == Constants.DATA_TYPE_IMAGE_2) {
                String newimageUrl;
                try {
                    if (info.getPath().contains("http://")) {
                        newimageUrl = info.getPath();
                    } else {
                        newimageUrl = "file://" + info.getPath();
                    }
                    ImageLoader.getInstance().displayImage(newimageUrl,
                            holder.iv, options);

                } catch (Exception e) {
                    holder.iv.setImageResource(R.mipmap.wait);
                    e.printStackTrace();
                }

            } else if (info.getType() == Constants.DATA_TYPE_FILE_6) {
                holder.iv.setImageResource(R.mipmap.other_file);
                holder.type.setVisibility(View.VISIBLE);
                holder.type.setText(MediaUtils.getEndType(info.getPath()));
            } else {
                holder.iv.setImageResource(R.mipmap.wait);
            }
            holder.iv.setAdjustViewBounds(true);
            holder.iv.setScaleType(ScaleType.CENTER_CROP);
            // 查看大图
            holder.imgDelete.setVisibility(View.VISIBLE);
            holder.type.setVisibility(View.GONE);
            holder.tv.setText(String.valueOf((position + 1)
                    + (TextUtils.isEmpty(info.getDirection()) ? "" : "_"
                    + info.getDirection())));

        }

        return convertView;
    }

    /**
     * 启动相机拍照
     */
    @SuppressLint("SimpleDateFormat")
    private void capture() {
        String savePath = "";
        // 判断是否挂载了SD卡
        String storageState = Environment.getExternalStorageState();
        if (storageState.equals(Environment.MEDIA_MOUNTED)) {
            savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/BankICP/";// 存放照片的文件夹

            File savedir = new File(savePath);
            if (!savedir.exists()) {
                savedir.mkdirs();
            }
        }

        // 没有挂载SD卡，无法保存文件
        if (TextUtils.isEmpty(savePath)) {
            ToastUtils.showToast(mContext, "无法保存照片，请检查SD卡是否挂载", Toast.LENGTH_SHORT);
            return;
        }

        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss")
                .format(new Date());
        String fileName = timeStamp + ".jpg";// 照片命名
        File out = new File(savePath, fileName);
        Uri uri = Uri.fromFile(out);

        String theLarge = savePath + fileName;// 该照片的绝对路径
        app.setCapturePath(theLarge);

        FileInfo fileInfo = new FileInfo();
        fileInfo.setPath(theLarge);
        app.getChooseImages().clear();
        app.getChooseImages().add(fileInfo);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        fm.startActivityForResult(intent, Constants.PIC_REQUEST_CODE);

    }

    /***
     * 相册
     */
    private void album() {
        Intent intent2 = new Intent();
        intent2.setClass(mContext, AblumActivity.class);
        fm.startActivityForResult(intent2, 2);
    }

    /***
     * 添加图片
     */
    private void addImage() {
        AlertUtils.showAlert(mContext, new String[]{// "（方向）连续拍照","普通拍照",
                "拍照", "相册"}, null, new OnAlertSelectId() {

            @Override
            public void onClick(int whichButton) {
                switch (whichButton) {
                    case 0:// 拍照
                        capture();
                        break;
                    case 1:// 相册
                        album();
                        break;
                    default:
                        break;
                }
            }

        });
    }

    public static class ViewHolder {
        TextView type;
        ImageView iv;
        ImageView imgDelete;
        TextView tv;
    }
}
