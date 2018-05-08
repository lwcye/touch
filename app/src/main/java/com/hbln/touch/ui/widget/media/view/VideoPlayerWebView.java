package com.hbln.touch.ui.widget.media.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AbsoluteLayout;

import com.hbln.touch.ui.widget.media.player.CustomIjkMediaPlayer;

import org.xutils.image.ImageOptions;
import org.xutils.x;

import cn.jzvd.JZUtils;
import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerStandard;


/**
 * <p>WebView的视频播放器</p><br>
 *
 * @author - lwc
 * @date - 2018/2/27
 * @note -
 * -------------------------------------------------------------------------------------------------
 * @modified -
 * @date -
 * @note -
 */
public class VideoPlayerWebView extends JZVideoPlayerStandard {
    /**
     * 构造类
     *
     * @param context 上下文
     */
    public VideoPlayerWebView(Context context) {
        super(context);
    }

    /**
     * 构造类
     *
     * @param context 上下文
     * @param attrs 属性
     */
    public VideoPlayerWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 删除视频播放
     *
     * @param webView WebView
     * @param title 标题.
     * @return true--成功 false--失败
     */
    public static boolean removeVideoPlayer(WebView webView, String title) {
        if (webView == null || TextUtils.isEmpty(title)) {
            return false;
        }
        View view = webView.findViewWithTag(title);
        if (view != null) {
            webView.removeView(view);
            releaseAllVideos();
            return true;
        } else {
            return false;
        }
    }

    /**
     * WebView 增加 VideoPlayer
     *
     * @param webView WebView
     * @param width 宽
     * @param height 高
     * @param top 上
     * @param left 左
     * @param url 播放链接
     * @param title 标题
     * @param thumbImage 微缩图
     * @return true--成功 false--失败
     */
    public boolean addVideoPlayer(WebView webView, final int width, final int height, final int top, final int left, String url, String title, String thumbImage) {
        if (webView == null || TextUtils.isEmpty(url)) {
            return false;
        }
        //标题为null，则隐藏标题
        if (TextUtils.isEmpty(title)) {
            titleTextView.setVisibility(GONE);
        } else {
            titleTextView.setVisibility(VISIBLE);
            View viewWithTag = webView.findViewWithTag(title);
            if (viewWithTag != null) {
                return false;
            } else {
                setTag(title);
            }
        }
        JZVideoPlayer.setMediaInterface(new CustomIjkMediaPlayer());
        setUp(url, JZVideoPlayer.SCREEN_WINDOW_LIST, title);
        //微缩图为null，则不加载
        if (!TextUtils.isEmpty(thumbImage)) {
            x.image().bind(thumbImageView, thumbImage, ImageOptions.DEFAULT);
        }


        ViewGroup.LayoutParams ll = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        AbsoluteLayout.LayoutParams layoutParams = new AbsoluteLayout.LayoutParams(ll);
        layoutParams.y = JZUtils.dip2px(getContext(), top);
        layoutParams.x = JZUtils.dip2px(getContext(), left);
        layoutParams.height = JZUtils.dip2px(getContext(), height);
        layoutParams.width = JZUtils.dip2px(getContext(), width);
        webView.addView(this, layoutParams);
        return true;
    }
}
