package com.moxin.videoline.adapter.recycler;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.moxin.videoline.CuckooApplication;
import com.moxin.videoline.R;
import com.moxin.videoline.json.jsonmodle.VideoModel;
import com.moxin.videoline.utils.Utils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * recycler-video-small适配器
 */

public class RecyclerVideoSmallAdapter extends BaseQuickAdapter<VideoModel, BaseViewHolder> {
    private int dp1;
    private int margin;
    private int itemWidth;
    private int itemHeight;

    public RecyclerVideoSmallAdapter(@Nullable List<VideoModel> data) {
        super(R.layout.adapter_video_list, data);

        dp1 = ConvertUtils.dp2px(1);
        margin = dp1 * 6;
        itemHeight = ScreenUtils.getScreenWidth() / 2;
    }

    @Override
    protected void convert(BaseViewHolder helper, VideoModel item) {

        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, itemHeight + (itemHeight / 3));
//        params.setMargins(margin, margin, margin, margin);
        helper.getConvertView().setLayoutParams(params);

        //图片背景和标题

        helper.setText(R.id.adapter_video_title, item.getTitle());
        //数据显示
        helper.setText(R.id.left_start_number, item.getViewed());
        helper.setText(R.id.left_love_number, item.getFollow_num());
        //是否付费
        helper.setGone(R.id.videolist_masking, item.getStatus().equals("2"));

        if (item.getStatus().equals("2")) {
            Utils.loadHttpImgBlue(CuckooApplication.getInstances(), item.getImg(), (ImageView) helper.getView(R.id.adapter_video_image), 0);
        } else {
            Utils.loadHttpImg(CuckooApplication.getInstances(), Utils.getCompleteImgUrl(item.getImg()), (ImageView) helper.getView(R.id.adapter_video_image));
        }

        View bg = helper.getView(R.id.onclick_bg);
        int p = ConvertUtils.dp2px(1);

        if ((helper.getAdapterPosition() + 1) % 2 == 0) {
            bg.setPadding(p, p, 0, 0);
        } else {
            bg.setPadding(0, p, p, 0);
        }
    }
}
