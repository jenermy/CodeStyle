package com.showsoft.fragment;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.showsoft.bean.InvestedYoutbBean;
import com.showsoft.consts.Consts;
import com.showsoft.consts.URLS;
import com.showsoft.duobaoyou.AppApplication;
import com.showsoft.duobaoyou.R;
import com.showsoft.utils.CheckUtils;
import com.showsoft.utils.FormatUtils;
import com.showsoft.utils.L;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class VotedCastFragment extends Fragment {
    private PullToRefreshListView voted_cast_list_lv;
    private View view;
    private ArrayList<InvestedYoutbBean> list;
    private int pageNum = 1;
    private int pageSize = 10;
    private HttpUtils httpUtils;
    AppApplication app;
    private Dialog dialog;
    private InvesAdapter adapter;
    String borrowId = "";
    String beginDate = "";
    String endDate = "";
    String hkStatus = "";
    private View noDataView;
    public VotedCastFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_voted_cast, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        voted_cast_list_lv = (PullToRefreshListView)view.findViewById(R.id.voted_cast_list_lv);
        voted_cast_list_lv.setMode(PullToRefreshBase.Mode.BOTH);
        voted_cast_list_lv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                list.clear();
                getHttpData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                getHttpData();
            }

        });
        dialog = new Dialog(getActivity(), R.style.progressdialog);
        dialog.setContentView(R.layout.fresh_progress);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        if (app == null) {
            app = (AppApplication) getActivity().getApplication();
        }
        if (list == null) {
            list = new ArrayList<InvestedYoutbBean>();
        }
        list.clear();
        adapter = new InvesAdapter(getActivity(),list);
        ListView mListView = voted_cast_list_lv.getRefreshableView();
        mListView.setAdapter(adapter);
        getHttpData();
    }

    /*
	 * cmd 必须 消息类型，此处为： UserTenderLists borrowId 可选 标 ID beginDate 可选 投标时间，
	 * YYYY-MM-DD 格式 endDate 可选 投标时间， YYYY-MM-DD 格式 hkStatus 可选 投资状态： -3 满标， -1
	 * 招标中， 1 已还款， 2 正在还款中 userId 必须 用户 ID phone 必须 手机号 pageNum 必须 查询数据的所在页号， >0
	 * 的整数 pageSize 必须 每页显示数 <= 20 sign
	 */
    private void getHttpData() {
        pageNum = list.size() / pageSize + 1;
        if (httpUtils == null) {
            httpUtils = new HttpUtils();
        }
        String url = URLS.planrecordMd5 + "cmd=Excellentrecord" + "&userId=" + app.persionData.getUser_id();
        url = url + "&phone=" + app.persionData.getPhone() + "&planId=" + borrowId  + "&pageNum=" + pageNum + "&pageSize=" + pageSize + "&beginDate=" + beginDate + "&endDate=" + endDate + "&hkStatus=" + hkStatus  ;
        RequestParams params = new RequestParams();
        params.addBodyParameter("cmd", "Excellentrecord");
        params.addBodyParameter("userId", app.persionData.getUser_id());
        params.addBodyParameter("phone", app.persionData.getPhone());
        params.addBodyParameter("planId", borrowId);
        params.addBodyParameter("pageNum", pageNum + "");
        params.addBodyParameter("pageSize", pageSize + "");
        params.addBodyParameter("beginDate", beginDate);
        params.addBodyParameter("endDate", endDate);
        params.addBodyParameter("hkStatus", hkStatus);
        params.addBodyParameter("token", app.persionData.getToken());
        params.addBodyParameter("sign", CheckUtils.getM5DEndo(url));// 参数非法

        httpUtils.send(HttpMethod.POST, URLS.planrecord, params, new RequestCallBack<String>() {

            private Gson gson;

            @Override
            public void onFailure(HttpException arg0, String arg1) {
                voted_cast_list_lv.onRefreshComplete();
                app.exception.onHttpFailure();
                if (dialog != null) {
                    dialog.dismiss();
                    dialog = null;
                }
            }

            @Override
            public void onSuccess(ResponseInfo<String> arg0) {
                if (dialog != null) {
                    dialog.dismiss();
                    dialog = null;
                }
                try {
                    voted_cast_list_lv.onRefreshComplete();
                    JSONObject backJson = new JSONObject(arg0.result);
                    L.i("wanlijun","getHttpData:"+backJson.toString());
                    if (backJson.getInt("errorCode") == 0) {
                        JSONObject results = backJson.getJSONObject("results");
                        if (gson == null) {
                            gson = new Gson();
                        }
                        List<InvestedYoutbBean> data = gson.fromJson(results.getString("lists"), new TypeToken<List<InvestedYoutbBean>>() {
                        }.getType());
                        list.addAll(data);
                        String total = results.getString("count");
                        Log.i("test", list.size() + "list的SIze");
                        if (list.size() >= Integer.valueOf(total)) {
                            voted_cast_list_lv.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                        } else {
                            voted_cast_list_lv.setMode(PullToRefreshBase.Mode.BOTH);
                        }
                        if(list.size() <= 0){
                            showEmptyView();
                        }else{
                            showListView();
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        app.exception.onReturnDataNotRight(backJson.getInt("errorCode"), voted_cast_list_lv, getActivity());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    app.exception.onDataException();
                }
            }

        });
    }

    public void onRefresh(String hks){
        hkStatus = hks;
        voted_cast_list_lv.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        voted_cast_list_lv.setRefreshing(true);
    }
    public void onRefresh(){
        voted_cast_list_lv.setRefreshing(true);
    }
    public void setDate(String begin,String end){
        beginDate = begin;
        endDate = end;
    }

    class InvesAdapter extends BaseAdapter {
        private FormatUtils formatUtils;
        private Context context;
        private ArrayList<InvestedYoutbBean> beanArrayList;

        public InvesAdapter(Context context, ArrayList<InvestedYoutbBean> beanArrayList) {
            this.context = context;
            this.beanArrayList = beanArrayList;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            InvesHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.lv_investment_item, null);
                holder = new InvesHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (InvesHolder) convertView.getTag();
            }
            if (formatUtils == null) {
                formatUtils = new FormatUtils();
            }
            InvestedYoutbBean bean = beanArrayList.get(position);
            holder.tv_inves_name.setText(bean.getB_title());
            holder.tv_inves_received_money.setText(formatUtils.m2(Double.valueOf(bean.getB_reMoney())));
            holder.tv_inves_unreceived_money.setText(formatUtils.m2(Double.valueOf(bean.getB_noreMoney())));
            holder.tv_inves_money.setText(bean.getB_money() + "元");
            holder.tv_inves_time.setText(FormatUtils.getDateByString(bean.getB_add_time()));
            holder.tv_inves_comment.setText(Consts.getStype(bean.getB_style()));
            holder.tv_inves_date.setText(bean.getB_time_limit() + "天");
            // 1--待匹配，2--收益中，3--已收益
            if("1".equals(bean.getB_hkstatus())){
                holder.tv_inves_type.setBackgroundColor(Color.parseColor("#d0d0d0"));
                holder.tv_inves_type.setText(getString(R.string.matching));
            }else if("2".equals(bean.getB_hkstatus())){
                holder.tv_inves_type.setBackgroundColor(Color.parseColor("#ff4e1f"));
                holder.tv_inves_type.setText(getString(R.string.incoming));
            }else if("3".equals(bean.getB_hkstatus())){
                holder.tv_inves_type.setBackgroundColor(Color.parseColor("#8bb5ef"));
                holder.tv_inves_type.setText(getString(R.string.earned));
            }
            return convertView;
        }

        @Override
        public int getCount() {

            return beanArrayList == null ? 0 : beanArrayList.size();
        }

        @Override
        public Object getItem(int position) {

            return beanArrayList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        private class InvesHolder {
            private View view;
            public TextView tv_inves_name;
            public TextView tv_inves_received_money;
            public TextView tv_inves_unreceived_money;
            public TextView tv_inves_date;
            public TextView tv_inves_money;
            public TextView tv_inves_comment;
            public TextView tv_inves_time;
            public TextView tv_inves_type;

            public InvesHolder(View view) {
                super();
                this.view = view;
                tv_inves_name = (TextView) view.findViewById(R.id.tv_invs_name);
                tv_inves_received_money = (TextView) view.findViewById(R.id.tv_invs_recevied_money);
                tv_inves_unreceived_money = (TextView) view.findViewById(R.id.tv_invs_unrecevied_money);
                tv_inves_date = (TextView) view.findViewById(R.id.tv_invs_date);
                tv_inves_money = (TextView) view.findViewById(R.id.tv_invs_money);
                tv_inves_comment = (TextView) view.findViewById(R.id.tv_invs_comment);
                tv_inves_time = (TextView) view.findViewById(R.id.tv_invs_time);
                tv_inves_type = (TextView) view.findViewById(R.id.tenderTypeTextView);
            }

        }
    }
    private void showEmptyView(){
        voted_cast_list_lv.setVisibility(View.GONE);
        if(noDataView == null){
            ViewStub emptyViewStub = (ViewStub)view.findViewById(R.id.emptyViewStub);
            noDataView = emptyViewStub.inflate();
        }else{
            noDataView.setVisibility(View.VISIBLE);
        }
    }
    private void showListView(){
        voted_cast_list_lv.setVisibility(View.VISIBLE);
        if(noDataView != null){
            noDataView.setVisibility(View.GONE);
        }
    }
}
