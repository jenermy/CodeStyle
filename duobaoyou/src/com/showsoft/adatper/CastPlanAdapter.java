package com.showsoft.adatper;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.showsoft.consts.Consts;
import com.showsoft.data.CastPlanData;
import com.showsoft.duobaoyou.R;
import com.showsoft.utils.FormatUtils;
import com.showsoft.view.ColorfulRingProgressView;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/10/31.
 */

public class CastPlanAdapter extends BaseAdapter {
    private static final String TAG = "CastPlanAdapter";
    LayoutInflater inflater;
    ArrayList<CastPlanData> castPlanDatas;
    FormatUtils formatUtils = new FormatUtils();
    Context context;
    public CastPlanAdapter(Context context,ArrayList<CastPlanData> castPlanDatas){
        inflater = LayoutInflater.from(context);
        this.castPlanDatas = castPlanDatas;
        this.context = context;
    }

    @Override
    public View getView(int i, View arg1, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (arg1 == null) {
            viewHolder = new ViewHolder();
            arg1 = inflater.inflate(R.layout.item_borrw, null);
            viewHolder.borrwTitleTextView = (TextView) arg1.findViewById(R.id.borrwTitleTextView);
            viewHolder.aprTextView = (TextView) arg1.findViewById(R.id.aprTextView);
            viewHolder.dayTextView = (TextView) arg1.findViewById(R.id.dayTextView);
            viewHolder.unitTextView = (TextView) arg1.findViewById(R.id.unitTextView);
            viewHolder.percentTextView = (TextView) arg1.findViewById(R.id.percentTextView);
            viewHolder.styleTextView = (TextView) arg1.findViewById(R.id.styleTextView);
            viewHolder.totalTextView = (TextView) arg1.findViewById(R.id.totalTextView);
            viewHolder.typeTextView = (TextView) arg1.findViewById(R.id.typeTextView);
            viewHolder.markTextView = (TextView) arg1.findViewById(R.id.markTextView);
            viewHolder.addTextView = (TextView) arg1.findViewById(R.id.addTextView);
            viewHolder.addDetailsTextView = (TextView) arg1.findViewById(R.id.addDetailsTextView);
            viewHolder.progressView = (ColorfulRingProgressView) arg1.findViewById(R.id.progressView);
            arg1.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) arg1.getTag();
        }
        try {
            final CastPlanData borrwData = castPlanDatas.get(i);
            viewHolder.addTextView.setVisibility(View.GONE);
            viewHolder.addDetailsTextView.setVisibility(View.GONE);
            viewHolder.borrwTitleTextView.setText(borrwData.getB_title());
            viewHolder.aprTextView.setText(borrwData.getB_apr());
            viewHolder.dayTextView.setText(borrwData.getB_time_limit());
            //优投计划只有天标，没有月标，目前没有新手，加息，奖励等活动
            viewHolder.unitTextView.setText("天");
            // 投资列表页面的投资进度计算的时候保留小数点后两位，和多宝贷的PC端保持一致，当不能整除的情况，第三位统一向第二位进一位
            double percent = Double.valueOf(borrwData.getB_account_yes()) * 100 / Double.valueOf(borrwData.getB_account());
            viewHolder.percentTextView.setText(formatUtils.m2(percent) + "%");
            viewHolder.progressView.setPercent((float) percent);
            viewHolder.styleTextView.setText(Consts.getStype(borrwData.getB_style()));
            double account_no = Double.valueOf(borrwData.getB_account()) - Double.valueOf(borrwData.getB_account_yes());
            String total = "共" + borrwData.getB_account() + "元/剩" + formatUtils.m2m(account_no) + "元";
            viewHolder.totalTextView.setText(total);
            if (borrwData.getB_hk_status().equals("0")) {
                if (borrwData.getB_fstatus().equals("1")) {
                    viewHolder.typeTextView.setText("可加入");
                    viewHolder.typeTextView.setBackgroundColor(Color.parseColor("#fd9525"));
                    viewHolder.progressView.setFgColorEnd(0xff176ce0);
                    viewHolder.progressView.setFgColorStart(0xff176ce0);
                    viewHolder.percentTextView.setTextColor(0xff176ce0);
                } else {
                    viewHolder.typeTextView.setText("已满");
                    viewHolder.typeTextView.setBackgroundColor(Color.parseColor("#8a8a8a"));
                    viewHolder.progressView.setFgColorEnd(0xff8a8a8a);
                    viewHolder.progressView.setFgColorStart(0xff8a8a8a);
                    viewHolder.percentTextView.setTextColor(0xff8a8a8a);
                }
                if (Double.valueOf(borrwData.getB_account()) <= Double.valueOf(borrwData.getB_account_yes())) {
                    viewHolder.typeTextView.setText("已满");
                    viewHolder.typeTextView.setBackgroundColor(Color.parseColor("#8a8a8a"));
                    viewHolder.progressView.setFgColorEnd(0xff8a8a8a);
                    viewHolder.progressView.setFgColorStart(0xff8a8a8a);
                    viewHolder.percentTextView.setTextColor(0xff8a8a8a);
                }
            } else {
                viewHolder.typeTextView.setText("已满");
                viewHolder.typeTextView.setBackgroundColor(Color.parseColor("#8a8a8a"));
                viewHolder.progressView.setFgColorEnd(0xff8a8a8a);
                viewHolder.progressView.setFgColorStart(0xff8a8a8a);
                viewHolder.percentTextView.setTextColor(0xff8a8a8a);
            }
            viewHolder.markTextView.setText(Consts.getMarkftype(borrwData.getB_ftype()));
            viewHolder.markTextView.setBackgroundResource(Consts.getBackgroundftype(borrwData.getB_ftype()));
//            showAnimation(context, arg1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return arg1;
    }


    @Override
    public int getCount() {
        return castPlanDatas.size();
    }

    @Override
    public Object getItem(int i) {
        return castPlanDatas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    class ViewHolder {
        TextView borrwTitleTextView;
        TextView aprTextView;
        TextView dayTextView;
        TextView unitTextView;
        TextView percentTextView;
        TextView styleTextView;
        TextView totalTextView;
        TextView typeTextView;
        TextView markTextView;
        TextView addTextView;
        TextView addDetailsTextView;
        ColorfulRingProgressView progressView;
    }
    private void showAnimation(Context context, View view) {
        Animation animation = (Animation) AnimationUtils.loadAnimation(context, R.anim.translate_out);
		/*
		 * LayoutAnimationController lac = new
		 * LayoutAnimationController(animation); lac.setDelay(0.5f); // 设置动画间隔时间
		 * lac.setOrder(LayoutAnimationController.ORDER_NORMAL); // 设置列表的显示顺序
		 */ view.setAnimation(animation);
    }
}
