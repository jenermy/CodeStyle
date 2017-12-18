package com.showsoft.duobaoyou;

import com.showsoft.utils.L;
import com.umeng.analytics.MobclickAgent;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

public abstract class BaseActivity extends FragmentActivity {
	public abstract void initUI();
	public abstract void initValue();
//	private BuryingPointReceiver mBuryingPointReceiver;
//	private LocalBroadcastManager localBroadcastManager;
	
	@Override
	public Resources getResources() {
		Resources res = super.getResources();
		Configuration config=new Configuration();
		config.setToDefaults();
		res.updateConfiguration(config,res.getDisplayMetrics() );
		return res;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        ViewGroup mContentView = (ViewGroup)findViewById(Window.ID_ANDROID_CONTENT);
//        int statusBarHeight = getStatusBarHeight(this);
//		View statusView = new View(this);
//		ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//				getStatusBarHeight(this));
//		statusView.setBackgroundColor(getResources().getColor(R.color.blue));
//		mContentView.addView(statusView, params);
    }

	public static int getStatusBarHeight(Context context)
	{
		int result = 0;
		int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0)
		{
			result = context.getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}
	@Override
	protected void onResume() {
		super.onResume();
		//MobclickAgent.onPageStart("BaseActivity");
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		//MobclickAgent.onPageEnd("BaseActivity");
		MobclickAgent.onPause(this);
	}

	//activity关闭的埋点
	@Override
	protected void onStop() {
		super.onStop();
//		L.i("wanlijun","BaseActivity:onStop");
//		LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
//		Intent intent = new Intent(Sp.ACTIVITY_STOP);
//		intent.putExtra(Sp.ACTIVITY_STOP,getComponentName().getClassName());
//		localBroadcastManager.sendBroadcast(intent);
	}

	//activity开启的埋点
	@Override
	protected void onStart() {
		super.onStart();
//		L.i("wanlijun","BaseActivity:onStart");
//		LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
//		Intent intent = new Intent(Sp.ACTIVITY_START);
//		intent.putExtra(Sp.ACTIVITY_START,getComponentName().getClassName());
//		localBroadcastManager.sendBroadcast(intent);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		L.i("wanlijun","BaseActivity:onDestroy");
	}

//	public void registerBuryingPointReceiver(){
//		IntentFilter intentFilter = new IntentFilter();
//		intentFilter.addAction(Sp.ACTIVITY_START);
//		intentFilter.addAction(Sp.ACTIVITY_STOP);
//		intentFilter.addAction(Sp.VIEW_CLICK);
//		mBuryingPointReceiver = new BuryingPointReceiver();
//		localBroadcastManager = LocalBroadcastManager.getInstance(this);
//		localBroadcastManager.registerReceiver(mBuryingPointReceiver,intentFilter);
//	}

//	public void unregisterBuryingPointReceiver(){
//		localBroadcastManager = LocalBroadcastManager.getInstance(this);
//		localBroadcastManager.unregisterReceiver(mBuryingPointReceiver);
//	}

	//控件点击事件
//	@Override
//	public boolean dispatchTouchEvent(MotionEvent ev) {
//		if(ev.getAction() == MotionEvent.ACTION_UP){
//			LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
//			Intent intent = new Intent(Sp.VIEW_CLICK);
//			intent.putExtra(Sp.VIEW_CLICK,ev);
//			localBroadcastManager.sendBroadcast(intent);
//		}
//		return super.dispatchTouchEvent(ev);
//	}

	//操作埋点广播接收器
//	public class BuryingPointReceiver extends BroadcastReceiver {
//
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			// TODO: This method is called when the BroadcastReceiver is receiving
//			String action = intent.getAction();
//			L.i("wanlijun","action="+action);
//			if(action.equals(Sp.ACTIVITY_START)) {
//				String componentName = intent.getStringExtra(Sp.ACTIVITY_START);
//				L.i("wanlijun",componentName+"页面开启了");
//			}else if(action.equals(Sp.ACTIVITY_STOP)) {
//				String componentName = intent.getStringExtra(Sp.ACTIVITY_STOP);
//				L.i("wanlijun",componentName+"页面关闭了");
//			}else if(action.equals(Sp.VIEW_CLICK)) {
//				L.i("wanlijun","控件点击");
//				MotionEvent motionEvent = intent.getParcelableExtra(Sp.VIEW_CLICK);
//				View view = searchClickedView(getWindow().getDecorView(),motionEvent);
//				if(view instanceof TextView){
//					L.i("wanlijun",((TextView) view).getText().toString() + view.getId());
//				}
//			}
//		}
		//递归遍历Activity中所有的view，找出被点击的view
		private View searchClickedView(View view,MotionEvent event){
			View clickedView = null;
			if(isInView(view,event) && view.getVisibility() == View.VISIBLE){
				if(view instanceof ViewGroup){
					ViewGroup viewGroup = (ViewGroup)view;
					for(int i = viewGroup.getChildCount() - 1; i >= 0; i--){
						View view1 = viewGroup.getChildAt(i);
						clickedView = searchClickedView(view1,event);
						if(clickedView != null){
							return clickedView;
						}
					}
				}
				clickedView = view;
			}
			return clickedView;
		}

		//点击的位置是否在View内
		private boolean isInView(View view,MotionEvent event){
			int clickX = (int)event.getRawX();
			int clickY = (int)event.getRawY();
			int[] location = new int[2];
			view.getLocationOnScreen(location);
			int X = location[0];
			int Y = location[1];
			int width = view.getWidth();
			int height = view.getHeight();
			if(clickX < X || clickX > X + width ||clickY < Y || clickY > Y + height){
				return false;
			}
			return true;
		}
//	}
}
