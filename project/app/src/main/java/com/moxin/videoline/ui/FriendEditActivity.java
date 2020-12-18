package com.moxin.videoline.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.moxin.videoline.R;
import com.moxin.videoline.adapter.FriendGridImageAdapter;
import com.moxin.videoline.adapter.FullyGridLayoutManager;
import com.moxin.videoline.base.BaseActivity;
import com.jaygoo.widget.RangeSeekBar;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class FriendEditActivity extends BaseActivity implements FriendGridImageAdapter.OnItemClickListener {

    @BindView(R.id.friend_add_image_rv)
    RecyclerView friendImageRv;

    @BindView(R.id.location_seek_bar)
    RangeSeekBar locationSeekBar;

    @BindView(R.id.age_seek_bar)
    RangeSeekBar ageSeekBar;

    @BindView(R.id.friend_edit_age_tv)
    TextView ageTv;

    @BindView(R.id.friend_edit_location_tv)
    TextView locationTv;

    private List<LocalMedia> selectList = new ArrayList<>();
    private FriendGridImageAdapter adapter;

    @Override
    protected Context getNowContext() {
        return this;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_friend_edit;
    }

    @Override
    protected void initView() {
        FullyGridLayoutManager manager = new FullyGridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
        friendImageRv.setLayoutManager(manager);

        adapter = new FriendGridImageAdapter(this);
        adapter.setList(selectList);
        friendImageRv.setAdapter(adapter);
        adapter.setOnItemClickListener(this);

        //设置范围
        ageSeekBar.setRange(18, 60);
        locationSeekBar.setRange(0, 60);

        ageSeekBar.setOnRangeChangedListener(new RangeSeekBar.OnRangeChangedListener() {
            @Override
            public void onRangeChanged(RangeSeekBar view, float min, float max, boolean isFromUser) {
                if (isFromUser) {
                    ageTv.setText(Math.round(min) + " - " + Math.round(max));
                }
            }
        });

        locationSeekBar.setOnRangeChangedListener(new RangeSeekBar.OnRangeChangedListener() {
            @Override
            public void onRangeChanged(RangeSeekBar view, float min, float max, boolean isFromUser) {
                if (isFromUser) {
                    locationTv.setText(Math.round(min) + "km");
                }
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
                    selectList = PictureSelector.obtainMultipleResult(data);

                    adapter.setList(selectList);
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
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

    @Override
    public void onItemClick(int position, View v) {
        //选视频 图片
        PictureSelector.create(FriendEditActivity.this)
                .openGallery(PictureMimeType.ofImage())
                .maxSelectNum(1)
                .previewVideo(false)
                .forResult(PictureConfig.CHOOSE_REQUEST);
    }



    @OnClick({R.id.bast_top_bar_cancel})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bast_top_bar_cancel:
                finish();
                break;

            default:
                break;
        }
    }


}
