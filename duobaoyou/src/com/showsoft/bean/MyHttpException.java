package com.showsoft.bean;

import com.showsoft.consts.Sp;
import com.showsoft.duobaoyou.AppApplication;
import com.showsoft.duobaoyou.LoginActivity;
import com.showsoft.event.LogoutEvent;
import com.showsoft.utils.SPUtils;
import com.showsoft.view.PopPromptDialog;
import com.showsoft.view.PopPromptDialog.OnConfirmClickListener;

import de.greenrobot.event.EventBus;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;

public class MyHttpException extends Exception {
	
	private Context context;

	public MyHttpException(Context context) {
		super();
		this.context = context;
	}

	public MyHttpException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public MyHttpException(String detailMessage) {
		super(detailMessage);
	}

	public MyHttpException(Throwable throwable) {
		super(throwable);
	}
	
	public void onHttpFailure(){
		//处理请求异常
		
	}
	PopPromptDialog popDialog;
	public void onReturnDataNotRight(int code,View view,final Activity contex){
		//处理返回码不成功
		if(code == 24){
			if(popDialog == null)
			{
				popDialog = new PopPromptDialog();
				popDialog.showPop(contex,view,"退出","重新登录","您的账号已在别处登录,您将被强制下线\n如有疑问请致电多宝优官方客服\n400-020-0769");
			}
			//popDialog.showPop(contex,view,"退出","重新登录","您的账号已在别处登录,强制下线\n如有疑问请致电多宝贷官方客户\n400-020-0769");
			popDialog.setOnConfirmClickListener(new OnConfirmClickListener() {
				@Override
				public void sure() {
					removeStores();
					Intent intent = new Intent(contex,LoginActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					contex.startActivity(intent);
					popDialog = null;
				}
				
				@Override
				public void onDismiss() {
					logout();
					popDialog = null;
				}
				
			});
		}else if(code == 6){
			if(popDisableDialog == null){
				popDisableDialog = new PopPromptDialog();
				popDisableDialog.showPop(contex, view,"取消","登录","请重新登录多宝优理财");
			}
			popDisableDialog.setOnConfirmClickListener(new OnConfirmClickListener() {
				@Override
				public void sure() {
					removeStores();
					popDisableDialog.dismiss();
					Intent intent = new Intent(contex,LoginActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					contex.startActivity(intent);
					popDisableDialog = null;
				}
				
				@Override
				public void onDismiss() {
					logout();
					popDisableDialog = null;
				}
			});
		}
	}
	PopPromptDialog popDisableDialog;
	
	private void logout() {
		AppApplication app = (AppApplication) context;
		app.persionData = null;
		app.balanceData = null;
		EventBus.getDefault().post(new LogoutEvent());
		//处理缓存
		SPUtils.remove(context, Sp.user_id);
		SPUtils.remove(context, Sp.user_islock);
		SPUtils.remove(context, Sp.user_name);
		SPUtils.remove(context, Sp.user_phone);
		SPUtils.remove(context, Sp.user_pwd);
		SPUtils.remove(context, Sp.user_realname);
		SPUtils.remove(context, Sp.user_realstatus);
		SPUtils.remove(context, Sp.user_sign);
		SPUtils.remove(context, Sp.user_token);
		SPUtils.remove(context, Sp.user_invite);
		SPUtils.remove(context, Sp.user_self);
	}
	
	private void removeStores(){
		AppApplication app = (AppApplication) context;
		app.persionData = null;
		app.balanceData = null;
		SPUtils.remove(context, Sp.user_id);
		SPUtils.remove(context, Sp.user_islock);
		SPUtils.remove(context, Sp.user_name);
		SPUtils.remove(context, Sp.user_phone);
		SPUtils.remove(context, Sp.user_pwd);
		SPUtils.remove(context, Sp.user_realname);
		SPUtils.remove(context, Sp.user_realstatus);
		SPUtils.remove(context, Sp.user_sign);
		SPUtils.remove(context, Sp.user_token);
		SPUtils.remove(context, Sp.user_invite);
		SPUtils.remove(context, Sp.user_self);
	}
	
	public void onDataException(){
		//处理数据异常
	}
	
}
