package com.hajaulee.mobileanime;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.WindowManager;

import java.lang.ref.WeakReference;

public class DialogSet {
    static final String TAG = "DialogSet";
    private Dialog helloDialog;
    private WeakReference<Context> sender;

    DialogSet(Context context) {
        sender = new WeakReference<>(context);
    }

    public void showHelloDialog() {
        if (sender != null) {
            final Context context = sender.get();
            helloDialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
            helloDialog.setCancelable(false);
            helloDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                    if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_BACK)
                        ((Activity) context).finish();
                    return false;
                }
            });
            helloDialog.addContentView(((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.layout_hello, null),
                    new WindowManager.LayoutParams(WindowManager.LayoutParams.FLAG_FULLSCREEN));
            helloDialog.show();
        }
    }

    public void hideHelloDialog() {
        if (helloDialog != null)
            helloDialog.dismiss();
    }
}
