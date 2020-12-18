package com.moxin.videoline.adapter.recycler;

import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.moxin.videoline.R;
import com.moxin.videoline.json.jsonmodle.VideoModel;
import com.moxin.videoline.utils.StringUtils;
import com.moxin.videoline.utils.Utils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * Created by weipeng on 2018/2/9.
 */

public class RecycleUserHomeVideoAdapter extends BaseQuickAdapter<VideoModel,BaseViewHolder> {

    private Context context;//上下文

    public RecycleUserHomeVideoAdapter(Context context, @Nullable List<VideoModel> data) {
        super(R.layout.item_user_home_video,data);

        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, VideoModel item) {

        //加载是视频封面图片
        Utils.loadHttpImg(context,Utils.getCompleteImgUrl(item.getImg()), (ImageView) helper.getView(R.id.item_iv_video_icon));
        helper.setVisible(R.id.tv_pay, StringUtils.toInt(item.getStatus()) == 2);
    }
}
