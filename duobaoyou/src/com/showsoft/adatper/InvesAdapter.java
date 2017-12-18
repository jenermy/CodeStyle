package com.showsoft.adatper;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.showsoft.bean.InvestedProjectBean;
import com.showsoft.consts.Consts;
import com.showsoft.duobaoyou.R;
import com.showsoft.utils.FormatUtils;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/10/31.
 */

public class InvesAdapter extends BaseAdapter {
    private FormatUtils formatUtils;
    private Context context;
    private ArrayList<InvestedProjectBean> beanArrayList;
    public InvesAdapter(Context context,ArrayList<InvestedProjectBean> beanArrayList){
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
        InvestedProjectBean bean = beanArrayList.get(position);
        holder.tv_inves_name.setText(bean.getTitle());
        holder.tv_inves_received_money.setText(formatUtils.m2(Double.valueOf(bean.getReMoney())));
        holder.tv_inves_unreceived_money.setText(formatUtils.m2(Double.valueOf(bean.getNoreMoney())));
        holder.tv_inves_money.setText(bean.getMoney() + "元");
        holder.tv_inves_time.setText(FormatUtils.getDateByString(bean.getAdd_time()));//
        holder.tv_inves_comment.setText(Consts.getStype(bean.getStyle()));
        holder.tv_inves_date.setText(bean.getIs_day().equals("0") ? bean.getTime_limit() + "个月" : bean.getTime_limit_day() + "天");
        // 标的列表中显示的角标当 "hk_status": 的值为0时读 "fstatus" 招标中 的值，否则以 hk_status回款中
        // 已回款 为准
        if ("0".equals(bean.getHk_status())) {
            if (bean.getFstatus().equals("1")) {
                if (bean.getAccount().equals(bean.getAccount_yes())) {
                    holder.tv_inves_type.setBackgroundColor(Color.parseColor(Consts.getColorTenderType("3")));
                    holder.tv_inves_type.setText(Consts.getTenderType("3"));
                } else {
                    holder.tv_inves_type.setBackgroundColor(Color.parseColor(Consts.getColorTenderType(bean.getFstatus())));
                    holder.tv_inves_type.setText(Consts.getTenderType(bean.getFstatus()));
                }
            } else {
                holder.tv_inves_type.setBackgroundColor(Color.parseColor(Consts.getColorTenderType(bean.getFstatus())));
                holder.tv_inves_type.setText(Consts.getTenderType(bean.getFstatus()));
            }
        } else {
            holder.tv_inves_type.setBackgroundColor(Color.parseColor(Consts.getColorHkType(bean.getHk_status())));
            holder.tv_inves_type.setText(Consts.getTender2Type(bean.getHk_status()));
        }

        return convertView;
    }

    @Override
    public int getCount() {

        return beanArrayList == null ? 0 : beanArrayList.size();
    }

    @Override
    public Object getItem(int position) {

        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
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
