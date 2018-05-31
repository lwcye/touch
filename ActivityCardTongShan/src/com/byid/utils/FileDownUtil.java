package com.byid.utils;

import android.content.Intent;

import com.blankj.utilcode.util.IntentUtils;
import com.blankj.utilcode.util.LogUtils;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.util.FileDownloadUtils;

import android_serialport_api.sample.base.RWCrashApplication;
import rx.functions.Action1;

/**
 * Created by 41569 on 2018/5/19.
 */
public class FileDownUtil {
    private static final FileDownUtil ourInstance = new FileDownUtil();

    public static FileDownUtil getInstance() {
        return ourInstance;
    }

    private FileDownUtil() {
    }

    /**
     * 下载Apk
     *
     * @param url 下载地址
     */
    public BaseDownloadTask downloadApk(String url) {
        LogUtils.d("download from: " + url);
        BaseDownloadTask baseDownloadTask = FileDownloader.getImpl()
                .create(url)
                .setPath(FileDownloadUtils.getDefaultSaveRootPath() + "/" + "update.apk")
                .setForceReDownload(true)
                .setListener(new FileDownloadListener() {
                    @Override
                    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                    }

                    @Override
                    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                    }

                    @Override
                    protected void completed(final BaseDownloadTask task) {
                        HttpUtil.getInstance().requestChangeStatus(new Action1<String>() {
                            @Override
                            public void call(String s) {
                                Intent installAppIntent = IntentUtils.getInstallAppIntent(task.getPath(), true);
                                RWCrashApplication.application.startActivity(installAppIntent);
                            }
                        });
                    }

                    @Override
                    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                    }

                    @Override
                    protected void warn(BaseDownloadTask task) {
                    }
                });
        baseDownloadTask.start();

        return baseDownloadTask;
    }

}
