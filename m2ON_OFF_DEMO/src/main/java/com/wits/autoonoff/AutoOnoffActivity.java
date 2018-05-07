/*
 * Copyright 2009 Cedric Priscal
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

package com.wits.autoonoff;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import com.wits.serialport.SerialPort;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.view.View.OnClickListener;

public class AutoOnoffActivity extends Activity {

    public static SerialPort mSerialPort;
    public static OutputStream mOutputStream;
    public static InputStream mInputStream;
    public static ReadThread mReadThread;
    private String TAG = "SerialPortActivity";
    private Button mbtnSet = null;
    private Button mbtnOff = null;
    private TextView txtMsg = null;
    private EditText etxtTimes = null;
    private boolean mFlag;
    private Timer timer;
    private Long onTimes;
    //设置自动开机时间后要关闭读串口的线程
    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.arg1) {
                case 1:
                    if (mReadThread != null)
                        mReadThread.interrupt();
                    txtMsg.setText("请点击关机,系统将在" + onTimes + "秒后自动开机!");
                    Log.i(TAG, "mReadThread exit!");
                    break;
            }
            super.handleMessage(msg);
        }
    };

    //读取串口返回数据
    public class ReadThread extends Thread {
        @Override
        public void run() {
            super.run();
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
                                Log.i(TAG, "Data error app will resent data===============" + +buffer[i]);
                                //串口返回0X55,说明单片机已经接收到正确开机数据.
                            } else if (buffer[i] == 0x55) {
                                Log.i(TAG, " Set boot time ok!" + buffer[i]);
                                size = 0;
                                mFlag = false;
                                if (timer != null) {
                                    timer.cancel();
                                    timer = null;
                                    Log.i(TAG, "timer cancel!");
                                }
                                Message msg = new Message();
                                msg.arg1 = 1;
                                myHandler.sendMessage(msg);

                            }
                        }
                        // flush_buffer
                        Arrays.fill(buffer, (byte) 0);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    class WriteTask extends TimerTask {
        public void run() {

            writeOnTimeToMC(1, onTimes);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autooff);
        etxtTimes = (EditText) findViewById(R.id.etxtTimes);
        txtMsg = (TextView) findViewById(R.id.textView1);
        mbtnSet = (Button) findViewById(R.id.btnSet);
        mbtnOff = (Button) findViewById(R.id.btnOff);
        mbtnSet.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mFlag = true;
                onTimes = Long.parseLong(etxtTimes.getText().toString());
                writeOnTimeToMC(1, onTimes);
            }
        });
        mbtnOff.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentSetOff = new Intent();
                intentSetOff.setAction("wits.com.simahuan.shutdown");
                sendBroadcast(intentSetOff);
            }
        });
        //串口读写数据流
        try {
            mSerialPort = getSerialPort();
            mOutputStream = mSerialPort.getOutputStream();
            mInputStream = mSerialPort.getInputStream();
            mFlag = true;
            Log.i(TAG, "-----------------mReadThread.start");
        } catch (SecurityException e) {
            Log.e(TAG, "-----------------SecurityException");
        } catch (IOException e) {
            Log.e(TAG, "-----------------IOException");
        } catch (InvalidParameterException e) {
            Log.e(TAG, "-----------------InvalidParameterException");
        }
    }

    //打开串口
    public SerialPort getSerialPort() throws SecurityException, IOException, InvalidParameterException {
        // M0,M1,M2默认串口号为ttyS7,M3串口号是ttyS5
        mSerialPort = new SerialPort(new File("/dev/ttyS2"), 9600, 0);
        return mSerialPort;
    }

    //关闭串口
    public void closeSerialPort() {
        Log.i(TAG, "closeSerialPort");
        if (mSerialPort != null) {
            mSerialPort.close();
            mSerialPort = null;
        }
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
            Log.e(TAG, "writeOnTimeToMC--------time=" + times);
            int i;
            for (i = 0; i < mBuffer.length; i++)
                Log.i(TAG, "BUFFER-----HASHCODE=" + mBuffer[i]);
            mReadThread = new ReadThread();
            mReadThread.start();

            if (mOutputStream != null) {
                mOutputStream.write(mBuffer);
                Log.e(TAG, "send data 9 byte to serialport.ok");
            } else {
                Log.e(TAG, "mOutputStream:--------null");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    @Override
    protected void onDestroy() {
        if (mReadThread != null)
            mReadThread.interrupt();
        closeSerialPort();
        mSerialPort = null;
        super.onDestroy();
    }
}
