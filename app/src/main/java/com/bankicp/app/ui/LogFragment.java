/**
 * @Description: TODO(“关于”界面，展现应用相关信息)
 * @author 广东省电信工程有限公司信息技术研发中心
 * @date 2014-4-24 上午9:53:48
 */
package com.bankicp.app.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.bankicp.app.Constants;
import com.bankicp.app.R;
import com.bankicp.app.adapter.CommonAdapter;
import com.bankicp.app.adapter.ViewHolder;
import com.bankicp.app.model.EventInfo;
import com.bankicp.app.model.EventLog;
import com.bankicp.app.model.UserInfo;
import com.bankicp.app.view.MultiListView;
import java.util.ArrayList;
import java.util.List;

public class LogFragment extends BaseFragment {

	
	// 当前页
	private int pageIndex = Constants.PAGEINDEX;
	private int pageSize = Constants.PAGESIZE;
	
    Button header_right, header_close;


    MultiListView log_list;
    List<EventLog> logList;
    CommonAdapter logAdapter;


    
    private UserInfo user;
    private EventInfo eventInfo;


    // list
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_frament);
        user = app.getUserInfo();
        logList=new ArrayList<EventLog>();
        if (bundle != null) {
        	eventInfo = (EventInfo) bundle.get("eventInfo");
			if (eventInfo != null) {
				if(eventInfo.getLogs() != null)
					logList.addAll(eventInfo.getLogs());
			}
		}
        try {
			IntentFilter intentFilter = new IntentFilter();
			intentFilter.addAction("com.bankicp.app.ACTION_ONREFRESH_LOG");
			
			LocalBroadcastManager.getInstance(mContext).registerReceiver(broadcastReceiver, intentFilter);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals("com.bankicp.app.ACTION_ONREFRESH_LOG")){
				addLogInfo(intent);
			}
			
			
		}
	};
    @Override
	public void back() {

        FragmentManager fm = getFragmentManager();
        fm.popBackStack();
    }

    protected void addLogInfo(Intent intent) {
    	EventLog eventLog = (EventLog) intent.getSerializableExtra("EventLog");
    	logList.add(0, eventLog);
    	logAdapter.notifyDataSetChanged();
	}

	@Override
    protected void initViews() {

		header_close = $(R.id.header_close);
		header_right = $(R.id.header_right);
		header_right.setText("跟进");
//		pull_refresh_list = $(R.id.pull_refresh_list);

		log_list = $(R.id.pull_refresh_list);
		logAdapter = new CommonAdapter<EventLog>(mContext, logList,
				R.layout.item) {
			@Override
			public void convert(ViewHolder viewHolder, EventLog item) {
				
				viewHolder.setText(R.id.title, item.getTitle());
				viewHolder.setText(R.id.remark,  item.getContent());
				viewHolder.setText(R.id.uploader, TextUtils.isEmpty(item.getCreateUserName())?"未知用户":item.getCreateUserName());
				
				viewHolder.setText(R.id.time, item.getCreateDate());
				// item.getImages();
				if (item.getImages() != null && item.getImages().size() > 0) {
					viewHolder.setVisible(R.id.show_more_rly, true);
					viewHolder.setImageList(R.id.imageview, item.getImages());
				} else {
					viewHolder.setVisible(R.id.show_more_rly, false);
				}
				
			}
		};
		log_list.setAdapter(logAdapter);
        header_close.setOnClickListener(listener);
        header_right.setOnClickListener(listener);
       
    }

//    private void initLog(int p) {
//
//        eventInfo = dataList.get(p);
//        status = eventInfo.getStatus()==0?1:eventInfo.getStatus();
//        initStatusBtn();
//        logList.clear();
//        logList.addAll(eventInfo.getLogs());
//        adapter.setSelectItem(p);
//        logAdapter.notifyDataSetChanged();
//    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
    }

    private OnClickListener listener = new OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.header_close:
                    back();
                    break;
                case R.id.header_right:
                	  CreateReportFragment fragment = new CreateReportFragment();
                    if(bundle != null) bundle.putInt("form", 1);
                  	fragment.setArguments(bundle);
                        toOutFm(fragment);
                    break;
             
            }

        }
    };

   
}
