package com.moxin.videoline.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.moxin.chat.model.CustomMessage;
import com.moxin.chat.model.Message;
import com.moxin.tisdk.AGRender;
import com.moxin.tisdk.VideoPreProcessing;
import com.moxin.videoline.LiveConstant;
import com.moxin.videoline.R;
import com.moxin.videoline.adapter.GiftInfoAdapter;
import com.moxin.videoline.api.Api;
import com.moxin.videoline.base.BaseActivity;
import com.moxin.videoline.business.CuckooCheckRateBusiness;
import com.moxin.videoline.business.CuckooVideoLineTimeBusiness;
import com.moxin.videoline.dialog.GiftBottomDialog;
import com.moxin.videoline.dialog.InputTextDialog;
import com.moxin.videoline.event.EImOnCloseVideoLine;
import com.moxin.videoline.event.EImOnNewMessages;
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
import com.moxin.videoline.modle.UserModel;
import com.moxin.videoline.modle.custommsg.CustomMsg;
import com.moxin.videoline.modle.custommsg.CustomMsgPrivateGift;
import com.moxin.videoline.modle.custommsg.TIMMsgModel;
import com.moxin.videoline.ui.common.Common;
import com.moxin.videoline.utils.BGTimedTaskManage;
import com.moxin.videoline.utils.DialogHelp;
import com.moxin.videoline.utils.StringUtils;
import com.moxin.videoline.utils.Utils;
import com.moxin.videoline.utils.im.IMHelp;
import com.moxin.videoline.widget.CircleTextProgressbar;
import com.moxin.videoline.widget.GiftAnimationContentView;
import com.lzy.okgo.callback.StringCallback;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.tencent.imsdk.TIMConversation;
import com.tencent.imsdk.TIMConversationType;
import com.tencent.imsdk.TIMElem;
import com.tencent.imsdk.TIMElemType;
import com.tencent.imsdk.TIMManager;
import com.tencent.imsdk.TIMMessage;
import com.tencent.imsdk.TIMTextElem;
import com.tencent.imsdk.TIMValueCallBack;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import cn.tillusory.sdk.TiSDKManager;
import cn.tillusory.tiui.TiPanelLayout;
import de.hdodenhof.circleimageview.CircleImageView;
import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import okhttp3.Call;
import okhttp3.Response;

import static io.agora.rtc.Constants.LOG_FILTER_DEBUG;

/**
 * 山东布谷鸟网络科技有限公司
 * 视频通话页面
 */

public class VideoLineActivity extends BaseActivity implements GiftBottomDialog.DoSendGiftListen, CuckooVideoLineTimeBusiness.VideoLineTimeBusinessCallback, InputTextDialog.SendMsgListener {


    public static final String IS_BE_CALL = "IS_BE_CALL";
    public static final String IS_NEED_CHARGE = "IS_NEED_CHARGE";
    public static final String VIDEO_DEDUCTION = "VIDEO_DEDUCTION";
    public static final String CALL_TYPE = "CALL_TYPE";
    public static final String CALL_USER_DATA = "CALL_USER_DATA";
    public static final String VIDEO_PX = "VIDEO_PX";

    //免费时长
    public static final String FREE_TIME = "FREE_TIME";

    //标记
    private static final int PERMISSION_REQ_ID_RECORD_AUDIO = 22;
    private static final int PERMISSION_REQ_ID_CAMERA = PERMISSION_REQ_ID_RECORD_AUDIO + 1;

    //本地视图
    @BindView(R.id.video_chat_small)
    FrameLayout video_chat_small;

    //本地视图bac
    @BindView(R.id.video_chat_small_bac)
    FrameLayout smallVideoViewBac;

    //目标视图
    @BindView(R.id.video_chat_big)
    FrameLayout video_chat_big;

    //目标视图bac
    @BindView(R.id.video_chat_big_bac)
    FrameLayout bigVideoViewBac;

    //关闭按钮
    @BindView(R.id.close_video_chat)
    ImageView closeVideo;

    //切换摄像头
    @BindView(R.id.videochat_switch)
    ImageView cutCamera;

    //是否关闭声音
    @BindView(R.id.videochat_voice)
    ImageView isSoundOut;

    //礼物按钮
    @BindView(R.id.videochat_gift)
    ImageView videoGift;

    //动画布局
    @BindView(R.id.ll_gift_content)
    GiftAnimationContentView mGiftAnimationContentView;

    //收费金额
    @BindView(R.id.videochat_unit_price)
    TextView tv_chat_unit_price;

    //通话计时时长
    @BindView(R.id.videochat_timer)
    Chronometer videoChatTimer;

    //用户信息
    @BindView(R.id.this_player_img)
    CircleImageView headImage;

    //关注数
    @BindView(R.id.this_player_number)
    TextView thisPlayerNumber;

    //昵称
    @BindView(R.id.this_player_name)
    TextView nickName;

    //关注按钮
    @BindView(R.id.this_player_loveme)
    TextView tv_follow;

    @BindView(R.id.tv_time_info)
    TextView tv_time_info;

    @BindView(R.id.tv_reward)
    TextView tv_reward;

    @BindView(R.id.user_coin)
    TextView tv_userCoin;

    @BindView(R.id.lv_live_room)
    RecyclerView giftInfoRv;

    @BindView(R.id.root_view)
    RelativeLayout root_view;

    @BindView(R.id.progress_bar_time)
    CircleTextProgressbar progress_bar_time;

    @BindView(R.id.iv_open_beauty)
    ImageView iv_open_beauty;

    private ImageView iv_close_camera;
    private ImageView iv_video_chat_lucky_corn;

    //创建 RtcEngine 对象
    private RtcEngine mRtcEngine;// Tutorial Step 1

    //是否需要扣费
    private boolean isNeedCharge = false;

    private VideoCanvas smallVideoCanvas;
    private VideoCanvas bigVideoCanvas;

    private GiftBottomDialog giftBottomDialog;

    private BGTimedTaskManage getVideoTimeInfoTask;
    private CuckooVideoLineTimeBusiness cuckooVideoLineTimeBusiness;

    private List<String> guardInfoList = new ArrayList<>();

    private GiftInfoAdapter giftInfoAdapter;
    private CuckooCheckRateBusiness cuckooCheckRateBusiness;

    private int videoUid;
    private int videoViewStatus = 1;
    private TIMConversation conversation;

    //分钟扣费金额
    private String videoDeduction;
    private String video_px;
    private int callType;


    private UserChatData chatData;
    private boolean isOpenCamera = true;

    // --- boGoBeauty start ---
    private TiSDKManager tiSDKManager;
    private VideoPreProcessing mVideoPreProcessing;
    private InputTextDialog inputTextDialog;

    // --- boGoBeauty end ---


    @Override
    protected Context getNowContext() {
        return VideoLineActivity.this;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_video_chat;
    }

    @BindView(R.id.top_holder)
    View top;

    @BindView(R.id.tl_view)
    TiPanelLayout tl_view;

    @Override
    protected void initView() {
        chatData = getIntent().getParcelableExtra(CALL_USER_DATA);
        video_px = getIntent().getStringExtra(VIDEO_PX);

        //禁止截屏
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        QMUIStatusBarHelper.translucent(this);
        Utils.initTransP(top);

        iv_close_camera = findViewById(R.id.iv_close_camera);
        iv_video_chat_lucky_corn = findViewById(R.id.iv_video_chat_lucky_corn);

        video_chat_small.setOnClickListener(this);
        video_chat_big.setOnClickListener(this);
        iv_video_chat_lucky_corn.setOnClickListener(this);
        iv_open_beauty.setOnClickListener(this);

        // 改变进度条颜色。
        progress_bar_time.setProgressColor(getResources().getColor(R.color.admin_color));
        // 改变外部边框颜色。
        progress_bar_time.setOutLineColor(getResources().getColor(R.color.transparent));
        // 改变圆心颜色。
        //progress_bar_time.setInCircleColor(Color.RED);

        mGiftAnimationContentView.startHandel();

        //通话类型
        callType = getIntent().getIntExtra(CALL_TYPE, 0);
        //分钟扣费金额
        videoDeduction = getIntent().getStringExtra(VIDEO_DEDUCTION);
        tv_chat_unit_price.setText(String.format(Locale.getDefault(), "%s%s/分钟", videoDeduction, RequestConfig.getConfigObj().getCurrency()));

        videoChatTimer.setTextColor(getResources().getColor(R.color.white));
        String msgAlert = ConfigModel.getInitData().getVideo_call_msg_alert();
        if (!TextUtils.isEmpty(msgAlert)) {
            ToastUtils.showLong(msgAlert);
        }

        tv_time_info.setText(String.format(Locale.CHINA, "通话消费:%s", videoDeduction));
        tv_reward.setText("礼物打赏:");
        tv_userCoin.setText("对方余额:");


        //礼物信息
        giftInfoRv.setLayoutManager(new LinearLayoutManager(this));
        giftInfoAdapter = new GiftInfoAdapter(guardInfoList, this);
        giftInfoRv.setAdapter(giftInfoAdapter);
    }

    @Override
    protected void initData() {


        //免费观看时长（s）
        int freeTime = getIntent().getIntExtra(FREE_TIME, 0);

        //isBeCall = getIntent().getBooleanExtra(IS_BE_CALL, false);
        isNeedCharge = getIntent().getBooleanExtra(IS_NEED_CHARGE, false);

        // 设置倒计时时间毫秒，默认3000毫秒。
        progress_bar_time.setTimeMillis(freeTime * 1000);
        cuckooVideoLineTimeBusiness = new CuckooVideoLineTimeBusiness(this, isNeedCharge, freeTime, chatData.getUserModel().getId(), this);

        if (freeTime == 0) {
            progress_bar_time.setVisibility(View.GONE);
        } else {
            progress_bar_time.start();
        }

        if (isNeedCharge) {
            initBeCallView();
            initBeCallAction();
        } else {
            initCallView();
            initCallAction();
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
        setOnclickListener(isSoundOut, closeVideo, cutCamera, videoGift, headImage, tv_follow, iv_close_camera);
        //初始化本地操作
        if (callType == 0) {
            initAgoraEngineAndJoinChannel();
        } else {
            initAgoraVoiceEngineAndJoinChannel();
            iv_close_camera.setVisibility(View.GONE);
        }
        //加入频道
        joinChannel();
    }


    /**
     * 初始化设置视图
     */
    private void initAgoraEngineAndJoinChannel() {
        //初始化RtcEngine对象
        try {
            mRtcEngine = RtcEngine.create(getBaseContext(), ConfigModel.getInitData().getApp_qgorq_key(), mRtcEventHandler);

            // 将日志过滤器等级设置为 LOG_FILTER_DEBUG
            mRtcEngine.setLogFilter(LOG_FILTER_DEBUG);

            // 获取在 SD 卡中的文件路径
            // 获取时间戳
//            String ts = new SimpleDateFormat("yyyyMMdd").format(new Date());
//            String filepath = "/sdcard/" + ts + ".log";
//            File file = new File(filepath);
//            mRtcEngine.setLogFile(filepath);

        } catch (Exception e) {
            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }

        //初始化设置信息
        mRtcEngine.enableVideo();
        //打开视频模式
        mRtcEngine.enableAudio();

        if (StringUtils.toInt(video_px) == 0) {
            mRtcEngine.setVideoProfile(Constants.VIDEO_PROFILE_480P, false);//设置视频分辨率
        } else {
            mRtcEngine.setVideoProfile(Constants.VIDEO_PROFILE_720P, false);//设置视频分辨率
        }

        //创建视频渲染视图, 设置本地视频视图##初始化本地视图
        SurfaceView surfaceView = RtcEngine.CreateRendererView(getBaseContext());
        surfaceView.setZOrderMediaOverlay(true);
        video_chat_small.addView(surfaceView);

        smallVideoCanvas = new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_HIDDEN, 0);
        mRtcEngine.setupLocalVideo(smallVideoCanvas);
        mRtcEngine.startPreview();

        if (!TextUtils.isEmpty(ConfigModel.getInitData().getBogokj_beauty_sdk_key())) {
            //TODO 美颜开关按钮
//            iv_open_beauty.setVisibility(View.VISIBLE);
            initBoGoBeautySdk();
        }
    }

    //初始化布谷科技美颜SDK
    private void initBoGoBeautySdk() {
        // --- boGoBeauty start ---
        tiSDKManager = TiSDKManager.getInstance();
        tl_view.init(tiSDKManager);
        addContentView(
                new TiPanelLayout(this).init(tiSDKManager),
                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        AGRender.init(tiSDKManager);

        //if (mVideoPreProcessing == null) {
        //    mVideoPreProcessing = new VideoPreProcessing();
        //}
        //mVideoPreProcessing.enablePreProcessing(true);
        // --- boGoBeauty end ---
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


    /**
     * 主播业务
     */
    private void initCallAction() {
        //鉴黄业务逻辑
//        cuckooCheckRateBusiness = new CuckooCheckRateBusiness(this);
//        if (ConfigModel.getInitData().getIs_open_check_huang() == 1) {
//            cuckooCheckRateBusiness.setTime(ConfigModel.getInitData().getCheck_huang_rate());
//            cuckooCheckRateBusiness.startCheck();
//        }
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
        tv_chat_unit_price.setVisibility(View.GONE);

        //礼物和打赏收入
        tv_time_info.setVisibility(View.VISIBLE);
        tv_reward.setVisibility(View.VISIBLE);
        tv_userCoin.setVisibility(View.VISIBLE);
    }

    /**
     * 被呼叫视频通话用户View初始化
     */
    private void initBeCallView() {

        tv_chat_unit_price.setVisibility(View.GONE);

        //礼物和打赏收入
        tv_time_info.setVisibility(View.GONE);
        tv_reward.setVisibility(View.GONE);
        tv_userCoin.setVisibility(View.GONE);
    }

    @Override
    protected void initPlayerDisplayData() {

    }

    /////////////////////////////////////////////监听事件处理/////////////////////////////////////////
    @OnClick({R.id.say_rl})
    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.iv_open_beauty:
                tl_view.open();
                break;
            case R.id.videochat_gift:
                clickOpenGiftDialog();
                break;
            case R.id.videochat_screen:
                //截屏
                break;
            case R.id.this_player_loveme:
                doLoveHer();
                break;
            case R.id.close_video_chat:
                logoutChat();
                break;
            case R.id.videochat_switch:
                doCutCamera();
                break;
            case R.id.videochat_voice:
                onLocalAudioMuteClicked();
                break;
            case R.id.this_player_img:
                Common.jumpUserPage(VideoLineActivity.this, chatData.getUserModel().getId());
                break;
            case R.id.video_chat_big:
                //switchVideoView();
                break;
            case R.id.video_chat_small:
                switchVideoView();
                break;
            case R.id.iv_close_camera:
                closeCamera();
                break;
            case R.id.iv_video_chat_lucky_corn:
                Intent intent = new Intent(this, DialogH5Activity.class);
                intent.putExtra("uri", ConfigModel.getInitData().getApp_h5().getTurntable_url());
                startActivity(intent);
                break;

            //发言框
            case R.id.say_rl:
                changeEditStatus(true);
                break;


            default:
                break;
        }
    }


    //显示输入框
    protected void changeEditStatus(boolean status) {
        if (status) {
            inputTextDialog = new InputTextDialog(this);
            inputTextDialog.show();
            inputTextDialog.setSendMsgListener(this);
//
        } else {

            if (inputTextDialog.isShowing()) {
                inputTextDialog.dismiss();
            }
        }

    }

    /**
     * 创建视频渲染视图, 设置远端视频视图
     *
     * @param uid 用户uid
     */
    private void setupRemoteVideo(int uid) {
        //创建视频渲染视图, 设置远端视频视图
        if (video_chat_big.getChildCount() >= 1) {
            return;
        }
        SurfaceView surfaceView = RtcEngine.CreateRendererView(getBaseContext());
        surfaceView.setTag(uid);
        video_chat_big.addView(surfaceView);
        videoUid = uid;

        bigVideoCanvas = new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_HIDDEN, uid);
        mRtcEngine.setupRemoteVideo(bigVideoCanvas);
    }

    //移除目标组件所有视图文件
    private void onRemoteUserLeft() {
        video_chat_big.removeAllViews();
        //hangUpVideo();
    }

    //初始化目标视图
    private void onRemoteUserVideoMuted(int uid, boolean muted) {
        SurfaceView surfaceView = (SurfaceView) video_chat_big.getChildAt(0);
        if (surfaceView == null) {
            return;
        }
        Object tag = surfaceView.getTag();
        if (tag != null && (Integer) tag == uid) {
            surfaceView.setVisibility(muted ? View.GONE : View.VISIBLE);
        }
    }


    //开关摄像头
    private void closeCamera() {
        if (isOpenCamera) {
            //关闭摄像头
            mRtcEngine.enableLocalVideo(false);

            if (videoViewStatus == 1) {
                smallVideoViewBac.setVisibility(View.VISIBLE);
                bigVideoViewBac.setVisibility(View.GONE);
            } else {
                smallVideoViewBac.setVisibility(View.GONE);
                bigVideoViewBac.setVisibility(View.VISIBLE);
            }

            iv_close_camera.setImageResource(R.mipmap.ic_close_camera);
            ToastUtils.showLong("摄像头已关闭");
        } else {
            mRtcEngine.enableLocalVideo(true);
            video_chat_small.setVisibility(View.VISIBLE);
            smallVideoViewBac.setVisibility(View.GONE);
            bigVideoViewBac.setVisibility(View.GONE);
            iv_close_camera.setImageResource(R.mipmap.ic_open_camera);
            ToastUtils.showLong("摄像头已打开");
        }
        isOpenCamera = !isOpenCamera;
    }

    //切换视图
    private void switchVideoView() {

        video_chat_small.removeAllViews();
        video_chat_big.removeAllViews();
        mRtcEngine.setupLocalVideo(null);
        mRtcEngine.setupRemoteVideo(null);
        video_chat_big.setVisibility(View.VISIBLE);

        if (videoViewStatus == 1) {

            //ToastUtils.showLong("1");
            //surfaceView.setZOrderMediaOverlay(true);
            //创建视频渲染视图, 设置本地视频视图##初始化本地视图
            SurfaceView surfaceView = RtcEngine.CreateRendererView(getBaseContext());
            surfaceView.setTag(videoUid);
            video_chat_small.addView(surfaceView);
            surfaceView.setZOrderMediaOverlay(true);

            //网络放小窗
            smallVideoCanvas = new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_HIDDEN, videoUid);
            mRtcEngine.setupRemoteVideo(smallVideoCanvas);


            SurfaceView surfaceView2 = RtcEngine.CreateRendererView(getBaseContext());
            video_chat_big.addView(surfaceView2);

            //本地放大窗
            bigVideoCanvas = new VideoCanvas(surfaceView2, VideoCanvas.RENDER_MODE_HIDDEN, 0);
            mRtcEngine.setupLocalVideo(bigVideoCanvas);

            videoViewStatus = 2;

        } else {

            //ToastUtils.showLong("2");
            //创建视频渲染视图, 设置本地视频视图##初始化本地视图
            SurfaceView surfaceView = RtcEngine.CreateRendererView(getBaseContext());
            video_chat_small.addView(surfaceView);
            surfaceView.setZOrderMediaOverlay(true);

            //本地放小窗
            smallVideoCanvas = new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_HIDDEN, 0);
            mRtcEngine.setupLocalVideo(smallVideoCanvas);

            SurfaceView surfaceView2 = RtcEngine.CreateRendererView(getBaseContext());
            surfaceView2.setTag(videoUid);
            video_chat_big.addView(surfaceView2);

            //网络放大窗
            bigVideoCanvas = new VideoCanvas(surfaceView2, VideoCanvas.RENDER_MODE_HIDDEN, videoUid);
            mRtcEngine.setupRemoteVideo(bigVideoCanvas);

            videoViewStatus = 1;
        }

        if (!isOpenCamera) {
            if (videoViewStatus == 1) {
                smallVideoViewBac.setVisibility(View.VISIBLE);
                bigVideoViewBac.setVisibility(View.GONE);
            } else {
                smallVideoViewBac.setVisibility(View.GONE);
                bigVideoViewBac.setVisibility(View.VISIBLE);
            }
        }

    }

    //发起会话通道名额外的可选的数据--uid(uid为空时自动赋予)
    private void joinChannel() {
        mRtcEngine.joinChannel(null, chatData.getChannelName(), null, 0);
        // --- boGoBeauty start ---
        if(mVideoPreProcessing != null){
//            mVideoPreProcessing.enablePreProcessing(true);
        }
        // --- boGoBeauty end ---
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

    /**
     * 执行切换前后相机
     */
    private void doCutCamera() {
        mRtcEngine.switchCamera();
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

        if (!isNeedCharge) {
            giftBottomDialog.hideMenu();
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

            //用户存储本地展示
            if (!isNeedCharge) {
                String from_msg = giftCustom.getTo_msg();
                guardInfoList.add("系统消息:" + from_msg);
            } else {
                String from_msg = giftCustom.getFrom_msg();
                guardInfoList.add("系统消息:" + from_msg);
            }


            //展示信息
            giftInfoAdapter.setData(guardInfoList);
            giftInfoAdapter.notifyDataSetChanged();
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
                    setupRemoteVideo(uid);
                }
            });
        }

        @Override
        public void onUserOffline(final int uid, int reason) {
            //用户不在线
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (uid != 1) {
                        onRemoteUserLeft();
                    }
                }
            });
        }

        @Override
        public void onUserMuteVideo(final int uid, final boolean muted) {
            // 在用户静音视频
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onRemoteUserVideoMuted(uid, muted);
                }
            });
        }


        @Override
        public void onUserEnableVideo(int uid, final boolean enabled) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (enabled) {
                        LogUtils.e("agora:" + "onUserEnableVideo" + enabled);
                        video_chat_big.setVisibility(View.VISIBLE);
                    } else {
                        LogUtils.e("agora:" + "onUserEnableVideo" + enabled);
                        video_chat_big.setVisibility(View.GONE);
                    }
                }
            });
        }

        @Override
        public void onUserEnableLocalVideo(int uid, final boolean enabled) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (enabled) {
                        LogUtils.e("agora:" + "onUserEnableLocalVideo" + enabled);
                        video_chat_big.setVisibility(View.VISIBLE);
                    } else {
                        LogUtils.e("agora:" + "onUserEnableLocalVideo" + enabled);
                        video_chat_big.setVisibility(View.GONE);
                    }
                }
            });
        }
    };

    /**
     * 退出聊天
     */
    private void logoutChat() {

        DialogHelp.getConfirmDialog(this, getString(R.string.is_huang_call), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                hangUpVideo();
            }
        }).show();

    }

    /**
     * 余额不足操作
     */
    private void doBalance() {
        hangUpVideo();
        ToastUtils.showShort(R.string.money_insufficient);
    }

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
                            concealView(tv_follow);//隐藏关注按钮
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

                        JsonRequestTarget requestObj = JsonRequestTarget.getJsonObj(s);
                        if (requestObj.getCode() == 1) {

                            TargetUserData targetUserData = requestObj.getData();
                            Utils.loadHttpImg(VideoLineActivity.this, Utils.getCompleteImgUrl(targetUserData.getAvatar()), headImage);
                            nickName.setText(targetUserData.getUser_nickname());
                            thisPlayerNumber.setText(String.format(Locale.CHINA, "关注：%s", targetUserData.getAttention_all()));
                            tv_follow.setVisibility(StringUtils.toInt(targetUserData.getAttention()) == 0 ? View.VISIBLE : View.GONE);

                            requestGetVideoCallTimeInfo();
                        } else {
                            showToastMsg("获取当前视频主播信息:" + requestObj.getMsg());
                        }
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

//        if (cuckooCheckRateBusiness != null) {
//            cuckooCheckRateBusiness.stopCheck();
//        }

        if (tiSDKManager != null) {
            // --- boGoBeauty start ---
//            mVideoPreProcessing.enablePreProcessing(false);
            tiSDKManager.destroy();
            // --- boGoBeauty end ---
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

    @Override
    public void onCallbackCallNotMuch(String msg) {
        DialogHelp.getConfirmDialog(VideoLineActivity.this, msg, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                RechargeActivity.startRechargeActivity(VideoLineActivity.this);
            }
        }).show();
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
        progress_bar_time.setText(String.valueOf(time));
    }

    @Override
    public void onFreeTimeEnd() {

        if (!isFinishing()) {
            progress_bar_time.setVisibility(View.GONE);

            DialogHelp.getConfirmDialog(this, "免费试看结束，是否进行付费视频，3秒后自动退出视频", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    cuckooVideoLineTimeBusiness.charging();
                }
            }).show();

            //倒计时结束通话
            new CountDownTimer(3 * 1000, 1000) {

                @Override
                public void onTick(long l) {

                }

                @Override
                public void onFinish() {
                    //结束视频通话
                    if (cuckooVideoLineTimeBusiness != null && !cuckooVideoLineTimeBusiness.isCharge()) {
                        hangUpVideo();
                    }
                }
            }.start();
        }
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

                //用户存储本地展示
                LogUtils.i("一对一视频礼物消息发送SUCCESS");
            }
        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewMsgEventThread(EImOnNewMessages var1) {

        TIMMsgModel msgModel = (TIMMsgModel) var1.msg;
        TIMMessage timMessage = msgModel.getTimMessage();

        CustomMsg realCustomMsg = msgModel.parseToModel(CustomMsg.class);

        //收到消息
        List<TIMElem> elems = new ArrayList<>();
        boolean hasText = false;
        if (timMessage != null) {
            for (int i = 0; i < timMessage.getElementCount(); ++i) {
                elems.add(timMessage.getElement(i));
                if (timMessage.getElement(i).getType() == TIMElemType.Text) {
                    hasText = true;
                } else {
                    hasText = false;
                }
            }

            SpannableStringBuilder stringBuilder = getString(elems, this);
            if (!hasText) {
                stringBuilder.insert(0, " ");
            }

            UserModel sender = realCustomMsg.getSender();
            String user_nickname = sender.getUser_nickname();

//            String user_nickname = "";
//            if (timMessage.getSender().equals(chatData.getUserModel().getId())) {
//                user_nickname = chatData.getUserModel().getUser_nickname();
//            } else if (timMessage.getSender().equals(SaveData.getInstance().getId())) {
//                user_nickname = SaveData.getInstance().userModel.getUser_nickname();
//            }
            if (mGiftAnimationContentView != null) {
                if (!TextUtils.isEmpty(stringBuilder.toString().trim())) {
                    guardInfoList.add(user_nickname + ":" + stringBuilder.toString());
                    //展示信息
                    giftInfoAdapter.setData(guardInfoList);
                }

                giftInfoAdapter.notifyDataSetChanged();


            } else {
                Log.e("giveMeMsg", "mGiftAnimationContentView = null");
            }


        }
    }

    public static SpannableStringBuilder getString(List<TIMElem> elems, Context context) {
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
        for (int i = 0; i < elems.size(); ++i) {
            switch (elems.get(i).getType()) {

                case Text:
                    TIMTextElem textElem = (TIMTextElem) elems.get(i);
                    Log.e("onSendMsgListener", textElem.getText());
                    stringBuilder.append(textElem.getText());
                    break;
            }

        }
        return stringBuilder;
    }


    /**
     * 发送消息
     *
     * @param trim
     */
    @Override
    public void onSendMsgListener(String trim) {
        Log.e("onSendMsgListener", trim);
        IMHelp.sendTextCallMsg(trim, chatData.getUserModel().getId(), TIMConversationType.C2C, new TIMValueCallBack<TIMMessage>() {
            @Override
            public void onError(int i, String s) {

            }

            @Override
            public void onSuccess(TIMMessage timMessage) {
                TIMMsgModel timMsgModel = new TIMMsgModel(timMessage);
                EImOnNewMessages event = new EImOnNewMessages();
                event.msg = timMsgModel;
                EventBus.getDefault().post(event);
            }
        });

        changeEditStatus(false);

    }
}
