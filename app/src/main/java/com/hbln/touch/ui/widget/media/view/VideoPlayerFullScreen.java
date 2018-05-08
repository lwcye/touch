package com.hbln.touch.ui.widget.media.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.hbln.touch.R;

import cn.jzvd.JZVideoPlayerStandard;


/**
 * <p>进来就是全屏的VideoPlayer</p><br>
 *
 * @author - lwc
 * @date - 2018/2/26
 * @note - 全屏状态播放完成，不退出全屏
 * -------------------------------------------------------------------------------------------------
 * @modified -
 * @date -
 * @note -
 */
public class VideoPlayerFullScreen extends JZVideoPlayerStandard {

    /**
     * 构造类
     *
     * @param context 上下文
     */
    public VideoPlayerFullScreen(Context context) {
        super(context);
    }

    /**
     * 构造类
     *
     * @param context 上下文
     * @param attrs 属性
     */
    public VideoPlayerFullScreen(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void init(Context context) {
        super.init(context);
    }

    @Override
    public void setUp(Object[] dataSourceObjects, int defaultUrlMapIndex, int screen, Object... objects) {
        super.setUp(dataSourceObjects, defaultUrlMapIndex, screen, objects);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.fullscreen) {
        } else if (i == R.id.back && getContext() instanceof Activity) {
            //全屏的返回，统一使用activity的返回
            ((Activity) getContext()).onBackPressed();
        } else {
            super.onClick(v);
        }
    }

    @Override
    public void onAutoCompletion() {
        //全屏状态播放完成，不退出全屏
        if (currentScreen == SCREEN_WINDOW_FULLSCREEN) {
            onStateAutoComplete();
        } else {
            super.onAutoCompletion();
        }
    }
}
