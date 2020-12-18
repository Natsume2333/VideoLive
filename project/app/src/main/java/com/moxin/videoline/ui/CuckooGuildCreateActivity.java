package com.moxin.videoline.ui;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;

import com.blankj.utilcode.util.ToastUtils;
import com.moxin.videoline.LiveConstant;
import com.moxin.videoline.R;
import com.moxin.videoline.api.Api;
import com.moxin.videoline.base.BaseActivity;
import com.moxin.videoline.helper.ImageUtil;
import com.moxin.videoline.json.JsonDoRequestGetOssInfo;
import com.moxin.videoline.json.JsonRequestBase;
import com.moxin.videoline.manage.SaveData;
import com.moxin.videoline.utils.StringUtils;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.lzy.okgo.callback.StringCallback;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import org.json.JSONObject;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Response;

public class CuckooGuildCreateActivity extends BaseActivity {


    @BindView(R.id.et_introduce)
    EditText et_introduce;

    @BindView(R.id.et_name)
    EditText et_name;

    @BindView(R.id.head_img)
    CircleImageView logo_img;
    private File headImgFile;
    private String uploadLogoImgUrl;

    @Override
    protected Context getNowContext() {
        return this;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.cuckoo_act_guild_create;
    }

    @Override
    protected void initView() {
        getTopBar().setTitle("创建公会");
    }

    @Override
    protected void initSet() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initPlayerDisplayData() {

    }

    @OnClick({R.id.btn_submit, R.id.head_img})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit:
                clickSubmit();
                break;
            case R.id.head_img:
                clickChangeLogo();
                break;
            default:
                break;
        }
    }

    private void clickChangeLogo() {

        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())
                .maxSelectNum(5)
                .enableCrop(true)// 是否裁剪 true or false
                .hideBottomControls(false)// 是否显示uCrop工具栏，默认不显示 true or false
                .freeStyleCropEnabled(true)// 裁剪框是否可拖拽 true or false
                .forResult(PictureConfig.CHOOSE_REQUEST);
    }

    private void clickSubmit() {

        Api.doRequestGetOSSInfo(SaveData.getInstance().getId(), SaveData.getInstance().getToken(), new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {

                JsonDoRequestGetOssInfo data = (JsonDoRequestGetOssInfo) JsonRequestBase.getJsonObj(s, JsonDoRequestGetOssInfo.class);
                if (StringUtils.toInt(data.getCode()) == 1) {

                    uploadImg(data);
                } else {
                    ToastUtils.showLong(data.getMsg());
                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                hideLoadingDialog();
            }
        });


    }

    private void uploadImg(final JsonDoRequestGetOssInfo data) {

        if (headImgFile == null || !headImgFile.exists()) {
            ToastUtils.showLong("形象图文件不存在！");
            return;
        }

        //设置上传后文件的key
        final String upkey = LiveConstant.VIDEO_COVER_IMG_DIR + System.currentTimeMillis() + "_" + headImgFile.getName();

        String token = data.getToken();
        UploadManager uploadManager = new UploadManager();
        uploadManager.put(headImgFile, upkey, token,
                new UpCompletionHandler() {
                    @Override
                    public void complete(String key, ResponseInfo info, JSONObject response) {
                        uploadLogoImgUrl = data.getDomain() + "/" + upkey;
                        createGuild();
                    }

                }, null);
    }

    private void createGuild() {

        String name = et_name.getText().toString();
        String introduce = et_introduce.getText().toString();

        showLoadingDialog("正在提交数据...");
        Api.doCreateGuild(uId, uToken, name, introduce, uploadLogoImgUrl, new StringCallback() {

            @Override
            public void onSuccess(String s, Call call, Response response) {
                hideLoadingDialog();
                JsonRequestBase data = JsonRequestBase.getJsonObj(s, JsonRequestBase.class);
                if (data.getCode() == 1) {
                    ToastUtils.showLong("申请成功，等待管理员审核！");
                    finish();
                } else {
                    ToastUtils.showLong(data.getMsg());
                }

            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                hideLoadingDialog();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片选择结果回调
                    List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);

                    headImgFile = new File(selectList.get(0).getPath());
                    logo_img.setImageBitmap(ImageUtil.getBitmapByPath(headImgFile.getAbsolutePath()));
                    break;
            }
        }
    }

    @Override
    protected boolean hasTopBar() {
        return true;
    }
}
