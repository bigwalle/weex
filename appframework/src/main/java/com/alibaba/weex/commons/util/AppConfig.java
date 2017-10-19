package com.alibaba.weex.commons.util;

import android.content.Context;


/**
 * Created by budao on 2016/10/12.
 */
public class AppConfig {
  private static final String TAG = "AppConfig";
  private static AppPreferences sPreferences = new AppPreferences();

  public static void init(Context context) {
    loadAppSetting(context);
  }

  public static String getLaunchUrl() {
//    if (isLaunchLocally()) {
//      return sPreferences.getString("local_url", "file://assets/index.js");
//    }
//    return sPreferences.getString("launch_url", "http://192.168.199.142:8080/dist/app.weex.js");
    //http://59.41.39.55:8019/dist/app.native.js
    return  "http://192.168.199.146:8081/dist/app.native.js";
  }

  public static Boolean isLaunchLocally() {
    return sPreferences.getBoolean("launch_locally", false);
  }

  private static void loadAppSetting(Context context) {
    AppConfigXmlParser parser = new AppConfigXmlParser();
    parser.parse(context);
    sPreferences = parser.getPreferences();
  }
}
