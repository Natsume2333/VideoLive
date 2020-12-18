package com.moxin.videoline.adapter.recycler;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.blankj.utilcode.util.ScreenUtils;
import com.moxin.videoline.CuckooApplication;
import com.moxin.videoline.R;
import com.moxin.videoline.json.jsonmodle.VideoModel;
import com.moxin.videoline.utils.StringUtils;
import com.moxin.videoline.utils.Utils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * Created by 魏鹏 on 2018/3/2.
 *
 * @author 山东布谷鸟网络科技有限公司著
 * @dw 短视频
 */

public class RecycleViewShortVideoAdapter extends BaseQuickAdapter<VideoModel, BaseViewHolder> {

    private Context mContext;
    private int itemHeight;

    public RecycleViewShortVideoAdapter(Context context, @Nullable List<VideoModel> data) {
        super(R.layout.adapter_video_list, data);
        mContext = context;
        itemHeight = ScreenUtils.getScreenWidth() / 2;
    }

    @Override
    protected void convert(BaseViewHolder helper, VideoModel item) {

        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, itemHeight + (itemHeight / 3));
//        params.setMargins(margin, margin, margin, margin);
        helper.getConvertView().setLayoutParams(params);

        //图片背景和标题
        Utils.loadHttpImg(mContext, Utils.getCompleteImgUrl(item.getImg()), (ImageView) helper.getView(R.id.adapter_video_image));
        helper.setText(R.id.adapter_video_title, item.getTitle());
        //数据显示
        helper.setText(R.id.left_start_number, item.getViewed());
        helper.setText(R.id.left_love_number, item.getShare());
        //是否付费


        helper.setVisible(R.id.videolist_masking, item.getStatus().equals("2"));

        ImageView stateIv = helper.getView(R.id.tv_status);
        if (StringUtils.toInt(item.getType()) == 0) {
//            helper.setText(R.id.tv_status, "审核中");
            stateIv.setImageResource(R.mipmap.audit_state_bac);
        } else if (StringUtils.toInt(item.getType()) == 2) {
//            helper.setText(R.id.tv_status, "审核不通过");
            stateIv.setImageResource(R.mipmap.audit_state_pass_bac);
        }

        if (item.getStatus().equals("2")) {
            Utils.loadHttpImgBlue(CuckooApplication.getInstances(), item.getImg(), (ImageView) helper.getView(R.id.adapter_video_image), 0);
        } else {
            Utils.loadHttpImg(CuckooApplication.getInstances(), Utils.getCompleteImgUrl(item.getImg()), (ImageView) helper.getView(R.id.adapter_video_image));
        }

        helper.setVisible(R.id.rl_status, !(StringUtils.toInt(item.getType()) == 1));
    }
}
