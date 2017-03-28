package com.bankicp.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.bankicp.app.R;
import com.bankicp.app.model.SimpleUser;

import java.util.List;

/**
 * @version 1.0
 * @created 2012-8-9
 */
public class GridViewAdapter extends BaseAdapter {
    // 定义Context
    private Context mContext;
    private LayoutInflater mInflater;
    private List<SimpleUser> list;

    public GridViewAdapter(Context c, List<SimpleUser> item) {
        mContext = c;
        mInflater = LayoutInflater.from(mContext);
        list = item;
    }

    // 获取图片的个数
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    // 获取图片在库中的位置
    public Object getItem(int position) {
        return list.get(position);
    }

    // 获取图片ID
    public long getItemId(int position) {
        return 0;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.home_type_items, null);
            holder = new ViewHolder();
            convertView.setTag(holder);
            holder.txtName = (TextView) convertView.findViewById(R.id.txtName);
            holder.isChoose = (CheckBox) convertView.findViewById(R.id.checkbox);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.txtName.setText(list.get(position).getUserName());
        holder.isChoose.setChecked(list.get(position).isChecked());
        return convertView;
    }

    static class ViewHolder {
        TextView txtName;
        CheckBox isChoose;
    }
}