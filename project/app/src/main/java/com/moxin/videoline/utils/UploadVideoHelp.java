package com.moxin.videoline.utils;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.moxin.video.videoupload.TXUGCPublish;
import com.moxin.video.videoupload.TXUGCPublishTypeDef;
import com.moxin.videoline.LiveConstant;
import com.moxin.videoline.api.Api;
import com.moxin.videoline.json.JsonDoRequestGetOssInfo;
import com.moxin.videoline.json.JsonRequestBase;
import com.moxin.videoline.manage.SaveData;
import com.moxin.videoline.modle.UserModel;
import com.lzy.okgo.callback.StringCallback;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import org.json.JSONObject;

import java.io.File;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by weipeng on 2018/3/1.
 */

public class UploadVideoHelp implements TXUGCPublishTypeDef.ITXVideoPublishListener {

    private UploadVideoListen uploadVideoListen;
    private JsonDoRequestGetOssInfo jsonDoRequestGetOssInfo;
    private UserModel userModel;
    private TXUGCPublish mVideoPublish;

    private String videoPath, coverPath;

    public UploadVideoHelp() {

        userModel = SaveData.getInstance().getUserInfo();
    }

    public void uploadVideo(String videoPath, String coverPath) {

        this.videoPath = videoPath;
        this.coverPath = coverPath;

        if (jsonDoRequestGetOssInfo == null) {
            //获取上传sign
            doRequestGetSign();
        } else {
            doUploadFile(jsonDoRequestGetOssInfo);
        }
    }

    //获取腾讯云上传sign
    private void doRequestGetSign() {

        Api.doRequestGetOSSInfo(SaveData.getInstance().getId(), SaveData.getInstance().getToken(), new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {

                JsonDoRequestGetOssInfo data = (JsonDoRequestGetOssInfo) JsonRequestBase.getJsonObj(s, JsonDoRequestGetOssInfo.class);
                if (StringUtils.toInt(data.getCode()) == 1) {
                    doUploadFile(data);
                } else {
                    ToastUtils.showLong(data.getMsg());
                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                if (uploadVideoListen != null) {
                    uploadVideoListen.onGetSignError();
                }
            }
        });
    }

    //上传视频
    private void doUploadFile(final JsonDoRequestGetOssInfo data) {

//        mVideoPublish = new TXUGCPublish(CuckooApplication.getInstances());
//        // 文件发布默认是采用断点续传
//        TXUGCPublishTypeDef.TXPublishParam param = new TXUGCPublishTypeDef.TXPublishParam();
//        param.signature = sign;                       // 需要填写第四步中计算的上传签名
//        // 录制生成的视频文件路径, ITXVideoRecordListener 的 onRecordComplete 回调中可以获取
//        param.videoPath = videoPath;
//        // 录制生成的视频首帧预览图，ITXVideoRecordListener 的 onRecordComplete 回调中可以获取
//        param.coverPath = coverPath;
//        mVideoPublish.publishVideo(param);
//        mVideoPublish.setListener(this);

        File file = new File(videoPath);
        //设置上传后文件的key
        final String upkey = LiveConstant.VIDEO_DIR + System.currentTimeMillis() + "_" + file.getName();

        String token = data.getToken();
        UploadManager uploadManager = new UploadManager();
        uploadManager.put(file, upkey, token,
                new UpCompletionHandler() {
                    @Override
                    public void complete(String key, ResponseInfo info, JSONObject response) {
                        String uploadFileVideoUrl = data.getDomain() + "/" + upkey;
                        //requestAddShortVideo();

                        if (uploadVideoListen != null) {
                            uploadVideoListen.onUploadVideoCommon(uploadFileVideoUrl);
                        }
                    }


                }, null);
    }

    //取消上传
    public void cancelUploadVideo() {
        if (mVideoPublish != null) {
            mVideoPublish.canclePublish();
        }
    }

    @Override
    public void onPublishProgress(long uploadBytes, long totalBytes) {
        LogUtils.i("上传大小:" + uploadBytes + "总大小:" + totalBytes);
        if (uploadVideoListen != null) {
            uploadVideoListen.onPublishProgress(uploadBytes, totalBytes);
        }
    }

    @Override
    public void onPublishComplete(TXUGCPublishTypeDef.TXPublishResult result) {

//        if(uploadVideoListen != null){
//            uploadVideoListen.onUploadVideoCommon(result);
//        }

        LogUtils.i("上传完成:code" + result.retCode + "msg:" + result.descMsg);

    }

    public void setUploadVideoListen(UploadVideoListen uploadVideoListen) {
        this.uploadVideoListen = uploadVideoListen;
    }

    public interface UploadVideoListen {

        void onGetSignError();

        void onPublishProgress(long uploadBytes, long totalBytes);

        void onUploadVideoCommon(String url);
    }
}
