package com.bankicp.app.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bankicp.app.R;
import com.bankicp.app.model.MachineItemInfo;
import com.bankicp.app.view.MGridView;

import java.util.List;

public class MachineItemAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<MachineItemInfo> dataItems;


    public MachineItemAdapter(Fragment fragment, List<MachineItemInfo> dataItems) {
        super();
        this.mContext = fragment.getActivity();
        this.dataItems = dataItems;
        this.mInflater = LayoutInflater.from(mContext);
    }

    public static class ViewHolder {
        TextView text_name;
        TextView text_value;
        LinearLayout list_item;
        MGridView grid_view;
        TextView stu_value;
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
        final MachineItemInfo info = (MachineItemInfo) getItem(position);

        if (view == null) {
            viewHolder = new ViewHolder();
            view = mInflater.inflate(R.layout.input_item, null);
            viewHolder.list_item = (LinearLayout) view.findViewById(R.id.list_item);
            viewHolder.text_name = (TextView) view.findViewById(R.id.text_name);
            viewHolder.text_value = (TextView) view.findViewById(R.id.text_value);
            viewHolder.grid_view = (MGridView) view.findViewById(R.id.grid_view);
            viewHolder.stu_value = (TextView) view.findViewById(R.id.stu_value);
            viewHolder.grid_view.setAdapter(new InputGridViewAdapter(mContext, info.getInspectionItemFileds()));
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.text_name.setText(info.getName());
        viewHolder.text_value.setVisibility(View.GONE);
        if (info.getName().contains("是否")) {
            viewHolder.text_value.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(final View v) {
                    String val = viewHolder.text_value.getText().toString()
                            .trim();
                    if ("是".equals(val)) {
                        val = "否";

                    } else if ("否".equals(val)) {
                        val = "是";
                    }
                    if ("绿灯".equals(val)) {
                        val = "红灯";
                    } else if ("红灯".equals(val)) {
                        val = "绿灯";

                    }
                    viewHolder.text_value.setText(val);
                    info.setInputValue(val);
                }
            });
            viewHolder.text_value.setVisibility(View.VISIBLE);
            viewHolder.text_value.setText(info.getInputValue());
        }

        ((InputGridViewAdapter) viewHolder.grid_view.getAdapter()).setList(info.getInspectionItemFileds());
        viewHolder.stu_value.setText(info.getStandardValue());
        return view;
    }
}
