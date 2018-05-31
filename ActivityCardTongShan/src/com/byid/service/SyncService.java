package com.byid.service;

import android.content.Intent;
import android.os.IBinder;

import com.blankj.utilcode.util.LogUtils;
import com.xdandroid.hellodaemon.AbsWorkService;

import rx.Subscription;

public class SyncService extends AbsWorkService {
    //是否 任务完成, 不再需要服务运行?
    public static boolean sShouldStopService;
    public static Subscription sDisposable;

    public SyncService() {
    }

    public static void stopService() {
        //我们现在不再需要服务运行了, 将标志位置为 true
        sShouldStopService = true;
        //取消对任务的订阅
        if (sDisposable != null) {
            sDisposable.unsubscribe();
        }
        //取消 Job / Alarm / Subscription
        cancelJobAlarmSub();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public Boolean shouldStopService(Intent intent, int flags, int startId) {
        return sShouldStopService;
    }

    @Override
    public void startWork(Intent intent, int flags, int startId) {

    }

    @Override
    public void stopWork(Intent intent, int flags, int startId) {
        stopService();

    }

    /**
     * 任务是否正在运行?
     *
     * @return 任务正在运行, true; 任务当前不在运行, false; 无法判断, 什么也不做, null.
     */
    @Override
    public Boolean isWorkRunning(Intent intent, int flags, int startId) {
        //若还没有取消订阅, 就说明任务仍在运行.
        return sDisposable != null && !sDisposable.isUnsubscribed();
    }

    @Override
    public IBinder onBind(Intent intent, Void v) {
        return null;
    }

    @Override
    public void onServiceKilled(Intent rootIntent) {
        LogUtils.e("onServiceKilled");
    }
}
