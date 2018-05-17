package android_serialport_api.sample;


import com.blankj.utilcode.util.CrashUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.Utils;
import com.by100.util.RWCrashHandler;

import java.io.File;

public class RWCrashApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		RWCrashHandler crashHandler = RWCrashHandler.getInstance();
		crashHandler.init(this);
		init();
	}

	private void init() {
		Utils.init(this);

		LogUtils.getConfig().setLogSwitch(true);
		LogUtils.getConfig().setGlobalTag("cqcity");
		LogUtils.getConfig().setConsoleFilter(LogUtils.D);
		LogUtils.getConfig().setLog2FileSwitch(true);

		File file = new File(getExternalCacheDir() + "/crash");
		if (!file.exists()) {
			file.mkdir();
		}
		CrashUtils.init(file);
	}
}
