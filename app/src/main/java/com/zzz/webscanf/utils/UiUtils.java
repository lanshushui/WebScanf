package com.zzz.webscanf.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UiUtils {
    public static <T extends View> T findFirstChildViewByType(ViewGroup viewGroup, Class<T> cls) {
        if (viewGroup == null)  return null;
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            T childAt = (T) viewGroup.getChildAt(i);
            if (cls.isAssignableFrom(childAt.getClass())) {
                return childAt;
            }
            if (childAt instanceof ViewGroup) {
                childAt = findFirstChildViewByType((ViewGroup) childAt, cls);
                if (childAt != null) {
                    return childAt;
                }
            }
        }
        return null;
    }
    public static int getStatusBarHeight(Context context) {
        int identifier = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return identifier > 0 ? context.getResources().getDimensionPixelSize(identifier) : null;
    }
    public static int dip2px(Context context, int value) {
        float density = context.getResources()
                .getDisplayMetrics().density;
        return (int) (density * value + 0.5f);
    }
    public static  int getScreenWidth(Context context){
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        float density= dm.density;
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        return  width;
    }
    /**
     * 判断字符串是否数字
     * @param str
     * @return
     */
    public static boolean isNumericzidai(String str) {
        Pattern pattern = Pattern.compile("-?[0-9]+\\.?[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }
}
