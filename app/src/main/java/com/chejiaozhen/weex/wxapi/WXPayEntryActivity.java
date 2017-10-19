package com.chejiaozhen.weex.wxapi;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;


import com.alibaba.weex.commons.AbsWeexActivity;
import com.chejiaozhen.weex.R;

import com.taobao.weex.WXSDKEngine;
import com.taobao.weex.WXSDKManager;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.mm.opensdk.utils.Log;


import java.util.HashMap;
import java.util.Map;

public class WXPayEntryActivity extends AbsWeexActivity implements IWXAPIEventHandler {
	private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";
    private IWXAPI api;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.pay_result);
    	api = WXAPIFactory.createWXAPI(this, Constants.APP_ID);
        api.handleIntent(getIntent(), this);
    }

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
        api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
	}

	@Override
	public void onResp(BaseResp resp) {
//		Log.e("resp.errStr",resp.errStr);
		switch (resp.errCode){
			case 0:
				Intent intent=new Intent();
				//与清单文件的receiver的anction对应
				intent.setAction("com.intent.payintent");
				intent.putExtra("code",0);
				//发送广播
				sendBroadcast(intent);

				finish();
				break;
			case -1:
				Intent intent1=new Intent();
				//与清单文件的receiver的anction对应
				intent1.setAction("com.intent.payintent");
				intent1.putExtra("code",-1);
				//发送广播
				sendBroadcast(intent1);
				finish();
				break;
			case -2:
				Intent intent2=new Intent();
				//与清单文件的receiver的anction对应
				intent2.setAction("com.intent.payintent");
				intent2.putExtra("code",-2);
				//发送广播
				sendBroadcast(intent2);
				finish();
				break;
		}
	}
}