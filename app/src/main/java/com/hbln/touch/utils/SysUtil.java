package com.hbln.touch.utils;

import android.app.Activity;
import android.content.Intent;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.wits.serialport.SerialPort;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by 41569 on 2018/5/6.
 */

public class SysUtil {
    private static final SysUtil ourInstance = new SysUtil();
    private static final Long onTimes = 60L;
    public static SerialPort mSerialPort;
    public static BufferedOutputStream mOutputStream;
    public static BufferedInputStream mInputStream;
    private boolean mFlag;
    private Timer timer;

    private SysUtil() {
        //串口读写数据流
        try {
            mSerialPort = getSerialPort();
            mOutputStream = new BufferedOutputStream(mSerialPort.getOutputStream());
            mInputStream = new BufferedInputStream(mSerialPort.getInputStream());
            mFlag = true;
            LogUtils.d("-----------------mReadThread.start");
        } catch (SecurityException e) {
            LogUtils.e("-----------------SecurityException");
        } catch (IOException e) {
            LogUtils.e("-----------------IOException");
        } catch (InvalidParameterException e) {
            LogUtils.e("-----------------InvalidParameterException");
        }
    }

    public static SysUtil getInstance() {
        return ourInstance;
    }

    //向单片机发送的数据9个byte
    public static byte[] longToByteArray(int flags, long times) {
        byte[] result = new byte[9];
        result[0] = (byte) 0x00;
        result[1] = (byte) 0xaa;
        result[2] = (byte) 0xff;
        result[3] = (byte) 0x55;

        result[4] = (byte) (flags);

        result[5] = (byte) ((times >> 16) & 0xFF);
        result[6] = (byte) ((times >> 8) & 0xFF);
        result[7] = (byte) (times & 0xFF);

        result[8] = (byte) 0x55;

        return result;
    }

    //关闭串口
    public void closeSerialPort() {
        LogUtils.d("closeSerialPort");
        if (mSerialPort != null) {
            mSerialPort.close();
            mSerialPort = null;
        }
    }

    /**
     * 关机
     *
     * @param activity 界面
     */
    public void shutDown(Activity activity) {
        Intent intentSetOff = new Intent();
        intentSetOff.setAction("wits.com.simahuan.shutdown");
        activity.sendBroadcast(intentSetOff);
    }

    public void onDestroy() {
        closeSerialPort();
        mSerialPort = null;
    }

    //打开串口
    public SerialPort getSerialPort() throws SecurityException, IOException, InvalidParameterException {
        // M0,M1,M2默认串口号为ttyS7,M3串口号是ttyS5
        mSerialPort = new SerialPort(new File("/dev/ttyS2"), 9600, 0);
        return mSerialPort;
    }

    /**
     * 设置或取消自动开机时间
     *
     * @param flags 1 为开启自动开机,0为关闭
     * @param times 开机倒计时(单位秒) ,如果要取消自动开机,请将flags,times都设为0
     */
    public void writeOnTimeToMC(int flags, long times) {

        try {
            byte[] mBuffer = longToByteArray(flags, times);
            LogUtils.e("writeOnTimeToMC--------time=" + times);
            int i;
            for (i = 0; i < mBuffer.length; i++) {
                LogUtils.d("BUFFER-----HASHCODE=" + mBuffer[i]);
            }
            Observable.just(null)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .doOnNext(new Action1<Object>() {
                        @Override
                        public void call(Object o) {
                            while (mFlag) {
                                int size;
                                try {
                                    byte[] buffer = new byte[1];
                                    if (mInputStream == null) {
                                        break;
                                    }
                                    size = mInputStream.read(buffer);

                                    if (size > 0) {
                                        for (int i = 0; i < size; i++) {
                                            //如果串口干扰返回错误数据,重新向单片机写入开机时间.
                                            if (buffer[i] != 0x55) {
                                                if (timer == null) {
                                                    timer = new Timer();
                                                    timer.schedule(new WriteTask(), 2000, 1000);
                                                }
                                                LogUtils.d("Data error app will resent data===============" + +buffer[i]);
                                                //串口返回0X55,说明单片机已经接收到正确开机数据.
                                            } else if (buffer[i] == 0x55) {
                                                LogUtils.d(" Set boot time ok!" + buffer[i]);
                                                size = 0;
                                                mFlag = false;
                                                if (timer != null) {
                                                    timer.cancel();
                                                    timer = null;
                                                    LogUtils.d("timer cancel!");
                                                }
                                                throw new SuccessException();
                                            }
                                        }
                                        // flush_buffer
                                        Arrays.fill(buffer, (byte) 0);
                                    }

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    })
                    .subscribe(new Action1<Object>() {
                        @Override
                        public void call(Object o) {

                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            if (throwable instanceof SuccessException) {
                                ToastUtils.showLong("请点击关机,系统将在" + onTimes + "秒后自动开机!");
                            }
                        }
                    }, new Action0() {
                        @Override
                        public void call() {

                        }
                    });

            if (mOutputStream != null) {
                mOutputStream.write(mBuffer);
                LogUtils.e("send data 9 byte to serialport.ok");
            } else {
                LogUtils.e("mOutputStream:--------null");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class WriteTask extends TimerTask {
        @Override
        public void run() {
            writeOnTimeToMC(1, onTimes);
        }
    }

    class SuccessException extends RuntimeException {
        public SuccessException() {
            super("成功");
        }
    }
}
