//package com.zzz.webscanf.ui.fragments;
//
//import android.media.Ringtone;
//import android.media.RingtoneManager;
//import android.net.Uri;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v4.app.Fragment;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Toast;
//
//import com.zzz.webscanf.model.BombItem;
//import com.zzz.webscanf.utils.UIutils;
//import com.zzz.webscanf.window.ShowAddWindow;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import cn.bmob.v3.BmobQuery;
//import cn.bmob.v3.exception.BmobException;
//import cn.bmob.v3.listener.FindListener;
//import me.dm7.barcodescanner.zbar.BarcodeFormat;
//import me.dm7.barcodescanner.zbar.Result;
//import me.dm7.barcodescanner.zbar.ZBarScannerView;
//
//
///**
// * Created by 懒鼠睡zzz on 2017/10/16.
// */
//
//public class ZbarScannerFragment extends Fragment implements ZBarScannerView.ResultHandler {
//    private ZBarScannerView  zbarScanner;
//    int mCanermId=0;
//    boolean isFlash=false;
//    boolean isAutoFocus=true;
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        isFlash = false;
//        isAutoFocus = true;
//        mCanermId=0;
//        zbarScanner = new ZBarScannerView(getActivity());
//        setFormats();
//        return zbarScanner;
//    }
//
//
//    /**
//     * 添加扫码支持格式
//     */
//    private void setFormats() {
//        zbarScanner.setFormats(BarcodeFormat.ALL_FORMATS);
//    }
//
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        if(zbarScanner!=null) {
//            zbarScanner.resumeCameraPreview(ZbarScannerFragment.this);
//            zbarScanner.setResultHandler(this);
//            zbarScanner.startCamera(mCanermId);
//            zbarScanner.setFlash(isFlash);
//            zbarScanner.setAutoFocus(isAutoFocus);
//            setFormats();
//        }
//
//    }
//    @Override
//    public void onPause() {
//        super.onPause();
//        zbarScanner.stopCamera();
//    }
//
//    @Override
//    public void handleResult(Result result) {
//        final String scan_num=result.getContents();
//        BmobQuery<BombItem> items=new BmobQuery<BombItem>();
//        items.addWhereEqualTo("scan_num",scan_num);
//        items.findObjects(new FindListener<BombItem>() {
//            @Override
//            public void done(List<BombItem> list, BmobException e) {
//                if(e==null){
//                    if(list.size()==0){
//                        ArrayList<String> templist=new ArrayList();
//                        templist.add(scan_num);
//                        new ShowAddWindow(ZbarScannerFragment.this,getActivity(),templist);
//                    }else {
//                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//                        Ringtone r = RingtoneManager.getRingtone(UIutils.getContext(), notification);
//                        r.play();
//                        ArrayList<String> templist=new ArrayList();
//                        templist.add(list.get(0).getName());
//                        templist.add(list.get(0).getPrice()+"");
//                        new ShowAddWindow(ZbarScannerFragment.this,getActivity(),templist);
//                    }
//                }else {
//                    Toast.makeText(UIutils.getContext(),"访问错误",Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//
//    }
//    //被调用接口
//    public void askRestart(){
//        zbarScanner.resumeCameraPreview(ZbarScannerFragment.this);
//    }
//
//
//}
