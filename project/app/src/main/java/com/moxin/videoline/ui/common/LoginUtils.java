package com.moxin.videoline.ui.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.moxin.videoline.CuckooApplication;
import com.moxin.videoline.R;
import com.moxin.videoline.api.Api;
import com.moxin.videoline.helper.TxLogin;
import com.moxin.videoline.manage.AppManager;
import com.moxin.videoline.manage.SaveData;
import com.moxin.videoline.modle.IsBindPhoneBean;
import com.moxin.videoline.modle.UserModel;
import com.moxin.videoline.ui.BindPhoneActivity;
import com.moxin.videoline.ui.CuckooMobileLoginActivity;
import com.moxin.videoline.ui.MainActivity;
import com.moxin.videoline.utils.SharedPreferencesUtils;
import com.fm.openinstall.OpenInstall;
import com.google.gson.Gson;
import com.lzy.okgo.callback.StringCallback;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMOfflinePushSettings;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by 魏鹏 on 2018/3/26.
 * email:1403102936@qq.com
 * 山东布谷鸟网络科技有限公司著
 */

public class LoginUtils {

    private static Intent intent;

    public static void doLogin(final Context context, final UserModel userData) {

        TxLogin.loginIm(
                userData.getId(),
                userData.getUser_sign(),
                new TIMCallBack() {

                    @Override
                    public void onError(int i, String s) {
                        //登录失败
                        LogUtils.i(s+"腾讯云登录有误!"+i);
                        ToastUtils.showLong(s);

                    }

                    @Override
                    public void onSuccess() {
                        //登录成功
                        LogUtils.i("腾讯云登录成功!");
                        TIMOfflinePushSettings settings = new TIMOfflinePushSettings();
                        //开启离线推送
                        settings.setEnabled(true);
                        //设置收到 C2C 离线消息时的提示声音，这里把声音文件放到了 res/raw 文件夹下
                        settings.setC2cMsgRemindSound(Uri.parse("android.resource://" + CuckooApplication.getInstances().getPackageName() + "/" + R.raw.call));
                        //设置收到群离线消息时的提示声音，这里把声音文件放到了 res/raw 文件夹下
                        //settings.setGroupMsgRemindSound(Uri.parse("android.resource://" + CuckooApplication.getInstances().getPackageName() + "/" + R.raw.call));

                        //通知SaveData类登录成功
                        SaveData.loginSuccess(userData);

                        //umeng
                        PushAgent mPushAgent = PushAgent.getInstance(context);
                        mPushAgent.addAlias(userData.getId(), "buguniao", new UTrack.ICallBack() {
                            @Override
                            public void onMessage(boolean isSuccess, String message) {

                                LogUtils.i("umeng推送添加别名：" + isSuccess + "---" + message);
                            }
                        });
                        OpenInstall.reportRegister();
                        //统计友盟注册用户数据
                        MobclickAgent.onProfileSignIn(userData.getId());

                        //绑定手机号
                        Api.getIsBindPhone(new StringCallback() {
                            @Override
                            public void onSuccess(String s, Call call, Response response) {
                                Log.e("getIsBindPhone", s);
                                IsBindPhoneBean bean = new Gson().fromJson(s, IsBindPhoneBean.class);
                                if (bean.getCode() == 1) {
                                    String forceBind = bean.getIs_force_binding_mobile();
                                    String is_binding_mobile = bean.getIs_binding_mobile();
                                    //"0未绑定手机号1已绑定
                                    switch (is_binding_mobile) {
                                        case "0":

                                            intent = new Intent(context, BindPhoneActivity.class);
                                            intent.putExtra("state", forceBind);
                                            context.startActivity(intent);
                                            break;
                                        case "1":

                                            //跳转页面
                                            ToastUtils.showLong(R.string.login_success);

                                            intent = new Intent(context, MainActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                                    Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            context.startActivity(intent);
                                            ((Activity) context).finish();

                                            break;

                                    }
                                }
                            }

                            @Override
                            public void onError(Call call, Response response, Exception e) {
                                super.onError(call, response, e);
                                ToastUtils.showLong("绑定手机信息获取失败");
                            }
                        });


                    }
                }
        );

    }

    //退出登录
    public static void doLoginOut(Context context) {

        TxLogin.logout(new TIMCallBack() {
            @Override
            public void onError(int i, String s) {

                LogUtils.i("退出登录腾讯云失败,error:" + s);
            }

            @Override
            public void onSuccess() {
                LogUtils.i("退出登录腾讯云成功");
            }
        });

        //umeng
        PushAgent mPushAgent = PushAgent.getInstance(context);
        mPushAgent.deleteAlias(SaveData.getInstance().getId(), "buguniao", new UTrack.ICallBack() {
            @Override
            public void onMessage(boolean isSuccess, String message) {

                LogUtils.i("umeng推送删除别名：" + isSuccess + "---" + message);
            }
        });

        //统计友盟退出用户数据
        MobclickAgent.onProfileSignOff();

        AppManager.getAppManager().finishAllActivity();
        //清除登录信息
        SaveData.getInstance().clearData();

        //清除sp信息
        SharedPreferencesUtils.clear(context);

        Intent intent = new Intent(context, CuckooMobileLoginActivity.class);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
