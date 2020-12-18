package com.moxin.videoline.business;

import android.content.Context;
import android.os.CountDownTimer;

import com.blankj.utilcode.util.LogUtils;
import com.moxin.videoline.ApiConstantDefine;
import com.moxin.videoline.api.Api;
import com.moxin.videoline.json.JsonRequestDoEndVideoCall;
import com.moxin.videoline.json.JsonRequestDoVideoCallTimeCharging;
import com.moxin.videoline.manage.SaveData;
import com.moxin.videoline.utils.BGTimedTaskManage;
import com.moxin.videoline.utils.StringUtils;
import com.moxin.videoline.utils.im.IMHelp;
import com.lzy.okgo.callback.StringCallback;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMValueCallBack;

import okhttp3.Call;
import okhttp3.Response;

public class CuckooVideoLineTimeBusiness {

    private Context context;
    private VideoLineTimeBusinessCallback callback;

    //是否需要扣费
    private boolean isNeedCharge;
    //是否有免费时长
    private int freeTime;

    private String toUserId;

    //是否开始扣费
    private boolean isCharge;

    //定时扣费任务
    private BGTimedTaskManage chargingBgTimedTaskManage;
    private CountDownTimer freeCountDownTimer;

    public CuckooVideoLineTimeBusiness(Context context, boolean isNeedCharge, int freeTime, String toUserId, final VideoLineTimeBusinessCallback callback) {
        this.callback = callback;
        this.context = context;
        this.toUserId = toUserId;
        this.freeTime = freeTime;

        if (isNeedCharge) {
            if (freeTime > 0) {
                //有免费试用
                freeCountDownTimer = new CountDownTimer(freeTime * 1000, 1000) {

                    @Override
                    public void onTick(long l) {
                        if (callback != null) {
                            callback.onFreeTime(l / 1000);
                        }
                    }

                    @Override
                    public void onFinish() {
                        if (callback != null) {
                            callback.onFreeTimeEnd();
                        }
                    }
                };
                freeCountDownTimer.start();
            } else {
                charging();
            }
        }
    }

    public void stop() {
        if (chargingBgTimedTaskManage != null) {
            chargingBgTimedTaskManage.stopRunnable();
            chargingBgTimedTaskManage = null;
        }

        if (freeCountDownTimer != null) {
            freeCountDownTimer.cancel();
        }
    }

    //开始扣费
    public void charging() {

        isCharge = true;
        //开始执行定时扣费任务时间间隔为1分钟
        if (chargingBgTimedTaskManage == null) {
            chargingBgTimedTaskManage = new BGTimedTaskManage();
            chargingBgTimedTaskManage.setTime(1000 * 60);
        }
        chargingBgTimedTaskManage.startRunnable(new BGTimedTaskManage.BGTimeTaskRunnable() {
            @Override
            public void onRunTask() {
                doTimeCharging();
                LogUtils.i("扣费中...");
            }
        }, true);
    }

    /**
     * 计时扣费
     */
    private void doTimeCharging() {

        Api.doVideoCallTimeCharging(SaveData.getInstance().getId(), SaveData.getInstance().getToken(), new StringCallback() {

            @Override
            public void onSuccess(String s, Call call, Response response) {

                JsonRequestDoVideoCallTimeCharging jsonObj = JsonRequestDoVideoCallTimeCharging.getJsonObj(s);
                if (jsonObj.getCode() == 1) {

                    //chatUnitPrice.setText(jsonObj.getCharging_coin());
                    LogUtils.i("扣费成功");
                    if (callback != null) {
                        callback.onCallbackChargingSuccess();
                    }

                } else if (jsonObj.getCode() == ApiConstantDefine.ApiCode.BALANCE_NOT_ENOUGH) {

                    if (callback != null) {
                        callback.onCallbackNotBalance();
                    }

                } else if (jsonObj.getCode() == ApiConstantDefine.ApiCode.VIDEO_CALL_RECORD_NOT_FOUNT) {

                    if (callback != null) {
                        callback.onCallbackCallRecordNotFount();
                    }

                } else if (jsonObj.getCode() == ApiConstantDefine.ApiCode.VIDEO_CALL_RECORD_NOT_BALANCE) {

                    if (callback != null) {
                        callback.onCallbackCallNotMuch(jsonObj.getMsg());
                    }

                } else {

                    if (callback != null) {
                        callback.onCallbackEndVideo(jsonObj.getMsg());
                    }
                }
            }
        });
    }


    //关闭通话
    public void doHangUpVideo() {

        Api.doEndVideoCall(SaveData.getInstance().getId(), SaveData.getInstance().getToken(), toUserId, new StringCallback() {

            @Override
            public void onSuccess(String s, Call call, Response response) {

                JsonRequestDoEndVideoCall jsonObj = JsonRequestDoEndVideoCall.getJsonObj(s);

                //发送挂断消息
                IMHelp.sendEndVideoCallMsg(toUserId, new TIMValueCallBack<TIMMessage>() {
                    @Override
                    public void onError(int i, String s) {

                    }

                    @Override
                    public void onSuccess(TIMMessage timMessage) {

                    }
                });
                if (callback != null) {
                    callback.onHangUpVideoSuccess(StringUtils.toInt(jsonObj.getFabulous()));
                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                if (callback != null) {
                    callback.onHangUpVideoSuccess(StringUtils.toInt("1"));
                }
            }
        });

    }

    public boolean isCharge() {
        return isCharge;
    }

    public interface VideoLineTimeBusinessCallback {

        //扣费成功
        void onCallbackChargingSuccess();

        //余额不足
        void onCallbackNotBalance();

        //扣费失败通话记录不存在
        void onCallbackCallRecordNotFount();

        //余额不够下一分钟
        void onCallbackCallNotMuch(String msg);

        //挂电话
        void onCallbackEndVideo(String msg);

        //挂断通话
        void onHangUpVideoSuccess(int isFabulous);

        //免费倒计时回调
        void onFreeTime(long time);

        //倒计时完毕回调
        void onFreeTimeEnd();


    }

}
