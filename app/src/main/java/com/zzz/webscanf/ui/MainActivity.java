package com.zzz.webscanf.ui;

import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.zzz.webscanf.R;
import com.zzz.webscanf.dialogs.BarcodeMsgDialog;
import com.zzz.webscanf.model.Wares;
import com.zzz.webscanf.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import me.dm7.barcodescanner.zxing.ZXingScannerView;


public class MainActivity extends BaseActivity implements ZXingScannerView.ResultHandler{
    private ZXingScannerView zingScanner;
    private FloatingActionButton seek;
    private ArrayList<BarcodeFormat> formats;
    int mCanermId=0;
    boolean isFlash=false;
    boolean isAutoFocus=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        zingScanner=findViewById(R.id.zxing);
        seek=findViewById(R.id.seek);
        setFormats();
        setStatusBarViewVisiable(View.GONE);
        seek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(MainActivity.this,SeekActivity.class);
                MainActivity.this.startActivity(i);
            }
        });

    }
    /**
     * 添加扫码支持格式
     */
    private void setFormats() {
        formats=new ArrayList<BarcodeFormat>();
        for(int i=0;i<ZXingScannerView.ALL_FORMATS.size();i++){
            formats.add(ZXingScannerView.ALL_FORMATS.get(i));
        }
        zingScanner.setFormats(formats);
    }
    @Override
    public void onResume() {
        super.onResume();
        if(zingScanner!=null) {
            zingScanner.resumeCameraPreview(this);
            zingScanner.setResultHandler(this);
            zingScanner.startCamera(mCanermId);
            zingScanner.setFlash(isFlash);
            zingScanner.setAutoFocus(isAutoFocus);
            setFormats();
        }

    }
    @Override
    public void onPause() {
        super.onPause();
        zingScanner.stopCamera();
    }


    @Override
    public void handleResult(Result result) {
        final String scan_num=result.getText();
        BmobQuery<Wares> items=new BmobQuery<Wares>();
        items.addWhereEqualTo("scan_num",scan_num);
        items.findObjects(new FindListener<Wares>() {
            @Override
            public void done(List<Wares> list, BmobException e) {
                if(e==null){
                    if(list.size()==0){
                        Wares wares =new Wares();
                        wares.setScan_num(scan_num);
                        new BarcodeMsgDialog(MainActivity.this, wares);
                    }else {
                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                        Ringtone r = RingtoneManager.getRingtone(MainActivity.this, notification);
                        r.play();
                        new BarcodeMsgDialog(MainActivity.this,list.get(0));
                        while (list.size()!=1){
                            list.get(1).delete(new UpdateListener() {
                                @Override
                                public void done(BmobException e) {

                                }
                            });
                            list.remove(1);
                        }
                    }
                }else {
                    ToastUtils.showToast("访问错误");
                    askRestart();
                }
            }
        });
    }
    //被调用接口
    public void askRestart(){
        zingScanner.resumeCameraPreview(this);
    }
}
