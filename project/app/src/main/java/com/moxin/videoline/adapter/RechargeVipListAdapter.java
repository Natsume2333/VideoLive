package com.moxin.videoline.adapter;

import android.support.annotation.Nullable;
import android.view.View;

import com.moxin.videoline.R;
import com.moxin.videoline.modle.RechargeVipBean;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

public class RechargeVipListAdapter extends BaseQuickAdapter<RechargeVipBean.VipRuleBean, BaseViewHolder> {


    public RechargeVipListAdapter(@Nullable List<RechargeVipBean.VipRuleBean> data) {
        super(R.layout.vip_chong_item, data);
    }


    @Override
    protected void convert(final BaseViewHolder helper, final RechargeVipBean.VipRuleBean item) {

        helper.setText(R.id.center, item.getName());
        helper.setText(R.id.top, "¥" + item.getMoney() );
        helper.setText(R.id.bottom, "每日仅需"+item.getDay_money());

        helper.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClickListener.onItemClickListener(helper.getPosition(), item);
            }
        });

        //Utils.loadHttpImg(Utils.getCompleteImgUrl(item.getIcon()), (ImageView) helper.getView(R.id.vip_list_icon_iv));
    }

    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener listener) {
        itemClickListener = listener;
    }

    public interface ItemClickListener {
        void onItemClickListener(int position, RechargeVipBean.VipRuleBean vipRuleBean);
    }

}
