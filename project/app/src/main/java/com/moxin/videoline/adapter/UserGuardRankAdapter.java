package com.moxin.videoline.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.moxin.videoline.R;
import com.moxin.videoline.modle.GuardRankBean;
import com.moxin.videoline.utils.Utils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * Created by 魏鹏 on 2018/3/19.
 * email:1403102936@qq.com
 * 山东布谷鸟网络科技有限公司著
 */

public class UserGuardRankAdapter extends BaseQuickAdapter<GuardRankBean.DataBean.ListBean, BaseViewHolder> {

    private Context context;

    public UserGuardRankAdapter(Context context, @Nullable List<GuardRankBean.DataBean.ListBean> data) {
        super(R.layout.guard_rank_item, data);
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, GuardRankBean.DataBean.ListBean charmData) {

        //数据绑定
        ImageView img = (ImageView) helper.getView(R.id.guard_icon);
        Utils.loadUserIcon(context, Utils.getCompleteImgUrl(charmData.getAvatar()), img);
        helper.setText(R.id.guard_name, charmData.getUser_nickname());
        helper.setText(R.id.guard_gxz, "贡献值:" + charmData.getGift_count());
        helper.setText(R.id.guard_start, helper.getPosition() + 1 + "");

    }
}
