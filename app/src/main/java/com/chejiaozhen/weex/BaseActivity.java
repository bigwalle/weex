package com.chejiaozhen.weex;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.ViewConfiguration;

import java.lang.reflect.Field;

import butterknife.ButterKnife;

/**
 * Created by 53257 on 2017/10/18.
 */

public abstract class BaseActivity extends Activity {

    protected Context mContext			= null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mContext = this;
//        KevinApplication.getInstance().mActivityStack.addActivity(this);
        setOverflowShowingAlways();
        super.onCreate(savedInstanceState);

        initContentView();
        ButterKnife.bind(this);
        init();
        initViews();
        initEvents();
    }

    /**
     * 设置总是显示溢出菜单
     *
     * @return void
     * @date 2015-7-25 12:01:31
     */
    private void setOverflowShowingAlways() {
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            menuKeyField.setAccessible(true);
            menuKeyField.setBoolean(config, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    public void finish() {
        super.finish();
//        KevinApplication.getInstance().mActivityStack.removeActivity(this);
    }

    /**
     * 初始化布局
     */
    protected abstract void initContentView();

    /**
     * 初始化
     */
    protected void init() {}

    /**
     * 初始化View
     */
    protected abstract void initViews();

    /**
     * 初始化事件
     */
    protected abstract void initEvents();
}
