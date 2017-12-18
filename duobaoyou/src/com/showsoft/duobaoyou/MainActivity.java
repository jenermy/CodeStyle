package com.showsoft.duobaoyou;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.igexin.sdk.PushManager;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.showsoft.consts.Consts;
import com.showsoft.consts.MyAppApiConfig;
import com.showsoft.consts.Sp;
import com.showsoft.consts.URLS;
import com.showsoft.data.PersionData;
import com.showsoft.event.BackMainEvent;
import com.showsoft.event.BackToInvestEvent;
import com.showsoft.event.ExitEvent;
import com.showsoft.event.LoginEvent;
import com.showsoft.event.LogoutEvent;
import com.showsoft.event.NotificationEvent;
import com.showsoft.event.RealNameEvent;
import com.showsoft.event.ReceivePushEvent;
import com.showsoft.event.ScrollEvent;
import com.showsoft.fragment.BorrwFragment;
import com.showsoft.fragment.HomeFragment;
import com.showsoft.fragment.MeFragment;
import com.showsoft.fragment.MoreFragment;
import com.showsoft.utils.AESUtils;
import com.showsoft.utils.CheckUtils;
import com.showsoft.utils.GetLocation;
import com.showsoft.utils.GetSystem;
import com.showsoft.utils.L;
import com.showsoft.utils.SPUtils;
import com.showsoft.utils.T;
import com.showsoft.utils.ToastErrorUtils;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import android.app.ActivityManager;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import de.greenrobot.event.EventBus;

public class MainActivity extends BaseActivity implements OnClickListener {
	private static final String TAG = "MainActivity";
	TextView homeTextView, taskTextView, meTextView, moreTextView;
	ViewPager channelViewPager;
	Fragment[] fragments = new Fragment[4];
	AppApplication app;
	public static boolean isShouYe = true;
	boolean isHome = true;
	public static boolean isConnect;
	private IWXAPI api;
	private Dialog dialog;
	private Dialog mUnloginPopupDialog;
	private Dialog mUpdateApkDialog;
	private  WebView wb;
	private int usePopup=0;//弹窗 jsontec modify on 20170509
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// 去掉Fragment缓存，防止崩溃后覆盖，或点击不切换现象
		// super.onSaveInstanceState(outState);
	}

	@Override
	protected void onCreate(Bundle arg0) {
		L.i("wanlijun","MainActivity:onCreate");
		super.onCreate(arg0);
		setContentView(R.layout.activity_main);
		MyAppApiConfig.Experiencehome = 0;
		initUI();
		initValue();
		receiver = new NetworkChangedReceiver();
		registerReceiver(receiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
		getPhoneInfomation();// 每次开启都要上传
		// 注册微信
		regToWx();
		// 个推
		if ((Boolean) SPUtils.get(MainActivity.this, Sp.receive_push, true)) {
			PushManager.getInstance().initialize(this.getApplicationContext());
		}
		if (app == null) {
			app = (AppApplication) getApplication();
		}
		app.isAppStart = true;
		Intent intent = getIntent();
		if (!TextUtils.isEmpty(intent.getStringExtra("type"))) {
			String type = intent.getStringExtra("type");
			if (WebActivity.class == NotificationEvent.getType(type)) {
				String links = intent.getStringExtra("links");
				Intent intent2 = new Intent(MainActivity.this, WebActivity.class);
				intent2.putExtra("url", links);
				startActivity(intent2);
				return;
			} else {
				Intent intent2 = new Intent(MainActivity.this, NotificationEvent.getType(type));
				startActivity(intent2);
			}
		}

	}

	//transsion-begin,add pop-up prompts on the first page,wanlijun,20170718
	//首页弹窗提示--2.2.18 弹窗提示/notice/info
	//The user has logged in,then display the webpage that the background returned
	private void getPopupPrompt(){
		if (app.persionData == null) {
			return;
		}
		HttpUtils httpUtils = new HttpUtils(60000);
		RequestParams params = new RequestParams();
		params.addBodyParameter("cmd","Notice");
		params.addBodyParameter("userId",app.persionData.getUser_id());
		httpUtils.send(HttpMethod.POST, URLS.noticeInfo, params, new RequestCallBack<String>() {
			@Override
			public void onFailure(HttpException e, String s) {
				ToastErrorUtils.Show(MainActivity.this, e, s);
				L.i("wanlijun","liyifeng1:onFailure");
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				try {
					L.i("wanlijun","liyifeng1:"+responseInfo.result.toString());
					JSONObject jsonObject = new JSONObject(responseInfo.result);
					L.i("wanlijun","liyifeng1:"+jsonObject.toString());
					L.i("wanlijun",app.mIsShowUnloginDialog+"");
					if(jsonObject.getInt("errorCode") == 0){
						L.i("wanlijun","currentPage="+currentPage);
							String style = jsonObject.getJSONObject("results").getString("style");
							String ftype = jsonObject.getJSONObject("results").getString("ftype");
							if (style.equals("1") && isHome && app.mIsShowUnloginDialog) {
								String webUrl = jsonObject.getJSONObject("results").getString("webUrl");
								Intent intent2 = new Intent(MainActivity.this, WebViewActivity.class);
								//intent2.putExtra("weburl", "https://xiao.duobaodai.com/callback/popshow/42021");
								intent2.putExtra("weburl", webUrl);
								startActivity(intent2);
							} else if (style.equals("2")) {
								if (jsonObject.getJSONObject("results") != null
										&& app.mIsShowUnloginDialog && isHome) {
										createSessionDialog(jsonObject);
								}
							}
					}
				}catch (JSONException e){
					e.printStackTrace();
					L.i("wanlijun","liyifeng:"+e.toString());
				}
			}
		});
	}

	//The user has not logged in,then display the dialog
	private void getPopupDialog(){
		HttpUtils httpUtils = new HttpUtils(60000);
		RequestParams params = new RequestParams();
		params.addBodyParameter("cmd","Notice");
		params.addBodyParameter("userId","");
		httpUtils.send(HttpMethod.POST, URLS.noticeInfo, params, new RequestCallBack<String>() {
			@Override
			public void onFailure(HttpException e, String s) {
				ToastErrorUtils.Show(MainActivity.this, e, s);
				L.i("wanlijun","liyifeng1:onFailure");
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				try {
					JSONObject jsonObject = new JSONObject(responseInfo.result);
					L.i("wanlijun","liyifeng1:"+jsonObject.toString());
					L.i("wanlijun",app.mIsShowUnloginDialog+"");
					if(jsonObject.getInt("errorCode") == 0){
							String style = jsonObject.getJSONObject("results").getString("style");
						String ftype = jsonObject.getJSONObject("results").getString("ftype");
							if (style.equals("1") && isHome && app.mIsShowUnloginDialog) {
								String webUrl = jsonObject.getJSONObject("results").getString("webUrl");
								Intent intent2 = new Intent(MainActivity.this, WebViewActivity.class);
								//intent2.putExtra("weburl", "https://xiao.duobaodai.com/callback/popshow/42021");
								intent2.putExtra("weburl", webUrl);
								startActivity(intent2);
							} else if (style.equals("2")) {
								if (jsonObject.getJSONObject("results") != null
										&& app.mIsShowUnloginDialog && isHome) {
										createSessionDialog(jsonObject);
								}
							}
					}
				}catch (JSONException e){
					e.printStackTrace();
					L.i("wanlijun","liyifeng:"+e.toString());
				}
			}
		});
	}

	private void createSessionDialog(JSONObject jsonObject){
		mUnloginPopupDialog = new Dialog(MainActivity.this, R.style.mydialog);
		View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.unlogin_notice_dialog,null);
		TextView mUnloginNoticeTitle = (TextView)view.findViewById(R.id.unlogin_notice_title);
		TextView mUnloginNoticeContent = (TextView)view.findViewById(R.id.unlogin_notice_content);
		TextView mUnloginNoticeCancel = (TextView)view.findViewById(R.id.unlogin_notice_cancel);
		TextView mUnloginNoticeConfirm = (TextView)view.findViewById(R.id.unlogin_notice_confirm);
		String title = "";
		String content = "";
		String ftype = "";
		String btn_name = getResources().getString(R.string.confirm);
		String btnUrl = "";
		try {
			title = jsonObject.getJSONObject("results").getString("title");
			content = jsonObject.getJSONObject("results").getString("content");
			ftype = jsonObject.getJSONObject("results").getString("ftype");
			btn_name = jsonObject.getJSONObject("results").getString("btn_name");
			btnUrl = jsonObject.getJSONObject("results").getString("url");
		}catch (Exception e){
			e.printStackTrace();
		}
		final  String webUrl = btnUrl;
		final String dialogType = ftype;
		mUnloginNoticeTitle.setText(title);
		mUnloginNoticeContent.setText(content);
		mUnloginNoticeConfirm.setText(btn_name);
		if(dialogType.equals("4")){
			mUnloginNoticeCancel.setVisibility(View.VISIBLE);
		}else{
			mUnloginNoticeCancel.setVisibility(View.GONE);
		}
		mUnloginNoticeCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				mUnloginPopupDialog.dismiss();
			}
		});
		mUnloginNoticeConfirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if(webUrl != null && !webUrl.equals("")){
					if(webUrl.endsWith(".apk")){
						if(!app.isDownload()){
							app.setDownload(true);
							deleteExistsApk(webUrl);
							downloadUpdate(webUrl);
						}
					}else{
						Intent intent2 = new Intent(MainActivity.this, WebActivity.class);
						intent2.putExtra("url",webUrl);
						startActivity(intent2);
					}
				}
				mUnloginPopupDialog.dismiss();
			}
		});
		mUnloginPopupDialog.setContentView(view);
//		Window dialogWindow = mUnloginPopupDialog.getWindow();
//		WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
//		p.width = LinearLayout.LayoutParams.MATCH_PARENT;
//		dialogWindow.setAttributes(p);
		L.i("wanlijun","user_id1="+SPUtils.get(this, Sp.user_id, "").toString());
		mUnloginPopupDialog.show();
	}
	//transsion-end

	private void deleteExistsApk(String downloadUrl){
		String[] split = downloadUrl.split("/");
		File file = new File(Environment.getExternalStorageDirectory()+"/"+split[split.length-1]);
		if(file.exists()){
			Log.i("test", file.getAbsolutePath());
			file.delete();
		}
	}

	protected void downloadUpdate(String downloadUrl) {
		HttpUtils httpUtils = new HttpUtils();
		Log.i("test", Environment.getExternalStorageDirectory()+"/duobaodai.apk");
		final String[] split = downloadUrl.split("/");

		HttpHandler<File> handler = httpUtils.download(downloadUrl,Environment.getExternalStorageDirectory()+"/"+split[split.length-1],true,true,new RequestCallBack<File>() {
//			private Dialog loadingDialog;

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				Log.i("test",arg1);
				app.setDownload(false);
				File file = new File(Environment.getExternalStorageDirectory()+"/"+split[split.length-1]);
				if(file.exists()){
					Log.i("test", file.getAbsolutePath());
					Intent intent = new Intent();
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.setAction(android.content.Intent.ACTION_VIEW);
					intent.setDataAndType(Uri.fromFile(file),"application/vnd.android.package-archive");
					startActivity(intent);
					killProcess();
				}else{
					String tickerText = getResources().getString(R.string.error_download);
					setUpNotification(tickerText,100,50,Notification.FLAG_AUTO_CANCEL);
					Toast.makeText(MainActivity.this, "下载失败，请重试",Toast.LENGTH_LONG).show();
				}
			}

			@Override
			public void onSuccess(ResponseInfo<File> arg0) {
				Log.i("test",arg0.result.getName());
				app.setDownload(false);
				String tickerText = getResources().getString(R.string.end_download);
				setUpNotification(tickerText,100,100,Notification.FLAG_AUTO_CANCEL);
//				if(loadingDialog!=null){
//					loadingDialog.dismiss();
//				}
				File file = new File(Environment.getExternalStorageDirectory()+"/"+split[split.length-1]);
				Log.i("test", file.getAbsolutePath());
				if(file.exists()){
					Log.i("test", file.getAbsolutePath());
					Intent intent = new Intent();
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.setAction(android.content.Intent.ACTION_VIEW);
					intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
					startActivity(intent);
					killProcess();
				}

			}

			@Override
			public void onLoading(long total, long current, boolean isUploading) {
				super.onLoading(total, current, isUploading);
				String tickerText = getResources().getString(R.string.during_download);
				setUpNotification(tickerText,(int) total,(int) current,Notification.FLAG_ONGOING_EVENT);
			}

		});
	}

	private void killProcess() {
		ActivityManager activityMan = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> process = activityMan.getRunningAppProcesses();

		int len = process.size();
		for(int i = 0;i<len;i++) {
			if (process.get(i).processName.equals("com.showsoft.duobaodai")) {
				android.os.Process.killProcess(process.get(i).pid);
			}
		}
	}



	private void shownote() {
		HttpUtils httpUtils = new HttpUtils(60000);
		//if (app == null || app.persionData == null) {
	//		return;
	//	}
		RequestParams params = new RequestParams();
		params.addBodyParameter("cmd", "Notice");
		params.addBodyParameter("sign", CheckUtils.getM5DEndo(URLS.noticeIndex));
		httpUtils.send(HttpMethod.POST, URLS.noticeIndex, params, new RequestCallBack<String>() {
			@Override
			public void onFailure(HttpException arg0, String arg1) {
				ToastErrorUtils.Show(MainActivity.this, arg0, arg1);
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				try {
					JSONObject object = new JSONObject(arg0.result);

/*
					//modify by jsontec dialog
					dialog = new Dialog(MainActivity.this, R.style.mydialog);
					dialog.setContentView(R.layout.dialog_notice_web);
					wb = (WebView) dialog.findViewById(R.id.notice_web);
					wb.loadUrl("http://www.baidu.com");
					wb.setWebViewClient(new WebViewClient(){
						public boolean shouldOverrideUrlLoading(WebView view, String url) {
							view.loadUrl(url);
							return true;
						}
					});



					//dialogTextView.setOnClickListener(BorrwInfoActivity.this);
					dialog.show();
*/

					if (object.getInt("errorCode") == 0) {
						String url = object.getJSONObject("results").getString("webUrl");
						wb.loadUrl(url);
						dialog.show();
						Log.d("test", url);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}








	private void regToWx() {
		api = WXAPIFactory.createWXAPI(this, Consts.WX_ID, true);
		boolean registerApp = api.registerApp(Consts.WX_ID);
		// Toast.makeText(this,"注册到微信"+(registerApp?"成功":"失败"),Toast.LENGTH_LONG).show();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	private void postInfomation() {
		if (app == null) {
			app = (AppApplication) getApplication();
		}
		TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date curDate = new Date();// 获取当前时间
		String currentTime = formatter.format(curDate);
		HttpUtils httpUtils = new HttpUtils(60000);
		RequestParams params = new RequestParams();
		params.addBodyParameter("cmd", "UserAppLogs");
		String userId = "";
		if (app.persionData != null && app.persionData.getUser_id() != null) {
			userId = app.persionData.getUser_id();
		}
		params.addBodyParameter("userId", userId);
		params.addBodyParameter("model", android.os.Build.MODEL);
		params.addBodyParameter("system", android.os.Build.VERSION.SDK + android.os.Build.VERSION.RELEASE);
		String imei = (String) SPUtils.get(this, "IMEI", "");
		if (TextUtils.isEmpty(imei)) {
			return;
		} else {
			params.addBodyParameter("IMEI", imei);
		}
		Log.d(TAG, "上传设备信息");
		String country = (String) SPUtils.get(this, "country", "");
		params.addBodyParameter("country", country);
		String province = (String) SPUtils.get(this, "province", "");
		params.addBodyParameter("province", province);
		String city = (String) SPUtils.get(this, "city", "");
		params.addBodyParameter("city", city);
		params.addBodyParameter("dateTimes", currentTime);
		PushManager.getInstance().initialize(this.getApplicationContext());
		String client_id = PushManager.getInstance().getClientid(this);
		params.addBodyParameter("clientid", client_id);
		httpUtils.send(HttpMethod.POST, URLS.saveimei, params, new RequestCallBack<String>() {
			@Override
			public void onFailure(HttpException arg0, String arg1) {
				ToastErrorUtils.Show(MainActivity.this, arg0, arg1);
				Log.d(TAG, "上传设备信息失败");
			}
			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				Log.d(TAG, "上传设备信息成功");
				try {
					JSONObject object = new JSONObject(arg0.result);
					if (object.getInt("errorCode") == 0) {
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void getPhoneInfomation() {
		TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		String devicedId = manager.getDeviceId();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		String currentTime = formatter.format(curDate);
		SPUtils.put(this, "IMEI", devicedId);
		SPUtils.put(this, "dateTimes", currentTime);
	}

	@Override
	public void initUI() {
		fragments[0] = new HomeFragment();
		fragments[1] = new BorrwFragment();
		fragments[2] = new MeFragment();
//		fragments[3] = new MicroClassroomFragment();
		fragments[3] = new MoreFragment();
		homeTextView = (TextView) findViewById(R.id.homeTextView);
		homeTextView.setOnClickListener(this);
		taskTextView = (TextView) findViewById(R.id.taskTextView);
		taskTextView.setOnClickListener(this);
		meTextView = (TextView) findViewById(R.id.meTextView);
		meTextView.setOnClickListener(this);
		noticeTextView = (TextView) findViewById(R.id.noticeTextView);
		noticeTextView.setOnClickListener(this);
		moreTextView = (TextView) findViewById(R.id.moreTextView);
		moreTextView.setOnClickListener(this);

		channelViewPager = (ViewPager) findViewById(R.id.channelViewPager);
		channelViewPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager()));
		channelViewPager.setOnPageChangeListener(onPageChangeListener);
		channelViewPager.setOffscreenPageLimit(6);
	}

	@Override
	public void initValue() {
		EventBus.getDefault().register(this);
		app = (AppApplication) getApplication();
		app.isAppStart = true;
		L.i("wanlijun","user_id="+SPUtils.get(this, Sp.user_id, "").toString());
		if (!TextUtils.isEmpty((String) SPUtils.get(this, Sp.user_id, ""))) {
			getUserInfomation();
		}else if((Boolean) SPUtils.get(this,Sp.APK_IS_LAST_VERSION,true)){
			getPopupDialog();
		}else if(app.isFirstUpdateDialog()){
			app.setFirstUpdateDialog(false);
			updateDialog();
		}
		if (GetSystem.inConnect(app.getBaseContext())) {
			GetLocation getLocation = new GetLocation(app.getBaseContext(), null);
			getLocation.getCurrentLocation();
			isConnect = true;
			postInfomation();
		} else {
			isConnect = false;
		}
	}

	public void onEvent(BackMainEvent event){
		L.i("wanlijun","BackMainEvent");
		channelViewPager.setCurrentItem(0, false);
	}
	public void onEvent(ScrollEvent event) {
		setSecondItem();
	}
	public void onExitEvent(ExitEvent event) {
		finish();
	}
	public void onEvent(LogoutEvent event) {
		L.i("wanlijun",app.mIsShowUnloginDialog+"");
		app.mIsShowUnloginDialog = false;
		channelViewPager.setCurrentItem(0, false);
		PushManager.getInstance().stopService(MainActivity.this);
		Intent intent = new Intent(this, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		L.i("wanlijun",app.mIsShowUnloginDialog+"123");
	}
	public void onEvent(BackToInvestEvent event) {
		channelViewPager.setCurrentItem(1, false);
	}
	public void onEvent(RealNameEvent event) {
		updateUserInfo();
	}
	public void onEvent(LoginEvent event) {
		if (mPopupWindow != null && mPopupWindow.isShowing()) {
			mPopupWindow.dismiss();
		}
		channelViewPager.setCurrentItem(0, false);
		postInfomation();
		if(!(Boolean) SPUtils.get(this,Sp.APK_IS_LAST_VERSION,true) && app.isFirstUpdateDialog()){
			app.setFirstUpdateDialog(false);
			updateDialog();
		}else{
			getPopupPrompt();
		}
	}
	public void onEvent(ReceivePushEvent event) {
		if (event.receive) {
			PushManager.getInstance().turnOnPush(this.getApplicationContext());
		} else {
			PushManager.getInstance().turnOffPush(this.getApplicationContext());
		}
	}

	private void updateUserInfo() {
		HttpUtils httpUtils = new HttpUtils(60000);
		if (app == null || app.persionData == null) {
			return;
		}
		String url = URLS.userInfoMd5 + "cmd=UserInfo" + "&userId=" + app.persionData.getUser_id() + "&phone=" + app.persionData.getPhone();
		RequestParams params = new RequestParams();
		params.addBodyParameter("cmd", "UserInfo");
		params.addBodyParameter("userId", app.persionData.getUser_id());
		params.addBodyParameter("phone", app.persionData.getPhone());
		params.addBodyParameter("token", app.persionData.getToken());
		params.addBodyParameter("sign", CheckUtils.getM5DEndo(url));
		L.i("wanlijun","sign="+url);
		httpUtils.send(HttpMethod.POST, URLS.userInfo, params, new RequestCallBack<String>() {
			@Override
			public void onFailure(HttpException arg0, String arg1) {
				ToastErrorUtils.Show(MainActivity.this, arg0, arg1);
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				try {
					JSONObject object = new JSONObject(arg0.result);
					L.i("wanlijun","updateUserInfo");
					L.i("wanlijun","object="+object.toString());
					if (object.getInt("errorCode") == 0) {
						String string = object.getJSONObject("results").getString("real_status");
						app.persionData.setReal_status(string);
						SPUtils.put(MainActivity.this, Sp.user_realstatus, string);
						String name = object.getJSONObject("results").getString("realname");
						app.persionData.setRealname(name);
						SPUtils.put(MainActivity.this, Sp.user_realname, name);
						Log.d("test", name);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
			case R.id.homeTextView :
				currentPage = 0;
				channelViewPager.setCurrentItem(0, false);
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						HomeFragment homeFragment = (HomeFragment) fragments[0];
						homeFragment.scrollToTop();
					}
				}, 30);
				break;
			case R.id.taskTextView :
				currentPage = 1;
				setSecondItem();
				break;
			case R.id.meTextView :
				currentPage = 2;
				channelViewPager.setCurrentItem(2, false);
				break;
//			case R.id.noticeTextView :
//				currentPage = 3;
//				channelViewPager.setCurrentItem(3, false);
//				break;
			case R.id.moreTextView :
				currentPage = 3;
				channelViewPager.setCurrentItem(3, false);
				break;
		}
	}

	public void showLogin() {
		LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
		View view = inflater.inflate(R.layout.pop_share, null);
		mPopupWindow = new PopupWindow(view, android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT);
		mPopupWindow.setAnimationStyle(R.style.popwin_anim_style);
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
		mPopupWindow.setFocusable(true);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.showAtLocation(meTextView, Gravity.CENTER, 0, 0);
		TextView loginTextView = (TextView) view.findViewById(R.id.loginTextView);
		loginTextView.setOnClickListener(onClickListener);
		TextView cancelTextView = (TextView) view.findViewById(R.id.cancelTextView);
		cancelTextView.setOnClickListener(onClickListener);
	}

	private OnClickListener onClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.loginTextView :
					startActivity(new Intent(MainActivity.this, LoginActivity.class));
					break;
				case R.id.cancelTextView :
					mPopupWindow.dismiss();
					Log.i("test", "currentPage:" + currentPage);
					channelViewPager.setCurrentItem(0, false);
					break;
			}
		}
	};

	private void setSecondItem() {
		if (isHome) {
			int scrollTime = 1000;
			HomeFragment homeFragment = (HomeFragment) fragments[0];
			homeFragment.ScrollBySlow(scrollTime);
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					channelViewPager.setCurrentItem(1, false);
				}
			}, scrollTime / 2);
		} else {
			channelViewPager.setCurrentItem(1, false);
		}
	}

	public class MyFragmentPagerAdapter extends FragmentStatePagerAdapter {

		public MyFragmentPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public int getCount() {
			return fragments.length;
		}

		@Override
		public Fragment getItem(int arg0) {
			return fragments[arg0];
		}
	}
	private int currentPage = 0;
	OnPageChangeListener onPageChangeListener = new OnPageChangeListener() {

		@Override
		public void onPageSelected(int arg0) {
			resetStatus();
			((HomeFragment) fragments[0]).closeTimer();

			switch (arg0) {
				case 0 :
					currentPage = 0;
					isHome = true;
					isShouYe = true;
					homeTextView.setEnabled(false);
					HomeFragment homeFragment = (HomeFragment) fragments[0];
					homeFragment.scrollToTop();
					homeFragment.startTimer();
					homeFragment.onRefresh();
					break;
				case 1 :
					currentPage = 1;
					isHome = false;
					isShouYe = false;
					taskTextView.setEnabled(false);
					BorrwFragment borrwFragment = (BorrwFragment) fragments[1];
					borrwFragment.onRefresh();
					break;
				case 2 :
					currentPage = 2;
					isHome = false;
					isShouYe = false;
					meTextView.setEnabled(false);
					//transsion-begin,resolve the bug repeat popup the login dialog,wanlijun,20170725
					//已登录，没有手势密码，不弹登录框
					//if (app.persionData == null||app.Gesturepwd==false) {
					if (app.persionData == null) {
						showLogin();
					} else {
						MeFragment meFragment = (MeFragment) fragments[2];
						meFragment.onRresh();
					}
					//transsion-end
					break;
//				case 3 :
//					currentPage = 3;
//					// MicroClassroomFragment microClassroomFragment =
//					// (MicroClassroomFragment) fragments[3];
//					// microClassroomFragment.loadBanner();
//					isHome = false;
//					isShouYe = false;
//					noticeTextView.setEnabled(false);
//					break;
				case 3 :
					currentPage = 3;
					isHome = false;
					isShouYe = false;
					moreTextView.setEnabled(false);
					break;
			}
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}
	};
	private NetworkChangedReceiver receiver;
	private TextView noticeTextView;
	private PopupWindow mPopupWindow;

	public void resetStatus() {
		homeTextView.setEnabled(true);
		taskTextView.setEnabled(true);
		meTextView.setEnabled(true);
//		noticeTextView.setEnabled(true);
		moreTextView.setEnabled(true);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
		unregisterReceiver(receiver);
//        unregisterBuryingPointReceiver();
		app.isAppStart = false;
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return super.onKeyDown(keyCode, event);
	}


	private void updateDialog(){
		String force = (String) SPUtils.get(MainActivity.this,Sp.APK_UPDATE_FORCE,"");
		String content = (String) SPUtils.get(MainActivity.this,Sp.UPDATE_DIALOG_CONTENT,"检查到有新版本，是否更新？");
		mUpdateApkDialog = new Dialog(MainActivity.this, R.style.mydialog);
		View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.unlogin_notice_dialog,null);
		TextView mUnloginNoticeTitle = (TextView)view.findViewById(R.id.unlogin_notice_title);
		TextView mUnloginNoticeContent = (TextView)view.findViewById(R.id.unlogin_notice_content);
		TextView mUnloginNoticeCancel = (TextView)view.findViewById(R.id.unlogin_notice_cancel);
		TextView mUnloginNoticeConfirm = (TextView)view.findViewById(R.id.unlogin_notice_confirm);
		mUnloginNoticeTitle.setText("更新提示");
		mUnloginNoticeContent.setText(content);
		mUnloginNoticeConfirm.setText("更新");
		mUnloginNoticeCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				mUpdateApkDialog.dismiss();
			}
		});
		mUnloginNoticeConfirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				String webUrl = (String) SPUtils.get(MainActivity.this,Sp.APK_DOWNLOAD_PATH,"");
				if(webUrl != null && !webUrl.equals("") && !app.isDownload()){
					    app.setDownload(true);
						deleteExistsApk(webUrl);
						downloadUpdate(webUrl);
				}else {
					T.showShort(MainActivity.this,"下载路径不正确！");
				}
				mUpdateApkDialog.dismiss();
			}
		});
		mUpdateApkDialog.setContentView(view);
		L.i("wanlijun","user_id1="+SPUtils.get(this, Sp.user_id, "").toString());
		if(force.equals("1")){
			mUnloginNoticeCancel.setVisibility(View.GONE);
			mUpdateApkDialog.setCanceledOnTouchOutside(false);
			mUpdateApkDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
				@Override
				public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
					return true;
				}
			});
		}else{
			mUnloginNoticeCancel.setVisibility(View.VISIBLE);
			mUpdateApkDialog.setCanceledOnTouchOutside(true);
			mUpdateApkDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
				@Override
				public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
					return false;
				}
			});
		}
		mUpdateApkDialog.show();
	}
}