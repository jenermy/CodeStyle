package com.showsoft.duobaoyou;

import java.util.ArrayList;
import java.util.List;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.showsoft.bean.MyHttpException;
import com.showsoft.consts.Sp;
import com.showsoft.data.BalanceData;
import com.showsoft.data.PersionData;
import com.showsoft.utils.AuthImageDownloader;
import com.showsoft.utils.SPUtils;
import com.tencent.smtt.sdk.QbSdk;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

public class AppApplication extends Application {

	/** 个人账户数据 **/
	public PersionData persionData;
	/** 个人账户余额数据 **/
	public BalanceData balanceData;
	public boolean needCheckGesture = false;
	public boolean isAppStart = false;
	public boolean hasInvested = false;
	public boolean  Gesturepwd=false;
	//未登录用户是否显示APP弹窗，加这个变量是因为当已登录用户退出登录时，首页会重新加载一遍，会再一次弹窗
	//已登录用户退出登录时不显示APP弹窗
	public boolean mIsShowUnloginDialog = true;

	public List<Activity> activitiesStack;
	public MyHttpException exception;
	private long backTime = 0;
	private boolean isDownload; //是否正在下载apk最新版本
	private boolean isFirstUpdateDialog ;//是否第一次弹更新对话框
	@Override
	public void onCreate() {
		super.onCreate();
		isDownload = false;
		isFirstUpdateDialog = true;
		exception = new MyHttpException(getApplicationContext());
		MobclickAgent.openActivityDurationTrack(false);
		ImageLoader imageLoader = ImageLoader.getInstance();
		imageLoader.init(new ImageLoaderConfiguration.Builder(this).imageDownloader(new AuthImageDownloader(getApplicationContext())).build());

		registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {

			@Override
			public void onActivityStopped(Activity arg0) {
				needCheckGesture = isApplicationBroughtToBackground(getApplicationContext());
				if (needCheckGesture) {
					backTime = System.currentTimeMillis();
				}
			}

			@Override
			public void onActivityStarted(Activity arg0) {
				if (needCheckGesture) {
					Log.i("test", System.currentTimeMillis() + "切进来");
					if (persionData != null && !TextUtils.isEmpty((String) SPUtils.get(getApplicationContext(), Sp.gesture_lock + persionData.getUser_id(), ""))) {
						if (System.currentTimeMillis() - backTime >= 5 * 60 * 1000) {
							Intent intent = new Intent(getApplicationContext(), CheckGestureActivity.class);
							intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(intent);
						}
					}
				}
			}

			@Override
			public void onActivitySaveInstanceState(Activity arg0, Bundle arg1) {

			}

			@Override
			public void onActivityResumed(Activity arg0) {

			}

			@Override
			public void onActivityPaused(Activity arg0) {

			}

			@Override
			public void onActivityDestroyed(Activity arg0) {
				activitiesStack.remove(arg0);
			}

			@Override
			public void onActivityCreated(Activity arg0, Bundle arg1) {
				if (activitiesStack == null) {
					activitiesStack = new ArrayList<Activity>();
				}
				activitiesStack.add(arg0);
			}
		});

		QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {

			@Override
			public void onViewInitFinished(boolean arg0) {
				// TODO Auto-generated method stub
				//x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
				Log.d("app", " onViewInitFinished is " + arg0);
			}

			@Override
			public void onCoreInitFinished() {
				// TODO Auto-generated method stub
			}
		};
		//x5内核初始化接口
		QbSdk.initX5Environment(getApplicationContext(),  cb);
	}
	public static boolean isApplicationBroughtToBackground(final Context context) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> tasks = am.getRunningTasks(1);
		if (!tasks.isEmpty()) {
			ComponentName topActivity = tasks.get(0).topActivity;
			if (!topActivity.getPackageName().equals(context.getPackageName())) {
				return true;// 本地不能保存用户账户信息
			}
		}
		return false;
	}

	public boolean isDownload(){
		return isDownload;
	}

	public void setDownload(boolean isDownload){
		this.isDownload = isDownload;
	}

	public boolean isFirstUpdateDialog() {
		return isFirstUpdateDialog;
	}

	public void setFirstUpdateDialog(boolean firstUpdateDialog) {
		isFirstUpdateDialog = firstUpdateDialog;
	}
}
