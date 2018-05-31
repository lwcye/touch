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

package android_serialport_api.sample;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.widget.Toast;

import com.blankj.utilcode.util.LogUtils;
import com.byid.android.ByIdActivity;
import com.google.gson.Gson;

import android_serialport_api.SerialPortFinder;
import android_serialport_api.sample.base.Application;

public class SerialPortPreferences extends PreferenceActivity {

    private Application mApplication;
    private SerialPortFinder mSerialPortFinder;

    public static boolean switching = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mApplication = (Application) getApplication();
        mSerialPortFinder = mApplication.mSerialPortFinder;
        System.out.println("的数据ffffffggggg");
        addPreferencesFromResource(R.xml.serial_port_preferences);
        CheckBoxPreference mCheckbox0 = (CheckBoxPreference) findPreference("checkbox_0");
        mCheckbox0.setChecked(false);
        mCheckbox0.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                //这里可以监听到这个CheckBox 的点击事件


                if (ByIdActivity.source == false & ByIdActivity.isOpen == false & switching == false) {

                    PowerOperate.enableRIFID_Module_5Volt();

                    switching = true;
                    ByIdActivity.source = true;
                    Toast.makeText(getApplicationContext(), "您已经打开手持机", Toast.LENGTH_LONG)
                            .show();
                    return false;
                }
                if (ByIdActivity.source == true & ByIdActivity.isOpen == false & switching == true) {
                    PowerOperate.disableRIFID_Module_5Volt();
                    switching = false;
                    ByIdActivity.source = false;

                    Toast.makeText(getApplicationContext(), "已经关闭", Toast.LENGTH_LONG)
                            .show();
                    return false;
                }

                return true;
            }
        });
        mCheckbox0.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference arg0, Object newValue) {
                //这里可以监听到checkBox中值是否改变了
                //并且可以拿到新改变的值
                System.out.println("测试");
                return true;
            }
        });

        // Devices
        final ListPreference devices = (ListPreference) findPreference("DEVICE");
        String[] entries = mSerialPortFinder.getAllDevices();
        String[] entryValues = mSerialPortFinder.getAllDevicesPath();
        devices.setEntries(entries);
        devices.setEntryValues(entryValues);
        LogUtils.e(devices.getValue());
        devices.setSummary(devices.getValue());
        LogUtils.e(new Gson().toJson(entries));
        LogUtils.e(new Gson().toJson(entryValues));
        devices.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                LogUtils.e((String) newValue);
                preference.setSummary((String) newValue);
                return true;
            }
        });

        // Baud rates
        final ListPreference baudrates = (ListPreference) findPreference("BAUDRATE");
        baudrates.setSummary(baudrates.getValue());
        baudrates.setValue("115200");
        baudrates.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                preference.setSummary((String) newValue);
                return true;
            }
        });
    }

}
