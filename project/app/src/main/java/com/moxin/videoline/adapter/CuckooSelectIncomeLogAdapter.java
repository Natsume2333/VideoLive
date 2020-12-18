package com.moxin.videoline.adapter;

import android.support.annotation.Nullable;

import com.moxin.videoline.R;
import com.moxin.videoline.modle.SelectIncomeLogModel;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

public class CuckooSelectIncomeLogAdapter extends BaseQuickAdapter<SelectIncomeLogModel, BaseViewHolder> {
    public CuckooSelectIncomeLogAdapter(@Nullable List<SelectIncomeLogModel> data) {
        super(R.layout.item_select_income_log, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, SelectIncomeLogModel item) {
        helper.setText(R.id.item_tv_name, item.getUser_nickname() + "(" + item.getUser_id() + ")");
        helper.setText(R.id.item_tv_profit, item.getProfit());
        helper.setText(R.id.item_tv_type, item.getContent());
        helper.setText(R.id.item_tv_time,item.getCreate_time());

    }
}
