package com.moxin.videoline.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;

import com.blankj.utilcode.util.LogUtils;
import com.moxin.videoline.R;
import com.moxin.videoline.adapter.FragAdapter;
import com.moxin.videoline.api.Api;
import com.moxin.videoline.api.ApiUtils;
import com.moxin.videoline.base.BaseFragment;
import com.moxin.videoline.helper.ImageUtil;
import com.moxin.videoline.json.JsonGetIsAuth;
import com.moxin.videoline.json.JsonRequestBase;
import com.moxin.videoline.manage.SaveData;
import com.moxin.videoline.modle.ConfigModel;
import com.moxin.videoline.ui.PushShortVideoActivity;
import com.moxin.videoline.ui.VideoRecordActivity;
import com.moxin.videoline.utils.SharedPreferencesUtils;
import com.moxin.videoline.utils.StringUtils;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.lzy.okgo.callback.StringCallback;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.QMUITabSegment;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.qmuiteam.qmui.widget.QMUIViewPager;
import com.qmuiteam.qmui.widget.popup.QMUIListPopup;
import com.qmuiteam.qmui.widget.popup.QMUIPopup;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 小视频
 * Created by wp on 2017/12/28 0028.
 */
public class VideoSmallFragment extends BaseFragment {
    //功能
    private QMUIViewPager rollViewViewpage;//viewPager
    private QMUITopBar rollTopBar;//标题
    private QMUITabSegment rollTabSegment;//顶部bar
    private View topBarView;
    private QMUIListPopup mPopup;//Popup框

    private List fragmentList;//fragment
    private List titleList;//标题

    private FragAdapter mVideoFragAdapter;//适配器

    private ImageView upLoadVideo;

    @Override
    protected View getBaseView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_index_videosmall, container, false);
    }

    @Override
    protected void initView(View view) {
        //顶部bar布局

        topBarView = LayoutInflater.from(getContext()).inflate(R.layout.view_top_page_small, null);
        rollTabSegment = view.findViewById(R.id.tab);
        rollTopBar = view.findViewById(R.id.video_tab_segment);
        rollViewViewpage = view.findViewById(R.id.video_view_viewpage);


        rollTabSegment.setTabTextSize(getResources().getDimensionPixelSize(R.dimen.home_tab_text));
        rollTabSegment.setDefaultSelectedColor(getResources().getColor(R.color.main_tab_sel));
        rollTabSegment.setDefaultNormalColor(getResources().getColor(R.color.main_tab_unsel));
        rollTabSegment.setHasIndicator(true);
    }

    @Override
    protected void initDate(View view) {
        fragmentList = new ArrayList();
        fragmentList.add(getAboutFragment(ApiUtils.VideoType.reference));
        fragmentList.add(getAboutFragment(ApiUtils.VideoType.latest));
        fragmentList.add(getAboutFragment(ApiUtils.VideoType.attention));
        //fragmentList.add(getAboutFragment(ApiUtils.VideoType.near));

        titleList = new ArrayList();
        titleList.add(getString(R.string.recommend));
        titleList.add(getString(R.string.newest));
        titleList.add(getString(R.string.follow));
        //titleList.add("附近");
    }

    @Override
    protected void initSet(View view) {
        //设置topbar中布局
        rollTopBar.setCenterView(topBarView);

        upLoadVideo = view.findViewById(R.id.prize_to_upload);
        upLoadVideo.setOnClickListener(this);

        int sex = (int) SharedPreferencesUtils.getParam(getContext(), "sex", 0);
        if (sex == 2) {
            upLoadVideo.setVisibility(View.VISIBLE);
        } else {
            upLoadVideo.setVisibility(View.GONE);
        }

        rollViewViewpage.setOffscreenPageLimit(3);

        mVideoFragAdapter = new FragAdapter(getChildFragmentManager(), fragmentList);
        mVideoFragAdapter.setTitleList(titleList);
        rollViewViewpage.setAdapter(mVideoFragAdapter);




        //关联viewPage
        rollTabSegment.setupWithViewPager(rollViewViewpage, true, false);
    }



    @Override
    protected void initDisplayData(View view) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.prize_to_upload:
                initListPopupIfNeed();
                mPopup.setAnimStyle(QMUIPopup.ANIM_GROW_FROM_CENTER);//显示动画
                mPopup.setPreferredDirection(QMUIPopup.DIRECTION_BOTTOM);//位置
                mPopup.show(v);//关联控件并显示
                break;
        }
    }


    /**
     * 根据不同的type获取不同的VideoRecyclerFragment
     *
     * @param type 类型
     * @return VideoRecyclerFragment
     */
    private Fragment getAboutFragment(String type) {
        VideoRecyclerFragment vf = new VideoRecyclerFragment();
        vf.setType(type);
        return vf;
    }

    /**
     * 初始化一个Popup框
     */
    private void initListPopupIfNeed() {
        if (mPopup == null) {
            final String[] listItems = new String[]{
                    getString(R.string.right_text_top),
                    getString(R.string.right_text_bottom)
            };
            int[] imgs = new int[]{
                    R.drawable.icon_record_video,
                    R.drawable.icon_upload_video
            };
            List<Map<String, Object>> data = new ArrayList<>();
            Map<String, Object> map = new HashMap<>();
            for (int i = 0; i < imgs.length; i++) {
                Map<String, Object> listem = new HashMap<String, Object>();
                listem.put("img", imgs[i]);
                listem.put("text", listItems[i]);
                data.add(listem);
            }
            String[] from = {"text", "img"};
            int[] to = {R.id.right_text_top, R.id.left_img_top};
            SimpleAdapter simAdapter = new SimpleAdapter(getContext(), data, R.layout.adapter_popuplist, from, to);
            mPopup = new QMUIListPopup(getContext(), QMUIPopup.DIRECTION_NONE, simAdapter);
            mPopup.create(QMUIDisplayHelper.dp2px(getContext(), 140), QMUIDisplayHelper.dp2px(getContext(), 100), new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    //showToastMsg(getContext(), listItems[i]);
                    clickPushVideo(i);
                    mPopup.dismiss();
                }
            });
            //消失监听
            mPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                }
            });
        }
    }

    //点击发布视频
    private void clickPushVideo(final int type) {

        if (StringUtils.toInt(ConfigModel.getInitData().getUpload_certification()) == 0) {
            if (type == 0) {
                Intent intent = new Intent(getContext(), VideoRecordActivity.class);
                startActivity(intent);
            } else {
                clickSelectVideo();
            }
            return;
        }

        Api.doRequestGetIsAuth(SaveData.getInstance().getId(), SaveData.getInstance().getToken(), new StringCallback() {

            @Override
            public void onSuccess(String s, Call call, Response response) {
                JsonGetIsAuth data = (JsonGetIsAuth) JsonRequestBase.getJsonObj(s, JsonGetIsAuth.class);
                if (data.getCode() == 1) {
                    if (data.getIs_auth() == 1) {
                        if (type == 0) {
                            Intent intent = new Intent(getContext(), VideoRecordActivity.class);
                            startActivity(intent);
                        } else {
                            clickSelectVideo();
                        }
                    } else {
                        showToastMsg(getContext(),getResources().getString(R.string.notAuth_hint)+"视频");

                    }
                } else {
                    showToastMsg(getContext(),getResources().getString(R.string.notAuth_hint)+"视频");

                }
            }
        });
    }


    //选择本地视频文件
    private void clickSelectVideo() {

        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            PictureSelector.create(this)
                    .openGallery(PictureMimeType.ofVideo())
                    .selectionMode(PictureConfig.SINGLE)
                    .forResult(PictureConfig.CHOOSE_REQUEST);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (resultCode == -1 && requestCode == PictureConfig.CHOOSE_REQUEST) {
                //录制监听
                List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                LogUtils.i("选择视频");
                if (selectList.size() > 0) {
                    LocalMedia videoFile = selectList.get(0);
                    //Bitmap thumb = BGVideoFile.getVideoThumbnail(videoFile.getPath());
                    Bitmap thumb = ThumbnailUtils.createVideoThumbnail(videoFile.getPath(), MediaStore.Video.Thumbnails.MINI_KIND);
                    if (thumb != null) {

                        File thumbFile = ImageUtil.getSaveFile(thumb, String.valueOf(System.currentTimeMillis()));
                        //发布编辑
                        Intent intent = new Intent(getContext(), PushShortVideoActivity.class);
                        intent.putExtra(PushShortVideoActivity.VIDEO_PATH, videoFile.getPath());
                        intent.putExtra(PushShortVideoActivity.VIDEO_COVER_PATH, thumbFile.getCanonicalPath());
                        startActivity(intent);

                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
