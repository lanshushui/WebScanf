package com.zzz.webscanf.model;


import android.text.TextUtils;

import cn.bmob.v3.BmobObject;

/**
 * Created by 懒鼠睡zzz on 2017/10/15.
 */

public class Wares extends BmobObject  {
    public  String name;
    public  double price;
    public  String  scan_num;
    public static boolean isLog(Wares wares){
        return !TextUtils.isEmpty(wares.getObjectId());
    }
    public String getScan_num() {
        return scan_num;
    }

    public void setScan_num(String scan_num) {
        this.scan_num = scan_num;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
