package com.hbln.touch.base;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;


public abstract class BaseActivity extends AppCompatActivity {
    public BaseActivity getActivity() {
        return this;
    }

    public Context getContext() {
        return this;
    }

    public abstract void initView();

    public abstract void initData();
}
