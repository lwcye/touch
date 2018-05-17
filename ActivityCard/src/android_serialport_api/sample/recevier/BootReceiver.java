package android_serialport_api.sample.recevier;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.byid.android.TestActivity;

/**
 * Created by 41569 on 2018/5/17.
 */

public class BootReceiver extends BroadcastReceiver {
    static final String action_boot = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (action_boot.equals(intent.getAction())) {
            Intent mBootIntent = new Intent(context, TestActivity.class);
            // 下面这句话必须加上才能开机自动运行app的界面
            mBootIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(mBootIntent);
        }
    }
}
