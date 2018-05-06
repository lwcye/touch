package com.hbln.touch.utils;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;

import rx.functions.Action1;

/**
 * Created by 41569 on 2018/5/6.
 */

public class DialogUtil {
    private static final DialogUtil ourInstance = new DialogUtil();

    public static DialogUtil getInstance() {
        return ourInstance;
    }

    private DialogUtil() {
    }

    public AlertDialog defAlert(Activity activity, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(message);
        AlertDialog alertDialog = builder.create();
        builder.setPositiveButton("确定", (dialogInterface, i) -> alertDialog.cancel());
        alertDialog.show();
        return alertDialog;
    }

    public AlertDialog defConfirm(Activity activity, String message, Action1<Boolean> action1) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(message);
        AlertDialog alertDialog = builder.create();
        builder.setPositiveButton("确定", (dialogInterface, i) -> {
            alertDialog.cancel();
            if (action1 != null) {
                action1.call(true);
            }
        });

        builder.setNeutralButton("取消", (dialogInterface, i) -> {
            alertDialog.cancel();
            if (action1 != null) {
                action1.call(false);
            }
        });
        alertDialog.setOnCancelListener(dialogInterface -> {
            alertDialog.cancel();
            if (action1 != null) {
                action1.call(false);
            }
        });
        alertDialog.show();
        return alertDialog;
    }

    public AlertDialog defPrompt(Activity activity, String message, Action1<String> action1) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        AppCompatEditText editText = new AppCompatEditText(activity);
        builder.setView(editText);
        editText.setText(message);
        editText.setSelection(message.length());
        AlertDialog alertDialog = builder.create();
        builder.setPositiveButton("确定", (dialogInterface, i) -> {
            alertDialog.cancel();
            if (action1 != null) {
                action1.call(editText.getText().toString().trim());
            }
        });

        builder.setNeutralButton("取消", (dialogInterface, i) -> {
            alertDialog.cancel();
            if (action1 != null) {
                action1.call("");
            }
        });
        alertDialog.setOnCancelListener(dialogInterface -> {
            alertDialog.cancel();
            if (action1 != null) {
                action1.call("");
            }
        });
        alertDialog.show();
        return alertDialog;
    }
}
