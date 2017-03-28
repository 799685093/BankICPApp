package com.bankicp.app.adapter;

import java.util.ArrayList;

import com.bankicp.app.MyApp;
import com.bankicp.app.R;
import com.bankicp.app.model.FileInfo;
import com.bankicp.app.utils.LogUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class ChooseImageAdapter extends BaseAdapter {
    private Context context;
    private MyApp app;
    private ArrayList<FileInfo> imageInfos;
    private LayoutInflater mInflater;
    private DisplayImageOptions options;
    private GridView gv;

    public ChooseImageAdapter(Context context, ArrayList<FileInfo> imageInfos, GridView gridView) {
        this.context = context;
        app = (MyApp) context.getApplicationContext();
        this.imageInfos = imageInfos;
        this.mInflater = LayoutInflater.from(context);
        this.gv = gridView;
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.wait)
                .showImageForEmptyUri(R.mipmap.wait)
                .showImageOnFail(R.mipmap.wait).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();

    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        // holder = new ViewHolder();
        final FileInfo imageInfo = imageInfos.get(position);
        try {

            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.choosse_ablum, null);
                holder.upload_ablum_choose = ((ImageView) convertView
                        .findViewById(R.id.upload_ablum_choose));
                // holder.upload_ablum_path = ((TextView)
                // convertView.findViewById(R.id.upload_ablum_path));
                holder.btn_upload_ablum_choose = ((Button) convertView
                        .findViewById(R.id.btn_upload_ablum_choose));
                // holder.upload_ablum_id = ((TextView)
                // convertView.findViewById(R.id.upload_ablum_id));
                // holder.upload_ablum_path = ((TextView)
                // convertView.findViewById(R.id.upload_ablum_path));
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            // String path = new String(imgPath.get(position));
            // String id = new String(imgID.get(position));
            // BitmapFactory.Options options = new BitmapFactory.Options();
            // options.inSampleSize = 8;

            // Bitmap bm = new Bitmap();

            // 设置图像源于资源ID。
            // the_imageView.setImageResource(mImageIds[position]);
            /* 使ImageView与边界适应 */

            if (app.getMapIsChoosed().get(imageInfo.getId()) == null) {
                app.getMapIsChoosed().put(imageInfo.getId(), false);
            }

            ImageLoader.getInstance().displayImage(
                    "file://" + imageInfo.getPath(),
                    holder.upload_ablum_choose, options);

            // holder.upload_ablum_choose.setScaleType(ScaleType.FIT_XY);

            // imageLoader.loadImage(context, holder.upload_ablum_choose,
            // imageInfo.getPath(),imageInfo.getId());

            if (app.getMapIsChoosed().get(imageInfo.getId())) {
                holder.btn_upload_ablum_choose
                        .setBackgroundResource(R.mipmap.btn_choose);
            } else {
                holder.btn_upload_ablum_choose
                        .setBackgroundResource(R.mipmap.btn_unchoose);
            }
            holder.btn_upload_ablum_choose.setTag(imageInfo);
        } catch (NumberFormatException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NotFoundException e) {
            LogUtil.recordExceptionLog(context, e, "MyDevicePhotoActivity",
                    true);
            e.printStackTrace();
        }
        AbsListView.LayoutParams param = new AbsListView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                this.gv.getWidth() / 6);   //用传递进来的GridView计算宽度，并设置为单元格高度，保证棋盘是正方形
        convertView.setLayoutParams(param);
        return convertView;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return imageInfos.size();
    }

    public static class ViewHolder {
        ImageView upload_ablum_choose;
        // TextView upload_ablum_path;
        // TextView upload_ablum_id;

        Button btn_upload_ablum_choose;
        TextView upload_chooseablum_mark;
        TextView item_value;

    }
}