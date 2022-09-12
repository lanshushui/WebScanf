package com.zzz.webscanf.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
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
import com.zzz.webscanf.utils.SoftKeyBoardUtil;
import com.zzz.webscanf.utils.ToastUtils;
import com.zzz.webscanf.utils.UiUtils;
import java.util.List;
import cn.leancloud.LCObject;
import cn.leancloud.LCQuery;
import cn.leancloud.types.LCNull;
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
                    getDataFromWeb(input.getText().toString());
                    return true;
                }
                return false;
            }
        });
    }


    @SuppressLint("CheckResult")
    private void getDataFromWeb(String data) {
        LCQuery<Wares> query = new LCQuery<>("Wares");
        query.whereContains("name", data);
        query.findInBackground().subscribe(new Consumer<List<Wares>>() {
            @Override
            public void accept(List<Wares> list) throws Exception {
                recyclerView.setAdapter(new MyAdapter(SeekActivity.this, list));
                recyclerView.getAdapter().notifyDataSetChanged();
                //添加Android自带的分割线
                //添加自定义分割线
                DividerItemDecoration divider = new DividerItemDecoration(
                        SeekActivity.this, DividerItemDecoration.VERTICAL);
                divider.setDrawable(ContextCompat.getDrawable(
                        SeekActivity.this, R.drawable.rv_divider_gray_5dp));
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                ToastUtils.showToast("访问错误");
            }
        });
    }


    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private List<Wares> datas;
        private Context context;

        public MyAdapter(Context context, List<Wares> datas) {
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
        @SuppressLint("CheckResult")
        public void onBindViewHolder(final MyAdapter.ViewHolder holder, int position) {
            final Wares ware = datas.get(position);
            holder.price.setText(ware.getPrice() + "");
            holder.name.setText(ware.getName());
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("敏感操作");
                    builder.setMessage("你确定要从云端删除该记录吗");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteData(ware);
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
                    ware.setName(holder.name.getText().toString());
                    ware.setPrice(Double.valueOf(holder.price.getText().toString()));
                    ware.saveInBackground().subscribe(new Consumer<LCObject>() {
                        @Override
                        public void accept(LCObject lcObject) throws Exception {
                            ToastUtils.showToast("修改成功");
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            ToastUtils.showToast("修改失败");
                        }
                    });
                }
            });
        }

        @Override
        public int getItemCount() {
            return datas.size();
        }

        @SuppressLint("CheckResult")
        private void deleteData(final Wares wares) {
            wares.deleteInBackground().subscribe(new Consumer<LCNull>() {
                @Override
                public void accept(LCNull lcNull) throws Exception {
                    ToastUtils.showToast("删除成功");
                    datas.remove(wares);
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
