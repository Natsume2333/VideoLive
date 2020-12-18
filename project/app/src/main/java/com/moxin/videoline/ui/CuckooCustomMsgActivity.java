package com.moxin.videoline.ui;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.moxin.videoline.R;
import com.moxin.videoline.adapter.CuckooCustomMsgAdapter;
import com.moxin.videoline.api.Api;
import com.moxin.videoline.base.BaseActivity;
import com.moxin.videoline.json.JsonCustomMsgList;
import com.moxin.videoline.json.JsonRequestBase;
import com.moxin.videoline.modle.CustomMsgModel;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.callback.StringCallback;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

public class CuckooCustomMsgActivity extends BaseActivity {

    @BindView(R.id.rv_content_list)
    RecyclerView rv_content_list;

    @BindView(R.id.top_bar_title_tv)
    TextView top_bar_title_tv;

    @BindView(R.id.top_bar_back_iv)
    ImageView top_bar_back_iv;

    private List<CustomMsgModel> list = new ArrayList<>();
    private CuckooCustomMsgAdapter cuckooCustomMsgAdapter;

    @Override
    protected Context getNowContext() {
        return this;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.act_custom_msg;
    }

    @Override
    protected void initView() {
        //标题
        top_bar_title_tv.setText("自定义打招呼话术");
        top_bar_back_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getNowContext());
        rv_content_list.setLayoutManager(linearLayoutManager);
        cuckooCustomMsgAdapter = new CuckooCustomMsgAdapter(list);
        rv_content_list.setAdapter(cuckooCustomMsgAdapter);

        cuckooCustomMsgAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(CuckooCustomMsgActivity.this, CuckooAddCustomMsgActivity.class);
                intent.putExtra("customMsg", (Serializable) list);
                intent.putExtra("operating", position);
                startActivityForResult(intent, 10011);
            }
        });
    }


    @Override
    protected void initSet() {

    }

    @Override
    protected void initData() {
        requestGetData();
    }

    @Override
    protected void initPlayerDisplayData() {

    }

    @OnClick({R.id.btn_save, R.id.btn_add})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_save:
                break;
            case R.id.btn_add:

                Intent intent = new Intent(this, CuckooAddCustomMsgActivity.class);
                intent.putExtra("customMsg", (Serializable) list);
                intent.putExtra("operating", -1);
                startActivityForResult(intent, 10011);
                break;

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //此处可以根据两个Code进行判断，本页面和结果页面跳过来的值
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 10011:
                    requestGetData();
                    break;
            }
        }
    }


    private void requestGetData() {

        Api.doRequestGetCustomMsgList(uId, uToken, new StringCallback() {

            @Override
            public void onSuccess(String s, Call call, Response response) {
                Log.e("CustomMsgList", s);
                JsonCustomMsgList jsonCustomMsgList = (JsonCustomMsgList) JsonRequestBase.getJsonObj(s, JsonCustomMsgList.class);
                if (jsonCustomMsgList.getCode() == 1) {
                    list.clear();
                    list.addAll(jsonCustomMsgList.getList());
                    cuckooCustomMsgAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    /**
     * @dw 编辑内容
     */
    private void clickShowEditTextDialog(final int position) {
        final QMUIDialog.EditTextDialogBuilder builder = new QMUIDialog.EditTextDialogBuilder(getNowContext());
        builder.setTitle("编辑内容")
                .setInputType(InputType.TYPE_CLASS_TEXT)
                .addAction("不改了", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                })
                .addAction("确认修改", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        CharSequence text = builder.getEditText().getText();
                        if (text != null && text.length() > 15) {
                            showToastMsg("长度超过最大15个字符！");
                            return;
                        }
                        if (text != null) {
                            list.get(position).setMsg(text.toString());
                            cuckooCustomMsgAdapter.notifyDataSetChanged();
                            dialog.dismiss();
                        }
                    }
                }).show();
        builder.getEditText().setText(list.get(position).getMsg());
    }

    @Override
    protected boolean hasTopBar() {
        return false;
    }
}
