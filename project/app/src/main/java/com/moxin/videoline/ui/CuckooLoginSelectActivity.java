package com.moxin.videoline.ui;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.moxin.videoline.R;
import com.moxin.videoline.api.Api;
import com.moxin.videoline.base.BaseActivity;
import com.moxin.videoline.inter.JsonCallback;
import com.moxin.videoline.json.JsonRequestUserBase;
import com.moxin.videoline.modle.ConfigModel;
import com.moxin.videoline.modle.CuckooOpenInstallModel;
import com.moxin.videoline.ui.common.LoginUtils;
import com.moxin.videoline.utils.Utils;
import com.fm.openinstall.OpenInstall;
import com.fm.openinstall.listener.AppInstallAdapter;
import com.fm.openinstall.model.AppData;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;
import cn.sharesdk.facebook.Facebook;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.PlatformDb;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import okhttp3.Call;
import okhttp3.Response;

public class CuckooLoginSelectActivity extends BaseActivity {

    @BindView(R.id.ll_mobile)
    RelativeLayout ll_mobile;

    @BindView(R.id.ll_qq)
    RelativeLayout ll_qq;

    @BindView(R.id.ll_wechat)
    RelativeLayout ll_wechat;

    @BindView(R.id.ll_facebook)
    RelativeLayout ll_facebook;
    private String uuid;

    @Override
    protected Context getNowContext() {
        return this;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_cuckoo_login_select;
    }

    @Override
    protected void initView() {
        QMUIStatusBarHelper.translucent(this); // 沉浸式状态栏

        ConfigModel config = ConfigModel.getInitData();
        if (config.getOpen_login_qq() == 1) {
            ll_qq.setVisibility(View.VISIBLE);
        }

        if (config.getOpen_login_wx() == 1) {
            ll_wechat.setVisibility(View.VISIBLE);
        }

        if (config.getOpen_login_facebook() == 1) {
            ll_facebook.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void initSet() {

    }

    @Override
    protected void initData() {

        uuid = Utils.getUniquePsuedoID();

    }

    @Override
    protected void initPlayerDisplayData() {

    }

    @OnClick({R.id.ll_wechat, R.id.ll_qq, R.id.ll_mobile, R.id.ll_facebook, R.id.terms_of_service})
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.ll_mobile:
                clickMobile();
                break;
            case R.id.ll_wechat:
                clickWeChat();
                break;
            case R.id.ll_qq:
                clickQQ();
                break;
            case R.id.ll_facebook:
                clickFacebook();
                break;
            case R.id.terms_of_service:
                doReadService();
                break;
            default:
                break;
        }
    }


    //阅读服务条款
    private void doReadService() {
        WebViewActivity.openH5Activity(this, false, getString(R.string.terms_of_service), ConfigModel.getInitData().getApp_h5().getPrivate_clause_url());
    }


    private void clickFacebook() {
        Platform plat = ShareSDK.getPlatform(Facebook.NAME);

        //执行登录，登录后在回调里面获取用户资料
        plat.showUser(null);
        plat.SSOSetting(false);  //设置false表示使用SSO授权方式
        plat.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int action, HashMap<String, Object> hashMap) {

                //用户资源都保存到res
                //通过打印res数据看看有哪些数据是你想要的
                if (action == Platform.ACTION_USER_INFOR) {
                    final PlatformDb platDB = platform.getDb();//获取数平台数据DB
                    //通过DB获取各种数据
                    platDB.getUserId();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            doPlatLogin(platDB.getUserId(), 4);
                        }
                    });
                }
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {

            }

            @Override
            public void onCancel(Platform platform, int i) {

            }
        });
        plat.removeAccount(true);
    }

    private void clickQQ() {

        Platform plat = ShareSDK.getPlatform(QQ.NAME);

        //执行登录，登录后在回调里面获取用户资料
        plat.showUser(null);
        plat.SSOSetting(false);  //设置false表示使用SSO授权方式
        plat.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int action, HashMap<String, Object> hashMap) {

                //用户资源都保存到res
                //通过打印res数据看看有哪些数据是你想要的
                if (action == Platform.ACTION_USER_INFOR) {
                    final PlatformDb platDB = platform.getDb();//获取数平台数据DB
                    //通过DB获取各种数据
                    platDB.getUserId();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            doPlatLogin(platDB.getUserId(), 2);
                        }
                    });
                }
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {

            }

            @Override
            public void onCancel(Platform platform, int i) {

            }
        });
        plat.removeAccount(true);

    }

    private void clickWeChat() {

        Platform plat = ShareSDK.getPlatform(Wechat.NAME);

        //执行登录，登录后在回调里面获取用户资料
        plat.showUser(null);
        plat.SSOSetting(false);  //设置false表示使用SSO授权方式
        plat.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int action, HashMap<String, Object> hashMap) {

                //用户资源都保存到res
                //通过打印res数据看看有哪些数据是你想要的
                if (action == Platform.ACTION_USER_INFOR) {
                    final PlatformDb platDB = platform.getDb();//获取数平台数据DB
                    //通过DB获取各种数据
                    platDB.getUserId();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            doPlatLogin(platDB.getUserId(), 3);
                        }
                    });
                }
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {

            }

            @Override
            public void onCancel(Platform platform, int i) {

            }
        });
        plat.removeAccount(true);
    }

    private void clickMobile() {
        Intent intent = new Intent(this, CuckooMobileLoginActivity.class);
        startActivity(intent);
    }

    //三方授权登录
    private void doPlatLogin(final String platId, final int loginway) {

        showLoadingDialog(getString(R.string.loading_login));

        //获取OpenInstall安装数据
        OpenInstall.getInstall(new AppInstallAdapter() {
            @Override
            public void onInstall(AppData appData) {
                //获取渠道数据
                String channelCode = appData.getChannel();
                //获取自定义数据
                String bindData = appData.getData();

                String inviteCode = "";
                String agent = "";
                if (!TextUtils.isEmpty(bindData)) {
                    CuckooOpenInstallModel data = JSON.parseObject(bindData, CuckooOpenInstallModel.class);
                    inviteCode = data.getInvite_code();
                    agent = data.getAgent();
                }

                Api.doPlatAuthLogin(platId, inviteCode, agent, uuid, loginway, new JsonCallback() {
                    @Override
                    public Context getContextToJson() {
                        return getNowContext();
                    }

                    @Override
                    public void onSuccess(String s, Call call, Response response) {

                        hideLoadingDialog();
                        JsonRequestUserBase requestObj = JsonRequestUserBase.getJsonObj(s);
                        if (requestObj.getCode() == 1) {

                            //是否完善资料
                            if (requestObj.getData().getIs_reg_perfect() == 1) {
                                LoginUtils.doLogin(CuckooLoginSelectActivity.this, requestObj.getData());
                            } else {
                                Intent intent = new Intent(getNowContext(), PerfectRegisterInfoActivity.class);
                                intent.putExtra(PerfectRegisterInfoActivity.USER_LOGIN_INFO, requestObj.getData());
                                startActivity(intent);
                                finish();
                            }
                        }
                        showToastMsg(requestObj.getMsg());
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        hideLoadingDialog();
                    }
                });

                Log.d("OpenInstall", "getInstall : installData = " + appData.toString());
            }
        });


    }
}
