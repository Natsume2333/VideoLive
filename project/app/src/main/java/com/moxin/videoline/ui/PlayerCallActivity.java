package com.moxin.videoline.ui;

import android.content.Context;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.moxin.videoline.R;
import com.moxin.videoline.api.Api;
import com.moxin.videoline.base.BaseActivity;
import com.moxin.videoline.event.EImVideoCallReplyMessages;
import com.moxin.videoline.json.JsonRequestBase;
import com.moxin.videoline.json.JsonRequestCheckIsCharging;
import com.moxin.videoline.json.JsonRequestHangUpVideoCall;
import com.moxin.videoline.modle.UserChatData;
import com.moxin.videoline.modle.UserModel;
import com.moxin.videoline.modle.custommsg.CustomMsg;
import com.moxin.videoline.modle.custommsg.CustomMsgVideoCallReply;
import com.moxin.videoline.utils.StringUtils;
import com.moxin.videoline.utils.UIHelp;
import com.moxin.videoline.utils.Utils;
import com.moxin.videoline.utils.im.IMHelp;
import com.lzy.okgo.callback.StringCallback;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMValueCallBack;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 呼叫页面
 */
public class PlayerCallActivity extends BaseActivity {

    @BindView(R.id.iv_bg)
    ImageView iv_bg;
    //数据
    private CircleImageView callPlayerImg;//player头像信息  !
    private TextView callPlayerName;//标题
    private TextView callPlayerMsg;//msg

    //拨打电话用户信息
    private UserModel callUserInfo;
    private String channelId = "";//通话频道ID

    public static final String CALL_USER_INFO = "CALL_USER_INFO";
    public static final String CALL_CHANNEL_ID = "CALL_CHANNEL_ID";
    public static final String CALL_TYPE = "CALL_TYPE";

    private MediaPlayer mediaPlayer;


    @Override
    protected Context getNowContext() {
        return PlayerCallActivity.this;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_call_player;
    }

    @Override
    protected void initView() {
        QMUIStatusBarHelper.translucent(this);
        callPlayerImg = findViewById(R.id.call_player_img);
        callPlayerName = findViewById(R.id.call_player_name);
        callPlayerMsg = findViewById(R.id.call_player_msg);
    }

    @Override
    protected void initSet() {
        setOnclickListener(R.id.repulse_call_btn, R.id.accept_call_btn);
    }

    @Override
    protected void initData() {
        callUserInfo = getIntent().getParcelableExtra(CALL_USER_INFO);
        channelId = getIntent().getStringExtra(CALL_CHANNEL_ID);

        if (callUserInfo == null) {
            //showToastMsg("信息错误");
            LogUtils.i("信息错误");
            finish();
            return;
        }

        Utils.loadHttpImgBlue(this, callUserInfo.getAvatar(), iv_bg, 0);

        if (channelId == null || TextUtils.isEmpty(channelId)) {
            //showToastMsg("通话频道ID错误");
            LogUtils.i("通话频道ID错误");
            finish();
        }

        mediaPlayer = MediaPlayer.create(this, R.raw.call);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if (mediaPlayer != null && !isFinishing()) {
                    mediaPlayer.start();
                }
            }
        });
        mediaPlayer.start();
    }

    @Override
    protected void initPlayerDisplayData() {
        callPlayerMsg.setText(R.string.call_player_msg_to);
        if (callUserInfo.getUser_nickname() != null) {
            callPlayerName.setText(callUserInfo.getUser_nickname());
        }

        //头像
        if (callUserInfo.getAvatar() != null) {
            Utils.loadHttpImg(this, Utils.getCompleteImgUrl(callUserInfo.getAvatar()), callPlayerImg);
        }
    }

    /////////////////////////////////////////////监听事件处理/////////////////////////////////////////
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.repulse_call_btn:
                doRepulseCall();
                break;
            case R.id.accept_call_btn:
                doAcceptCall();
                break;

            default:
                break;
        }
    }

    /////////////////////////////////////////////业务逻辑处理/////////////////////////////////////////

    /**
     * 接通
     */
    private void doAcceptCall() {
        showToastMsg(getString(R.string.have_been_connected));
    }

    /**
     * 挂断
     */
    private void doRepulseCall() {
        showToastMsg(getString(R.string.hang_up));

        showLoadingDialog(getString(R.string.now_hang_up));

        Api.doHangUpVideoCall(uId, uToken, new StringCallback() {

            @Override
            public void onSuccess(String s, Call call, Response response) {
                hideLoadingDialog();
                JsonRequestHangUpVideoCall jsonObj = JsonRequestHangUpVideoCall.getJsonObj(s);
                if (jsonObj.getCode() == 1) {
                    IMHelp.sendReplyVideoCallMsg(channelId, "2", callUserInfo.getId(), new TIMValueCallBack<TIMMessage>() {
                        @Override
                        public void onError(int i, String s) {
                            LogUtils.i("取消电话腾讯云回调ERROR：" + s);
                        }

                        @Override
                        public void onSuccess(TIMMessage timMessage) {

                        }
                    });
                    finish();
                } else {
                    showToastMsg(jsonObj.getMsg());
                }

            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                hideLoadingDialog();
            }
        });
    }



    //跳转视频通话页面
    private void jumpVideoCallActivity() {

        Api.doCheckIsNeedCharging(uId, callUserInfo.getId(), new StringCallback() {

            @Override
            public void onSuccess(String s, Call call, Response response) {

                JsonRequestCheckIsCharging jsonObj = (JsonRequestCheckIsCharging) JsonRequestBase.getJsonObj(s, JsonRequestCheckIsCharging.class);
                if (jsonObj.getCode() == 1) {

                    if (channelId != null && !TextUtils.isEmpty(channelId)) {
                        //组装拨打电话信息跳转通话页面
                        UserChatData userChatData = new UserChatData();
                        userChatData.setChannelName(channelId);
                        userChatData.setUserModel(callUserInfo);

                        int type = getIntent().getIntExtra(CALL_TYPE, 0);

                        UIHelp.startVideoLineActivity(PlayerCallActivity.this, type, jsonObj.getResolving_power(), jsonObj.getIs_need_charging(),
                                jsonObj.getVideo_deduction(), jsonObj.getFree_time(), userChatData);
                        finish();
                    }
                } else {
                    showToastMsg(jsonObj.getMsg());
                }
            }
        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventVideoCallThread(EImVideoCallReplyMessages var1) {

        LogUtils.i("收到消息一对一视频回复消息:" + var1.msg.getCustomMsg().getSender().getUser_nickname());

        try {
            CustomMsg customMsg = var1.msg.getCustomMsg();
            CustomMsgVideoCallReply customMsgVideoCallReply = ((CustomMsgVideoCallReply) customMsg);
            //挂断通话关闭弹窗
            if (StringUtils.toInt(customMsgVideoCallReply.getReply_type()) == 2) {
                showToastMsg(getString(R.string.user_refuse_call));
                finish();
            } else {
                //接通跳转视频通话页面
                jumpVideoCallActivity();
            }
        } catch (Exception e) {
            LogUtils.i("跳转接通电话页面错误error" + e.getMessage());
        }

    }

    @Override
    public void onBackPressed() {
        doRepulseCall();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayer.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
