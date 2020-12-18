package com.moxin.videoline.fragment;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.moxin.videoline.ApiConstantDefine;
import com.moxin.videoline.R;
import com.moxin.videoline.api.Api;
import com.moxin.videoline.api.ApiUtils;
import com.moxin.videoline.base.BaseFragment;
import com.moxin.videoline.dialog.GiftBottomDialog;
import com.moxin.videoline.dialog.ShowPayVideoDialog;
import com.moxin.videoline.event.CuckooBuyVideoCommonEvent;
import com.moxin.videoline.event.CuckooOnTouchShortVideoChangeEvent;
import com.moxin.videoline.json.JsonRequestBase;
import com.moxin.videoline.json.JsonRequestDoGetVideo;
import com.moxin.videoline.json.JsonRequestDoPrivateSendGif;
import com.moxin.videoline.json.jsonmodle.VideoModel;
import com.moxin.videoline.manage.AppConfig;
import com.moxin.videoline.manage.RequestConfig;
import com.moxin.videoline.manage.SaveData;
import com.moxin.videoline.modle.GiftAnimationModel;
import com.moxin.videoline.modle.IsBindPhoneBean;
import com.moxin.videoline.modle.custommsg.CustomMsgPrivateGift;
import com.moxin.videoline.ui.CuckooVideoTouchPlayerActivity;
import com.moxin.videoline.ui.common.Common;
import com.moxin.videoline.ui.dialog.InviteShareDialog;
import com.moxin.videoline.utils.StringUtils;
import com.moxin.videoline.utils.Utils;
import com.moxin.videoline.widget.GiftAnimationContentView;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.lzy.okgo.callback.StringCallback;
import com.tencent.rtmp.ITXLivePlayListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePlayConfig;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 小视频播放
 */
public class CuckooVideoPlayerFragment extends BaseFragment implements GiftBottomDialog.DoSendGiftListen, ITXLivePlayListener {

    @BindView(R.id.iv_video_pause)
    ImageView ivVideoPause;


    private int MY_PERMISSIONS_REQUEST = 1001;
    //功能
    private TXCloudVideoView mTXCloudVideoView;
    private ImageView toLoadVideo;//加载...
    private ImageView lovePlayerImg;//是否喜欢当前视频图像
    //数据
    private CircleImageView thisPlayerImg;//当前player头像--点击进入player主页
    private TextView thisPlayerName;//当前player的昵称
    private TextView sharePlayerNumber; //分享人数
    private TextView lovePlayerNumber; //喜欢、关注人数
    private TextView playerVideoTitle;//当前视频title
    //按钮
    private ImageView thisPlayerLoveme;//关注这个player
    private GiftBottomDialog giftBottomDialog;

    @BindView(R.id.tv_look_count)
    TextView tv_look_count;


    @BindView(R.id.video_coin_price_tv)
    TextView videoPriceTv;

    @BindView(R.id.ll_gift_content)
    GiftAnimationContentView ll_gift_content;

    @BindView(R.id.iv_gift)
    ImageView giftIv;
    //else
    private VideoModel videoData;
    //是否关注
    private int isFollow = 0;

    private boolean mVideoIsPlay;
    private TXLivePlayer mTXLivePlayer = null;
    private TXLivePlayConfig mTXPlayConfig = null;


    public static final String IS_FOLLOW = "IS_FOLLOW";
    public static final String VIDEO_DATA = "VIDEO_DATA";
    private View view;
    private boolean isFollowVideo;
    @BindView(R.id.holder)
    ImageView holder;

    @Override
    protected View getBaseView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_cuckoo_video_player, container, false);
    }

    @Override
    protected void initView(View view) {
        this.view = view;
        mTXCloudVideoView = view.findViewById(R.id.video_play_video);
        mTXCloudVideoView.setOnClickListener(this);
        toLoadVideo = view.findViewById(R.id.toload_video);
        lovePlayerImg = view.findViewById(R.id.love_player_img);
        thisPlayerImg = view.findViewById(R.id.this_player_img);
        thisPlayerName = view.findViewById(R.id.this_player_name);
        sharePlayerNumber = view.findViewById(R.id.share_player_number);
        lovePlayerNumber = view.findViewById(R.id.love_player_number);
        playerVideoTitle = view.findViewById(R.id.player_video_title);
        thisPlayerLoveme = view.findViewById(R.id.this_player_loveme);

        view.findViewById(R.id.call_player_btn).setOnClickListener(this);
        thisPlayerImg.setOnClickListener(this);

        ll_gift_content.startHandel();

        int sex = SaveData.getInstance().getUserInfo().getSex();
        if (sex == 2) {
            giftIv.setVisibility(View.GONE);
        }
    }

    @Override
    protected void initDate(View view) {

        isFollow = StringUtils.toInt(getArguments().getString(IS_FOLLOW));
        videoData = getArguments().getParcelable(VIDEO_DATA);

        sharePlayerNumber.setText(videoData.getShare());
        playerVideoTitle.setText(videoData.getTitle());
        tv_look_count.setText(videoData.getViewed());
        thisPlayerLoveme.setVisibility("0".equals(videoData.getAttention()) ? View.VISIBLE : View.GONE);

        toLoadVideo.setVisibility(View.VISIBLE);
        requestUserByVideo();//获取视频的用户信息
        Glide.with(this).load(videoData.getImg()).into(holder);
        holder.setVisibility(View.VISIBLE);

    }

    @Override
    protected void initSet(View view) {

        initLive();
    }

    @Override
    protected void initDisplayData(View view) {

    }

    private void initLive() {

        mTXLivePlayer = new TXLivePlayer(getContext());
        mTXPlayConfig = new TXLivePlayConfig();

        mTXLivePlayer.setPlayerView(mTXCloudVideoView);
        mTXLivePlayer.setPlayListener(this);
        mTXLivePlayer.enableHardwareDecode(false);
        mTXLivePlayer.setRenderRotation(TXLiveConstants.RENDER_ROTATION_PORTRAIT);
        mTXLivePlayer.setRenderMode(TXLiveConstants.RENDER_ROTATION_PORTRAIT);//RENDER_MODE_ADJUST_RESOLUTION
        mTXLivePlayer.setConfig(mTXPlayConfig);

    }

    @OnClick({R.id.video_play_close, R.id.ll_share, R.id.iv_gift, R.id.love_player_img, R.id.iv_video_pause})
    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.love_player_img:
                clickLoveVideo();
                break;
            //点击进入player主页
            case R.id.this_player_img:
                toThisPlayer();
                break;
            case R.id.video_play_close:
                getActivity().finish();
                break;
            //分享
            case R.id.ll_share:
                showSimpleBottomSheetGrid();
                break;
            case R.id.iv_gift:
                clickOpenGiftDialog();
                break;

            //与当前player1v1视频
            case R.id.call_player_btn:
                callPlayer();
                break;

            case R.id.iv_video_pause:
                if (ivVideoPause.getVisibility() == View.VISIBLE) {
                    ivVideoPause.setVisibility(View.GONE);
                    mTXLivePlayer.resume();
                }
                break;

            case R.id.video_play_video:
                if (ivVideoPause.getVisibility() == View.GONE) {
                    ivVideoPause.setVisibility(View.VISIBLE);
                    mTXLivePlayer.pause();
                } else {
                    ivVideoPause.setVisibility(View.GONE);
                    mTXLivePlayer.resume();
                }
                break;
            default:
                break;
        }
    }

    private void toThisPlayer() {

        Common.jumpUserPage(getContext(), videoData.getUid());
    }


    //视频当前player
    private void callPlayer() {
        Common.callVideo(getContext(), videoData.getUid(), 0);
    }

    //点赞视频
    private void clickLoveVideo() {

        lovePlayerImg.setImageResource(R.drawable.zan_se);
        Api.doFollowVideo(uId, uToken, videoData.getId(), new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {

                JsonRequestBase jsonObj = JsonRequestBase.getJsonObj(s, JsonRequestBase.class);
                if (jsonObj.getCode() == 1) {

                    if (isFollowVideo) {
                        lovePlayerImg.setImageResource(R.drawable.zan_un);
                    } else {
                        lovePlayerImg.setImageResource(R.drawable.zan_se);
                    }

                    isFollowVideo = !isFollowVideo;

                    getLoveShareNum();
                    showToastMsg(getContext(), getString(R.string.zan_success));
                } else {
                    showToastMsg(getContext(), jsonObj.getMsg());
                }
            }
        });
    }

    private void getLoveShareNum() {
        Api.doGetVideo(SaveData.getInstance().getId(), SaveData.getInstance().getToken(), videoData.getId(), new StringCallback() {

            @Override
            public void onSuccess(String s, Call call, Response response) {
                Log.e("doGetVideo", s);
                JsonRequestDoGetVideo jsonObj = (JsonRequestDoGetVideo) JsonRequestDoGetVideo.getJsonObj(s, JsonRequestDoGetVideo.class);
                if (jsonObj.getCode() == 1) {

                    lovePlayerNumber.setText(jsonObj.getFollow_num());
                    sharePlayerNumber.setText(jsonObj.getShare());
                } else {
                    showToastMsg(getContext(), jsonObj.getMsg());
                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                toLoadVideo.setVisibility(View.GONE);
            }
        });
    }

    /**
     * 获取当前视频主播信息
     */
    private void requestUserByVideo() {

        if (CuckooVideoTouchPlayerActivity.select_video_id != StringUtils.toInt(videoData.getId())) {
            return;
        }
        Api.doGetVideo(SaveData.getInstance().getId(), SaveData.getInstance().getToken(), videoData.getId(), new StringCallback() {

            @Override
            public void onSuccess(String s, Call call, Response response) {
                Log.e("doGetVideo", s);
                JsonRequestDoGetVideo jsonObj = (JsonRequestDoGetVideo) JsonRequestDoGetVideo.getJsonObj(s, JsonRequestDoGetVideo.class);
                if (jsonObj.getCode() == 1) {
                    toLoadVideo.setVisibility(View.GONE);
                    videoData.setVideo_url(jsonObj.getVideo_url());
                    videoData.setFollow_num(jsonObj.getFollow_num());

                    if (ApiUtils.isTrueUrl(jsonObj.getAvatar())) {
                        Utils.loadUserIcon(getContext(), Utils.getCompleteImgUrl(jsonObj.getAvatar()), thisPlayerImg);
                    }

                    thisPlayerName.setText(videoData.getTitle());
                    isFollowVideo = StringUtils.toInt(jsonObj.getIs_follow()) == 1;
                    if (isFollowVideo) {
                        lovePlayerImg.setImageResource(R.drawable.zan_se);
                    } else {
                        lovePlayerImg.setImageResource(R.drawable.zan_un);
                    }
                    lovePlayerNumber.setText(jsonObj.getFollow_num());


                    if (!TextUtils.isEmpty(jsonObj.getCoin())) {
                        videoPriceTv.setText(jsonObj.getCoin() + RequestConfig.getConfigObj().getCurrency() + getString(R.string.minute));
                    } else {
                        videoPriceTv.setText("视频");
                    }

                    startPlay();


                } else if (jsonObj.getCode() == ApiConstantDefine.ApiCode.VIDEO_NOT_PAY) {

                    ShowPayVideoDialog dialog = new ShowPayVideoDialog(getContext(), videoData);
                    dialog.setDialogClickCallback(new ShowPayVideoDialog.DialogClickCallback() {
                        @Override
                        public void onClickLeft() {
                            getActivity().finish();
                        }

                        @Override
                        public void onClickRight() {

                        }
                    });
                    dialog.show();
                } else {
                    showToastMsg(getContext(), jsonObj.getMsg());
                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                toLoadVideo.setVisibility(View.GONE);
                Log.e("doGetVideo", e.toString());
            }
        });
    }

    private boolean startPlay() {

        int result = mTXLivePlayer.startPlay(videoData.getVideo_url(), TXLivePlayer.PLAY_TYPE_VOD_MP4); // result返回值：0 success;  -1 empty url; -2 invalid url; -3 invalid playType;
        if (result != 0) {
            return false;
        }

        mVideoIsPlay = true;
        return true;
    }

    protected void stopPlay(boolean clearLastFrame) {
        if (mTXLivePlayer != null) {
            mTXLivePlayer.setPlayListener(null);
            mTXLivePlayer.stopPlay(clearLastFrame);
            mVideoIsPlay = false;
        }
    }

    //礼物弹窗
    private void clickOpenGiftDialog() {
        if (giftBottomDialog == null) {

            giftBottomDialog = new GiftBottomDialog(getContext(), videoData.getUid());
            giftBottomDialog.setType(1);
            giftBottomDialog.setChanelId("");
            giftBottomDialog.setDoSendGiftListen(this);
        }

        giftBottomDialog.show();
    }

    /**
     * 显示一个分享框
     */
    private void showSimpleBottomSheetGrid() {
        Api.sendShareInfo(videoData.getId(), new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                Log.e("sendShareInfo", s);
                IsBindPhoneBean bean = new Gson().fromJson(s, IsBindPhoneBean.class);
                if (bean.getCode() == 1) {
                    getLoveShareNum();

                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION},
                                MY_PERMISSIONS_REQUEST);
                    } else {
                        InviteShareDialog dialog = new InviteShareDialog(getContext());
                        dialog.setVideo(videoData.getTitle(), videoData.getImg(), AppConfig.MAIN_URL + "/mapi/public/index.php/api/share_api/index/id/" + videoData.getId() + "/invite_code/" + SaveData.getInstance().getId());
                        dialog.show();
                    }
                } else {
                    ToastUtils.showShort(bean.getMsg());
                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                Log.e("sendShareInfo", e.toString());
            }
        });

    }


    //视频页面滑动切换
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTouchPageChange(CuckooOnTouchShortVideoChangeEvent event) {

        if ((isVisible() && CuckooVideoTouchPlayerActivity.select_video_id == StringUtils.toInt(videoData.getId()))) {
            if (mTXCloudVideoView != null) {
                requestUserByVideo();
            }
        } else {
            stopPlay(false);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTouchPageChange(CuckooBuyVideoCommonEvent event) {

        if (event.getVideoId().equals(videoData.getId())) {
            requestUserByVideo();
        }
    }

    @Override
    protected boolean isRegEvent() {
        return true;
    }


    @Override
    public void onResume() {
        super.onResume();
        mTXLivePlayer.resume();
        holder.setVisibility(View.VISIBLE);
        Log.i("视频", "onResume: ");
    }

    boolean pause = false;

    @Override
    public void onPause() {
        super.onPause();
        holder.setVisibility(View.VISIBLE);
        mTXLivePlayer.pause();
        pause = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mTXLivePlayer != null) {
            mTXLivePlayer.stopPlay(true);
        }
        if (mTXCloudVideoView != null) {
            mTXCloudVideoView.onDestroy();
        }
    }

    @Override
    public void onSuccess(JsonRequestDoPrivateSendGif sendGif) {

        final CustomMsgPrivateGift gift = new CustomMsgPrivateGift();
        gift.fillData(sendGif.getSend());
        if (ll_gift_content != null) {
            pushGiftMsg(gift);
        }
    }


    //添加礼物消息
    private void pushGiftMsg(CustomMsgPrivateGift giftCustom) {

        GiftAnimationModel giftAnimationModel = new GiftAnimationModel();

        giftAnimationModel.setUserAvatar(giftCustom.getSender().getAvatar());
        giftAnimationModel.setUserNickname(giftCustom.getSender().getUser_nickname());
        giftAnimationModel.setMsg(giftCustom.getFrom_msg());
        giftAnimationModel.setGiftIcon(giftCustom.getProp_icon());
        if (ll_gift_content != null) {
            ll_gift_content.addGift(giftAnimationModel);
        }
    }

    @Override
    public void onPlayEvent(int i, Bundle bundle) {
        Log.i("event", "onPlayEvent: " + i);
        if (holder.getVisibility() == View.VISIBLE && i == 2003) {
            holder.setVisibility(View.GONE);
        }
        if (pause && i == 2004) {
            holder.setVisibility(View.GONE);
            pause = false;
        }
        if (i == TXLiveConstants.PLAY_EVT_PLAY_END)
            mTXLivePlayer.startPlay(videoData.getVideo_url(), TXLivePlayer.PLAY_TYPE_VOD_MP4);
    }

    @Override
    public void onNetStatus(Bundle bundle) {

    }
}
