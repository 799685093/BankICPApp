package com.bankicp.app.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bankicp.app.R;
import com.bankicp.app.model.MachineInfo;

import java.util.List;

/**
 * @version 1.0
 * @created 2012-8-9
 */
public class MachineGridViewAdapter extends BaseAdapter {
    // 定义Context
    private Context mContext;
    private LayoutInflater mInflater;
    private List list;

    public MachineGridViewAdapter(Context c, List item) {
        mContext = c;
        mInflater = LayoutInflater.from(mContext);
        list = item;
    }

    // 获取图片的个数
    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    // 获取图片在库中的位置
    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    // 获取图片ID
    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.home_type_items, null);
            holder = new ViewHolder();
            holder.tv = (TextView) convertView.findViewById(R.id.txtName);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        MachineInfo machineInfo = (MachineInfo) list.get(position);
        if (position == selectItem) {
            holder.tv.setBackgroundColor(ContextCompat.getColor(mContext, R.color.task_list_item_select_bg));
        } else {
            holder.tv.setBackgroundColor(ContextCompat.getColor(mContext, R.color.aliceblue));
        }
        holder.tv.setText(machineInfo.getEquipmentName());

        return convertView;
    }

    static class ViewHolder {
        TextView tv;
    }

    public void setSelectItem(int selectItem) {
        this.selectItem = selectItem;
    }

    private int selectItem = -1;
}