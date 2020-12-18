package com.moxin.videoline.ui;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.moxin.videoline.R;
import com.moxin.videoline.api.Api;
import com.moxin.videoline.base.BaseActivity;
import com.moxin.videoline.json.JsonRequestBase;
import com.moxin.videoline.modle.CustomMsgModel;
import com.lzy.okgo.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

public class CuckooAddCustomMsgActivity extends BaseActivity {


    @BindView(R.id.top_bar_title_tv)
    TextView top_bar_title_tv;

    @BindView(R.id.top_bar_back_iv)
    ImageView top_bar_back_iv;


    @BindView(R.id.add_custom_msg_et)
    EditText customMsgEt;

    @BindView(R.id.add_custom_msg_et_max)
    TextView maxNum;

    private List<CustomMsgModel> list = new ArrayList<>();

    //-1 新增  其他 编辑operating为编辑位置
    private int operating;

    @Override
    protected Context getNowContext() {
        return this;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_cuckoo_add_custom_msg;
    }

    @Override
    protected void initView() {
        list = (List<CustomMsgModel>) getIntent().getSerializableExtra("customMsg");
        operating = getIntent().getIntExtra("operating", 0);

        //标题
        if (operating == -1) {
            top_bar_title_tv.setText("添加话术");
        } else {
            top_bar_title_tv.setText("编辑话术");
            customMsgEt.setHint(list.get(operating).getMsg());
        }

        top_bar_back_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        customMsgEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                maxNum.setText(customMsgEt.getText().length() + "/" + 100);
            }
        });
    }

    @Override
    protected void initSet() {

    }

    @Override
    protected void initData() {

    }


    @OnClick({R.id.top_bar_save_tv})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.top_bar_save_tv:
                String customStr = customMsgEt.getText().toString().trim();
                if (customStr != null && customStr.length() > 15) {
                    showToastMsg("长度超过最大15个字符！");
                    return;
                }

                //新增
                if (operating == -1) {
                    if (customStr != null) {
                        CustomMsgModel model = new CustomMsgModel();
                        model.setMsg(customStr);
                        model.setId(0);
                        list.add(model);
                    }
                } else {
                    if (customStr != null) {
                        list.get(operating).setMsg(customStr);
                    }
                }

                clickSaveData();
                break;


        }
    }


    private void clickSaveData() {
        StringBuilder msgStr = new StringBuilder();
        for (CustomMsgModel item : list) {
            msgStr.append(item.getId() + ":" + item.getMsg() + "-");
        }

        showLoadingDialog("正在保存...");
        Api.doRequestSaveCustomMsgData(uId, uToken, msgStr.toString(), new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                hideLoadingDialog();
                Log.e("SaveCustomMsgData", s);
                JsonRequestBase data = JsonRequestBase.getJsonObj(s, JsonRequestBase.class);
                if (data.getCode() == 1) {
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    ToastUtils.showLong(data.getMsg());
                }


            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                hideLoadingDialog();
            }
        });
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }

    @Override
    protected void initPlayerDisplayData() {

    }

    @Override
    protected boolean hasTopBar() {
        return false;
    }
}
