package com.bankicp.app.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bankicp.app.R;
import com.bankicp.app.model.TaskInfo;

import java.util.List;

public class TaskListAdapter extends BaseAdapter {
    private Context mContext;
    private List<TaskInfo> dataItems;

    public TaskListAdapter(Fragment fragment, List<TaskInfo> dataItems) {
        super();
        this.mContext = fragment.getActivity();
        this.dataItems = dataItems;
    }

    public static class ViewHolder {
        TextView type;
        TextView state;
        TextView name;
        TextView time;
        LinearLayout list_view;
    }

    @Override
    public int getCount() {
        return dataItems == null ? 0 : dataItems.size();
    }

    @Override
    public Object getItem(int position) {
        return dataItems == null ? null : dataItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.task_item, null);
            viewHolder.list_view = (LinearLayout) view.findViewById(R.id.list_view);
            viewHolder.name = (TextView) view.findViewById(R.id.name);
            viewHolder.type = (TextView) view.findViewById(R.id.type);
            viewHolder.time = (TextView) view.findViewById(R.id.time);
            viewHolder.state = (TextView) view.findViewById(R.id.state);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        TaskInfo taskInfo = (TaskInfo) getItem(position);
        if (taskInfo.getTaskState() == 0) {
            view.setBackgroundColor(ContextCompat.getColor(mContext, R.color.aliceblue));
        } else {
            view.setBackgroundColor(ContextCompat.getColor(mContext, R.color.item_line));
        }

        if (taskInfo.getTaskState() == 1) {
            view.setBackgroundColor(ContextCompat.getColor(mContext, R.color.aliceblue));
        } else if (taskInfo.getTaskState() == 2) {
            view.setBackgroundColor(ContextCompat.getColor(mContext, R.color.task_list_item_select_bg));
        } else {
            view.setBackgroundColor(Color.TRANSPARENT);
        }
        if (position == selectItem) {
            view.setBackgroundColor(ContextCompat.getColor(mContext, R.color.gray));
        }
        viewHolder.name.setText(taskInfo.getCabinet().getCabinetName() + " "
                + taskInfo.getCabinet().getCabinetNo());
        viewHolder.time.setText(taskInfo.getCabinet().getCreateDate());
        return view;
    }

    public void setSelectItem(int selectItem) {
        this.selectItem = selectItem;
    }

    private int selectItem = -1;

}
