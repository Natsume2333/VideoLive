package com.moxin.videoline.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.moxin.videoline.R;
import com.moxin.videoline.modle.GuildModel;
import com.moxin.videoline.utils.Utils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

public class GuildListAdapter extends BaseQuickAdapter<GuildModel, BaseViewHolder> {
    public GuildListAdapter(@Nullable List<GuildModel> data) {
        super(R.layout.item_guild_list, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, GuildModel item) {
        helper.setText(R.id.item_tv_name, item.getName());
        helper.setText(R.id.item_tv_num, item.getNum() + "人");
        Utils.loadHttpImg(item.getLogo(), (ImageView) helper.getView(R.id.iv_logo));
    }
}
