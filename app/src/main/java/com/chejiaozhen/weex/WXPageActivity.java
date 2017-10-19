package com.chejiaozhen.weex;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.weex.commons.AbsWeexActivity;
import com.alibaba.weex.commons.util.CommonUtils;
import com.alibaba.weex.commons.util.DevOptionHandler;
import com.alibaba.weex.commons.util.ShakeDetector;
import com.alibaba.weex.constants.Constants;
import com.taobao.weex.WXEnvironment;
import com.taobao.weex.WXRenderErrorCode;
import com.taobao.weex.WXSDKEngine;
import com.taobao.weex.WXSDKInstance;
import com.taobao.weex.ui.component.NestedContainer;
import com.taobao.weex.utils.WXSoInstallMgrSdk;
import java.util.LinkedHashMap;
public class WXPageActivity extends AbsWeexActivity implements
    WXSDKInstance.NestedInstanceInterceptor {
  private static final String TAG = "WXPageActivity";
  private ProgressBar mProgressBar;
  private TextView mTipView;
  private AlertDialog mDevOptionsDialog;
  private boolean mIsShakeDetectorStarted = false;
  private boolean mIsDevSupportEnabled = WXEnvironment.isApkDebugable();
  private final LinkedHashMap<String, DevOptionHandler> mCustomDevOptions = new LinkedHashMap<>();
  private ShakeDetector mShakeDetector;

  @Override
  public void onCreateNestInstance(WXSDKInstance instance, NestedContainer container) {
    Log.d(TAG, "Nested Instance created.");
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_wxpage);
    mContainer = (ViewGroup) findViewById(R.id.container);
    mProgressBar = (ProgressBar) findViewById(R.id.progress);
    mTipView = (TextView) findViewById(R.id.index_tip);
    Uri uri = getIntent().getData();
    Bundle bundle = getIntent().getExtras();

    if (uri != null) {
      mUri = uri;
    }

    if (bundle != null) {
      String bundleUrl = bundle.getString(Constants.PARAM_BUNDLE_URL);
      if (!TextUtils.isEmpty(bundleUrl)) {
        mUri = Uri.parse(bundleUrl);
      }
    }

    if (mUri == null) {
      Toast.makeText(this, "the uri is empty!", Toast.LENGTH_SHORT).show();
      finish();
      return;
    }


    if (!WXSoInstallMgrSdk.isCPUSupport()) {
      mProgressBar.setVisibility(View.INVISIBLE);
      mTipView.setText(R.string.cpu_not_support_tip);
      return;
    }

    loadUrl(getUrl(mUri));
  }

  @Override
  public void onResume() {
    super.onResume();
    if (!mIsShakeDetectorStarted && mShakeDetector != null) {
      mShakeDetector.start((SensorManager) getApplicationContext().getSystemService(Context.SENSOR_SERVICE));
      mIsShakeDetectorStarted = true;
    }
  }

  @Override
  public void onPause() {
    super.onPause();
    if (mIsShakeDetectorStarted && mShakeDetector != null) {
      mShakeDetector.stop();
      mIsShakeDetectorStarted = false;
    }
  }
  // 定义一个变量，来标识是否退出
  private static boolean isExit = false;

  Handler mHandler = new Handler() {

    @Override
    public void handleMessage(Message msg) {
      super.handleMessage(msg);
      isExit = false;
    }
  };

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK) {
      if(getUrl().contains("app")){//仅主业有效
        exit();
        return false;
      }

    }
    return super.onKeyDown(keyCode, event);
  }

  private void exit() {
    if (!isExit) {
      isExit = true;
      Toast.makeText(getApplicationContext(), "再按一次退出程序",
              Toast.LENGTH_SHORT).show();
      // 利用handler延迟发送更改状态信息
      mHandler.sendEmptyMessageDelayed(0, 2000);
    } else {
      finish();
      System.exit(0);
    }
  }
  private String getUrl(Uri uri) {
    String url = uri.toString();
    String scheme = uri.getScheme();
    if (uri.isHierarchical()) {
      if (TextUtils.equals(scheme, "http") || TextUtils.equals(scheme, "https")) {
        String weexTpl = uri.getQueryParameter(Constants.WEEX_TPL_KEY);
        if (!TextUtils.isEmpty(weexTpl)) {
          url = weexTpl;
        }
      }
    }
    return url;
  }

  protected void preRenderPage() {
    mProgressBar.setVisibility(View.VISIBLE);
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    Intent intent = new Intent("requestPermission");
    intent.putExtra("REQUEST_PERMISSION_CODE", requestCode);
    intent.putExtra("permissions", permissions);
    intent.putExtra("grantResults", grantResults);
    LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
  }

  @Override
  public void onRenderSuccess(WXSDKInstance instance, int width, int height) {
    mProgressBar.setVisibility(View.GONE);
    mTipView.setVisibility(View.GONE);
  }

  @Override
  public void onException(WXSDKInstance instance, String errCode, String msg) {
    mProgressBar.setVisibility(View.GONE);
    mTipView.setVisibility(View.VISIBLE);
    if (TextUtils.equals(errCode, WXRenderErrorCode.WX_NETWORK_ERROR)) {
      mTipView.setText(R.string.index_tip);
    } else {
      mTipView.setText("render error:" + errCode);
    }
  }


  // Put up our own UI for how to handle the decoded contents.
  private void handleDecodeInternally(String code) {
    if (!TextUtils.isEmpty(code)) {
      Uri uri = Uri.parse(code);
      if (uri.getQueryParameterNames().contains("bundle")) {
        WXEnvironment.sDynamicMode = uri.getBooleanQueryParameter("debug", false);
        WXEnvironment.sDynamicUrl = uri.getQueryParameter("bundle");
        String tip = WXEnvironment.sDynamicMode ? "Has switched to Dynamic Mode" : "Has switched to Normal Mode";
        Toast.makeText(this, tip, Toast.LENGTH_SHORT).show();
        finish();
        return;
      } else if (uri.getQueryParameterNames().contains("_wx_devtool")) {
        WXEnvironment.sRemoteDebugProxyUrl = uri.getQueryParameter("_wx_devtool");
        WXEnvironment.sDebugServerConnectable = true;
        WXSDKEngine.reload();
        Toast.makeText(this, "devtool", Toast.LENGTH_SHORT).show();
        return;
      } else if (code.contains("_wx_debug")) {
        uri = Uri.parse(code);
        String debug_url = uri.getQueryParameter("_wx_debug");
        WXSDKEngine.switchDebugModel(true, debug_url);
        finish();
      } else {
        Toast.makeText(this, code, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Constants.ACTION_OPEN_URL);
        intent.setPackage(getPackageName());
        intent.setData(Uri.parse(code));
        startActivity(intent);
      }
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if (mShakeDetector != null) {
      mShakeDetector.stop();
    }
  }

  public void addCustomDevOption(
      String optionName,
      DevOptionHandler optionHandler) {
    mCustomDevOptions.put(optionName, optionHandler);
  }
}
