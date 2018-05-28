package com.byid.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.blankj.utilcode.util.ToastUtils;
import com.byid.android.ByIdActivity;
import com.byid.utils.HttpUtil;

import android_serialport_api.sample.PowerOperate;
import android_serialport_api.sample.R;
import android_serialport_api.sample.SerialPortPreferences;
import android_serialport_api.sample.SerialPortPreferencesFinish;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by 41569 on 2018/5/19.
 */

public class HomeActivity extends ByIdActivity implements View.OnClickListener {
    private AppCompatImageView mIvReadUserIcon;
    /**
     * 读取二代证卡
     */
    private AppCompatButton mBtnRead;
    /**
     * 身份证扫描
     */
    private Button mBtnHomeRead;
    /**
     * 用户登陆
     */
    private Button mBtnHomePwd;
    private LinearLayout mLlHomeRead;
    /**
     * 输入用户身份证
     */
    private EditText mEtHomeUsername;
    /**
     * 输入用户密码
     */
    private EditText mEtHomePwd;
    /**
     * 登陆
     */
    private AppCompatButton mBtnLogin;
    private LinearLayout mLlHomePwd;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initView();
        HttpUtil.getInstance().getConfig(10, true);
//        HttpUtil.getInstance().login("51222619550110641X",null,this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mBtnRead.setEnabled(true);
    }

    @Override
    public void onReadSfCode(String[] decodeInfo, StringBuilder text, Bitmap bitmap) {
        super.onReadSfCode(decodeInfo, text, bitmap);
        if (decodeInfo.length > 5 && !TextUtils.isEmpty(decodeInfo[5])) {
            HttpUtil.getInstance().login(decodeInfo[5], null, this);
        }
    }

    /**
     * 读取身份证
     */
    private void readSfCode() {
        if (isOpen == false & source == true & SerialPortPreferences.switching == true) {

            isOpen = true;
            source = true;
            ToastUtils.showShort("开始读卡");
            return;

        }
        if (isOpen == false & source == false & SerialPortPreferences.switching == false) {

            //PowerOperate.enableRIFID_Module_5Volt();
            source = true;
            ToastUtils.showShort("开始读卡");
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {


                    try {
                        // btread.performClick();

                        isOpen = true;
                        return;
                    } catch (Exception e) {
                    }


                }
            }, 1500);


        } else {
            if (isOpen == true & source == true & SerialPortPreferences.switching == false) {

                source = false;
                isOpen = false;
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {


                        try {
                            // btread.performClick();
                            //PowerOperate.disableRIFID_Module_5Volt();
                            ToastUtils.showShort("已经关闭");
                        } catch (Exception e) {
                        }
                    }
                }, 1500);
            } else {
                if (isOpen == true & source == true & SerialPortPreferences.switching == true) {

                    isOpen = false;
                    source = false;
                    SerialPortPreferences.switching = false;
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {


                            try {
                                //btread.performClick();
                                PowerOperate.disableRIFID_Module_5Volt();
                                ToastUtils.showShort("已经关闭");
                            } catch (Exception e) {
                            }
                        }
                    }, 500);
                }

            }
        }
        if (isOpen == false & source == false & SerialPortPreferences.switching == true) {

            PowerOperate.enableRIFID_Module_5Volt();
            source = true;
            ToastUtils.showShort("开始读卡");
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {


                    try {
                        // btread.performClick();
                        isOpen = true;

                    } catch (Exception e) {
                    }
                }
            }, 1500);
        }

    }

    /**
     * 设置
     */
    private void setting() {
        startActivity(new Intent(this, SerialPortPreferencesFinish.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_home_read:
//                mBtnHomeRead.setBackgroundColor(Color.parseColor("#3fff"));
//                mBtnHomePwd.setBackgroundColor(Color.parseColor("#3000"));
                mLlHomeRead.setVisibility(View.VISIBLE);
                mLlHomePwd.setVisibility(View.GONE);
                break;
            case R.id.btn_home_pwd:
//                mBtnHomePwd.setBackgroundColor(Color.parseColor("#3fff"));
//                mBtnHomeRead.setBackgroundColor(Color.parseColor("#3000"));
                mLlHomeRead.setVisibility(View.GONE);
                mLlHomePwd.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_read:
                Observable.just(null)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(o -> setting(), throwable -> ToastUtils.showLong(throwable.getMessage()), () -> {
                            readSfCode();
                            mBtnRead.setEnabled(false);
                        });
                break;
            case R.id.btn_login:
                String username = mEtHomeUsername.getText().toString().trim();
                if (TextUtils.isEmpty(username)) {
                    ToastUtils.showLong("请输入用户身份证");
                    break;
                }
                String password = mEtHomePwd.getText().toString().trim();
                if (TextUtils.isEmpty(password)) {
                    ToastUtils.showLong("请输入登陆密码");
                    break;
                }
                HttpUtil.getInstance().login(username, password, this);
                break;
            default:
                break;
        }
    }

    @Override
    public void initView() {
        mIvReadUserIcon = (AppCompatImageView) findViewById(R.id.iv_read_user_icon);
        mBtnRead = (AppCompatButton) findViewById(R.id.btn_read);
        mBtnHomeRead = (Button) findViewById(R.id.btn_home_read);
        mBtnHomeRead.setOnClickListener(this);
        mBtnHomePwd = (Button) findViewById(R.id.btn_home_pwd);
        mBtnHomePwd.setOnClickListener(this);
        mBtnRead.setOnClickListener(this);
        mBtnRead.setEnabled(true);
        mLlHomeRead = (LinearLayout) findViewById(R.id.ll_home_read);
        mEtHomeUsername = (EditText) findViewById(R.id.et_home_username);
        mEtHomePwd = (EditText) findViewById(R.id.et_home_pwd);
        mBtnLogin = (AppCompatButton) findViewById(R.id.btn_login);
        mBtnLogin.setOnClickListener(this);
        mLlHomePwd = (LinearLayout) findViewById(R.id.ll_home_pwd);
    }
}
