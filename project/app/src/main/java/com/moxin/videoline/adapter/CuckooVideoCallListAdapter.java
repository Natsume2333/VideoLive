package com.moxin.videoline.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.moxin.videoline.R;
import com.moxin.videoline.modle.CuckooVideoCallListModel;
import com.moxin.videoline.utils.Utils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

public class CuckooVideoCallListAdapter extends BaseQuickAdapter<CuckooVideoCallListModel,BaseViewHolder>{
    public CuckooVideoCallListAdapter(@Nullable List data) {
        super(R.layout.item_video_call_list,data);
    }


    @Override
    protected void convert(BaseViewHolder helper, CuckooVideoCallListModel item) {
        Utils.loadHttpImg(item.getAvatar(), (ImageView) helper.getView(R.id.item_iv_avatar));
        helper.setText(R.id.item_tv_name,item.getUser_nickname());
        helper.setText(R.id.item_tv_status,item.getCreate_time());
    }
}
