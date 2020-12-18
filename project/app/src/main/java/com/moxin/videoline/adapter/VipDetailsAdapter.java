package com.moxin.videoline.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.moxin.videoline.R;
import com.moxin.videoline.modle.VipDetailsModel;
import com.moxin.videoline.utils.Utils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

public class VipDetailsAdapter extends BaseQuickAdapter<VipDetailsModel, BaseViewHolder> {
    public VipDetailsAdapter(@Nullable List<VipDetailsModel> data) {
        super(R.layout.vip_details_item, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, VipDetailsModel item) {
        Utils.loadUserIcon(item.getImg(), (ImageView) helper.getView(R.id.icon));
        helper.setText(R.id.text, item.getCenter());
    }
}
