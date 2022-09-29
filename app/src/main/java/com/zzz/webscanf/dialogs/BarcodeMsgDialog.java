package com.zzz.webscanf.dialogs;

import android.annotation.SuppressLint;
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
import android.widget.EditText;
import android.widget.TextView;

import com.zzz.webscanf.ApiManager;
import com.zzz.webscanf.BarcodeApi;
import com.zzz.webscanf.BarcodeBean;
import com.zzz.webscanf.model.Wares;
import com.zzz.webscanf.R;
import com.zzz.webscanf.ui.MainActivity;
import com.zzz.webscanf.utils.ToastUtils;
import com.zzz.webscanf.utils.UiUtils;
import cn.leancloud.LCObject;
import io.reactivex.functions.Consumer;
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
    private TextView sure,visitData;
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
            @SuppressLint("CheckResult")
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
        visitData=contentView.findViewById(R.id.visitData);
        visitData.setText(wares.getCheckStr());
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
            @SuppressLint("CheckResult")
            @Override
            public void onClick(View view) {
                String priceStr=price.getText().toString();
                String nameStr=name.getText().toString();
                String scanNum=code.getText().toString();
                if(TextUtils.isEmpty(nameStr)|| !UiUtils.isNumericzidai(priceStr)){
                    ToastUtils.showToast("数据错误");
                    return;
                }
                if(dialog.getCurrentFocus()!=null&&dialog.getCurrentFocus().getWindowToken()!=null) {
                    InputMethodManager manager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                    manager.hideSoftInputFromWindow(dialog.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
                dialog.dismiss();
                wares.setScan_num(scanNum);
                    wares.setName(nameStr);
                    wares.setPrice(Double.valueOf(priceStr));
                    wares.saveInBackground().subscribe(new Consumer<LCObject>() {
                        @Override
                        public void accept(LCObject lcObject) throws Exception {
                            ToastUtils.showToast("保存成功");
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            ToastUtils.showToast("保存失败");
                        }
                    });

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
