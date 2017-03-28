package com.bankicp.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bankicp.app.R;
import com.bankicp.app.model.ItemFiled;
import com.bankicp.app.utils.AlertUtils;

import java.util.List;


/**
 * @version 1.0
 * @created 2012-8-9
 */
public class InputGridViewAdapter extends BaseAdapter {
    // 定义Context
    private Context mContext;
    private LayoutInflater mInflater;
    private List<ItemFiled> list;

    public InputGridViewAdapter(Context c, List<ItemFiled> item) {
        this.mContext = c;
        this.mInflater = LayoutInflater.from(mContext);
        this.list = item;
    }

    public void setList(List<ItemFiled> list) {
        this.list = list;
        notifyDataSetChanged();
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
            convertView = mInflater.inflate(R.layout.input_item_text, null);
            holder = new ViewHolder();
            convertView.setTag(holder);
            holder.text_name = (TextView) convertView.findViewById(R.id.text_name);
            holder.text_value = (TextView) convertView.findViewById(R.id.text_value);
            holder.text_unit = (TextView) convertView.findViewById(R.id.text_unit);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final ItemFiled fild = list.get(position);

        holder.text_value.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(final View v) {

                AlertUtils.showEdit(mContext, null, "请输入异常值", new AlertUtils.OnEditListener() {

                    @Override
                    public void onTextChanged(String str) {

                    }

                    @Override
                    public void onEditFinish(String str) {
                        ((TextView) v).setText(str);
                        fild.setInputValue(str);
                    }
                }, 1);
            }
        });
        holder.text_name.setText(fild.getName());
        holder.text_value.setText(fild.getInputValue());
        holder.text_unit.setText(fild.getUnit());
        return convertView;
    }

    static class ViewHolder {
        TextView text_name;
        TextView text_value;
        TextView text_unit;
    }

}