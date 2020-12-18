package com.moxin.videoline.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.moxin.videoline.CuckooApplication;
import com.moxin.videoline.LiveConstant;
import com.moxin.videoline.R;
import com.moxin.videoline.adapter.FullyGridLayoutManager;
import com.moxin.videoline.adapter.GridImageAdapter;
import com.moxin.videoline.api.Api;
import com.moxin.videoline.audiorecord.AudioPlaybackManager;
import com.moxin.videoline.audiorecord.AudioRecordJumpUtil;
import com.moxin.videoline.audiorecord.entity.AudioEntity;
import com.moxin.videoline.audiorecord.util.PaoPaoTips;
import com.moxin.videoline.audiorecord.view.CommonSoundItemView;
import com.moxin.videoline.base.BaseActivity;
import com.moxin.videoline.event.EventBusConfig;
import com.moxin.videoline.event.RefreshMessageEvent;
import com.moxin.videoline.event.VoiceRecordEvent;
import com.moxin.videoline.helper.ImageUtil;
import com.moxin.videoline.json.JsonRequestBase;
import com.moxin.videoline.manage.SaveData;
import com.moxin.videoline.utils.CuckooQiniuFileUploadUtils;
import com.moxin.videoline.utils.StringUtils;
import com.moxin.videoline.utils.Utils;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.lzy.okgo.callback.StringCallback;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

public class PushDynamicActivity extends BaseActivity {

    @BindView(R.id.voice_hint)
    TextView mBtnVoiceRecord;

    @BindView(R.id.bt_hint)
    TextView mBtnVideoRecord;

    @BindView(R.id.et_input)
    EditText mEtInput;

    @BindView(R.id.tv_mark)
    TextView mark;

    @BindView(R.id.recycler)
    RecyclerView recyclerView;

    @BindView(R.id.pp_sound_item_view)
    CommonSoundItemView soundItemView;

    @BindView(R.id.title)
    View title;

    @BindView(R.id.location)
    CheckBox location;

    private GridImageAdapter adapter;

    private boolean hasVoiceFile = false;
    private String voiceFilePath = "";
    private List<LocalMedia> selectList = new ArrayList<>();
    private int maxSelectNum = 9;

    private int selectType = PictureMimeType.ofImage();

    /**
     * 1 视频 0图片
     */
    private int fileType = 0;


    private String uploadVideoUrl = "";
    private String uploadVideoThmbUrl = "";
    private String uploadAudoUrl = "";
    private List<String> uploadImgUrlList = new ArrayList<>();
    private String content;

    private int mDuration;
    private CuckooQiniuFileUploadUtils cuckooQiniuFileUploadUtils;

    @Override
    protected Context getNowContext() {
        return this;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_push_dynamic;
    }


    private String city = "";

    @Override
    protected void initView() {
        QMUIStatusBarHelper.translucent(this); // 沉浸式状态栏
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        Utils.initTransP(title);
        FullyGridLayoutManager manager = new FullyGridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);

        adapter = new GridImageAdapter(this, onAddPicClickListener);
        adapter.setList(selectList);
        adapter.setSelectMax(maxSelectNum);
        recyclerView.setAdapter(adapter);
        city = CuckooApplication.getInstances().getLocation().get("city");
        location.setText("发布位置 " + city);
    }

    @Override
    protected void initSet() {
    }

    @Override
    protected void initData() {
        cuckooQiniuFileUploadUtils = new CuckooQiniuFileUploadUtils();

    }

    @Override
    protected void initPlayerDisplayData() {

    }

    private GridImageAdapter.onAddPicClickListener onAddPicClickListener = new GridImageAdapter.onAddPicClickListener() {
        @Override
        public void onAddPicClick() {
            //添加图片视频前 清理掉 语音
            deleteVoiceFile();

            //选视频 图片
            PictureSelector.create(PushDynamicActivity.this)
                    .openGallery(selectType)
                    .maxSelectNum(maxSelectNum)
                    .previewVideo(true)
                    .recordVideoSecond(60)
                    .forResult(PictureConfig.CHOOSE_REQUEST);
        }
    };

    @OnClick({R.id.rl_input, R.id.btn_voice_record, R.id.tv_push, R.id.tv_cancel, R.id.btn_video_record})
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.rl_input:
                KeyboardUtils.showSoftInput(mEtInput);
                break;
            case R.id.tv_cancel:
                finish();
                break;
            case R.id.tv_push:
                clickPushDynamic();
                break;
            case R.id.btn_voice_record:
                //上传语音前 清理掉图片/视频
                cleanSelectVideo();

                //上传语音
                clickRecrodVoice();
                break;
            case R.id.btn_video_record:
                clickSelectVideo();
                break;

//            case R.id.iv_add_img:
//
//
//                PictureSelector.create(this)
//                        .openGallery(PictureMimeType.ofImage())
//                        .maxSelectNum(9)
//                        .forResult(PictureConfig.CHOOSE_REQUEST);
//                break;

            default:
                break;
        }
    }

    private void clickRecrodVoice() {
        if (hasVoiceFile) {
            deleteVoiceFile();
            return;
        }

        AudioRecordJumpUtil.startRecordAudio(PushDynamicActivity.this);
    }

    /**
     * 删除语音 整套逻辑
     */
    private void deleteVoiceFile() {
        File tempFile = new File(voiceFilePath);
        if (tempFile.exists()) {
            tempFile.delete();
        }
        hasVoiceFile = false;
        soundItemView.setVisibility(View.GONE);
        mBtnVoiceRecord.setText("录制音频");
        AudioPlaybackManager.getInstance().stopAudio();
    }

    /**
     * 点击上传按钮切换视频 图片
     */
    private void clickSelectVideo() {
        String trim = mBtnVideoRecord.getText().toString().trim();
        if ("上传视频".equals(trim)) {
            mBtnVideoRecord.setText("上传图片");
            mark.setText("添加视频不超过1个，文字备注不超过300字");
            selectType = PictureMimeType.ofVideo();
            selectList.clear();

            maxSelectNum = 1;

        } else {
            mBtnVideoRecord.setText("上传视频");
            mark.setText("添加图片不超过9张，文字备注不超过300字");
            selectType = PictureMimeType.ofImage();
            selectList.clear();

            maxSelectNum = 9;
        }

        adapter.setSelectMax(maxSelectNum);
        adapter.setList(selectList);
        adapter.notifyDataSetChanged();
    }

    /**
     * 清理所选视频图片
     */
    private void cleanSelectVideo() {
        String trim = mBtnVideoRecord.getText().toString().trim();
        if ("上传视频".equals(trim)) {

            mBtnVideoRecord.setText("上传视频");
            mark.setText("添加图片不超过9张，文字备注不超过300字");
            selectType = PictureMimeType.ofImage();
            selectList.clear();

            maxSelectNum = 9;
        } else {
            mBtnVideoRecord.setText("上传图片");
            mark.setText("添加视频不超过1个，文字备注不超过300字");
            selectType = PictureMimeType.ofVideo();
            selectList.clear();

            maxSelectNum = 1;


        }

        adapter.setSelectMax(maxSelectNum);
        adapter.setList(selectList);
        adapter.notifyDataSetChanged();
    }

    //发布动态
    private void clickPushDynamic() {
        content = mEtInput.getText().toString();
        if (TextUtils.isEmpty(content)) {
            ToastUtils.showLong("内容不能为空！");
            return;
        }

        //上传视频 图片到七牛云
        showLoadingDialog(getString(R.string.loading_upload_info));

        //上传音频 如果有
        if (hasVoiceFile) {
            uploadVoiceFile();
        } else {
            //上传视频 图片
            uploadImgAndVideo();
        }

    }

    private void toPush() {
        showLoadingDialog("正在发布...");
        Api.doRequestPushDynamic(city, SaveData.getInstance().getId(), SaveData.getInstance().getToken(),
                content,
                hasVoiceFile ? 1 : 0,
                uploadImgUrlList, uploadVideoUrl, uploadAudoUrl, fileType, uploadVideoThmbUrl, mDuration, new StringCallback() {

                    @Override
                    public void onSuccess(String s, Call call, Response response) {

                        hideLoadingDialog();
                        JsonRequestBase data = JsonRequestBase.getJsonObj(s, JsonRequestBase.class);
                        if (StringUtils.toInt(data.getCode()) == 1) {
                            ToastUtils.showLong("发布成功！");
                            EventBus.getDefault().post(new RefreshMessageEvent("refresh_dynamic_list"));
                            finish();
                        } else {
                            ToastUtils.showLong(data.getMsg());
                        }

                    }
                });
    }

    private void uploadImgAndVideo() {

        uploadImgUrlList.clear();

        if (selectType == PictureMimeType.ofImage()) {
            fileType = 0;

            //如果没选择图片
            if (selectList.size() == 0) {
                //发布
                toPush();
                return;
            }

            cuckooQiniuFileUploadUtils.uploadFileLocalMedia(LiveConstant.VIDEO_COVER_IMG_DIR, selectList, new CuckooQiniuFileUploadUtils.CuckooQiniuFileUploadCallback() {
                @Override
                public void onUploadFileSuccess(List<String> fileUrlList) {
                    hideLoadingDialog();
                    if (fileUrlList.size() == selectList.size()) {
                        uploadImgUrlList.addAll(fileUrlList);
                        //发布
                        toPush();
                    } else {
                        ToastUtils.showLong("图片上传错误！");
                    }
                }
            });

        } else {
            fileType = 1;

            //如果没选择视频
            if (selectList.size() == 0) {
                //发布
                toPush();
                return;
            }

            //获取封面
            MediaMetadataRetriever media = new MediaMetadataRetriever();
            media.setDataSource(selectList.get(0).getPath());// videoPath 本地视频的路径
            Bitmap bitmap = media.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
            File saveFile = ImageUtil.getSaveFile(bitmap, String.valueOf(System.currentTimeMillis()));

            List<File> thumbList = new ArrayList<>();
            thumbList.add(new File(saveFile.getPath()));

            //上传封面
            cuckooQiniuFileUploadUtils.uploadFile(LiveConstant.VIDEO_COVER_IMG_DIR, thumbList, new CuckooQiniuFileUploadUtils.CuckooQiniuFileUploadCallback() {
                @Override
                public void onUploadFileSuccess(List<String> fileUrlList) {

                    if (fileUrlList.size() > 0) {
                        uploadVideoThmbUrl = fileUrlList.get(0);
                        //上传视频
                        uploadVideo();
                    } else {
                        hideLoadingDialog();
                        ToastUtils.showLong("图片上传错误！");
                    }
                }
            });

        }
    }

    //上传音频
    private void uploadVoiceFile() {

        List<File> fileList = new ArrayList<>();
        fileList.add(new File(voiceFilePath));
        cuckooQiniuFileUploadUtils.uploadFile(LiveConstant.AUDIO_DIR, fileList, new CuckooQiniuFileUploadUtils.CuckooQiniuFileUploadCallback() {
            @Override
            public void onUploadFileSuccess(List<String> fileUrlList) {
                hideLoadingDialog();
                if (fileUrlList.size() > 0) {
                    uploadAudoUrl = fileUrlList.get(0);
                    //上传视频 图片
                    uploadImgAndVideo();
                } else {
                    ToastUtils.showLong("上传音频文件失败！");
                }
            }
        });
    }

    //上传视频
    private void uploadVideo() {

        cuckooQiniuFileUploadUtils.uploadFileLocalMedia(LiveConstant.VIDEO_DIR, selectList, new CuckooQiniuFileUploadUtils.CuckooQiniuFileUploadCallback() {
            @Override
            public void onUploadFileSuccess(List<String> fileUrlList) {
                hideLoadingDialog();
                if (fileUrlList.size() > 0) {
                    uploadVideoUrl = fileUrlList.get(0);
                    //发布
                    toPush();
                } else {
                    ToastUtils.showLong("视频文件上传失败！");
                }
            }
        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(VoiceRecordEvent mainThreadEvent) {
        if (mainThreadEvent.getWhat() == EventBusConfig.SOUND_FEED_RECORD_FINISH) {
            Object soundPath = mainThreadEvent.getObj();
            if (soundPath != null && soundPath instanceof String) {
                String path = (String) soundPath;
                voiceFilePath = path;
                AudioEntity entity = new AudioEntity();
                entity.setUrl(path);
                int duration = AudioPlaybackManager.getDuration(path);
                if (duration <= 0) {
                    //PPLog.d(TAG, "duration <= 0");
                    PaoPaoTips.showDefault(this, "无权限");

                    File tempFile = new File(path);
                    if (tempFile.exists()) {
                        tempFile.delete();
                        return;
                    }
                } else {
                    entity.setDuration(duration / 1000);
                    mDuration = duration / 1000;
                    mBtnVoiceRecord.setText("删除音频");
                    soundItemView.setSoundData(entity);
                    soundItemView.setVisibility(View.VISIBLE);
                    hasVoiceFile = true;
                    //PPLog.d(TAG, "soundPath:" + path);
                }
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择结果回调
                    selectList = PictureSelector.obtainMultipleResult(data);
                    // 例如 LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                    // 如果裁剪并压缩了，已取压缩路径为准，因为是先裁剪后压缩的
                    for (LocalMedia media : selectList) {
                        Log.i("图片-----》", media.getPath());
                    }
                    adapter.setList(selectList);
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    }
}
