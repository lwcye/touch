package com.hbln.touch.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.hbln.touch.R;
import com.hbln.touch.base.BaseActivity;
import com.hbln.touch.ui.widget.media.player.CustomIjkMediaPlayer;
import com.hbln.touch.ui.widget.media.view.VideoPlayerFullScreen;

import cn.jzvd.JZUserAction;
import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerStandard;


/**
 * <p>视频播放</p><br>
 *
 * @author - lwc
 * @date - 2017/11/27
 * @note -
 * 需要通过Intent传递 INTENT_VIDEO_PATH，INTENT_VIDEO_TITLE，INTENT_VIDEO_SEEK
 * -------------------------------------------------------------------------------------------------
 * @modified - lwc
 * @date -2018/02/26
 * @note - 修改为JiaoZi的框架
 */
public class VideoActivity extends BaseActivity implements JZUserAction {
    /** 视频链接INTENT_KEY */
    public static final String INTENT_VIDEO_PATH = "url";
    /** 视频标题INTENT_KEY */
    public static final String INTENT_VIDEO_TITLE = "title";
    /** 视频进度INTENT_KEY */
    public static final String INTENT_VIDEO_SEEK = "seek";
    /** 全屏的播放器 */
    private VideoPlayerFullScreen mMyJZVideoPlayerFullScreenStandard;
    /** 视频链接 */
    private String mVideoPath;
    /** 视频标题 */
    private String mVideoTitle;
    /** 视频进度 */
    private int mVideoSeek;
    /** 视频 */
    private Uri mVideoUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        handleIntent(getIntent());
        //初始化布局
        initView();
    }

    /**
     * 初始化布局
     */
    @Override
    public void initView() {
        mMyJZVideoPlayerFullScreenStandard = (VideoPlayerFullScreen) findViewById(R.id.vp_video);

        //设置为ijk播放器
        JZVideoPlayer.setMediaInterface(new CustomIjkMediaPlayer());
        if (mVideoPath != null) {
            mMyJZVideoPlayerFullScreenStandard.setUp(mVideoPath, JZVideoPlayerStandard.SCREEN_WINDOW_FULLSCREEN, mVideoTitle);
        } else if (mVideoUri != null) {
            mMyJZVideoPlayerFullScreenStandard.setUp(mVideoUri.toString(), JZVideoPlayerStandard.SCREEN_WINDOW_FULLSCREEN, mVideoTitle);
        } else {
            finish();
            return;
        }

        //自动播放
        mMyJZVideoPlayerFullScreenStandard.startVideo();

    }

    @Override
    public void initData() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        //home back
        JZVideoPlayer.goOnPlayOnResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        JZVideoPlayer.clearSavedProgress(this, null);
        //home back
        JZVideoPlayer.goOnPlayOnPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        JZVideoPlayer.releaseAllVideos();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    /**
     * 处理Handle数据
     *
     * @param intent 传递过来的意图
     */
    private void handleIntent(Intent intent) {
        mVideoPath = intent.getStringExtra(INTENT_VIDEO_PATH);
        mVideoTitle = intent.getStringExtra(INTENT_VIDEO_TITLE);
        try {
            mVideoSeek = Integer.valueOf(intent.getStringExtra(INTENT_VIDEO_SEEK));
        } catch (NumberFormatException ignored) {
        }
        String intentAction = intent.getAction();
        if (!TextUtils.isEmpty(intentAction)) {
            if (intentAction.equals(Intent.ACTION_VIEW)) {
                mVideoPath = intent.getDataString();
            } else if (intentAction.equals(Intent.ACTION_SEND)) {
                mVideoUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
            }
        }
    }

    @Override
    public void onEvent(int type, Object url, int screen, Object... objects) {

    }
}
