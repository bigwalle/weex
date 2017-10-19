package com.chejiaozhen.weex.module;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alipay.sdk.app.H5PayCallback;
import com.alipay.sdk.app.PayTask;
import com.alipay.sdk.util.H5PayResultModel;

import com.taobao.weex.common.WXModule;
import com.taobao.weex.common.WXModuleAnno;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;


import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
/**
 * Created by 53257 on 2017/8/31.
 * 支付模块
 */
public class PayMoudle extends WXModule
{
    @WXModuleAnno(runOnUIThread = true)
public void wxPay(String result) throws JSONException {
    final IWXAPI api = WXAPIFactory.createWXAPI(mWXSDKInstance.getContext(), "wx248ae9bebdd04898");
        Log.e("result",result);
        System.out.println("^^^^^^^^^^^^"+result);
        String appid,noncestr,packageValue,partnerid,prepayid,sign,timestamp;
        try{
            JSONObject reulstObj= (JSONObject) JSON.parse(result);
              appid  = reulstObj.getString("appid");
              noncestr  =  reulstObj.getString("noncestr");
              packageValue  = reulstObj.getString("package");
              partnerid  = reulstObj.getString("partnerid");
              prepayid  = reulstObj.getString("prepayid");
              sign  =  reulstObj.getString("sign");
              timestamp  =  reulstObj.getString("timestamp");
            PayReq req = new PayReq();
            req.partnerId =partnerid;
            req.prepayId =prepayid;
            req.nonceStr =noncestr;
            req.timeStamp =timestamp;
            req.packageValue =packageValue;
            req.sign =sign;
            //req.extData = "app data"; // optional
            req.appId=appid;
            api.sendReq(req);
        }catch (Exception ex){

        }finally {
        }

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.intent.payintent");
        mWXSDKInstance.getContext().registerReceiver(mReceiver,intentFilter);
}

    public class Receiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            int code= intent.getIntExtra("code",0);
            Map<String,Object> params1=new HashMap<>();
            params1.put("payway","wxpay");
            params1.put("code",code);
            mWXSDKInstance.fireGlobalEventCallback("onPayCallBack",params1);
        }
    }
  public    Receiver  mReceiver=new Receiver();

    @WXModuleAnno(runOnUIThread = true)
    public void aliPay(String result) throws JSONException {
        System.out.println("######################"+result);
        WebView     mWebView = new WebView(mWXSDKInstance.getContext());
        mWebView.setVisibility(View.VISIBLE);
        WebSettings settings = mWebView.getSettings();
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        settings.setJavaScriptEnabled(true);
        settings.setSavePassword(false);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setMinimumFontSize(settings.getMinimumFontSize() + 8);
        settings.setAllowFileAccess(false);
        settings.setTextSize(WebSettings.TextSize.NORMAL);
        mWebView.setVerticalScrollbarOverlay(true);
        mWebView.setWebViewClient(new MyWebViewClient());
        mWebView.loadUrl(result);
    }
    private class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(final WebView view, String url) {
            if (!(url.startsWith("http") || url.startsWith("https"))) {
                return true;
            }

            /**
             * 推荐采用的新的二合一接口(payInterceptorWithUrl),只需调用一次
             */
            final PayTask task = new PayTask((Activity) mWXSDKInstance.getContext());
            boolean isIntercepted = task.payInterceptorWithUrl(url, true, new H5PayCallback() {
                @Override
                public void onPayResult(final H5PayResultModel result) {

                    String code =result.getResultCode();
                    Map<String,Object> params1=new HashMap<>();
                    params1.put("code",code);
                    params1.put("payway","alipay");
                    mWXSDKInstance.fireGlobalEventCallback("onPayCallBack",params1);
                    final String url=result.getReturnUrl();

                }
            });
            if(!isIntercepted)
                view.loadUrl(url);
            return true;
        }
    }
}
