package com.moxin.videoline.adapter;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.moxin.videoline.R;
import com.moxin.videoline.modle.SystemMessageModel;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

public class SystemMessageAdapter extends BaseQuickAdapter<SystemMessageModel,BaseViewHolder>{
    public SystemMessageAdapter(@Nullable List<SystemMessageModel> data) {
        super(R.layout.item_system_message,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, SystemMessageModel item) {

        helper.setText(R.id.item_tv_title,item.getTitle());
        helper.setText(R.id.item_tv_content,item.getCentent());
        helper.setText(R.id.item_tv_time,item.getAddtime());
        if(TextUtils.isEmpty(item.getUrl())){
            helper.setVisible(R.id.item_tv_url,false);
        }else{

            helper.setVisible(R.id.item_tv_url,true);
        }
        helper.addOnClickListener(R.id.item_tv_url);
    }
}
