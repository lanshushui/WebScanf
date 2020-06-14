package com.zzz.webscanf.utils;

import android.support.annotation.StringRes;
import android.widget.Toast;
import com.zzz.webscanf.Global.MyApplication;

public class ToastUtils {
    private static  Toast toast= Toast.makeText(MyApplication.getmContext(),"",Toast.LENGTH_SHORT);
    public static void showToast(String s){
        toast.setText(s);
        toast.show();
    }

}
