package com.moxin.videoline.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.moxin.videoline.R;
import com.moxin.videoline.api.Api;
import com.moxin.videoline.base.BaseActivity;
import com.moxin.videoline.dialog.CuckooShareDialog;
import com.moxin.videoline.manage.AppConfig;
import com.moxin.videoline.manage.SaveData;
import com.moxin.videoline.modle.DrawalBean;
import com.moxin.videoline.ui.dialog.InviteShareDialog;
import com.moxin.videoline.utils.Utils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.lzy.okgo.callback.StringCallback;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;


public class InviteActivityNew extends BaseActivity implements BaseQuickAdapter.RequestLoadMoreListener {


    public static void start(Context c) {
        Intent intent = new Intent(c, InviteActivityNew.class);
        c.startActivity(intent);
    }


    @Override
    protected Context getNowContext() {
        return null;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_invite_n;
    }

    @BindView(R.id.title_all)
    View title;

    @Override
    protected void initView() {
        Utils.initTransTitleBar(title, "分成计划", this);
    }


    CuckooShareDialog shareDialog;

    public void toShared() {
        if (shareDialog == null) {
            shareDialog = new CuckooShareDialog(this);
            shareDialog.setImg(SaveData.getInstance().getUserInfo().getAvatar());
            shareDialog.setShareUrl(AppConfig.MAIN_URL + "/mapi/public/index.php/api/download_api/index?invite_code=" + SaveData.getInstance().getId());
        }
        shareDialog.show();
    }

    @OnClick({R.id.tgmx, R.id.tx})
    public void click(View v) {
        switch (v.getId()) {
            case R.id.tgmx:
                InviteDetailedActivity.start(this);
                break;
            case R.id.tx:
                WealthDetailedActivity.start(this, WealthDetailedActivity.TINVITE_YPE_RECHARGE);
                break;
        }
    }

    @Override
    protected void initSet() {

    }


    @BindView(R.id.ye)
    TextView ye;

    @Override
    protected void initData() {
        Api.getWithdrawal(new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                DrawalBean bean = new Gson().fromJson(s, DrawalBean.class);
                ye.setText(bean.getData().getInvitation_coin());
            }
        });

    }

    @Override
    protected void initPlayerDisplayData() {

    }

    @OnClick({R.id.to_shared})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.to_shared:

                if (ContextCompat.checkSelfPermission(InviteActivityNew.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION},
                            MY_PERMISSIONS_REQUEST);
                } else {
                    clickShare();
                }
//                Intent intent1 = new Intent(this, PosterActivity.class);
//                startActivity(intent1);
                break;
        }
    }

    private int MY_PERMISSIONS_REQUEST = 1111;
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
            grantResults) {
//        Permissions4M.onRequestPermissionsResult(this,requestCode,grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void clickShare() {
//        CuckooShareDialog shareDialog = new CuckooShareDialog(this);
//        shareDialog.setImg(SaveData.getInstance().getUserInfo().getAvatar());
//        shareDialog.setShareUrl(Utils.getShareExtensionUrl());
//        shareDialog.show();


        new InviteShareDialog(this).show();

    }

    @Override
    public void onLoadMoreRequested() {

    }
}
