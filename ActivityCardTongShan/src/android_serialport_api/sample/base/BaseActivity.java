package android_serialport_api.sample.base;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;


public abstract class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean ret = super.dispatchTouchEvent(ev);
        /*
         * 若点击Activity的任何区域(除了输入框之外，应隐藏键盘)
         */
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            View v = getCurrentFocus();

            if (v != null && shouldHideInput(v, ev)) {
                hideInput(v);
            }
        }

        return ret;
    }

    /**
     * 是否应该隐藏输入
     *
     * @param v     焦点控件
     * @param event 动作事件
     * @return true -- 是  false -- 否
     */
    protected boolean shouldHideInput(View v, MotionEvent event) {
        boolean should = true;

        // 仅点击到输入框时，键盘不隐藏
        if (v != null && v instanceof EditText) {
            int[] loc = new int[2];
            v.getLocationOnScreen(loc);

            // 焦点控件位置
            int left = loc[0];
            int top = loc[1];
            int right = left + v.getWidth();
            int bottom = top + v.getHeight();

            int touchX = (int) event.getRawX();
            int touchY = (int) event.getRawY();

            // 是否点击到输入框
            if ((touchX >= left && touchX <= right) &&
                    (touchY >= top && touchY <= bottom)) {

                should = false;
            }
        }

        return should;
    }

    /**
     * 隐藏键盘
     *
     * @param v 控件
     */
    private void hideInput(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public BaseActivity getActivity() {
        return this;
    }

    public Context getContext() {
        return this;
    }

    public abstract void initView();

    public abstract void initData();

    /**
     * 通过兼容取Color
     *
     * @param resId ColorRes
     * @return ColorInt
     */
    @ColorInt
    public int getCompatColor(@ColorRes int resId) {
        return ContextCompat.getColor(this, resId);
    }

    /**
     * 通过兼容器取Drawable
     *
     * @param resId DrawableRes
     * @return Drawable
     */
    public Drawable getCompatDrawable(@DrawableRes int resId) {
        return ContextCompat.getDrawable(this, resId);
    }

    /**
     * 通过根布局在主线程的Handle运行runnable
     *
     * @param runnable task
     */
    public void post(Runnable runnable) {
        getWindow().getDecorView().post(runnable);
    }

    @CallSuper
    @Override
    protected void onDestroy() {
        getWindow().getDecorView().removeCallbacks(null);
        super.onDestroy();
    }

    public String TAG() {
        return getClass().getSimpleName();
    }
}
