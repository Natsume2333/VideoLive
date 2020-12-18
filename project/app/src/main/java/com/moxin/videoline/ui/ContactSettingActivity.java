package com.moxin.videoline.ui;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.blankj.utilcode.util.ToastUtils;
import com.moxin.videoline.R;
import com.moxin.videoline.api.Api;
import com.moxin.videoline.base.BaseActivity;
import com.moxin.videoline.modle.ContactBean;
import com.moxin.videoline.modle.IsBindPhoneBean;
import com.google.gson.Gson;
import com.lzy.okgo.callback.StringCallback;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

public class ContactSettingActivity extends BaseActivity {

    @BindView(R.id.contact_wechat_num_et)
    EditText wechatNumEt;

    @BindView(R.id.contact_wechat_num_price_et)
    EditText wechatNumPriceEt;

    @BindView(R.id.contact_qq_num_et)
    EditText qqNumEt;

    @BindView(R.id.contact_qq_num_price_et)
    EditText qqNumPriceEt;

    @BindView(R.id.contact_phone_num_et)
    EditText phoneNumEt;

    @BindView(R.id.contact_phone_num_price_et)
    EditText phoneNumPriceEt;


    @Override
    protected Context getNowContext() {
        return this;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_contact_setting;
    }

    @Override
    protected boolean hasTopBar() {
        return true;
    }

    @Override
    protected void initView() {
        getTopBar().setTitle("设置联系方式");
        getTopBar().setBackgroundColor(getResources().getColor(R.color.admin_color));
    }


    @OnClick({R.id.contact_upload_contact_info_btn})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.contact_upload_contact_info_btn:
                String wechatNum = wechatNumEt.getText().toString();
                String wechatPrice = wechatNumPriceEt.getText().toString();

                String qqNum = qqNumEt.getText().toString();
                String qqPrice = qqNumPriceEt.getText().toString();

                String phoneNum = phoneNumEt.getText().toString();
                String phonePrice = phoneNumPriceEt.getText().toString();


                valueIsEmpty(wechatNum, "微信号不可为空");
                valueIsEmpty(wechatPrice, "微信号价格不可为空");
                valueIsEmpty(qqNum, "QQ号不可为空");
                valueIsEmpty(qqPrice, "QQ号价格不可为空");
                valueIsEmpty(phoneNum, "手机号不可为空");
                valueIsEmpty(phonePrice, "手机号价格不可为空");


                Log.e("saveContactInfo",wechatNum+"="+wechatPrice+"="+qqNum+"="+qqPrice+"="+phoneNum+"="+phonePrice);
                Api.saveContactInfo(wechatNum, wechatPrice, qqNum, qqPrice, phoneNum, phonePrice, new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        Log.e("saveContactInfo",s);
                        IsBindPhoneBean bean = new Gson().fromJson(s, IsBindPhoneBean.class);
                        if (bean.getCode() == 1) {
                            ToastUtils.showShort(bean.getMsg());
                            finish();
                        } else {
                            ToastUtils.showShort(bean.getMsg());
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Log.e("saveContactInfo",e.toString());
                    }
                });


                break;
            default:
                break;
        }
    }

    private void valueIsEmpty(String value, String toast) {
        if (TextUtils.isEmpty(value)) {
            ToastUtils.showShort(toast);
            return;
        }
    }

    @Override
    protected void initSet() {

    }

    @Override
    protected void initData() {
        getContactData();
    }

    private void getContactData() {
        //type 1 不传是h5页面
        Api.getContactData("1", new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                Log.e("getContactData",s);
                ContactBean bean = new Gson().fromJson(s, ContactBean.class);

                if (bean.getCode() == 1) {
                    ContactBean.UserBean data = bean.getUser();
                    String wx_number = data.getWx_number();
                    String wx_price = data.getWx_price();
                    String qq_number = data.getQq_number();
                    String qq_price = data.getQq_price();
                    String phone_number = data.getPhone_number();
                    String phone_price = data.getPhone_price();

                    setContactData(wechatNumEt, wx_number);
                    setContactData(wechatNumPriceEt, wx_price);
                    setContactData(qqNumEt, qq_number);
                    setContactData(qqNumPriceEt, qq_price);
                    setContactData(phoneNumEt, phone_number);
                    setContactData(phoneNumPriceEt, phone_price);

                } else {
                    ToastUtils.showShort(bean.getMsg());
                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
            }
        });
    }

    private void setContactData(EditText et, String number) {
        if (!TextUtils.isEmpty(number)) {
            et.setText(number);
        }
    }

    @Override
    protected void initPlayerDisplayData() {

    }


}
