package com.bankicp.app.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bankicp.app.R;
import com.bankicp.app.model.RoomInfo;
import com.bankicp.app.model.TaskInfo;
import com.bankicp.app.model.TimeGroupInfo;
import com.bankicp.app.utils.Util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static com.bankicp.app.ui.HomeFragment.mMap;

public class MyExpandableListAdapter extends BaseExpandableListAdapter {

    public interface SignBtnListener {
        void onClick(TimeGroupInfo groupInfo);
    }

    protected Context context;
    private LayoutInflater inflater;
    private ArrayList<TimeGroupInfo> groupList;
    private SignBtnListener signBtnListener;

    public MyExpandableListAdapter(Context context, ArrayList<TimeGroupInfo> groupList) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.groupList = groupList;
    }

    // 返回父列表个数
    @Override
    public int getGroupCount() {
        return groupList.size();
    }

    // 返回子列表个数
    @Override
    public int getChildrenCount(int groupPosition) {
        return groupList.get(groupPosition).getRoomList().size();
    }

    @Override
    public TimeGroupInfo getGroup(int groupPosition) {
        return groupPosition > -1 ? groupList.get(groupPosition) : null;
    }

    @Override
    public RoomInfo getChild(int groupPosition, int childPosition) {
        return groupList.get(groupPosition).getRoomList().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {

        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupHolder groupHolder = null;
        if (convertView == null) {
            groupHolder = new GroupHolder();
            convertView = inflater.inflate(R.layout.group, null);
            groupHolder.textView = (TextView) convertView.findViewById(R.id.group);
            groupHolder.imageView = (ImageView) convertView.findViewById(R.id.image);
            groupHolder.button = (TextView) convertView.findViewById(R.id.sign_btn);
            convertView.setTag(groupHolder);
        } else {
            groupHolder = (GroupHolder) convertView.getTag();
        }

        final TimeGroupInfo groupInfo = getGroup(groupPosition);

        if (groupInfo.getRoomList().size() > 0 && isExpanded) {
            groupHolder.button.setVisibility(View.VISIBLE);
            groupHolder.button.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (signBtnListener != null) {
                        signBtnListener.onClick(groupInfo);
                    }
                }
            });
            boolean isFinish = false;
            int size = groupInfo.getRoomList().size();
            if (groupInfo.getRoomList().size() > 0) {
                for (int i = 0; i < size; i++) {
                    RoomInfo areaInfo = groupInfo.getRoomList().get(i);
                    for (TaskInfo task : areaInfo.getTaskList()) {
                        if (task.getTaskState() == 0) {
                            isFinish = true;
                            break;
                        }
                    }
                }
            }

            if (!isFinish) {
                groupHolder.button.setText("已领取");
            } else {
                groupHolder.button.setText("批量领取");
            }
        } else {
            groupHolder.button.setVisibility(View.GONE);
        }
        groupHolder.textView.setText(groupInfo.getStartTime() + "~" + groupInfo.getEndTime());

        if (isExpanded){
            groupHolder.imageView.setImageResource(R.mipmap.expanded);
        }
        else{
            groupHolder.imageView.setImageResource(R.mipmap.collapse);
        }


        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        ChildHolder childHolder = null;
//        if (convertView == null) {
            childHolder = new ChildHolder();
            convertView = inflater.inflate(R.layout.task_group_item, null);

            childHolder.textName = (TextView) convertView.findViewById(R.id.name);
            childHolder.textType = (TextView) convertView.findViewById(R.id.task_type);
            childHolder.textCount = (TextView) convertView.findViewById(R.id.task_count);
            childHolder.arrow_right = (Button) convertView.findViewById(R.id.arrow_right);

            convertView.setTag(childHolder);
//        } else {
//            childHolder = (ChildHolder) convertView.getTag();
//        }


        if(!(mMap.get(groupPosition).containsKey(childPosition))){
            Log.i("tag","第一次加载数据");
            RoomInfo info = getChild(groupPosition, childPosition);
            String type = "";
            Set<String> types = new HashSet<>();
            String startTime = "", endTime = "";
            for (TaskInfo task : info.getTaskList()) {
                if (TextUtils.isEmpty(startTime) && TextUtils.isEmpty(endTime)) {
                    startTime = task.getTaskTime();
                    endTime = task.getTaskEndTime();
                }
                try {
                    types.add(task.getTaskType());
                    if (Util.toDate(task.getTaskTime()).before(Util.toDate(startTime))) {
                        startTime = task.getTaskTime();
                    }
                    if (Util.toDate(task.getTaskEndTime()).before(Util.toDate(endTime))) {
                        endTime = task.getTaskEndTime();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            for (String string : types) {
                type = string;
            }
            childHolder.textName.setText(info.getName());
            childHolder.textType.setText("任务类型：" + type + "");
            childHolder.textCount.setText("任务个数：" + (info.getTaskList() == null ? "0" : info.getTaskList().size() + ""));

                mMap.get(groupPosition).put(childPosition,childHolder);


        }else{
            Log.i("tag","已经有数据了, groupposition  "+groupPosition+"  childposition "+childPosition);
            ChildHolder c= (ChildHolder) mMap.get(groupPosition).get(childPosition);
            childHolder.textName.setText(c.textName.getText());
            childHolder.textType.setText(c.textType.getText());
            childHolder.textCount.setText(c.textCount.getText());
        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    class GroupHolder {
        TextView textView;
        ImageView imageView;
        TextView button;
    }

    class ChildHolder {
        public Button arrow_right;
        TextView textName;
        TextView textType;
        TextView textCount;
    }


    public void setSignBtnListener(SignBtnListener signBtnListener) {
        this.signBtnListener = signBtnListener;
    }

}
