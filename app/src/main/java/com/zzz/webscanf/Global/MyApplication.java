package com.zzz.webscanf.Global;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

import com.tencent.bugly.Bugly;
import com.zzz.webscanf.model.DataBaseItem;
import com.zzz.webscanf.model.Wares;
import com.zzz.webscanf.utils.ToastUtils;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.util.List;
import cn.leancloud.LCObject;
import cn.leancloud.LCQuery;
import cn.leancloud.LeanCloud;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

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
        LeanCloud.initialize(this, "OYtg69HFMLtlz8I7HK3pDFcs-gzGzoHsz", "p0qic6oI2yiakS9RnOPf9mYP",
                "https://oytg69hf.lc-cn-n1-shared.com");
        LCObject.registerSubclass(Wares.class);
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
                new LCQuery<Wares>("Wares")
                        .limit(1000)
                        .findInBackground().subscribe(new Observer<List<Wares>>() {
                    public void onSubscribe(Disposable disposable) {}

                    @Override
                    public void onNext(@NonNull List<Wares> list) {
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
                    public void onError(Throwable throwable) {}
                    public void onComplete() {}
                });
            }
        }).start();
    }
}
