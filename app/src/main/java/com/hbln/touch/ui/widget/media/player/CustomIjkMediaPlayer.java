package com.hbln.touch.ui.widget.media.player;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.view.Surface;

import com.blankj.utilcode.util.ObjectUtils;

import java.io.IOException;
import java.net.URI;

import cn.jzvd.JZMediaInterface;
import cn.jzvd.JZMediaManager;
import cn.jzvd.JZVideoPlayerManager;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.IjkTimedText;

/**
 * <p>自定义的 ijk 播放器</p><br>
 *
 * @author - lwc
 * @date - 2018/2/27 15:52
 * @note -
 * -------------------------------------------------------------------------------------------------
 * @modified -
 * @date -
 * @note -
 */
public class CustomIjkMediaPlayer extends JZMediaInterface implements IMediaPlayer.OnPreparedListener, IMediaPlayer.OnVideoSizeChangedListener,
        IMediaPlayer.OnCompletionListener, IMediaPlayer.OnErrorListener, IMediaPlayer.OnInfoListener, IMediaPlayer.OnBufferingUpdateListener,
        IMediaPlayer.OnSeekCompleteListener, IMediaPlayer.OnTimedTextListener {
    /** ijk 播放器 */
    private tv.danmaku.ijk.media.player.IjkMediaPlayer mIjkMediaPlayer;

    @Override
    public void start() {
        mIjkMediaPlayer.start();
    }

    @Override
    public void prepare() {
        mIjkMediaPlayer = new tv.danmaku.ijk.media.player.IjkMediaPlayer();
        mIjkMediaPlayer.setOption(tv.danmaku.ijk.media.player.IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 0);
        mIjkMediaPlayer.setOption(tv.danmaku.ijk.media.player.IjkMediaPlayer.OPT_CATEGORY_PLAYER, "opensles", 0);
        mIjkMediaPlayer.setOption(tv.danmaku.ijk.media.player.IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", IjkMediaPlayer.SDL_FCC_RV32);
        mIjkMediaPlayer.setOption(tv.danmaku.ijk.media.player.IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 1);
        mIjkMediaPlayer.setOption(tv.danmaku.ijk.media.player.IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 0);
        mIjkMediaPlayer.setOption(tv.danmaku.ijk.media.player.IjkMediaPlayer.OPT_CATEGORY_FORMAT, "http-detect-range-support", 0);
        mIjkMediaPlayer.setOption(tv.danmaku.ijk.media.player.IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 48);

        mIjkMediaPlayer.setOnPreparedListener(CustomIjkMediaPlayer.this);
        mIjkMediaPlayer.setOnVideoSizeChangedListener(CustomIjkMediaPlayer.this);
        mIjkMediaPlayer.setOnCompletionListener(CustomIjkMediaPlayer.this);
        mIjkMediaPlayer.setOnErrorListener(CustomIjkMediaPlayer.this);
        mIjkMediaPlayer.setOnInfoListener(CustomIjkMediaPlayer.this);
        mIjkMediaPlayer.setOnBufferingUpdateListener(CustomIjkMediaPlayer.this);
        mIjkMediaPlayer.setOnSeekCompleteListener(CustomIjkMediaPlayer.this);
        mIjkMediaPlayer.setOnTimedTextListener(CustomIjkMediaPlayer.this);

        try {
            if (currentDataSource != null) {
                mIjkMediaPlayer.setDataSource(currentDataSource.toString());
                mIjkMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mIjkMediaPlayer.setScreenOnWhilePlaying(true);
                mIjkMediaPlayer.prepareAsync();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void pause() {
        mIjkMediaPlayer.pause();
    }

    @Override
    public boolean isPlaying() {
        return mIjkMediaPlayer.isPlaying();
    }

    @Override
    public void seekTo(long time) {
        if (mIjkMediaPlayer != null) {
            if (time < getCurrentPosition() && canSeekBackward()) {
                mIjkMediaPlayer.seekTo(time);
            } else if (time > getCurrentPosition() && canSeekForward()) {
                mIjkMediaPlayer.seekTo(time);
            }
        }
    }

    @Override
    public void release() {
        if (mIjkMediaPlayer != null) {
            mIjkMediaPlayer.release();
        }
    }

    @Override
    public long getCurrentPosition() {
        if (mIjkMediaPlayer != null) {
            return mIjkMediaPlayer.getCurrentPosition();
        } else {
            prepare();
        }
        return 0L;
    }

    @Override
    public long getDuration() {
        return mIjkMediaPlayer.getDuration();
    }

    @Override
    public void setSurface(Surface surface) {
        mIjkMediaPlayer.setSurface(surface);
    }

    @Override
    public void setVolume(float leftVolume, float rightVolume) {
        mIjkMediaPlayer.setVolume(leftVolume, rightVolume);
    }

    @Override
    public void onPrepared(IMediaPlayer iMediaPlayer) {
        mIjkMediaPlayer.start();
        if (currentDataSource.toString().toLowerCase().contains("mp3")) {
            JZMediaManager.instance().mainThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (JZVideoPlayerManager.getCurrentJzvd() != null) {
                        JZVideoPlayerManager.getCurrentJzvd().onPrepared();
                    }
                }
            });
        }
    }

    @Override
    public void onVideoSizeChanged(IMediaPlayer iMediaPlayer, int i, int i1, int i2, int i3) {
        JZMediaManager.instance().currentVideoWidth = iMediaPlayer.getVideoWidth();
        JZMediaManager.instance().currentVideoHeight = iMediaPlayer.getVideoHeight();
        JZMediaManager.instance().mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (JZVideoPlayerManager.getCurrentJzvd() != null) {
                    JZVideoPlayerManager.getCurrentJzvd().onVideoSizeChanged();
                }
            }
        });
    }

    @Override
    public void onCompletion(IMediaPlayer iMediaPlayer) {
        JZMediaManager.instance().mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (JZVideoPlayerManager.getCurrentJzvd() != null) {
                    JZVideoPlayerManager.getCurrentJzvd().onAutoCompletion();
                }
            }
        });
    }

    @Override
    public boolean onError(IMediaPlayer iMediaPlayer, final int what, final int extra) {
        JZMediaManager.instance().mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (JZVideoPlayerManager.getCurrentJzvd() != null) {
                    JZVideoPlayerManager.getCurrentJzvd().onError(what, extra);
                }
            }
        });
        return true;
    }

    @Override
    public boolean onInfo(IMediaPlayer iMediaPlayer, final int what, final int extra) {
        JZMediaManager.instance().mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (JZVideoPlayerManager.getCurrentJzvd() != null) {
                    if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                        JZVideoPlayerManager.getCurrentJzvd().onPrepared();
                    } else {
                        JZVideoPlayerManager.getCurrentJzvd().onInfo(what, extra);
                    }
                }
            }
        });
        return false;
    }

    @Override
    public void onBufferingUpdate(IMediaPlayer iMediaPlayer, final int percent) {
        JZMediaManager.instance().mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (JZVideoPlayerManager.getCurrentJzvd() != null) {
                    JZVideoPlayerManager.getCurrentJzvd().setBufferProgress(percent);
                }
            }
        });
    }

    @Override
    public void onSeekComplete(IMediaPlayer iMediaPlayer) {
        JZMediaManager.instance().mainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (JZVideoPlayerManager.getCurrentJzvd() != null) {
                    JZVideoPlayerManager.getCurrentJzvd().onSeekComplete();
                }
            }
        });
    }

    @Override
    public void onTimedText(IMediaPlayer iMediaPlayer, IjkTimedText ijkTimedText) {

    }

    /**
     * 判断是否能后退
     *
     * @return true 能 false 否
     */
    private boolean canSeekBackward() {
        if (getDuration() < getCurrentPosition()) {
            return false;
        }
        if (ObjectUtils.isNotEmpty(currentDataSource)) {
            URI mUri = URI.create(currentDataSource.toString());
            if (mUri != null && !TextUtils.isEmpty(mUri.getScheme()) && !TextUtils.isEmpty(mUri.getPath())) {
                if (mUri.getScheme().equals("rtmp") || mUri.getScheme().equals("rtsp") || mUri.getPath().contains("m3u8")) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 判断是否能快进
     *
     * @return true 能 false 否
     */
    private boolean canSeekForward() {
        if (getDuration() < getCurrentPosition()) {
            return false;
        }
        if (ObjectUtils.isNotEmpty(currentDataSource)) {
            URI mUri = URI.create(currentDataSource.toString());
            if (mUri != null && !TextUtils.isEmpty(mUri.getScheme()) && !TextUtils.isEmpty(mUri.getPath())) {
                if (mUri.getScheme().equals("rtmp") || mUri.getScheme().equals("rtsp") || mUri.getPath().contains("m3u8")) {
                    return false;
                }
            }
        }
        return true;
    }
}
