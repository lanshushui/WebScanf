package com.zzz.webscanf.Global;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

import com.tencent.bugly.Bugly;
import com.tencent.bugly.crashreport.CrashReport;
import com.zzz.webscanf.model.DataBaseItem;
import com.zzz.webscanf.model.Wares;
import com.zzz.webscanf.utils.ToastUtils;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by 懒鼠睡zzz on 2017/10/15.
 */

public class MyApplication extends Application {
    private    static Context mContext;
    private  static Handler mHandle;
    private static boolean isUpdateDataBase=false;
    @Override
    public void onCreate() {
        super.onCreate();
        Bugly.init(getApplicationContext(), "ec0cc0d600", false);
        mContext=getApplicationContext();
        mHandle=new Handler();
        LitePal.initialize(mContext);
    }

    public static Context getmContext() {
        return mContext;
    }

    public static Handler getmHandle() {
        return mHandle;
    }
    public  static  void updateDataBase(){
        if(isUpdateDataBase) return;
        isUpdateDataBase=true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                BmobQuery<Wares> query = new BmobQuery<Wares>();
                query.setLimit(500);
                query.findObjects(new FindListener<Wares>() {
                    @Override
                    public void done(List<Wares> list, BmobException e) {
                        if(e!=null||list==null||list.size()==0) {
                            ToastUtils.showToast("更新数据失败");
                            return;
                        }
                        DataSupport.deleteAll(DataBaseItem.class);
                        for(Wares wares :list){
                            DataBaseItem first=DataBaseItem.getInstant(wares);
                            first.setScan_num(wares.scan_num);
                            first.setPrice(wares.price);
                            first.setName(wares.name);
                            first.save();
                        }
                        ToastUtils.showToast("更新数据成功");
                    }
                });
            }
        }).start();
    }
}
