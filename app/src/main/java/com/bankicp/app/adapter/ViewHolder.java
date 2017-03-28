package com.bankicp.app.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bankicp.app.model.FileInfo;
import com.bankicp.app.model.URLs;
import com.bankicp.app.ui.GalleryFileActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ViewHolder {
    private SparseArray<View> mViews;
    private int mPosition;
    private View mConvertView;
    private Context mContext;
    private int mLayoutId;
    private int width;

    public ViewHolder(Context context, ViewGroup parent, int layoutId, int position) {
        mContext = context;
        mLayoutId = layoutId;
        this.mPosition = position;
        this.mViews = new SparseArray<View>();
        mConvertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        mConvertView.setTag(this);
        DisplayMetrics dm = new DisplayMetrics();
        //获取屏幕信息
        ((Activity) this.mContext).getWindowManager().getDefaultDisplay().getMetrics(dm);
        width = dm.widthPixels / 3;
    }

    public static ViewHolder get(Context context, View convertView,
                                 ViewGroup parent, int layoutId, int position) {
        if (convertView == null) {
            return new ViewHolder(context, parent, layoutId, position);
        } else {
            ViewHolder holder = (ViewHolder) convertView.getTag();
            holder.mPosition = position;
            return holder;
        }
    }

    public int getPosition() {
        return mPosition;
    }

    /**
     * 通过viewId获取控件
     *
     * @param viewId
     * @return
     */
    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    public View getConvertView() {
        return mConvertView;
    }

    /**
     * 设置TextView的值
     *
     * @param viewId
     * @param text
     * @return
     */
    public ViewHolder setText(int viewId, String text) {
        TextView tv = getView(viewId);
        tv.setText(text);
        return this;
    }


    public ViewHolder setVisible(int viewId, boolean visible) {
        View view = getView(viewId);
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
        return this;
    }


    public ViewHolder setTag(int viewId, Object tag) {
        View view = getView(viewId);
        view.setTag(tag);
        return this;
    }

    public ViewHolder setTag(int viewId, int key, Object tag) {
        View view = getView(viewId);
        view.setTag(key, tag);
        return this;
    }

    /***
     * 设置多图
     */
    public ViewHolder setImageList(int viewId, final List<String> images) {


        GridView view = getView(viewId);

        BaseAdapter adapter = new ImageGridAdapter(mContext, images);
        int imageWidth = 0;
        imageWidth = (width - width / 3) / 3;

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
        if (adapter.getCount() == 1) {
            params.width = imageWidth;
            params.height = params.width;
            view.setLayoutParams(params);
            view.setNumColumns(1);
            view.setColumnWidth(imageWidth);
            view.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        } else if (adapter.getCount() == 4 || adapter.getCount() == 2) {
            params.width = 2 * (imageWidth + 5);
            params.height = params.width;
            view.setLayoutParams(params);
            view.setNumColumns(2);
            view.setColumnWidth(imageWidth);
            view.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        } else {
            params.width = 3 * (imageWidth + 5);
            params.height = params.width;
            view.setLayoutParams(params);
            view.setNumColumns(3);
            view.setColumnWidth(imageWidth);
            view.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        }
        view.setAdapter(adapter);

        view.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> ImageAdapter,
                                    View rowView, int p, long rowid) {
                Intent intent = new Intent();
                intent.setClass(mContext, GalleryFileActivity.class);
                Bundle bundle = new Bundle();
                List<FileInfo> list = new ArrayList<FileInfo>();
                for (String string : images) {
                    FileInfo info = new FileInfo();
                    if (!string.contains("http://")) {
                        info.setPath(URLs.URL_BASE_PIC + string);
                    } else {
                        info.setPath(string);
                    }
                    list.add(info);
                }
                bundle.putSerializable("imageInfos", (Serializable) list);
                bundle.putBoolean("isEdit", false);
                bundle.putInt("position", p);
                intent.putExtras(bundle);
                mContext.startActivity(intent);
            }
        });
        return this;
    }

    /**
     * 关于事件的
     */
    public ViewHolder setOnClickListener(int viewId,
                                         View.OnClickListener listener) {
        View view = getView(viewId);
        view.setOnClickListener(listener);
        return this;
    }


}
