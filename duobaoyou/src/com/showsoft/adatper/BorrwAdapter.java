package com.showsoft.adatper;

import java.util.List;

import com.showsoft.consts.Consts;
import com.showsoft.data.BorrwData;
import com.showsoft.duobaoyou.R;
import com.showsoft.utils.FormatUtils;
import com.showsoft.view.ColorfulRingProgressView;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONObject;


public class BorrwAdapter extends BaseAdapter {
	private static final String TAG = "BorrwAdapter";
	LayoutInflater inflater;
	List<BorrwData> borrwDatas;
	FormatUtils formatUtils = new FormatUtils();
	Context context;
	public BorrwAdapter(Context context, List<BorrwData> borrwDatas) {
		inflater = LayoutInflater.from(context);
		this.borrwDatas = borrwDatas;
		this.context = context;
	}

	@Override
	public int getCount() {
		return borrwDatas.size();
	}

	@Override
	public Object getItem(int arg0) {
		return borrwDatas.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
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


			final BorrwData borrwData = borrwDatas.get(arg0);
			if(borrwData.getFtype()=="200")
			{
				viewHolder.addTextView.setVisibility(View.GONE);
				viewHolder.addDetailsTextView.setVisibility(View.GONE);
				viewHolder.typeTextView.setText("体验标");
				viewHolder.styleTextView.setText("到期返还收益");

				viewHolder.totalTextView.setText("体验金投资:"+borrwData.getAccount());
				if(borrwData.getIs_new().equals("0")) {

					viewHolder.typeTextView.setBackgroundColor(Color.parseColor("#00CD66"));
					viewHolder.progressView.setFgColorEnd(0xff176ce0);
					viewHolder.progressView.setFgColorStart(0xff176ce0);
					viewHolder.percentTextView.setTextColor(0xff176ce0);
					viewHolder.percentTextView.setText("未体验");
					viewHolder.progressView.setPercent(0);
				}
				else
				{
					viewHolder.typeTextView.setBackgroundColor(Color.parseColor("#8a8a8a"));
					viewHolder.progressView.setFgColorEnd(0xff8a8a8a);
					viewHolder.progressView.setFgColorStart(0xff8a8a8a);
					viewHolder.percentTextView.setTextColor(0xff8a8a8a);
					viewHolder.percentTextView.setText("已体验");
					viewHolder.progressView.setPercent(100);
				}

				viewHolder.borrwTitleTextView.setText(borrwData.getTitle());
				viewHolder.aprTextView.setText(borrwData.getApr());

				viewHolder.dayTextView.setText(borrwData.getTime_limit_day());
				viewHolder.unitTextView.setText("天");

				viewHolder.typeTextView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						{
							if (onNewClistener != null) {
								onNewClistener.click3();
							}
						}

					}
				});






			}
			else {


				viewHolder.addTextView.setVisibility(View.GONE);
				viewHolder.addDetailsTextView.setVisibility(View.GONE);
				//final BorrwData borrwData = borrwDatas.get(arg0);
				viewHolder.borrwTitleTextView.setText(borrwData.getTitle());
				viewHolder.aprTextView.setText(borrwData.getApr());
				if (borrwData.getIs_day().equals("1")) {
					viewHolder.dayTextView.setText(borrwData.getTime_limit_day());
					viewHolder.unitTextView.setText("天");
					//普通标的活动标，天标也显示加息
					//活动标加息
					if(borrwData.getShow_activity() != null && !borrwData.getShow_activity().toString().equals("")){
						JSONObject jsonObject = new JSONObject(borrwData.getShow_activity().toString());
						if(jsonObject.has("lv") && !jsonObject.getString("lv").equals("0") && !jsonObject.getString("lv").equals("")){
							String jiaxi = String.format("%.2f",Double.valueOf(jsonObject.getString("lv")));
							viewHolder.addDetailsTextView.setVisibility(View.VISIBLE);
							viewHolder.addDetailsTextView.setText("+" + jiaxi + "%");
							viewHolder.addTextView.setVisibility(View.VISIBLE);
							viewHolder.addTextView.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View arg0) {
									if (onNewClistener != null) {
										onNewClistener.click2();
									}
								}
							});
						}else{
							viewHolder.addTextView.setVisibility(View.GONE);
							viewHolder.addDetailsTextView.setVisibility(View.GONE);
						}
					}else {
						viewHolder.addTextView.setVisibility(View.GONE);
						viewHolder.addDetailsTextView.setVisibility(View.GONE);
					}
				} else {
					viewHolder.dayTextView.setText(borrwData.getTime_limit());
					viewHolder.unitTextView.setText("个月");

					//20170321 modify by jsontec 加息
					{

						if (borrwData.getRewardApr() != null) {
							if (!borrwData.getRewardApr().equals("0")) {
								viewHolder.addTextView.setVisibility(View.VISIBLE);
								viewHolder.addTextView.setOnClickListener(new OnClickListener() {
									@Override
									public void onClick(View arg0) {
										if (onNewClistener != null) {
											onNewClistener.click2();
										}
									}
								});


								viewHolder.addDetailsTextView.setVisibility(View.VISIBLE);
								viewHolder.addDetailsTextView.setText("+" + borrwData.getRewardApr() + "%");
							} else {
								//活动标加息
								if(borrwData.getShow_activity() != null && !borrwData.getShow_activity().toString().equals("")){
									JSONObject jsonObject = new JSONObject(borrwData.getShow_activity().toString());
									if(jsonObject.has("lv") && !jsonObject.getString("lv").equals("0") && !jsonObject.getString("lv").equals("")){
										String jiaxi = String.format("%.2f",Double.valueOf(jsonObject.getString("lv")));
										viewHolder.addDetailsTextView.setVisibility(View.VISIBLE);
										viewHolder.addDetailsTextView.setText("+" + jiaxi + "%");
										viewHolder.addTextView.setVisibility(View.VISIBLE);
										viewHolder.addTextView.setOnClickListener(new OnClickListener() {
											@Override
											public void onClick(View arg0) {
												if (onNewClistener != null) {
													onNewClistener.click2();
												}
											}
										});
									}else{
										viewHolder.addTextView.setVisibility(View.GONE);
										viewHolder.addDetailsTextView.setVisibility(View.GONE);
									}
								}else {
									viewHolder.addTextView.setVisibility(View.GONE);
									viewHolder.addDetailsTextView.setVisibility(View.GONE);
								}
							}
						}else{
							//活动标加息
							if(borrwData.getShow_activity() != null && !borrwData.getShow_activity().toString().equals("")){
								JSONObject jsonObject = new JSONObject(borrwData.getShow_activity().toString());
								if(jsonObject.has("lv") && !jsonObject.getString("lv").equals("0") && !jsonObject.getString("lv").equals("")){
									String jiaxi = String.format("%.2f",Double.valueOf(jsonObject.getString("lv")));
									viewHolder.addDetailsTextView.setVisibility(View.VISIBLE);
									viewHolder.addDetailsTextView.setText("+" + jiaxi + "%");
									viewHolder.addTextView.setVisibility(View.VISIBLE);
									viewHolder.addTextView.setOnClickListener(new OnClickListener() {
										@Override
										public void onClick(View arg0) {
											if (onNewClistener != null) {
												onNewClistener.click2();
											}
										}
									});
								}else{
									viewHolder.addTextView.setVisibility(View.GONE);
									viewHolder.addDetailsTextView.setVisibility(View.GONE);
								}
							}else {
								viewHolder.addTextView.setVisibility(View.GONE);
								viewHolder.addDetailsTextView.setVisibility(View.GONE);
							}
						}
					}

				/*
				if (TimeProcess.isBigerNowTime("2016-11-30 23:59:59")) {
					if (borrwData.getStyle().equals("0")) {
						viewHolder.addTextView.setVisibility(View.VISIBLE);
						viewHolder.addDetailsTextView.setVisibility(View.VISIBLE);
						if (borrwData.getTime_limit().equals("3")) {
							viewHolder.addDetailsTextView.setText("+0.3%");
						} else if (borrwData.getTime_limit().equals("6")) {
							viewHolder.addDetailsTextView.setText("+0.5%");
						} else if (borrwData.getTime_limit().equals("12")) {
							viewHolder.addDetailsTextView.setText("+0.7%");
						} else {
							viewHolder.addTextView.setVisibility(View.GONE);
							viewHolder.addDetailsTextView.setVisibility(View.GONE);
						}
					} else {
						viewHolder.addTextView.setVisibility(View.GONE);
						viewHolder.addDetailsTextView.setVisibility(View.GONE);
					}
				}*/
				}
				// 投资列表页面的投资进度计算的时候保留小数点后两位，和多宝贷的PC端保持一致，当不能整除的情况，第三位统一向第二位进一位
				double percent = Double.valueOf(borrwData.getAccount_yes()) * 100 / Double.valueOf(borrwData.getAccount());
				viewHolder.percentTextView.setText(formatUtils.m2(percent) + "%");
				viewHolder.progressView.setPercent((float) percent);
				viewHolder.styleTextView.setText(Consts.getStype(borrwData.getStyle()));
				double account_no = Double.valueOf(borrwData.getAccount()) - Double.valueOf(borrwData.getAccount_yes());
				String total = "共" + borrwData.getAccount() + "元/剩" + formatUtils.m2m(account_no) + "元";
				viewHolder.totalTextView.setText(total);
				if (borrwData.getNk_status().equals("0")) {
					if (borrwData.getFstatus().equals("1")) {
						if (borrwData.getIs_new().equals("1")) {
							viewHolder.typeTextView.setText("新手体验");
						} else {
							viewHolder.typeTextView.setText("可投资");
						}
						viewHolder.typeTextView.setBackgroundColor(Color.parseColor("#fd9525"));
						viewHolder.progressView.setFgColorEnd(0xff176ce0);
						viewHolder.progressView.setFgColorStart(0xff176ce0);
						viewHolder.percentTextView.setTextColor(0xff176ce0);
					} else {
						viewHolder.typeTextView.setText("已满标");
						viewHolder.typeTextView.setBackgroundColor(Color.parseColor("#8a8a8a"));
						viewHolder.progressView.setFgColorEnd(0xff8a8a8a);
						viewHolder.progressView.setFgColorStart(0xff8a8a8a);
						viewHolder.percentTextView.setTextColor(0xff8a8a8a);
					}
					if (Double.valueOf(borrwData.getAccount()) <= Double.valueOf(borrwData.getAccount_yes())) {
						viewHolder.typeTextView.setText("已满标");
						viewHolder.typeTextView.setBackgroundColor(Color.parseColor("#8a8a8a"));
						viewHolder.progressView.setFgColorEnd(0xff8a8a8a);
						viewHolder.progressView.setFgColorStart(0xff8a8a8a);
						viewHolder.percentTextView.setTextColor(0xff8a8a8a);
					}
				} else {
					if (borrwData.getNk_status().equals("1")) {
						viewHolder.typeTextView.setText("已还款");
					} else {
						viewHolder.typeTextView.setText("还款中");
					}
					viewHolder.typeTextView.setBackgroundColor(Color.parseColor("#8a8a8a"));
					viewHolder.progressView.setFgColorEnd(0xff8a8a8a);
					viewHolder.progressView.setFgColorStart(0xff8a8a8a);
					viewHolder.percentTextView.setTextColor(0xff8a8a8a);
				}


				viewHolder.typeTextView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						if (borrwData.getNk_status().equals("0")) {
							if (borrwData.getFstatus().equals("1")) {
								if (borrwData.getIs_new().equals("1")) {
									if (onNewClistener != null) {
										onNewClistener.click();
									}
								}
							}
						}
					}
				});

			}


			viewHolder.markTextView.setText(Consts.getMarkftype(borrwData.getFtype()));
			viewHolder.markTextView.setBackgroundResource(Consts.getBackgroundftype(borrwData.getFtype()));
//			showAnimation(context, arg1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return arg1;
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
	OnNewClistener onNewClistener;
	public void setOnNewClistener(OnNewClistener onNewClistener) {
		this.onNewClistener = onNewClistener;
	}
	public interface OnNewClistener {
		void click();
		void click2();
		void click3();
	}



}
