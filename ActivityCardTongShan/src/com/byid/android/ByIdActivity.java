package com.byid.android;


import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;

import com.by100.util.AppConfig;
import com.by100.util.CopyFileToSD;
import com.by100.util.NationDeal;
import com.ivsign.android.IDCReader.IDCReaderSDK;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

import android_serialport_api.sample.PowerOperate;
import android_serialport_api.sample.R;
import android_serialport_api.sample.SerialPortActivity;
import android_serialport_api.sample.SerialPortPreferences;


public class ByIdActivity extends SerialPortActivity {
    public int Readflage = -99;
    public int datalen;
    public boolean isRun = true;
    public static boolean isOpen = false;
    public boolean isPlay;
    public byte[] cmd_SAM = {(byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x96, 0x69, 0x00, 0x03, 0x12, (byte) 0xFF, (byte) 0xEE};
    public byte[] cmd_find = {(byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x96, 0x69, 0x00, 0x03, 0x20, 0x01, 0x22};
    public byte[] cmd_selt = {(byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x96, 0x69, 0x00, 0x03, 0x20, 0x02, 0x21};
    public byte[] cmd_read = {(byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x96, 0x69, 0x00, 0x03, 0x30, 0x01, 0x32};
    public byte[] cmd_sleep = {(byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x96, 0x69, 0x00, 0x02, 0x00, 0x02};
    public byte[] cmd_weak = {(byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x96, 0x69, 0x00, 0x02, 0x01, 0x03};
    public byte[] recData = new byte[5000];

    public byte[] myData = {(byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x96, 0x69, 0x05, 0x0A};
    public byte[] myData_b = {(byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x96, 0x69, 0x09, 0x0A};

    public byte[] tempData = new byte[5000];
    public UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public String[] decodeInfo = new String[10];
    public MediaPlayer player;
    public SharedPreferences prefs;
    public static boolean isopent = false;
    public static boolean source = false;
    public static boolean jie = true;
    public static boolean fingerprint = false;
    int flag = 0;


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CopyFileToSD cFileToSD = new CopyFileToSD();
        cFileToSD.initDB(this);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        player = MediaPlayer.create(this, R.raw.success);
        new Thread(new ThreadRun()).start();
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {

    }

    private class ThreadRun implements Runnable {

        @Override
        public void run() {
            while (isRun) {
                try {
                    Thread.sleep(0);

                    if (isOpen)
                        ReadCard();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }


    private void ReadCard() {
        if ((mInputStream == null) || (mInputStream == null)) {

            Readflage = -2;// 连接异常
            return;
        }


        if (mInputStream != null) {

            if (isOpen == true & source == true) {
                try {
                    //读取串口数据到tempData数组中
                    //Thread.sleep(600);
                    datalen = mInputStream.read(tempData);
                    System.out.println("读取到数据" + datalen);


                } catch (Exception e) {
                }
            }
        }

        //把tempData数据存放到recData中，并且计数
        for (int i = 0; i < datalen; i++, flag++) {

            recData[flag] = tempData[i];
        }
        System.out.println("读据");

        //判断串口读取到的数据是无指纹1297字节或是有指纹数据2321字节，主要是预防串口读取数据时出现数据丢失
        try {
            if (flag == 1297 || flag == 2321) {
                System.out.println("读据kkk");
                int not = 0;
                int exist = 0;
                //无指纹数据整合
                for (int i = 0; i < 7; i++) {

                    if (recData[i] == myData[i]) {
                        not++;
                    }
                }
                //有指纹数据整合

                for (int i = 0; i < 7; i++) {
                    if (recData[i] == myData_b[i]) {
                        exist++;
                    }
                }

                //判断串口读取到的数据，如果是有指纹的身份证数据或是无指纹的身份证数据就解析
                if (not == 7 || exist == 7) {
                    not = 0;
                    exist = 0;

                    byte[] newData = new byte[1384];
                    //把recData数组中的数据截取存放到newData，首先存放前14个字节，然后跳过两个字节，再存放1281个字节
                    //注意截取后的身份证数据是1295个字节，所以newData一定是1295个字节。
                    for (int data = 0; data < 14; data++) {
                        newData[data] = recData[data];
                    }
                    for (int data = 14; data < 1281; data++) {
                        newData[data] = recData[2 + data];

                    }


                    if (flag == 2321) {
                        fingerprint = true;
                    }

                    flag = 0;


                    if (newData[9] == -112) {

                        byte[] dataBuf = new byte[256];
                        for (int i = 0; i < 256; i++) {
                            dataBuf[i] = newData[14 + i];
                        }
                        try {
                            String TmpStr = new String(dataBuf, "UTF16-LE");
                            TmpStr = new String(TmpStr.getBytes("UTF-8"));

                            decodeInfo[0] = TmpStr.substring(0, 15);
                            decodeInfo[1] = TmpStr.substring(15, 16);
                            decodeInfo[2] = TmpStr.substring(16, 18);
                            decodeInfo[3] = TmpStr.substring(18, 26);
                            decodeInfo[4] = TmpStr.substring(26, 61);
                            decodeInfo[5] = TmpStr.substring(61, 79);
                            decodeInfo[6] = TmpStr.substring(79, 94);
                            decodeInfo[7] = TmpStr.substring(94, 102);
                            decodeInfo[8] = TmpStr.substring(102, 110);
                            decodeInfo[9] = TmpStr.substring(110, 128);
                        } catch (Exception e) {
                        }
                        if (decodeInfo[1].equals("1"))
                            decodeInfo[1] = "男";
                        else
                            decodeInfo[1] = "女";
                        try {
                            int code = Integer.parseInt(decodeInfo[2]
                                    .toString());
                            decodeInfo[2] = NationDeal.decodeNation(code);
                        } catch (Exception e) {
                            decodeInfo[2] = "";
                        }

                        // 照片解码
                        try {

                            int ret = IDCReaderSDK.Init();
                            if (ret == 0) {
                                byte[] datawlt = new byte[1384];
                                byte[] byLicData = {(byte) 0x05,
                                        (byte) 0x00, (byte) 0x01,
                                        (byte) 0x00, (byte) 0x5B,
                                        (byte) 0x03, (byte) 0x33,
                                        (byte) 0x01, (byte) 0x5A,
                                        (byte) 0xB3, (byte) 0x1E,
                                        (byte) 0x00};

                                for (int i = 0; i < 1295; i++) {
                                    datawlt[i] = newData[i];

                                }

                                int t = IDCReaderSDK.unpack(datawlt, byLicData);

                                if (t == 1) {
                                    Readflage = 1;// 读卡成功
                                } else {
                                    Readflage = 6;// 照片解码异常
                                }
                            } else {
                                Readflage = 6;// 照片解码异常
                            }
                        } catch (Exception e) {
                            Readflage = 6;// 照片解码异常
                        }
                        handler.sendEmptyMessage(0);


                    }
                }


            } else {

                if (flag > 2321) {
                    flag = 0;
                }
            }


        } catch (Exception e) {
        }

    }


    private void sendMessage(byte[] outS) {
        try {
            if (mOutputStream != null) {
                mOutputStream.write(outS);
                mOutputStream.write('\n');
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDataReceived(byte[] buffer, int size) {
        datalen = size;
        if (tempFlag == -1)

            recData = buffer;
    }

    @SuppressLint("HandlerLeak")
    public Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what != 0) {
                return;
            }
            StringBuilder text = new StringBuilder();
            Bitmap bitmap;
            try {
                if (Readflage > 0) {
                    if (fingerprint == true) {
                        fingerprint = false;
                        text.append("姓名：" + decodeInfo[0] + "\n" + "性别："
                                + decodeInfo[1] + "\n" + "民族：" + decodeInfo[2]
                                + "\n" + "出生日期：" + decodeInfo[3] + "\n" + "地址："
                                + decodeInfo[4] + "\n" + "身份号码：" + decodeInfo[5]
                                + "\n" + "签发机关：" + decodeInfo[6] + "\n" + "有效期限："
                                + decodeInfo[7] + "-" + decodeInfo[8] + "\n"
                                + "有指纹" + "\n");
                    } else {
                        text.append("姓名：" + decodeInfo[0] + "\n" + "性别："
                                + decodeInfo[1] + "\n" + "民族：" + decodeInfo[2]
                                + "\n" + "出生日期：" + decodeInfo[3] + "\n" + "地址："
                                + decodeInfo[4] + "\n" + "身份号码：" + decodeInfo[5]
                                + "\n" + "签发机关：" + decodeInfo[6] + "\n" + "有效期限："
                                + decodeInfo[7] + "-" + decodeInfo[8] + "\n"
                                + "无指纹" + "\n");

                    }
                    if (Readflage == 1) {
                        FileInputStream fis = new FileInputStream(
                                Environment.getExternalStorageDirectory()
                                        + "/wltlib/zp.bmp");
                        Bitmap bmp = BitmapFactory.decodeStream(fis);
                        fis.close();
                        bitmap = bmp;
                    } else {
                        text.append("照片解码失败，请检查路径"
                                + AppConfig.RootFile);
                        bitmap = BitmapFactory.decodeResource(
                                getResources(), R.drawable.face);
                    }
                    if (isPlay)
                        player.start();

                } else {
                    bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.face);
                    if (Readflage == -2) {
                        text.append("连接异常");
                    }
                    if (Readflage == -3) {
                        text.append("无卡或卡片已读过");
                    }
                    if (Readflage == -4) {
                        text.append("无卡或卡片已读过");
                    }
                    if (Readflage == -5) {
                        text.append("读卡失败");
                    }
                    if (Readflage == -99) {
                        text.append("操作异常");
                    }
                }
                Thread.sleep(0);
            } catch (IOException e) {
                text.append("读取数据异常！");
                bitmap = BitmapFactory.decodeResource(
                        getResources(), R.drawable.face);
            } catch (InterruptedException e) {
                text.append("读取数据异常！");
                bitmap = BitmapFactory.decodeResource(
                        getResources(), R.drawable.face);
            }
            onReadSfCode(decodeInfo, text, bitmap);
        }
    };

    /**
     * 读取身份证响应的数据
     *
     * @param decodeInfo 身份证所有信息
     * @param text       提示信息
     * @param bitmap     图片
     */
    public void onReadSfCode(String[] decodeInfo, StringBuilder text, Bitmap bitmap) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        isPlay = prefs.getBoolean("checkbox", false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isRun = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
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
                    } catch (Exception e) {
                        // TODO: handle exception
                    }


                }
            }, 500);
        } else {
            if (isOpen == true & source == true & SerialPortPreferences.switching == false) {

                isOpen = false;
                source = false;
                SerialPortPreferences.switching = false;
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {


                        try {
                            //btread.performClick();
                            //PowerOperate.disableRIFID_Module_5Volt();
                        } catch (Exception e) {
                            // TODO: handle exception
                        }
                    }
                }, 500);


            }

        }
        if (source == true & isOpen == false) {

            //PowerOperate.disableRIFID_Module_5Volt();
        }

    }


}