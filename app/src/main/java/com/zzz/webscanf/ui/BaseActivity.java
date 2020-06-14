package com.zzz.webscanf.ui;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;


import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrInterface;
import com.r0adkll.slidr.model.SlidrListenerAdapter;
import com.r0adkll.slidr.model.SlidrPosition;
import com.r0adkll.slidr.widget.CanvasProxyView;
import com.r0adkll.slidr.widget.SliderPanel;
import com.zzz.webscanf.R;
import com.zzz.webscanf.utils.UiUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;



public abstract class BaseActivity extends AppCompatActivity {
    public Toolbar toolbar;
    private boolean shouldInitSlidr = true;
    private SlidrInterface mSlidrControler;
    protected SliderPanel mPanelView;
    private SlidrConfig config;
    private View statusBarView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.getInstance().addActivity(this); //添加到栈中
        //设置主题
        initSlidr();
        setFullScreen();

    }

    private void initSlidr() {
        if (this.shouldInitSlidr) {
             config = new SlidrConfig.Builder().position(SlidrPosition.LEFT).sensitivity(1.0f).scrimColor(-16777216).scrimStartAlpha(0.8f).scrimEndAlpha(0.0f).listener(new SlidrListenerAdapter() {

                public void onSlideChange(float f) {
                    super.onSlideChange(f);
                    BaseActivity.this.onSlideChange(f);
                }

             }).velocityThreshold(2400.0f).distanceThreshold(0.1f).edge(true).edgeSize(0.2f).build();
            this.mSlidrControler = Slidr.attach(this, config);
            if (this.mPanelView == null) {
                this.mPanelView = (SliderPanel) UiUtils.findFirstChildViewByType((ViewGroup) getWindow().getDecorView(), SliderPanel.class);
            }
            SliderPanel sliderPanel = this.mPanelView;
            if (sliderPanel != null) {
                sliderPanel.getDimView().setVisibility(View.INVISIBLE);
            }
        }
        LinearLayout slidable_content=findViewById(R.id.slidable_content);
        addStatusBarView(slidable_content);

    }
    protected void onSlideChange(float f) {
        SliderPanel sliderPanel = this.mPanelView;
        if (sliderPanel != null) {
            if (!false) {
                CanvasProxyView canvasProxyView = sliderPanel.getCanvasProxyView();
                if (f == 1.0f && canvasProxyView.hasProxyView()) {
                    canvasProxyView.setProxyView(null);
                    canvasProxyView.setX(0.0f);
                } else if (f < 1.0f) {
                    canvasProxyView.setX(getResources().getFraction(R.fraction.left_in_out_fraction, 0, canvasProxyView.getWidth()) * f);
                    if (!canvasProxyView.hasProxyView()) {
                        Activity activityUnder = ActivityManager.getUnderActivity();
                        if (activityUnder != null) {
                            canvasProxyView.setProxyView(activityUnder.getWindow().getDecorView());
                        }
                    }
                }
            }
            if (f == 1.0f) {
                this.mPanelView.getDimView().setVisibility(View.INVISIBLE);
            } else if (f < 1.0f) {
                this.mPanelView.getDimView().setVisibility(View.VISIBLE);
            }
        }
    }


    public void setFullScreen() {
        setStatusBarDarkMode(true,this);
        getWindow().setStatusBarColor( Color.TRANSPARENT);
        getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN| View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }

    public void setSlidrEnable(boolean z) {
        SlidrInterface slidrInterface = this.mSlidrControler;
        if (slidrInterface != null) {
            if (z) {
                slidrInterface.unlock();
            } else {
                slidrInterface.lock();
            }
        }
    }
    public  void addStatusBarView(LinearLayout linearLayout){
        //增加占位状态栏
        statusBarView = new View(this);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                UiUtils.getStatusBarHeight(this));
        statusBarView.setBackgroundColor(Color.WHITE);
        linearLayout.addView(statusBarView,0, lp);
    }
    public void setStatusBarViewVisiable(int viewVisiable){
        statusBarView.setVisibility(viewVisiable);
    }
    public void setStatusBarViewColor(int color){
        statusBarView.setBackgroundColor(color);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    /**
     * 垃圾miui 安卓6.0状态栏变化
     * @param darkmode
     * @param activity
     */
    public void setStatusBarDarkMode(boolean darkmode, Activity activity) {
        Class<? extends Window> clazz = activity.getWindow().getClass();
        try {
            int darkModeFlag = 0;
            Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
            extraFlagField.invoke(activity.getWindow(), darkmode ? darkModeFlag : 0, darkModeFlag);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManager.getInstance().finishActivity(this);
       // Glide.get(this).clearMemory();
    }
    protected void setStatusBarPaddingAndHeight(View toolBar) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (toolBar != null) {
                int statusBarHeight = UiUtils.getStatusBarHeight(this);
                toolBar.setPadding(toolBar.getPaddingLeft(), statusBarHeight, toolBar.getPaddingRight(),
                        toolBar.getPaddingBottom());
                toolBar.getLayoutParams().height = statusBarHeight +
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 45, getResources().getDisplayMetrics());
            }
        }
    }

    public void runOnMainUIThread(Runnable r){
        this.getWindow().getDecorView().post(r);
    }

}
