package com.zzz.webscanf.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zzz.webscanf.ApiManager;
import com.zzz.webscanf.BarcodeApi;
import com.zzz.webscanf.BarcodeBean;
import com.zzz.webscanf.model.Wares;
import com.zzz.webscanf.model.DataBaseItem;
import com.zzz.webscanf.R;
import com.zzz.webscanf.ui.MainActivity;
import com.zzz.webscanf.utils.ToastUtils;
import com.zzz.webscanf.utils.UiUtils;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by 懒鼠睡zzz on 2018/1/16.
 */

public class BarcodeMsgDialog {
    private Dialog dialog;
    private EditText code,name,price;
    private Context mContext;
    private TextView sure,cancle;
    public BarcodeMsgDialog(Activity activity, final Wares wares){
        mContext=activity;
        dialog = new Dialog(activity, R.style.BottomDialog);
        View contentView = LayoutInflater.from(activity).inflate(R.layout.dialog_barcode, null);
        dialog.setContentView(contentView);
        ViewGroup.LayoutParams layoutParams = contentView.getLayoutParams();
        layoutParams.width = mContext.getResources().getDisplayMetrics().widthPixels;
        contentView.setLayoutParams(layoutParams);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if(mContext instanceof MainActivity){
                    MainActivity mainActivity= (MainActivity) mContext;
                    mainActivity.askRestart();
                }
            }
        });
        dialog.show();
        code=(EditText)contentView.findViewById(R.id.et_code);
        name=(EditText)contentView.findViewById(R.id.et_name);
        price=(EditText)contentView.findViewById(R.id.et_price);
        sure=contentView.findViewById(R.id.sure);
        cancle=contentView.findViewById(R.id.cancel);
        if(!Wares.isLog(wares)){
            TextView tip=contentView.findViewById(R.id.tip);
            tip.setText("未录入");
            tip.setTextColor(Color.RED);
            sure.setText("录入");
        }
        code.setText(wares.scan_num);
        if(Wares.isLog(wares)){
            name.setText(wares.name);
            price.setText(String.valueOf(wares.price));
        }else {
            BarcodeApi barcodeApi = ApiManager.getInstance().create(BarcodeApi.class);
            Call<BarcodeBean> call = barcodeApi.getBarcode(wares.scan_num);
            call.enqueue(new Callback<BarcodeBean>() {
                @Override
                public void onResponse(Call<BarcodeBean> call, Response<BarcodeBean> response) {
                    BarcodeBean data = response.body();
                    if(data==null||data.getCode()==0) return;
                    name.setText("");
                    name.setText(data.getData().getGoodsName());
                    price.setText("");
                    price.setText(data.getData().getPrice());
                }
                @Override
                public void onFailure(Call<BarcodeBean> call, Throwable t) {

                }
            });
        }
        contentView.findViewById(R.id.sureView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String priceStr=price.getText().toString();
                String nameStr=name.getText().toString();
                if(TextUtils.isEmpty(nameStr)|| !UiUtils.isNumericzidai(priceStr)){
                    ToastUtils.showToast("数据错误");
                    return;
                }
                if(dialog.getCurrentFocus()!=null&&dialog.getCurrentFocus().getWindowToken()!=null) {
                    InputMethodManager manager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                    manager.hideSoftInputFromWindow(dialog.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
                dialog.dismiss();
                if(wares.price==Double.valueOf(priceStr)&&TextUtils.equals(wares.name,nameStr)) return;
                if(!Wares.isLog(wares)){
                    wares.setName(nameStr);
                    wares.setPrice(Double.valueOf(priceStr));
                    wares.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if(e==null) {
                                ToastUtils.showToast("保存成功");
                                DataBaseItem dataBaseItem = DataBaseItem.getInstant(wares);
                                dataBaseItem.setName(wares.name);
                                dataBaseItem.setPrice(wares.price);
                                dataBaseItem.save();
                            }else {
                                ToastUtils.showToast("保存失败");
                            }
                        }
                    });
                }else{
                    wares.setName(nameStr);
                    wares.setPrice(Double.valueOf(priceStr));
                    wares.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if(e==null) {
                                ToastUtils.showToast("保存成功");
                                DataBaseItem dataBaseItem = DataBaseItem.getInstant(wares);
                                dataBaseItem.setName(wares.name);
                                dataBaseItem.setPrice(wares.price);
                                dataBaseItem.save();
                            }else {
                                ToastUtils.showToast("保存失败");
                            }
                        }
                    });
                }
            }
        });
        contentView.findViewById(R.id.cancelView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dialog.getCurrentFocus()!=null&&dialog.getCurrentFocus().getWindowToken()!=null) {
                    InputMethodManager manager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                    manager.hideSoftInputFromWindow(dialog.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }

}
