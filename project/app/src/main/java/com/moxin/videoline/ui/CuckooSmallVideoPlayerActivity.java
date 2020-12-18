package com.moxin.videoline.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.moxin.videoline.R;
import com.bumptech.glide.Glide;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.tencent.rtmp.ITXLivePlayListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePlayConfig;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CuckooSmallVideoPlayerActivity extends AppCompatActivity implements ITXLivePlayListener {

    @BindView(R.id.holder)
    ImageView holder;

    private ProgressBar toLoadVideo;//加载...
    private String video_url, cover_url;
    private TXCloudVideoView mTXCloudVideoView;
    private TXLivePlayer mTXLivePlayer = null;
    private TXLivePlayConfig mTXPlayConfig = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_small_video_player);
        ButterKnife.bind(this);
        initView();
        initData();
        initSet();
    }


    @OnClick(R.id.video_play_close)
    public void onb() {
        onBackPressed();
    }

    public void initView() {

        QMUIStatusBarHelper.translucent(this);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        toLoadVideo = findViewById(R.id.toload_video);
        mTXCloudVideoView = findViewById(R.id.video_play_video);

        video_url = getIntent().getStringExtra("VIDEO_URL");
        cover_url = getIntent().getStringExtra("COVER_URL");

        toLoadVideo.setVisibility(View.VISIBLE);
        Glide.with(this).load(cover_url).into(holder);
        holder.setVisibility(View.VISIBLE);

    }

    protected void initSet() {
        initLive();
    }

    private void initLive() {

        mTXLivePlayer = new TXLivePlayer(this);
        mTXPlayConfig = new TXLivePlayConfig();

        mTXLivePlayer.setPlayerView(mTXCloudVideoView);
        mTXLivePlayer.setPlayListener(this);
        mTXLivePlayer.enableHardwareDecode(false);
        mTXLivePlayer.setRenderRotation(TXLiveConstants.RENDER_ROTATION_PORTRAIT);
        mTXLivePlayer.setRenderMode(TXLiveConstants.RENDER_ROTATION_PORTRAIT);//RENDER_MODE_ADJUST_RESOLUTION
        mTXLivePlayer.setConfig(mTXPlayConfig);

        startPlay();
    }


    public void initData() {

    }

    protected void initPlayerDisplayData() {

    }


    @Override
    public void onPlayEvent(int i, Bundle bundle) {
        if (holder.getVisibility() == View.VISIBLE && i == 2003) {
            holder.setVisibility(View.GONE);
        }
        if (pause && i == 2004) {
            holder.setVisibility(View.GONE);
            pause = false;
        }
        if (i == TXLiveConstants.PLAY_EVT_PLAY_END)
            mTXLivePlayer.startPlay(video_url, TXLivePlayer.PLAY_TYPE_VOD_MP4);
        toLoadVideo.setVisibility(View.GONE);
    }

    @Override
    public void onNetStatus(Bundle bundle) {

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


    private boolean startPlay() {

        int result = mTXLivePlayer.startPlay(video_url, TXLivePlayer.PLAY_TYPE_VOD_MP4); // result返回值：0 success;  -1 empty url; -2 invalid url; -3 invalid playType;
        if (result != 0) {
            //mStartPreview.setBackgroundResource(R.drawable.icon_record_start);
            return false;
        }

        return true;
    }

    protected void stopPlay(boolean clearLastFrame) {
        if (mTXLivePlayer != null) {
            mTXLivePlayer.setPlayListener(null);
            mTXLivePlayer.stopPlay(clearLastFrame);

        }
    }

}
