package com.byid.android;


import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android_serialport_api.sample.R;
import android_serialport_api.sample.SerialPortActivity;
import android_serialport_api.sample.SerialPortPreferences;

import com.by100.util.AppConfig;
import com.by100.util.CopyFileToSD;
import com.by100.util.NationDeal;
import com.ivsign.android.IDCReader.IDCReaderSDK;


public class ByIdActivity extends SerialPortActivity {
    int Readflage = -99;
    int datalen;
    boolean isRun = true;
    boolean isOpen = false;
    boolean isPlay;
    byte[] cmd_SAM = {(byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x96, 0x69, 0x00, 0x03, 0x12, (byte) 0xFF, (byte) 0xEE  };
    byte[] cmd_find  = {(byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x96, 0x69, 0x00, 0x03, 0x20, 0x01, 0x22  };
	byte[] cmd_selt  = {(byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x96, 0x69, 0x00, 0x03, 0x20, 0x02, 0x21  };
	byte[] cmd_read  = {(byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x96, 0x69, 0x00, 0x03, 0x30, 0x01, 0x32 };
	byte[] cmd_sleep  = {(byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x96, 0x69, 0x00, 0x02, 0x00, 0x02};
	byte[] cmd_weak  = {(byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0x96, 0x69, 0x00, 0x02, 0x01, 0x03 };
	byte[] recData = new byte[1500];
	
    String DEVICE_NAME1 = "BY-100A";
    String DEVICE_NAME2 = "IDCReader";
    String DEVICE_NAME3 = "COM2";
    String DEVICE_NAME4 = "BOLUTEK";
    byte[] tempData;
    UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    String[] decodeInfo = new String[10];
    TextView ett;
    ImageView image;
    Button btread;
    MediaPlayer player;
    SharedPreferences prefs;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        CopyFileToSD cFileToSD = new CopyFileToSD();
        cFileToSD.initDB(this);
        prefs =PreferenceManager.getDefaultSharedPreferences(this);
        player = MediaPlayer.create(this,R.raw.success);

        Button btconn = (Button)findViewById(R.id.btconn);
        btread = (Button)findViewById(R.id.btread);
        ett = (TextView)findViewById(R.id.textView1);
        image = (ImageView)findViewById(R.id.imageView1);   
        btconn.setOnClickListener(new Button.OnClickListener(){
        	public void onClick(View v){
        		startActivity(new Intent(ByIdActivity.this, SerialPortPreferences.class));
        	}
        });
        btread.setOnClickListener(new Button.OnClickListener(){
        	public void onClick(View v){
        		isOpen = !isOpen;
        		if(isOpen){
        			btread.setText("停止");
        		}else{
        			btread.setText("读卡");
        		}
            }
        });
        new Thread(new ThreadRun()).start();
    }
    private class ThreadRun implements Runnable{

		@Override
		public void run() {
			while(isRun){
				try{
					Thread.sleep(0);
					if(isOpen)
						ReadCard();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
    	
    } 

	private void ReadCard(){
		try {
			if ((mInputStream == null) || (mInputStream == null)) {
				Readflage = -2;// 连接异常
				return;
			}
			mOutputStream.write(cmd_find);
			
			Thread.sleep(200);
			int datalen = mInputStream.read(recData);
			
			if (recData[9] == -97) {
				
				mOutputStream.write(cmd_selt);
				
				Thread.sleep(200);
				datalen = mInputStream.read(recData);
				System.out.println("5");
				if (recData[9] == -112) {
					
					mOutputStream.write(cmd_read);
					
					Thread.sleep(0);
					datalen=0;
					byte[] tempData = new byte[1500];
					if (mInputStream.available() > 0) {
						datalen = mInputStream.read(tempData);
						
					} else {
						Thread.sleep(500);
						if (mInputStream.available() > 0) {
							datalen = mInputStream.read(tempData);
							
						}
					}
					int flag = 0;
					if (datalen < 1294) {
						for (int i = 0; i < datalen; i++, flag++) {
							recData[flag] = tempData[i];
						}
						Thread.sleep(1000);
						if (mInputStream.available() > 0) {
							datalen = mInputStream.read(tempData);
						} else {
							Thread.sleep(500);
							if (mInputStream.available() > 0) {
								datalen = mInputStream.read(tempData);
							}
						}
						for (int i = 0; i < datalen; i++, flag++) {
							recData[flag] = tempData[i];
						}

					} else {
						for (int i = 0; i < datalen; i++, flag++) {
							recData[flag] = tempData[i];
						}
					}
					tempData = null;
					
					if(flag == 1295){
						
						if (recData[9] == -112) {

							byte[] dataBuf = new byte[256];
							for (int i = 0; i < 256; i++) {
								dataBuf[i] = recData[14 + i];
							}
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
								if (ret == 0){
									byte[] datawlt = new byte[1384];
									byte[] byLicData = { (byte) 0x05,
											(byte) 0x00, (byte) 0x01,
											(byte) 0x00, (byte) 0x5B,
											(byte) 0x03, (byte) 0x33,
											(byte) 0x01, (byte) 0x5A,
											(byte) 0xB3, (byte) 0x1E,
											(byte) 0x00 };
									for (int i = 0; i < 1295; i++) {
										datawlt[i] = recData[i];
									}
									int t = IDCReaderSDK.unpack(datawlt,
											byLicData);
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
						} else {
							Readflage = -5;// 读卡失败！
						}
					} else {
						Readflage = -5;// 读卡失败
					}
				} else {
					Readflage = -4;// 选卡失败
				}
			} else {
				Readflage = -3;// 寻卡失败
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			Readflage = -99;// 读取数据异常
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			Readflage = -99;// 读取数据异常
		}
	}
   private void sendMessage(byte[] outS){
	   try {
		   if(mOutputStream!=null){
				mOutputStream.write(outS);
				mOutputStream.write('\n'); 
		   }

		} catch (IOException e) {
			e.printStackTrace();
		}
   }

	@Override
	protected void onDataReceived(byte[] buffer, int size) {
		// TODO Auto-generated method stub
		datalen = size;
		if(tempFlag==-1)
			recData = buffer;
	}
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if(msg.what!=0){
				return;
			}
			try {
				if(Readflage > 0) {
					ett.setText("姓名：" + decodeInfo[0] + "\n" + "性别："
							+ decodeInfo[1] + "\n" + "民族：" + decodeInfo[2]
							+ "\n" + "出生日期：" + decodeInfo[3] + "\n" + "地址："
							+ decodeInfo[4] + "\n" + "身份号码：" + decodeInfo[5]
							+ "\n" + "签发机关：" + decodeInfo[6] + "\n" + "有效期限："
							+ decodeInfo[7] + "-" + decodeInfo[8] + "\n"
							+ decodeInfo[9] + "\n");
					if (Readflage == 1) {
						FileInputStream fis = new FileInputStream(
								Environment.getExternalStorageDirectory()
										+ "/wltlib/zp.bmp");
						Bitmap bmp = BitmapFactory.decodeStream(fis);
						fis.close();
						image.setImageBitmap(bmp);
					} else {
						ett.append("照片解码失败，请检查路径"
								+ AppConfig.RootFile);
						image.setImageBitmap(BitmapFactory.decodeResource(
								getResources(), R.drawable.face));
					}
					if(isPlay)
						player.start();
					
				}else{
					image.setImageBitmap(BitmapFactory.decodeResource(
							getResources(), R.drawable.face));
					if (Readflage == -2) {
						ett.setText("连接异常");
					}
					if (Readflage == -3) {
						ett.setText("无卡或卡片已读过");
					}
					if (Readflage == -4) {
						ett.setText("无卡或卡片已读过");
					}
					if (Readflage == -5) {
						ett.setText("读卡失败");
					}
					if (Readflage == -99) {
						ett.setText("操作异常");
					}
				}
				Thread.sleep(0);
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				ett.setText("读取数据异常！");
				image.setImageBitmap(BitmapFactory.decodeResource(
						getResources(), R.drawable.face));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				ett.setText("读取数据异常！");
				image.setImageBitmap(BitmapFactory.decodeResource(
						getResources(), R.drawable.face));
			}
		}
		
	};
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	    isPlay = prefs.getBoolean("checkbox",false);
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		isRun = false;
	}
	
	
}