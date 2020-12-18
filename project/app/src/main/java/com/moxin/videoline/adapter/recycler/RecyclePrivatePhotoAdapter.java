package com.moxin.videoline.adapter.recycler;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.blankj.utilcode.util.ScreenUtils;
import com.moxin.videoline.R;
import com.moxin.videoline.modle.PrivatePhotoModel;
import com.moxin.videoline.utils.StringUtils;
import com.moxin.videoline.utils.Utils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * Created by weipeng on 2018/2/24.
 */

public class RecyclePrivatePhotoAdapter extends BaseQuickAdapter<PrivatePhotoModel, BaseViewHolder> {

    private Context context;
    private int itemWidth;

    public RecyclePrivatePhotoAdapter(@Nullable Context context, @Nullable List<PrivatePhotoModel> data) {
        super(R.layout.item_private_photo, data);
        this.context = context;
        itemWidth = ScreenUtils.getScreenWidth() / 3;
    }

    @Override
    protected void convert(BaseViewHolder helper, PrivatePhotoModel item) {

        ImageView ivPhoto = (ImageView) helper.getView(R.id.iv_photo);
        View view = helper.getConvertView();
        view.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, itemWidth));

        Utils.loadHttpImg(context, Utils.getCompleteImgUrl(item.getImg()), ivPhoto);

        ImageView stateIv = helper.getView(R.id.tv_status);
        if (StringUtils.toInt(item.getStatus()) == 0) {
//            helper.setText(R.id.tv_status, "审核中");
            stateIv.setVisibility(View.VISIBLE);
            stateIv.setImageResource(R.mipmap.audit_state_bac);
        } else if (StringUtils.toInt(item.getStatus()) == 2) {
//            helper.setText(R.id.tv_status, "审核不通过");
            stateIv.setVisibility(View.VISIBLE);
            stateIv.setImageResource(R.mipmap.audit_state_pass_bac);
        }

        helper.setVisible(R.id.rl_status, !(StringUtils.toInt(item.getStatus()) == 1));

    }
}
