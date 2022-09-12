package com.zzz.webscanf.Global;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

import com.tencent.bugly.Bugly;
import com.zzz.webscanf.model.Wares;
import cn.leancloud.LCObject;
import cn.leancloud.LeanCloud;

/**
 * Created by 懒鼠睡zzz on 2017/10/15.
 */

public class MyApplication extends Application {
    private    static Context mContext;
    private  static Handler mHandle;
    @Override
    public void onCreate() {
        super.onCreate();
        Bugly.init(getApplicationContext(), "ec0cc0d600", false);
        LeanCloud.initialize(this, "OYtg69HFMLtlz8I7HK3pDFcs-gzGzoHsz", "p0qic6oI2yiakS9RnOPf9mYP",
                "https://oytg69hf.lc-cn-n1-shared.com");
        LCObject.registerSubclass(Wares.class);
        mContext=getApplicationContext();
        mHandle=new Handler();
    }

    public static Context getmContext() {
        return mContext;
    }

    public static Handler getmHandle() {
        return mHandle;
    }
}
