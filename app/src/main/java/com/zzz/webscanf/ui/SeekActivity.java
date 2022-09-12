package com.zzz.webscanf.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.zzz.webscanf.R;
import com.zzz.webscanf.model.Wares;
import com.zzz.webscanf.model.DataBaseItem;
import com.zzz.webscanf.utils.SoftKeyBoardUtil;
import com.zzz.webscanf.utils.ToastUtils;
import com.zzz.webscanf.utils.UiUtils;

import org.litepal.crud.DataSupport;

import java.util.Collections;
import java.util.List;
import cn.leancloud.LCObject;
import cn.leancloud.LCQuery;
import cn.leancloud.types.LCNull;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class SeekActivity extends BaseActivity {
    RecyclerView recyclerView;
    EditText input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seek);
        input = findViewById(R.id.input_keyword);
        recyclerView = findViewById(R.id.recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        //  linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        getWindow().getDecorView().postDelayed(new Runnable() {
            @Override
            public void run() {
                input.requestFocus();
                SoftKeyBoardUtil.showSoftInput(input);
            }
        }, 500);
        findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SoftKeyBoardUtil.hideSoftInput(input);
                SeekActivity.this.finish();
            }
        });
        input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    SoftKeyBoardUtil.hideSoftInput(input);
                    getDataFromDB(input.getText().toString());
                    return true;
                }
                return false;
            }
        });
    }


    private void getDataFromDB(String data) {
        List<DataBaseItem> datas;
        if (TextUtils.isEmpty(data)) {
            datas = DataSupport.findAll(DataBaseItem.class);
            Collections.reverse(datas);
        } else {
            datas = DataSupport
                    .where("name LIKE ?", "%" + data + "%")
                    .find(DataBaseItem.class);
        }
        recyclerView.setAdapter(new MyAdapter(this, datas));
        recyclerView.getAdapter().notifyDataSetChanged();
        //添加Android自带的分割线
        //添加自定义分割线
        DividerItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(this, R.drawable.rv_divider_gray_5dp));
        //  recyclerView.addItemDecoration(divider);
    }


    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private List<DataBaseItem> datas;
        private Context context;

        public MyAdapter(Context context, List<DataBaseItem> datas) {
            this.context = context;
            this.datas = datas;
        }

        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_obj_msg, parent, false);
            MyAdapter.ViewHolder holder = new MyAdapter.ViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final MyAdapter.ViewHolder holder, int position) {
            final DataBaseItem dataBaseItem = datas.get(position);
            holder.price.setText(dataBaseItem.getPrice() + "");
            holder.name.setText(dataBaseItem.getName());
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("敏感操作");
                    builder.setMessage("你确定要从云端删除该记录吗");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteData(dataBaseItem);
                        }
                    });
                    builder.setNegativeButton("取消", null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });
            holder.update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (TextUtils.isEmpty(holder.name.getText().toString()) || TextUtils.isEmpty(holder.price.getText().toString())) {
                        ToastUtils.showToast("不能设置为空");
                        return;
                    }
                    if (!UiUtils.isNumericzidai(holder.price.getText().toString())) {
                        ToastUtils.showToast("数字格式错误");
                        return;
                    }
                    updateData(dataBaseItem, holder.name.getText().toString(), Double.valueOf(holder.price.getText().toString()));
                }
            });
        }


        /**
         * 点击修改按钮后修改数据
         *
         * @param dataBaseItem
         */
        @SuppressLint("CheckResult")
        private void updateData(final DataBaseItem dataBaseItem, final String name, final double price) {
            LCQuery<Wares> query = new LCQuery<>("Wares");
            query.whereEqualTo("scan_num", dataBaseItem.scan_num);
            query.findInBackground().subscribe(new Observer<List<Wares>>() {
                public void onSubscribe(Disposable disposable) {}
                public void onNext(@NonNull List<Wares> list) {
                    if(list.size()!=0){
                        Wares wares = list.get(0);
                        wares.setName(name);
                        wares.setPrice(price);
                        wares.saveInBackground().subscribe(new Consumer<LCObject>() {
                            @Override
                            public void accept(LCObject lcObject) throws Exception {
                                dataBaseItem.setName(name);
                                dataBaseItem.setPrice(price);
                                dataBaseItem.save();
                                ToastUtils.showToast("修改成功");
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                ToastUtils.showToast("修改失败");
                            }
                        });
                    }
                }
                public void onError(Throwable throwable) {
                    ToastUtils.showToast("访问错误");
                }
                public void onComplete() {}
            });
        }

        @Override
        public int getItemCount() {
            return datas.size();
        }

        @SuppressLint("CheckResult")
        private void deleteData(final DataBaseItem dataBaseItem) {
            LCQuery<Wares> query = new LCQuery<>("Wares");
            query.whereEqualTo("scan_num", dataBaseItem.scan_num);
            query.deleteAllInBackground().subscribe(new Consumer<LCNull>() {
                @Override
                public void accept(LCNull lcNull) throws Exception {
                    ToastUtils.showToast("删除成功");
                    datas.remove(dataBaseItem);
                    notifyDataSetChanged();
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    ToastUtils.showToast("删除失败");
                }
            });
        }


        class ViewHolder extends RecyclerView.ViewHolder {
            EditText name, price;
            ImageView delete, update;

            public ViewHolder(View itemView) {
                super(itemView);
                name = (EditText) itemView.findViewById(R.id.et_name);
                price = (EditText) itemView.findViewById(R.id.et_price);
                delete = (ImageView) itemView.findViewById(R.id.delete);
                update = (ImageView) itemView.findViewById(R.id.edit);
            }


        }


    }


}
