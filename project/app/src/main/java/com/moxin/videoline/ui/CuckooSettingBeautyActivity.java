package com.moxin.videoline.ui;

import android.content.Context;
import android.util.Log;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.moxin.tisdk.AGRender;
import com.moxin.tisdk.VideoPreProcessing;
import com.moxin.videoline.R;
import com.moxin.videoline.base.BaseActivity;
import com.moxin.videoline.modle.ConfigModel;
import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;
import cn.tillusory.sdk.TiSDKManager;
import cn.tillusory.sdk.TiSDKManagerBuilder;
import cn.tillusory.tiui.TiPanelLayout;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;

import static com.umeng.commonsdk.statistics.AnalyticsConstants.LOG_TAG;

public class CuckooSettingBeautyActivity extends BaseActivity {

    @BindView(R.id.video_chat_small)
    FrameLayout video_chat_small;

    private RtcEngine mRtcEngine;

    private VideoCanvas smallVideoCanvas;

    private TiSDKManager tiSDKManager;
    private VideoPreProcessing mVideoPreProcessing;

    @Override
    protected Context getNowContext() {
        return this;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_cuckoo_setting_beauty;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initSet() {
        try {
            mRtcEngine = RtcEngine.create(getBaseContext(), ConfigModel.getInitData().getApp_qgorq_key(), new IRtcEngineEventHandler() {
                @Override
                public void onError(int err) {
                    super.onError(err);
                    Log.e(LOG_TAG, err + "");
                }

            });
        } catch (Exception e) {
            Log.e(LOG_TAG, Log.getStackTraceString(e));
            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }

        //创建视频渲染视图, 设置本地视频视图##初始化本地视图
        SurfaceView surfaceView = RtcEngine.CreateRendererView(getBaseContext());
        surfaceView.setZOrderMediaOverlay(true);
        video_chat_small.addView(surfaceView);

        smallVideoCanvas = new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_HIDDEN, 0);
        mRtcEngine.enableVideo();
        mRtcEngine.setupLocalVideo(smallVideoCanvas);
        mRtcEngine.startPreview();


        // --- tillusory start ---
        tiSDKManager = new TiSDKManagerBuilder().build();
        TiPanelLayout tiPanelLayout = new TiPanelLayout(this).init(tiSDKManager);
        addContentView(tiPanelLayout,
                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        tiPanelLayout.open();
        AGRender.init(tiSDKManager);

        if (mVideoPreProcessing == null) {
            mVideoPreProcessing = new VideoPreProcessing();
        }

//        mVideoPreProcessing.enablePreProcessing(true);
        // --- tillusory end ---
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initPlayerDisplayData() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        RtcEngine.destroy();
        mRtcEngine = null;

        if (DialogH5Activity.instance != null) {
            DialogH5Activity.instance.finish();
        }

        // --- tillusory start ---
//        mVideoPreProcessing.enablePreProcessing(false);
        tiSDKManager.destroy();
        // --- tillusory end ---
    }
}