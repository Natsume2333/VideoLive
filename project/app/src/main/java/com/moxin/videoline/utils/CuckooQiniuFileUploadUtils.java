package com.moxin.videoline.utils;

import com.blankj.utilcode.util.ToastUtils;
import com.moxin.videoline.api.Api;
import com.moxin.videoline.json.JsonDoRequestGetOssInfo;
import com.moxin.videoline.json.JsonRequestBase;
import com.moxin.videoline.json.JsonUploadFile;
import com.moxin.videoline.manage.SaveData;
import com.moxin.videoline.modle.ConfigModel;
import com.luck.picture.lib.entity.LocalMedia;
import com.lzy.okgo.callback.StringCallback;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import org.greenrobot.greendao.annotation.NotNull;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class CuckooQiniuFileUploadUtils {

    private JsonDoRequestGetOssInfo jsonDoRequestGetOssInfo;
    private int uploadFileSize = 0;

    private List<String> uploadFileUrlList = new ArrayList<>();
    private CuckooQiniuFileUploadCallback cuckooQiniuFileUploadCallback;
    private final UploadManager uploadManager;

    public CuckooQiniuFileUploadUtils() {
        getUploadOssSign();

        uploadManager = new UploadManager();
    }

    //获取七牛上传sign
    private void getUploadOssSign() {
        Api.doRequestGetOSSInfo(SaveData.getInstance().getId(), SaveData.getInstance().getToken(), new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                jsonDoRequestGetOssInfo = (JsonDoRequestGetOssInfo) JsonRequestBase.getJsonObj(s, JsonDoRequestGetOssInfo.class);

            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
            }
        });
    }

    //上传文件
    public void uploadFile(String dir, final List<File> fileList, @NotNull CuckooQiniuFileUploadCallback callback) {
        if (jsonDoRequestGetOssInfo == null) {
            ToastUtils.showLong("初始化失败！");
            return;
        }
        uploadFileUrlList.clear();
        uploadFileSize = fileList.size();
        cuckooQiniuFileUploadCallback = callback;

        for (File file : fileList) {
            uploadFileQiniuOss(dir, file);
        }

    }

    //上传文件
    public void uploadFileLocalMedia(String dir, final List<LocalMedia> fileList, @NotNull CuckooQiniuFileUploadCallback callback) {
        if (jsonDoRequestGetOssInfo == null) {
            ToastUtils.showLong("初始化失败！");
            return;
        }

        uploadFileUrlList.clear();
        uploadFileSize = fileList.size();
        cuckooQiniuFileUploadCallback = callback;

        if (fileList.size() == 0) {
            cuckooQiniuFileUploadCallback.onUploadFileSuccess(uploadFileUrlList);
            return;
        }

        for (LocalMedia localMedia : fileList) {
            if (Utils.isHttpUrl(localMedia.getPath())) {
                uploadFileUrlList.add(localMedia.getPath());
                if (uploadFileSize == uploadFileUrlList.size()) {
                    cuckooQiniuFileUploadCallback.onUploadFileSuccess(uploadFileUrlList);
                }
            } else {
                File file = new File(localMedia.getPath());
                uploadFileQiniuOss(dir, file);
            }
        }

    }

    private void uploadFileQiniuOss(String dir, File file) {
        if (StringUtils.toInt(ConfigModel.getInitData().getUpload_type()) == 1) {
            String token = jsonDoRequestGetOssInfo.getToken();
            //设置上传后文件的key
            final String upkey = dir + System.currentTimeMillis() + "_" + file.getName();
            uploadManager.put(file, upkey, token,
                    new UpCompletionHandler() {
                        @Override
                        public void complete(String key, ResponseInfo info, JSONObject response) {

                            String fileUrl = jsonDoRequestGetOssInfo.getDomain() + "/" + upkey;
                            uploadFileUrlList.add(fileUrl);
                            if (uploadFileSize == uploadFileUrlList.size()) {
                                cuckooQiniuFileUploadCallback.onUploadFileSuccess(uploadFileUrlList);
                            }
                        }

                    }, null);
        } else {
            Api.doUploadFile(SaveData.getInstance().getId(), SaveData.getInstance().getToken(), file, new StringCallback() {
                @Override
                public void onSuccess(String s, Call call, Response response) {
                    JsonUploadFile jsonUploadFile = (JsonUploadFile) JsonRequestBase.getJsonObj(s, JsonUploadFile.class);
                    if (jsonUploadFile.getCode() == 1) {

                        String fileUrl = jsonUploadFile.getUrl();
                        uploadFileUrlList.add(fileUrl);
                        if (uploadFileSize == uploadFileUrlList.size()) {
                            cuckooQiniuFileUploadCallback.onUploadFileSuccess(uploadFileUrlList);
                        }
                    } else {
                        ToastUtils.showLong(jsonUploadFile.getMsg());
                    }
                }

                @Override
                public void onError(Call call, Response response, Exception e) {
                    super.onError(call, response, e);

                }
            });
        }

    }

    public interface CuckooQiniuFileUploadCallback {
        void onUploadFileSuccess(List<String> fileUrlList);
    }

}
