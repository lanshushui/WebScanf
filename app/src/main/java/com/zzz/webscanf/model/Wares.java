package com.zzz.webscanf.model;


import android.text.TextUtils;
import android.util.Log;

import cn.leancloud.LCObject;
import cn.leancloud.annotation.LCClassName;
import io.reactivex.Observable;

/**
 * Created by 懒鼠睡zzz on 2017/10/15.
 */
@LCClassName("Wares")
public class Wares extends LCObject {

    public Wares(){
        super("Wares");
    }
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

    @Override
    protected void resetByRawData(LCObject LCObject) {
        super.resetByRawData(LCObject);
        try {
            if(serverData.size()!=0){
                name=serverData.get("name").toString();
                price=Double.parseDouble(serverData.get("price").toString());
                scan_num=serverData.get("scan_num").toString();
            }
        }catch (Exception e){
            Log.e("Wares",e.toString());
        }
    }

    @Override
    public Observable<? extends LCObject> saveInBackground() {
        put("name",name);
        put("price",price+"");
        put("scan_num",scan_num);
        return super.saveInBackground();

    }
}
