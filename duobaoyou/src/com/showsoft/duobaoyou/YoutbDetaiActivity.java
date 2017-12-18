package com.showsoft.duobaoyou;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.showsoft.bean.YoutbRecord;
import com.showsoft.bean.YoutbRecordBean;
import com.showsoft.consts.MyAppApiConfig;
import com.showsoft.consts.Sp;
import com.showsoft.consts.URLS;
import com.showsoft.data.CastPlanData;
import com.showsoft.data.VerifiedData;
import com.showsoft.utils.CheckUtils;
import com.showsoft.utils.L;
import com.showsoft.utils.SPUtils;
import com.showsoft.utils.ToastErrorUtils;
import com.showsoft.utils.ToastTool;
import com.showsoft.view.PopPromptDialog;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class YoutbDetaiActivity extends BaseActivity implements View.OnClickListener{
    private ImageView mBackImageView;
    private TextView mMenuTextView;
    private TextView mTitleTextView;
    private WebView mYtbWebview;
    private String ytbData = "";
    private String title = "";
    List<YoutbRecord> borrwRecords;
    AppApplication app;
    private PopPromptDialog popDialog;
    private PopPromptDialog mPopPromptDialog;
    private PopPromptDialog popBindBankcardDialog;
    private ProgressDialog progressDialog;
    private CastPlanData borrwData;
    private boolean isWebFinished = false;    //网页是否加载完毕
    private boolean isDataSend1 = false;    //接口数据是否加载到网页
    private boolean isDataSend2 = false;
    private TextView joinTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtb_detai);
        ytbData = getIntent().getStringExtra("ytbData");
        title = getIntent().getStringExtra("title");
        borrwData = (CastPlanData) getIntent().getSerializableExtra("BorrwData");
        initUI();
        initValue();
    }

    @Override
    public void initUI() {
        joinTextView = (TextView)findViewById(R.id.joinTextView);
        joinTextView.setOnClickListener(this);
        mMenuTextView = (TextView)findViewById(R.id.menuTextView);
        mMenuTextView.setText(getString(R.string.recharge));
        mMenuTextView.setOnClickListener(this);
        mBackImageView = (ImageView)findViewById(R.id.backImageView);
        mBackImageView.setOnClickListener(this);
        mTitleTextView = (TextView)findViewById(R.id.titleTextView);
        mTitleTextView.setText(title);
        mYtbWebview = (WebView)findViewById(R.id.ytb_webview);
        mYtbWebview.getSettings().setJavaScriptEnabled(true);
        mYtbWebview.getSettings().setAppCacheEnabled(true);
        mYtbWebview.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        mYtbWebview.getSettings().setDomStorageEnabled(true);
        mYtbWebview.loadUrl("file:///android_asset/ytb/ytObjDetail.html");
        mYtbWebview.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView webView, String s, Bitmap bitmap) {
                L.i("wanlijun","onPageStarted");
                super.onPageStarted(webView, s, bitmap);
            }

            @Override
            public void onPageFinished(WebView webView, String s) {
                L.i("wanlijun","onPageFinished");
                isWebFinished = true;
                if(isDataSend1){
                    L.i("wanlijun",MyAppApiConfig.ytbDetail.toString());
                    mYtbWebview.loadUrl("javascript:jsRequest(" + MyAppApiConfig.ytbDetail + ")");
                }
                if(isDataSend2){
                    mYtbWebview.loadUrl("javascript:jsRequest(" + MyAppApiConfig.ytbRecord + ")");
                }
                super.onPageFinished(webView, s);

            }
        });
    }

    @Override
    public void initValue() {
        if(app == null){
            app = (AppApplication) getApplication();
        }
        getYoutbDetailinfo();
        getYoutbRecord();
    }

    //获取优投宝详情
    private void getYoutbDetailinfo() {
        final AppApplication app = (AppApplication) getApplication();
        HttpUtils httpUtils = new HttpUtils();
        RequestParams params = new RequestParams();
        //modify by jsontec 2017 02 14
        params.addBodyParameter("cmd", "Yborrowdetails");
        if(TextUtils.isEmpty(ytbData)){
            params.addBodyParameter("yborrowId", "48");
        }else{
            params.addBodyParameter("yborrowId", ytbData);
        }
        httpUtils.send(HttpMethod.POST, URLS.ybdetails, params, new RequestCallBack<String>() {
            @Override
            public void onFailure(HttpException arg0, String arg1) {
                app.exception.onHttpFailure();
            }
            @Override
            public void onSuccess(ResponseInfo<String> arg0) {
                try {
                    JSONObject object = new JSONObject(arg0.result);
                    L.i("wanlijun","优投宝详情");
                    L.i("wanlijun",object.toString());
                    if (object.getInt("errorCode") == 0) {
                        MyAppApiConfig.ytbDetail=object;
                        MyAppApiConfig.ytbDetail.put("key", "ytbDetail");
                        isDataSend1 = true;
                        if(isWebFinished) {
                            L.i("wanlijun",MyAppApiConfig.ytbDetail.toString());
                            mYtbWebview.loadUrl("javascript:jsRequest(" + MyAppApiConfig.ytbDetail + ")");
                        }
                        //System.out.println("javascript:jsRequest('" +MyAppApiConfig.borrowInfo.toString()+ "')");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 优投宝投标记录
     */
    private void getYoutbRecord() {
        HttpUtils httpUtils = new HttpUtils();
        RequestParams params = new RequestParams();
        params.addBodyParameter("cmd", "Yborrowcreditor");
        if(TextUtils.isEmpty(ytbData)){
            params.addBodyParameter("yborrowId", "48");
        }else{
            params.addBodyParameter("yborrowId", ytbData);
        }
        httpUtils.send(HttpMethod.POST, URLS.ybcreditor, params, new RequestCallBack<String>() {

            @Override
            public void onFailure(HttpException arg0, String arg1) {

                ToastTool.showMessage(YoutbDetaiActivity.this, "获取投标记录失败");

            }

            @Override
            public void onSuccess(ResponseInfo<String> arg0) {
                try {
                    JSONObject jsonObject = new JSONObject(arg0.result);
                    L.i("wanlijun","优投宝投标记录");
                    L.i("wanlijun",jsonObject.toString());
                    if (jsonObject.getInt("errorCode") == 0) {
                        Gson gson = new Gson();
                        YoutbRecordBean fromJson = gson.fromJson(arg0.result, YoutbRecordBean.class);
                        borrwRecords = fromJson.results.lists;
                        //已投标人数
                        //tv_total.setText(fromJson.results.total);
                        if (null == borrwRecords) {
                            borrwRecords = new ArrayList<YoutbRecord>();
                        }
                        MyAppApiConfig.ytbRecord=jsonObject;
                        MyAppApiConfig.ytbRecord.put("key", "ytbRecord");
                        isDataSend2 = true;
                        if(isWebFinished) {
                            mYtbWebview.loadUrl("javascript:jsRequest(" + MyAppApiConfig.ytbRecord + ")");
                        }
                        //System.out.println("javascript:jsRequest('" +MyAppApiConfig.borrowTenderlists.toString()+ "')");

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.i("test", "失败");
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            Intent intent = new Intent(YoutbDetaiActivity.this,ExcellentPlanActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra("BorrwData",borrwData);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.backImageView:
                Intent intent = new Intent(YoutbDetaiActivity.this,ExcellentPlanActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("BorrwData",borrwData);
                startActivity(intent);
                finish();
                break;
            case R.id.menuTextView:
                if(isRealStatus()){
                    if(app.persionData.getIs_auth() == null || app.persionData.getIs_auth().equals("0")){
                        showActivateSingnetPromptDialog();
                    }else{
                        if((Boolean) SPUtils.get(YoutbDetaiActivity.this, Sp.BIND_BANKCARD,false)){
                            Intent rechargeIntent = new Intent(this, RechargeActivity.class);
                            rechargeIntent.putExtra("Mark", true);
                            startActivity(rechargeIntent);
                        }else{
                            showBindBankcardDialog();
                        }
                    }
                }
                break;
            case R.id.joinTextView:
                intent = new Intent(YoutbDetaiActivity.this,ExcellentPlanActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("BorrwData",borrwData);
                startActivity(intent);
                finish();
                break;
        }
    }

    //是否登录实名
    private boolean isRealStatus(){
        if(app.persionData == null){
            startActivity(new Intent(YoutbDetaiActivity.this,LoginActivity.class));
            return false;
        }else if(app.persionData.getReal_status().equals("1")){
            return true;
        }else{
            showBeSingnetPromptDialog();
            return false;
        }
    }
    //开通存管
    private void showBeSingnetPromptDialog(){
        if(popDialog == null){
            popDialog = new PopPromptDialog();
        }
        popDialog.showPop(YoutbDetaiActivity.this,mMenuTextView,"取消","开通存管",getString(R.string.open_deposit_tip));
        popDialog.setOnConfirmClickListener(new PopPromptDialog.OnConfirmClickListener() {
            @Override
            public void sure() {
                certificationRealName();
            }

            @Override
            public void onDismiss() {
            }
        });
    }
    //汇付账户激活存管
    private void showActivateSingnetPromptDialog() {
        if (mPopPromptDialog == null) {
            mPopPromptDialog = new PopPromptDialog();
        }
        mPopPromptDialog.showPop(YoutbDetaiActivity.this, mMenuTextView, "取消", "激活存管", getString(R.string.activate_deposit_tip));
        mPopPromptDialog.setOnConfirmClickListener(new PopPromptDialog.OnConfirmClickListener() {

            @Override
            public void sure() {
                certificationRealName();
            }
            @Override
            public void onDismiss() {

            }
        });
    }

    private void showBindBankcardDialog(){
        if(popBindBankcardDialog == null){
            popBindBankcardDialog = new PopPromptDialog();
        }
        popBindBankcardDialog.showPop(YoutbDetaiActivity.this,mMenuTextView,getString(R.string.cancel),getString(R.string.bind_card),getString(R.string.bind_card_tips));
        popBindBankcardDialog.setOnConfirmClickListener(new PopPromptDialog.OnConfirmClickListener() {
            @Override
            public void sure() {
                addNewbank();
            }
            @Override
            public void onDismiss() {

            }
        });
    }
    private void showProgressDialog(String message,Context context){
        if(progressDialog == null){
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage(message);
        }
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                return true;
            }
        });
        progressDialog.show();
    }
    private void closeProgressDialog(){
        if(progressDialog != null  && progressDialog.isShowing()){
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    /** 添加新网银行卡 **/
    private void addNewbank() {
        String url = URLS.addnewbankMd5 + "cmd=AddNewbankCard" + "&userId=" + app.persionData.getUser_id() + "&phone=" + app.persionData.getPhone();
        HttpUtils httpUtils = new HttpUtils();
        RequestParams params = new RequestParams();
        params.addBodyParameter("cmd", "AddNewbankCard");
        params.addBodyParameter("userId", app.persionData.getUser_id());
        params.addBodyParameter("phone", app.persionData.getPhone());
        params.addBodyParameter("token", app.persionData.getToken());
        params.addBodyParameter("sign", CheckUtils.getM5DEndo(url));
        // 等待
//        final ProgressDialog dialog = ProgressDialog.show(AddBankcardActivity.this, null, "正在加载中...");
        showProgressDialog("正在加载，请稍后。",YoutbDetaiActivity.this);
        // dialog.show();
        httpUtils.send(HttpMethod.POST, URLS.addnewbank, params, new RequestCallBack<String>() {

            @Override
            public void onFailure(HttpException arg0, String arg1) {
                closeProgressDialog();
            }

            @Override
            public void onSuccess(ResponseInfo<String> arg0) {
                closeProgressDialog();
                try {
                    JSONObject object = new JSONObject(arg0.result);
                    if (object.getInt("errorCode") == 0) {
                        Intent intent = new Intent(YoutbDetaiActivity.this, WebVerfiedActivity.class);
                        intent.putExtra("url", object.getString("url"));
                        intent.putExtra("title", "添加银行卡");
                        intent.putExtra("flag","AddNewbankCard");
                        startActivity(intent);
                    } else {
                        //app.exception.onReturnDataNotRight(bean.getErrorCode(), list_bankcard, AddBankcardActivity.this);
                        Toast.makeText(YoutbDetaiActivity.this, object.getString("errorMess"), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void certificationRealName(){
        final Dialog dialog = new Dialog(YoutbDetaiActivity.this,R.style.progressdialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.fresh_progress);
        dialog.show();
        HttpUtils httpUtils = new HttpUtils();
        RequestParams params = new RequestParams();
        String url = URLS.newbankMd5 + "cmd=UserNewbank&" + "userid=" + app.persionData.getUser_id() + "&phone=" + app.persionData.getPhone();
        params.addBodyParameter("cmd","UserNewbank");
        params.addBodyParameter("userId",app.persionData.getUser_id());
        params.addBodyParameter("phone",app.persionData.getPhone());
        params.addBodyParameter("token",app.persionData.getToken());
        params.addBodyParameter("sign", CheckUtils.getM5DEndo(url));
        httpUtils.send(HttpMethod.POST, URLS.newbank, params, new RequestCallBack<String>() {
            @Override
            public void onFailure(HttpException e, String s) {
                if(dialog != null){
                    dialog.dismiss();
                }
                ToastErrorUtils.Show(YoutbDetaiActivity.this, e, s);
            }

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                if(dialog != null){
                    dialog.dismiss();
                }
                Gson gson = new Gson();
                VerifiedData verifiedData = gson.fromJson(responseInfo.result,VerifiedData.class);
                if(verifiedData.getErrorCode() == 0){
                    Intent intent = new Intent(YoutbDetaiActivity.this,WebVerfiedActivity.class);
                    intent.putExtra("url", verifiedData.getUrl());
                    try {
                        if(app.persionData!=null && app.persionData.getReal_status() != null && app.persionData.getReal_status().equals("1")){
                            intent.putExtra("title", "激活存管");
                        }else {
                            intent.putExtra("title", "开通存管");
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        intent.putExtra("title", "开通存管");
                    }
                    intent.putExtra("flag","UserNewbank");
                    startActivity(intent);
                }else{
                    Toast.makeText(YoutbDetaiActivity.this,verifiedData.getErrorMess(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
