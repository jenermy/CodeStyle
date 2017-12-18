package com.showsoft.duobaoyou.wxapi;


import com.showsoft.consts.Consts;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

	private IWXAPI iwxapi;
	private TextView wechat;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.test);
		//wechat = (TextView) findViewById(R.id.return_wechat);
		iwxapi = WXAPIFactory.createWXAPI(this,Consts.WX_ID,true);
		iwxapi.handleIntent(getIntent(),this);
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		iwxapi.handleIntent(intent, this);
		//WXEntryActivity
	}
	
	@Override
	public void onReq(BaseReq arg0) {
	}

	@Override
	public void onResp(BaseResp arg0) {
		String result = "";
		switch (arg0.errCode) {
		case BaseResp.ErrCode.ERR_OK:
			result = "分享成功";
			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:
			result = "取消分享";
			break;
		case BaseResp.ErrCode.ERR_AUTH_DENIED:
			result = "分享失败";
			break;
		default:
			result = "分享失败";
			break;
		}
		Toast.makeText(this, result, Toast.LENGTH_LONG).show();
		finish();
	}
}
