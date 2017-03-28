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
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;

import com.bankicp.app.Constants;
import com.bankicp.app.R;
import com.bankicp.app.adapter.CommonAdapter;
import com.bankicp.app.adapter.ViewHolder;
import com.bankicp.app.model.EventInfo;
import com.bankicp.app.model.Result;
import com.bankicp.app.model.URLs;
import com.bankicp.app.utils.HttpRequest;
import com.bankicp.app.utils.JsonUtil;
import com.bankicp.app.utils.Util;
import com.bankicp.app.view.MultiListView;
import com.bankicp.app.view.MultiListView.OnLoadMoreListener;
import com.bankicp.app.view.MultiListView.OnRefreshListener;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportFragment extends BaseFragment {


    // 当前页
    private int pageIndex = Constants.PAGEINDEX;
    private int pageSize = Constants.PAGESIZE;

    Button header_right, header_close;

    MultiListView pull_refresh_list;
    List<EventInfo> dataList;
    CommonAdapter adapter;


    // list
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_frament);
        try {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("com.bankicp.app.ACTION_ONREFRESH_REPORT");

            LocalBroadcastManager.getInstance(mContext).registerReceiver(broadcastReceiver, intentFilter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.bankicp.app.ACTION_ONREFRESH_REPORT")) {
                addEventInfo(intent);
            }


        }
    };

    @Override
    public void back() {

        FragmentManager fm = getFragmentManager();
        fm.popBackStack();
    }

    protected void addEventInfo(Intent intent) {
        EventInfo eventInfo = (EventInfo) intent.getSerializableExtra("EventInfo");
        dataList.add(0, eventInfo);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void initViews() {

        header_close = $(R.id.header_close);
        header_right = $(R.id.header_right);
        pull_refresh_list = $(R.id.pull_refresh_list);
        dataList = new ArrayList<>();
        adapter = new CommonAdapter<EventInfo>(mContext, dataList, R.layout.item) {
            @Override
            public void convert(ViewHolder viewHolder, EventInfo item) {
                viewHolder.setText(R.id.title, item.getTitle());
                viewHolder.setText(R.id.remark, item.getContent());
                viewHolder.setText(R.id.uploader, TextUtils.isEmpty(item.getCreateUserName()) ? "未知用户" : item.getCreateUserName());

                viewHolder.setText(R.id.time, item.getCreateDate());
                if (item.getImages() != null && item.getImages().size() > 0) {
                    viewHolder.setVisible(R.id.show_more_rly, true);
                    viewHolder.setImageList(R.id.imageview, item.getImages());
                } else {
                    viewHolder.setVisible(R.id.show_more_rly, false);
                }

                if (viewHolder.getPosition() == this.getSelectItem()) {
                    viewHolder.setVisible(R.id.choose_ic, true);
                } else {
                    viewHolder.setVisible(R.id.choose_ic, false);
                }
            }
        };
        pull_refresh_list.setAdapter(adapter);
        pull_refresh_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EventInfo eventInfo = dataList.get(position - 1);
                LogFragment fragment = new LogFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("eventInfo", eventInfo);
                fragment.setArguments(bundle);
                toOutFm(fragment);

            }
        });
        pull_refresh_list.setOnRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh() {
                pageIndex = 1;
                new GetDataTask().execute();
            }
        });
        pull_refresh_list.setAutoLoadMore(false);
        pull_refresh_list.setOnLoadListener(new OnLoadMoreListener() {

            @Override
            public void onLoadMore() {
                pageIndex += 1;
                new GetDataTask().execute();
            }
        });
        header_close.setOnClickListener(listener);
        header_right.setOnClickListener(listener);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        new GetDataTask().execute();
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
                    Bundle bundle = new Bundle();
                    bundle.putInt("form", 0);
                    fragment.setArguments(bundle);
                    toOutFm(fragment);
                    break;
            }

        }
    };

    /***
     * 联网获取事件列表
     */
    private class GetDataTask extends AsyncTask<String, String, List> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(List list) {
            super.onPostExecute(list);
            if (pageIndex == 1) {
                dataList.clear();
            }
            dataList.addAll(list);
            adapter.notifyDataSetChanged();
            pull_refresh_list.onRefreshComplete();
            pull_refresh_list.onLoadMoreComplete();
        }

        @Override
        protected List doInBackground(String... params) {
            List<EventInfo> list = new ArrayList<>();
            try {
                Map<String, String> map = new HashMap<>();
                map.put("pageIndex", pageIndex + "");
                map.put("pageSize", pageSize + "");
                Result res = Util.processResult(HttpRequest.sendPostRequest(
                        URLs.URL_GET_EVENTLIST, map));
                if (res != null) {
                    list.addAll(processData(res.getData().toString()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return list;
        }
    }

    private List<EventInfo> processData(String json) {
        if (TextUtils.isEmpty(json))
            return Collections.EMPTY_LIST;

        try {
            JSONArray tasks = new JSONArray(json);
            if (tasks == null || tasks.length() == 0) {
                return Collections.EMPTY_LIST;
            }
            List<EventInfo> infos = JsonUtil.jsonToList(json,
                    EventInfo.class);
            return infos;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.EMPTY_LIST;
    }
}
