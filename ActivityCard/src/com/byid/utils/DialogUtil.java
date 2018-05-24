package com.byid.utils;

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

    private DialogUtil() {
    }

    public static DialogUtil getInstance() {
        return ourInstance;
    }

    public AlertDialog defAlert(Activity activity, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(message);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                alertDialog.cancel();
            }
        });
        return alertDialog;
    }

    public AlertDialog defConfirm(Activity activity, String message, Action1<Boolean> action1) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(message);
        builder.setPositiveButton("确定", (dialogInterface, i) -> {
            dialogInterface.cancel();
            if (action1 != null) {
                action1.call(true);
            }
        });

        builder.setNeutralButton("取消", (dialogInterface, i) -> {
            dialogInterface.cancel();
            if (action1 != null) {
                action1.call(false);
            }
        });
        builder.setOnCancelListener(dialogInterface -> {
            dialogInterface.cancel();
            if (action1 != null) {
                action1.call(false);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        return alertDialog;
    }

    public AlertDialog defPrompt(Activity activity, String message, Action1<String> action1) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        AppCompatEditText editText = new AppCompatEditText(activity);
        builder.setView(editText);
        editText.setText(message);
        editText.setSelection(message.length());
        builder.setPositiveButton("确定", (dialogInterface, i) -> {
            dialogInterface.cancel();
            if (action1 != null) {
                action1.call(editText.getText().toString().trim());
            }
        });

        builder.setNegativeButton("取消", (dialogInterface, i) -> {
            dialogInterface.cancel();
            if (action1 != null) {
                action1.call("");
            }
        });
        builder.setOnCancelListener(dialogInterface -> {
            dialogInterface.cancel();
            if (action1 != null) {
                action1.call("");
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        return alertDialog;
    }
}
