package com.moxin.videoline.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.moxin.chat.model.CustomMessage;
import com.moxin.chat.model.Message;
import com.moxin.videoline.LiveConstant;
import com.moxin.videoline.R;
import com.moxin.videoline.adapter.GiftInfoAdapter;
import com.moxin.videoline.api.Api;
import com.moxin.videoline.api.ApiUtils;
import com.moxin.videoline.base.BaseActivity;
import com.moxin.videoline.business.CuckooVideoLineTimeBusiness;
import com.moxin.videoline.dialog.GiftBottomDialog;
import com.moxin.videoline.event.EImOnCloseVideoLine;
import com.moxin.videoline.event.EImOnPrivateMessage;
import com.moxin.videoline.event.EImVideoCallEndMessages;
import com.moxin.videoline.inter.JsonCallback;
import com.moxin.videoline.json.JsonRequest;
import com.moxin.videoline.json.JsonRequestBase;
import com.moxin.videoline.json.JsonRequestDoPrivateSendGif;
import com.moxin.videoline.json.JsonRequestTarget;
import com.moxin.videoline.json.JsonRequestVideoEndInfo;
import com.moxin.videoline.json.jsonmodle.TargetUserData;
import com.moxin.videoline.manage.RequestConfig;
import com.moxin.videoline.manage.SaveData;
import com.moxin.videoline.modle.ConfigModel;
import com.moxin.videoline.modle.GiftAnimationModel;
import com.moxin.videoline.modle.UserChatData;
import com.moxin.videoline.modle.custommsg.CustomMsg;
import com.moxin.videoline.modle.custommsg.CustomMsgPrivateGift;
import com.moxin.videoline.ui.common.Common;
import com.moxin.videoline.ui.dialog.CallNoMuchDialog;
import com.moxin.videoline.utils.BGTimedTaskManage;
import com.moxin.videoline.utils.DialogHelp;
import com.moxin.videoline.utils.StringUtils;
import com.moxin.videoline.utils.Utils;
import com.moxin.videoline.widget.GiftAnimationContentView;
import com.lzy.okgo.callback.StringCallback;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMValueCallBack;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 山东布谷鸟网络科技有限公司
 * 视频通话页面
 */

public class CuckooVoiceCallActivity extends BaseActivity implements GiftBottomDialog.DoSendGiftListen, CuckooVideoLineTimeBusiness.VideoLineTimeBusinessCallback {

    public static final String IS_BE_CALL = "IS_BE_CALL";
    public static final String IS_NEED_CHARGE = "IS_NEED_CHARGE";
    public static final String VIDEO_DEDUCTION = "VIDEO_DEDUCTION";
    public static final String CALL_TYPE = "CALL_TYPE";

    @BindView(R.id.videochat_voice)
    ImageView isSoundOut;//是否关闭声音

    @BindView(R.id.videochat_gift)
    ImageView videoGift;//礼物按钮

    //动画布局
    @BindView(R.id.ll_gift_content)
    GiftAnimationContentView mGiftAnimationContentView;

    //信息
    @BindView(R.id.videochat_unit_price)
    TextView chatUnitPrice;//收费金额

    @BindView(R.id.videochat_timer)
    Chronometer videoChatTimer;//通话计时时长

    //用户信息
    @BindView(R.id.this_player_img)
    CircleImageView headImage;//头像

    @BindView(R.id.this_player_name)
    TextView nickName;//昵称

    @BindView(R.id.this_player_loveme)
    ImageView thisPlayerLoveme;//关注按钮

    @BindView(R.id.tv_time_info)
    TextView tv_time_info;

    @BindView(R.id.tv_reward)
    TextView tv_reward;

    @BindView(R.id.user_coin)
    TextView tv_userCoin;

    @BindView(R.id.lv_live_room)
    RecyclerView giftInfoRv;

    @BindView(R.id.voice_call_location_tv)
    TextView locationTv;

    @BindView(R.id.voice_call_sign_tv)
    TextView signTv;

    //标记
    private static final int PERMISSION_REQ_ID_RECORD_AUDIO = 22;
    private static final int PERMISSION_REQ_ID_CAMERA = PERMISSION_REQ_ID_RECORD_AUDIO + 1;

    private UserChatData chatData;
    private boolean isOpenCamera = true;
    //创建 RtcEngine 对象
    private RtcEngine mRtcEngine;// Tutorial Step 1

    //是否是被呼叫用户
    private boolean isBeCall = false;

    //是否需要扣费
    private boolean isNeedCharge = false;

    private GiftBottomDialog giftBottomDialog;

    private TIMConversation conversation;

    //分钟扣费金额
    private String videoDeduction = "";

    private BGTimedTaskManage getVideoTimeInfoTask;
    private CuckooVideoLineTimeBusiness cuckooVideoLineTimeBusiness;
    private ImageView iv_lucky;
    private String video_px;
    private int callType;
    private List<String> guardInfoList = new ArrayList<>();
    private GiftInfoAdapter giftInfoAdaper;

    @Override
    protected Context getNowContext() {
        return CuckooVoiceCallActivity.this;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_voice_call;
    }

    @Override
    protected void initView() {
        QMUIStatusBarHelper.translucent(this);
        QMUIStatusBarHelper.setStatusBarLightMode(this);

        iv_lucky = findViewById(R.id.videochat_lucky_corn);
        findViewById(R.id.close_video_chat).setOnClickListener(this);
        iv_lucky.setOnClickListener(this);

        mGiftAnimationContentView.startHandel();

        //通话类型
        callType = getIntent().getIntExtra(CALL_TYPE, 0);
        //分钟扣费金额
        videoDeduction = getIntent().getStringExtra(VIDEO_DEDUCTION);
        chatUnitPrice.setText(String.format(Locale.getDefault(), "%s%s/分钟", videoDeduction, RequestConfig.getConfigObj().getCurrency()));

        videoChatTimer.setTextColor(getResources().getColor(R.color.white));
        String msgAlert = ConfigModel.getInitData().getVideo_call_msg_alert();
        if (!TextUtils.isEmpty(msgAlert)) {
            ToastUtils.showLong(msgAlert);
        }

        tv_time_info.setText("通话消费:" + videoDeduction);
        tv_reward.setText("礼物打赏:0");
        tv_userCoin.setText("对方余额:");

        //初始化礼物信息
        giftInfoRv.setLayoutManager(new LinearLayoutManager(this));
        giftInfoAdaper = new GiftInfoAdapter(guardInfoList, this);
        giftInfoRv.setAdapter(giftInfoAdaper);
    }

    @Override
    protected void initData() {

        chatData = getIntent().getParcelableExtra(VideoLineActivity.CALL_USER_DATA);
        video_px = getIntent().getStringExtra(VideoLineActivity.VIDEO_PX);

        isBeCall = getIntent().getBooleanExtra(IS_BE_CALL, false);
        isNeedCharge = getIntent().getBooleanExtra(IS_NEED_CHARGE, false);

        cuckooVideoLineTimeBusiness = new CuckooVideoLineTimeBusiness(this, isNeedCharge, 0, chatData.getUserModel().getId(), this);
        if (isBeCall) {
            initBeCallView();
            initBeCallAction();
        } else {
            initCallView();
            initCallAction();
        }

        //是否是用户
        if (isNeedCharge) {
            videoGift.setVisibility(View.VISIBLE);
            chatUnitPrice.setVisibility(View.GONE);

            //礼物和打赏收入
            tv_time_info.setVisibility(View.GONE);
            tv_reward.setVisibility(View.GONE);
            tv_userCoin.setVisibility(View.GONE);
        } else {
            chatUnitPrice.setVisibility(View.GONE);

            //礼物和打赏收入
            tv_time_info.setVisibility(View.VISIBLE);
            tv_reward.setVisibility(View.VISIBLE);
            tv_userCoin.setVisibility(View.VISIBLE);
        }

        conversation = TIMManager.getInstance().getConversation(TIMConversationType.C2C, chatData.getUserModel().getId());

        //开始计时
        videoChatTimer.start();

        //实时获取收益消费信息
        getVideoTimeInfoTask = new BGTimedTaskManage();
        //这里不让60s刷新一次了
        getVideoTimeInfoTask.setTime(10 * 6000);
        getVideoTimeInfoTask.setTimeTaskRunnable(new BGTimedTaskManage.BGTimeTaskRunnable() {
            @Override
            public void onRunTask() {
                requestGetVideoCallTimeInfo();
            }
        });

        getVideoTimeInfoTask.startRunnable(false);

        requestUserData();
    }


    @Override
    protected void initSet() {
        setOnclickListener(isSoundOut, videoGift, headImage, thisPlayerLoveme);
        //初始化本地操作
        initAgoraVoiceEngineAndJoinChannel();
        //加入频道
        joinChannel();
    }


    /**
     * 呼叫用户业务
     */
    private void initCallAction() {

    }

    /**
     * 余额不足操作
     */
    private void doBalance() {
        hangUpVideo();
        ToastUtils.showShort(R.string.money_insufficient);
    }

    /**
     * 被呼叫用户业务
     */
    private void initBeCallAction() {
    }

    /**
     * 呼叫视频通话用户View初始化
     */
    private void initCallView() {

    }

    /**
     * 被呼叫视频通话用户View初始化
     */
    private void initBeCallView() {
        videoGift.setVisibility(View.GONE);
    }

    @Override
    protected void initPlayerDisplayData() {
    }

    /////////////////////////////////////////////监听事件处理/////////////////////////////////////////
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.videochat_gift:
                clickOpenGiftDialog();
                break;
            case R.id.this_player_loveme:
                doLoveHer();
                //onCallbackCallNotMuch("余额不足");
                break;
            case R.id.close_video_chat:
                logoutChat();
                break;
            case R.id.videochat_voice:
                onLocalAudioMuteClicked();
                break;
            case R.id.this_player_img:
                Common.jumpUserPage(CuckooVoiceCallActivity.this, chatData.getUserModel().getId());
                break;
            case R.id.videochat_lucky_corn:
                Intent intent = new Intent(this, DialogH5Activity.class);
                intent.putExtra("uri", ConfigModel.getInitData().getApp_h5().getTurntable_url());
                startActivity(intent);
                break;
        }
    }

    /**
     * 初始化设置视图
     */
    private void initAgoraVoiceEngineAndJoinChannel() {
        //初始化RtcEngine对象
        try {
            mRtcEngine = RtcEngine.create(getBaseContext(), ConfigModel.getInitData().getApp_qgorq_key(), mRtcEventHandler);
        } catch (Exception e) {
            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }

        mRtcEngine.disableVideo();
        mRtcEngine.enableAudio();
    }


    //发起会话通道名额外的可选的数据--uid(uid为空时自动赋予)
    private void joinChannel() {
        mRtcEngine.joinChannel(null, chatData.getChannelName(), null, 0);
    }

    //本地音频静音
    public void onLocalAudioMuteClicked() {
        if (isSoundOut.isSelected()) {
            isSoundOut.setSelected(false);
            isSoundOut.setImageResource(R.drawable.icon_call_unmute);
        } else {
            isSoundOut.setSelected(true);
            isSoundOut.setImageResource(R.drawable.icon_call_muted);
        }
        mRtcEngine.muteLocalAudioStream(isSoundOut.isSelected());
    }


    private void operationVideoAndAudio(boolean muted) {
        mRtcEngine.muteLocalAudioStream(muted);
        mRtcEngine.muteLocalVideoStream(muted);
        mRtcEngine.muteAllRemoteVideoStreams(muted);
        mRtcEngine.muteAllRemoteAudioStreams(muted);

    }

    //销毁操作
    private void leaveChannel() {
        if (mRtcEngine != null) {
            mRtcEngine.leaveChannel();
        }
    }

    //关闭通话
    private void hangUpVideo() {
        operationVideoAndAudio(true);
        showLoadingDialog(getString(R.string.loading_huang_up));
        cuckooVideoLineTimeBusiness.doHangUpVideo();

        if (getVideoTimeInfoTask != null) {
            getVideoTimeInfoTask.stopRunnable();
        }
    }

    //礼物弹窗
    private void clickOpenGiftDialog() {
        if (giftBottomDialog == null) {

            giftBottomDialog = new GiftBottomDialog(this, chatData.getUserModel().getId());
            giftBottomDialog.setType(1);
            giftBottomDialog.setChanelId(chatData.getChannelName());
            giftBottomDialog.setDoSendGiftListen(this);
        }

        giftBottomDialog.show();
    }


    //添加礼物消息
    private void pushGiftMsg(CustomMsgPrivateGift giftCustom) {

        GiftAnimationModel giftAnimationModel = new GiftAnimationModel();

        giftAnimationModel.setUserAvatar(giftCustom.getSender().getAvatar());
        giftAnimationModel.setUserNickname(giftCustom.getSender().getUser_nickname());
        giftAnimationModel.setMsg(giftCustom.getFrom_msg());
        giftAnimationModel.setGiftIcon(giftCustom.getProp_icon());
        if (mGiftAnimationContentView != null) {

            guardInfoList.clear();
            //用户存储本地展示
            if (!isNeedCharge) {
                String from_msg = giftCustom.getTo_msg();
                guardInfoList.add("系统消息:" + from_msg);

                //展示信息
                giftInfoAdaper.setData(guardInfoList);
                giftInfoAdaper.notifyDataSetChanged();

            } else {
                String from_msg = giftCustom.getFrom_msg();
                guardInfoList.add("系统消息:" + from_msg);

                //展示信息
                giftInfoAdaper.setData(guardInfoList);
                giftInfoAdaper.notifyDataSetChanged();
            }


            mGiftAnimationContentView.addGift(giftAnimationModel);
        }
    }


    /**
     * 回调监听
     */
    private final IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() { // Tutorial Step 1

        @Override
        public void onFirstRemoteVideoDecoded(final int uid, int width, int height, int elapsed) {
            // 在第一个远程的
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                }
            });
        }

        @Override
        public void onUserOffline(final int uid, int reason) {
            //用户不在线
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                }
            });
        }

        @Override
        public void onUserMuteVideo(final int uid, final boolean muted) {
            // 在用户静音视频
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                }
            });
        }


        @Override
        public void onUserEnableVideo(int uid, final boolean enabled) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                }
            });
        }

        @Override
        public void onUserEnableLocalVideo(int uid, final boolean enabled) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                }
            });
        }

    };


    @Override
    protected void doLogout() {
        super.doLogout();
        leaveChannel();
        RtcEngine.destroy();
        mRtcEngine = null;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventVideoCallEndThread(EImVideoCallEndMessages var1) {

        LogUtils.i("收到消息一对一视频结束请求消息:" + var1.msg.getCustomMsg().getSender().getUser_nickname());

        try {
            CustomMsg customMsg = var1.msg.getCustomMsg();
            showLiveLineEnd(1);

        } catch (Exception e) {
            LogUtils.i("收到消息一对一视频结束请求消息错误error" + e.getMessage());
        }

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPrivateGiftEvent(EImOnPrivateMessage var1) {

        pushGiftMsg(var1.customMsgPrivateGift);
        LogUtils.i("收到消息发送礼物消息:" + var1.customMsgPrivateGift.getFrom_msg());

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCloseVideoEvent(EImOnCloseVideoLine var1) {

        //后台结束通话警示信息
        DialogHelp.getMessageDialog(this, var1.customMsgCloseVideo.getMsg_content()).show();
        hangUpVideo();

        LogUtils.i("收到后台关闭视频消息:" + var1.customMsgCloseVideo.getMsg_content());
    }

    //赠送礼物
    @Override
    public void onSuccess(JsonRequestDoPrivateSendGif sendGif) {

        final CustomMsgPrivateGift gift = new CustomMsgPrivateGift();
        gift.fillData(sendGif.getSend());
        Message message = new CustomMessage(gift, LiveConstant.CustomMsgType.MSG_PRIVATE_GIFT);
        conversation.sendMessage(message.getMessage(), new TIMValueCallBack<TIMMessage>() {
            @Override
            public void onError(int i, String s) {
                LogUtils.i("一对一视频礼物消息发送失败");
            }

            @Override
            public void onSuccess(TIMMessage timMessage) {

                pushGiftMsg(gift);
                LogUtils.i("一对一视频礼物消息发送SUCCESS");
            }
        });
    }


    //实时获取的受益消费信息
    private void requestGetVideoCallTimeInfo() {

        Api.doRequestGetVideoCallTimeInfo(SaveData.getInstance().getId(), chatData.getChannelName(), new StringCallback() {

            @Override
            public void onSuccess(String s, Call call, Response response) {

                JsonRequestVideoEndInfo data = (JsonRequestVideoEndInfo) JsonRequestBase.getJsonObj(s, JsonRequestVideoEndInfo.class);
                if (StringUtils.toInt(data.getCode()) == 1) {
                    tv_time_info.setText("通话消费:" + data.getVideo_call_total_coin());
                    tv_reward.setText("礼物打赏:" + data.getGift_total_coin());
                    tv_userCoin.setText("对方余额:" + data.getUser_coin());
                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
            }
        });
    }


    /**
     * 关注目标主播
     */
    private void doLoveHer() {
        Api.doLoveTheUser(
                chatData.getUserModel().getId(),
                uId,
                uToken,
                new JsonCallback() {
                    @Override
                    public Context getContextToJson() {
                        return getNowContext();
                    }

                    @Override
                    public void onSuccess(String s, Call call, Response response) {

                        JsonRequest requestObj = JsonRequest.getJsonObj(s);
                        if (requestObj.getCode() == 1) {
                            thisPlayerLoveme.setImageResource(R.drawable.menu_attationed);//隐藏关注按钮
                            showToastMsg("关注成功!");
                        }
                    }
                }
        );
    }

    /**
     * 获取当前视频主播信息
     */
    private void requestUserData() {

        Api.getUserData(
                chatData.getUserModel().getId(),
                uId,
                uToken,
                new JsonCallback() {
                    @Override
                    public Context getContextToJson() {
                        return getNowContext();
                    }

                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        Log.e("getUserData", s);
                        JsonRequestTarget requestObj = JsonRequestTarget.getJsonObj(s);
                        if (requestObj.getCode() == 1) {
                            TargetUserData targetUserData = requestObj.getData();
                            if (ApiUtils.isTrueUrl(targetUserData.getAvatar())) {
                                Utils.loadHttpImg(CuckooVoiceCallActivity.this, Utils.getCompleteImgUrl(targetUserData.getAvatar()), headImage);
                            }
                            nickName.setText(targetUserData.getUser_nickname());
                            thisPlayerLoveme.setImageResource("0".equals(targetUserData.getAttention()) ? R.drawable.menu_no_attantion : R.drawable.menu_attationed);

                            signTv.setText(targetUserData.getSignature() + "");
                            locationTv.setText(targetUserData.getAddress() + "");


                            requestGetVideoCallTimeInfo();
                        } else {
                            showToastMsg("获取当前视频主播信息:" + requestObj.getMsg());
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Log.e("getUserData", e.toString());
                    }
                }
        );
    }


    @Override
    public void onBackPressed() {
        logoutChat();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }


    @Override
    protected void onPause() {
        super.onPause();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (mGiftAnimationContentView != null) {
            mGiftAnimationContentView.stopHandel();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (cuckooVideoLineTimeBusiness != null) {
            cuckooVideoLineTimeBusiness.stop();
        }

        if (getVideoTimeInfoTask != null) {
            getVideoTimeInfoTask.stopRunnable();
        }
        leaveChannel();
        RtcEngine.destroy();
        mRtcEngine = null;

        if (DialogH5Activity.instance != null) {
            DialogH5Activity.instance.finish();
        }

    }

    /**
     * 跳转视频结束页面
     */
    private void showLiveLineEnd(int isFabulous) {
        if (DialogH5Activity.instance != null) {
            DialogH5Activity.instance.finish();
        }
        Intent intent = new Intent(this, VideoLineEndActivity.class);
        intent.putExtra(VideoLineEndActivity.USER_HEAD, chatData.getUserModel().getAvatar());
        intent.putExtra(VideoLineEndActivity.USER_NICKNAME, chatData.getUserModel().getUser_nickname());
        intent.putExtra(VideoLineEndActivity.LIVE_LINE_TIME, videoChatTimer.getText());
        intent.putExtra(VideoLineEndActivity.LIVE_CHANNEL_ID, chatData.getChannelName());
        intent.putExtra(VideoLineEndActivity.IS_CALL_BE_USER, !isNeedCharge);
        intent.putExtra(VideoLineEndActivity.USER_ID, chatData.getUserModel().getId());
        intent.putExtra(VideoLineEndActivity.IS_FABULOUS, isFabulous);
        startActivity(intent);
        finish();
    }

    @Override
    public void onCallbackChargingSuccess() {

    }

    @Override
    public void onCallbackNotBalance() {

        doBalance();
    }

    @Override
    public void onCallbackCallRecordNotFount() {

        showToastMsg("通话记录不存在");
        finishNow();
    }

    /**
     * 退出聊天
     */
    private void logoutChat() {

        DialogHelp.getConfirmDialog(this, "是否挂断音频通话", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                hangUpVideo();
            }
        }).show();

    }

    //余额不足提示
    @Override
    public void onCallbackCallNotMuch(String msg) {
//        DialogHelp.getConfirmDialog(CuckooVoiceCallActivity.this, msg, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                RechargeActivity.startRechargeActivity(CuckooVoiceCallActivity.this);
//            }
//        }).show();
        new CallNoMuchDialog(this, msg).show();
    }

    @Override
    public void onCallbackEndVideo(String msg) {

        showToastMsg(msg);
        cuckooVideoLineTimeBusiness.doHangUpVideo();
    }

    @Override
    public void onHangUpVideoSuccess(int isFabulous) {

        hideLoadingDialog();
        showLiveLineEnd(isFabulous);
    }

    @Override
    public void onFreeTime(long time) {

    }

    @Override
    public void onFreeTimeEnd() {

    }
}
