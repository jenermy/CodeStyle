package com.showsoft.view;


import com.showsoft.duobaoyou.R;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

public class PopPromptDialog {
	PopupWindow mPopupWindow;

	/**
	 * 弹出框
	 * 
	 * @param activity
	 * @param v
	 */
	public void showPop(final Activity activity, View v,String cancel ,String sure,String hint) {
		LayoutInflater inflater = LayoutInflater.from(activity);
		View view = inflater.inflate(R.layout.pop_prompt_dialog, null);
		mPopupWindow = new PopupWindow(view, android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT);
		mPopupWindow.setAnimationStyle(R.style.popwin_anim_style);
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
		mPopupWindow.setFocusable(true);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
		mPopupWindow.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				if(onConfirmClickListener != null){
					//onConfirmClickListener.onDismiss();
				}
			}
		});
		TextView contentTextView = (TextView) view.findViewById(R.id.contentTextView);
		if(hint.startsWith("您的账号已在别处登录")){
			contentTextView.setTextSize(14);
		}
		contentTextView.setText(hint);
		TextView cancelTextView = (TextView)view.findViewById(R.id.cancelTextView);
		
		cancelTextView.setText(cancel);
		cancelTextView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				onConfirmClickListener.onDismiss();
				mPopupWindow.dismiss();
			}
		});
		TextView sureTextView = (TextView)view.findViewById(R.id.sureTextView);
		sureTextView.setText(sure);
		sureTextView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				onConfirmClickListener.sure();
				mPopupWindow.dismiss();
			}
		});
		
		if(hint.startsWith("请重新登录多宝优理财")){
			cancelTextView.setVisibility(View.GONE);
			sureTextView.setTextColor(Color.parseColor("#333333"));
		}
		
	}

	public  void dismiss(){
		if(mPopupWindow != null){
			mPopupWindow.dismiss();
		}
	}
	
	public interface OnConfirmClickListener {
		void sure();
		void onDismiss();
	}

	OnConfirmClickListener onConfirmClickListener;

	public void setOnConfirmClickListener(OnConfirmClickListener onConfirmClickListener) {
		this.onConfirmClickListener = onConfirmClickListener;
	}
}
