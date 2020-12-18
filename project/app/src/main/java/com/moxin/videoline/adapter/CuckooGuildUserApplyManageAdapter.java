package com.moxin.videoline.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.moxin.videoline.R;
import com.moxin.videoline.modle.GuildUserModel;
import com.moxin.videoline.utils.Utils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

public class CuckooGuildUserApplyManageAdapter extends BaseQuickAdapter<GuildUserModel, BaseViewHolder> {
    public CuckooGuildUserApplyManageAdapter(@Nullable List<GuildUserModel> data) {
        super(R.layout.item_guild_user_apply_list, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, GuildUserModel item) {
        helper.setText(R.id.item_tv_name, item.getUser_nickname());
        Utils.loadHttpImg(item.getAvatar(), (ImageView) helper.getView(R.id.item_iv_avatar));
    }
}
