package com.bankicp.app.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.bankicp.app.ImageOptions;
import com.bankicp.app.model.URLs;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

public class ImageGridAdapter extends BaseAdapter {
    // 定义Context
    private Context mContext;
    // 定义整型数组 即图片源
    private List<String> fileInfos;
    private int width;
    private DisplayImageOptions options;

    public ImageGridAdapter(Context c, List<String> list) {
        mContext = c;
        this.fileInfos = list;
        DisplayMetrics dm = new DisplayMetrics();
        // 获取屏幕信息
        ((Activity) this.mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);
        width = dm.widthPixels / 3;
        options = ImageOptions.getImageOptions();
    }

    // 获取图片的个数
    @Override
    public int getCount() {
        return fileInfos.size();
    }

    // 获取图片在库中的位置
    @Override
    public Object getItem(int position) {
        return fileInfos.get(position);
    }

    // 获取图片ID
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = null;
        if (convertView == null) {
            // 给ImageView设置资源
            imageView = new ImageView(mContext);

        } else {
            imageView = (ImageView) convertView;
        }
        // 异步加载图片

        String newimageUrl;

        try {
            String imgspath = fileInfos.get(position);
            if (!imgspath.contains("http://")) {
                newimageUrl = URLs.URL_BASE_PIC + imgspath;
            } else {
                newimageUrl = imgspath;
            }
        } catch (Exception e) {
            e.printStackTrace();
            newimageUrl = "";
        }

        ImageLoader.getInstance().displayImage(newimageUrl, imageView, options);
        int imageWidth = 0;
        imageWidth = (width - width / 3) / 3;
        imageView.setLayoutParams(new GridView.LayoutParams(LayoutParams.MATCH_PARENT, imageWidth));

        // 设置显示比例类型
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);

        return imageView;
    }

}
