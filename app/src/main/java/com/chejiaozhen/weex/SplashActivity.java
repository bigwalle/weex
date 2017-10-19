package com.chejiaozhen.weex;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;

import com.alibaba.weex.commons.util.AppConfig;
public class SplashActivity extends AppCompatActivity {


  private Handler myHandler = new Handler();
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_splash);


    myHandler.postDelayed(new Runnable() {
      @Override
      public void run() {
        String url = AppConfig.getLaunchUrl();
        if (!TextUtils.isEmpty(url)) {
          Intent intent = new Intent(Intent.ACTION_VIEW);
          String scheme = Uri.parse(url).getScheme();
          StringBuilder builder = new StringBuilder();
          if (TextUtils.equals("file", scheme)) {
            intent.putExtra("isLocal", true);
          } else if (!TextUtils.equals("http", scheme) && !TextUtils.equals("https", scheme)) {
            builder.append("http:");
          }
          builder.append(url);
          Uri uri = Uri.parse(builder.toString());
          intent.setData(uri);
          intent.addCategory("com.taobao.android.intent.category.WEEX");
          intent.setPackage(getPackageName());
          startActivity(intent);
          finish();
        }
      }
    },1000);

  }
}
