package com.moxin.videoline.ui;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.moxin.videoline.R;
import com.moxin.videoline.api.Api;
import com.moxin.videoline.base.BaseActivity;
import com.moxin.videoline.json.JsonGetUserRatio;
import com.moxin.videoline.json.JsonRequestBase;
import com.moxin.videoline.modle.UserRatioModel;
import com.lzy.okgo.callback.StringCallback;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

public class CuckooChangeUserRatioActivity extends BaseActivity {

    public static final String USER_ID = "USER_ID";

    @BindView(R.id.et_video)
    EditText et_video;

    @BindView(R.id.et_gift)
    EditText et_gift;

    @BindView(R.id.et_msg)
    EditText et_msg;

    @BindView(R.id.et_short_video)
    EditText et_short_video;

    @BindView(R.id.et_img)
    EditText et_img;

    @BindView(R.id.tv_videoline_max)
    TextView tv_videoline_max;

    @BindView(R.id.tv_gift_max)
    TextView tv_gift_max;

    @BindView(R.id.tv_chat_max)
    TextView tv_chat_max;

    @BindView(R.id.tv_video_max)
    TextView tv_video_max;

    @BindView(R.id.tv_photo_max)
    TextView tv_photo_max;

    private int userId;
    private UserRatioModel userRatioModel;

    @Override
    protected Context getNowContext() {
        return this;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.cuckoo_act_change_user_ratio;
    }

    @Override
    protected void initView() {
        getTopBar().setTitle("修改提成比例");
    }

    @Override
    protected void initSet() {

    }

    @Override
    protected void initData() {
        userId = getIntent().getIntExtra(USER_ID, 0);
        requestGetData();
    }

    @Override
    protected void initPlayerDisplayData() {

    }

    @OnClick({R.id.btn_submit})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit:
                clickSubmit();
                break;
            default:
                break;
        }
    }

    private void clickSubmit() {
        String videoLineRatio = et_video.getText().toString();
        String giftRatio = et_gift.getText().toString();
        String photoRatio = et_img.getText().toString();
        String shortVideoRatio = et_short_video.getText().toString();
        String chatRatio = et_msg.getText().toString();

        showLoadingDialog("正在提交...");
        Api.doChangeUserRatio(uId, uToken, userId, videoLineRatio, giftRatio, photoRatio, shortVideoRatio, chatRatio, new StringCallback() {

            @Override
            public void onSuccess(String s, Call call, Response response) {
                hideLoadingDialog();
                JsonRequestBase data = JsonRequestBase.getJsonObj(s, JsonRequestBase.class);
                if (data.getCode() == 1) {
                    ToastUtils.showLong("修改成功！");
                    requestGetData();
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

    private void requestGetData() {
        Api.doRequestGetRatioInfo(uId, uToken, userId, new StringCallback() {

            @Override
            public void onSuccess(String s, Call call, Response response) {
                JsonGetUserRatio data = (JsonGetUserRatio) JsonGetUserRatio.getJsonObj(s, JsonGetUserRatio.class);
                if (data.getCode() == 1) {
                    userRatioModel = data.getData();
                    et_gift.setText(userRatioModel.getHost_bay_gift_proportion());
                    et_img.setText(userRatioModel.getHost_bay_phone_proportion());
                    et_video.setText(userRatioModel.getHost_one_video_proportion());
                    et_short_video.setText(userRatioModel.getHost_bay_video_proportion());
                    et_msg.setText(userRatioModel.getHost_direct_messages());

                    tv_gift_max.setText("最大比例:" + userRatioModel.getGuild_max_gift_ratio());
                    tv_chat_max.setText("最大比例:" + userRatioModel.getGuild_max_chat_ratio());
                    tv_video_max.setText("最大比例:" + userRatioModel.getGuild_max_video_ratio());
                    tv_videoline_max.setText("最大比例:" + userRatioModel.getGuild_max_videoline_ratio());
                    tv_photo_max.setText("最大比例:" + userRatioModel.getHost_bay_phone_proportion());
                } else {
                    ToastUtils.showLong(data.getMsg());
                }
            }
        });
    }


    @Override
    protected boolean hasTopBar() {
        return true;
    }
}
