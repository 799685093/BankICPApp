package com.bankicp.app.adapter;

import java.util.ArrayList;

import com.bankicp.app.ImageOptions;
import com.bankicp.app.R;
import com.bankicp.app.model.FileFolder;
import com.bankicp.app.ui.ImageActivity;
import com.bankicp.app.utils.ImageUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ImageFolderAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<FileFolder> dataItems = new ArrayList<>();
    private LayoutInflater mInflater;
    DisplayImageOptions options;

    public ImageFolderAdapter(Context context, ArrayList<FileFolder> fileFolders) {
        super();
        this.mContext = context;
        this.dataItems = fileFolders;
        this.mInflater = LayoutInflater.from(mContext);
        options = ImageOptions.getImageOptions();

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return dataItems == null ? null : dataItems.get(position);
    }

    @Override
    public int getCount() {
        return dataItems.size();
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {

            convertView = mInflater.inflate(R.layout.choosse_ablum_folder, null);
            holder = new ViewHolder();
            holder.upload_ablum_folder_choose = ((ImageView) convertView.findViewById(R.id.upload_ablum_folder_choose));
            holder.upload_ablum_folder_count = ((TextView) convertView.findViewById(R.id.upload_ablum_folder_count));
            holder.upload_ablum_folder_name = ((TextView) convertView.findViewById(R.id.upload_ablum_folder_name));
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final FileFolder fileFolder = dataItems.get(position);

        if (fileFolder != null) {

            holder.upload_ablum_folder_count.setText(String.valueOf(fileFolder.getFileInfos().size()));
            holder.upload_ablum_folder_name.setText(fileFolder.getName());

            holder.upload_ablum_folder_choose
                    .setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent();
                            intent.putExtra("images", fileFolder);
                            intent.setClass(mContext, ImageActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            mContext.startActivity(intent);
                            //
                        }
                    });

            if (fileFolder.getFileInfos().size() > 0) {
                String url = fileFolder.getFileInfos().get(0).getPath();
                ImageLoader.getInstance().displayImage("file://" + url,
                        holder.upload_ablum_folder_choose, options);

            } else {
                holder.upload_ablum_folder_choose.setImageResource(R.mipmap.wait);
            }

        }
        return convertView;
    }

    public static class ViewHolder {
        ImageView upload_ablum_folder_choose;
        TextView upload_ablum_folder_count;
        TextView upload_ablum_folder_name;
    }
}