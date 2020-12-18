package com.moxin.videoline.ui;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.moxin.videoline.R;
import com.moxin.videoline.api.Api;
import com.moxin.videoline.api.ApiUtils;
import com.moxin.videoline.base.BaseActivity;
import com.moxin.videoline.inter.JsonCallback;
import com.moxin.videoline.modle.IsBindPhoneBean;
import com.moxin.videoline.utils.Utils;
import com.google.gson.Gson;
import com.lzy.okgo.callback.StringCallback;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

/***
 * 绑定手机号
 */
public class BindPhoneActivity extends BaseActivity {

    @BindView(R.id.et_mobile)
    EditText et_mobile;

    @BindView(R.id.et_code)
    EditText et_code;

    @BindView(R.id.tv_send_code)
    TextView tv_send_code;


    @Override
    protected Context getNowContext() {
        return this;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_bind_phone;
    }

    @Override
    protected void initView() {
        getTopBar().setTitle("绑定手机号");
        String state = getIntent().getStringExtra("state");

        //0 不强制绑定  1强制绑定
        if (!"1".equals(state)) {
            Button rightBtn = getTopBar().addRightTextButton("跳过", R.id.right_btn);
            rightBtn.setTextColor(getResources().getColor(R.color.black));
            rightBtn.setOnClickListener(this);
        }

    }

    @Override
    protected void initSet() {

    }

    @Override
    protected void initData() {

    }

    @OnClick({R.id.tv_send_code, R.id.btn_submit})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.right_btn:
                //跳转页面
                ToastUtils.showLong(R.string.login_success);

                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                break;

            //发送验证码
            case R.id.tv_send_code:
                clickSendCode();
                break;

            //绑定
            case R.id.btn_submit:

                clickDoLogin();
                break;
            default:
                break;
        }
    }


    private void clickDoLogin() {

        if (!tv_send_code.getText().toString().equals("")) {
            bindPhone(et_mobile.getText().toString(), et_code.getText().toString());
        } else {
            showToastMsg(getString(R.string.mobile_login_code_not_empty));
        }
    }

    private void bindPhone(String mobile, String code) {
        Api.bindMobile(mobile, code, new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                IsBindPhoneBean bean = new Gson().fromJson(s, IsBindPhoneBean.class);
                if (bean.getCode() == 1) {
                    ToastUtils.showLong(R.string.login_success);

                    Intent intent = new Intent(BindPhoneActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                    ToastUtils.showShort(bean.getMsg());
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


    //发送验证码
    private void clickSendCode() {

        if (Utils.isMobile(et_mobile.getText().toString())) {
            sendCode(et_mobile.getText().toString());
            tv_send_code.setEnabled(false);

            new CountDownTimer(60 * 1000, 1000) {

                @Override
                public void onTick(long l) {
                    tv_send_code.setText("（" + (l / 1000) + "）");
                }

                @Override
                public void onFinish() {
                    tv_send_code.setText("发送验证码");
                    tv_send_code.setEnabled(true);
                }
            }.start();

        } else {
            showToastMsg(getString(R.string.mobile_login_mobile_error));
        }
    }

    /**
     * 发送验证码
     */
    private void sendCode(String str) {
        Api.sendCodeByRegister(str, new JsonCallback() {
            @Override
            public Context getContextToJson() {
                return getNowContext();
            }

            @Override
            public void onSuccess(String s, Call call, Response response) {
                super.onSuccess(s, call, response);
                showToastMsg(ApiUtils.getJsonObj2(s).getString("msg"));
            }
        });
    }


    @Override
    protected void initPlayerDisplayData() {

    }

    @Override
    protected boolean hasTopBar() {
        return true;
    }
}
