package com.moxin.videoline.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.moxin.videoline.R;
import com.moxin.videoline.modle.RechargeVipBean;
import com.moxin.videoline.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class PayWayListAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<RechargeVipBean.PayListBean> paywayList = new ArrayList<>();
    private int selectPosi = 0;

    public PayWayListAdapter(Context context, List<RechargeVipBean.PayListBean> list) {
        this.context = context;
        paywayList.addAll(list);
    }


    public void setSelectPosi(int position) {
        selectPosi = position;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new PayWayListAdapter.PayWayViewHolder(View.inflate(context, R.layout.vip_details_item, null));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        PayWayListAdapter.PayWayViewHolder viewHolder = (PayWayViewHolder) holder;

        final RechargeVipBean.PayListBean payListBean = paywayList.get(position);

        viewHolder.text.setText(payListBean.getPay_name());
        Utils.loadUserIcon(payListBean.getIcon(),viewHolder.icon);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClickListener.onItemClickListener(position,payListBean);

            }
        });






    }


    @Override
    public int getItemCount() {
        return paywayList.size();
    }



    public class PayWayViewHolder extends RecyclerView.ViewHolder {


        private final TextView text;
        private final ImageView icon;

        public PayWayViewHolder(View itemView) {
            super(itemView);

            text = itemView.findViewById(R.id.text);
            text.setTextColor(Color.parseColor("#646464"));
            icon = itemView.findViewById(R.id.icon);
        }
    }

    private ItemClickListener itemClickListener;

    public void setPayWayItemClickListener(ItemClickListener listener) {
        itemClickListener = listener;
    }

    public interface ItemClickListener {
        void onItemClickListener(int position, RechargeVipBean.PayListBean paywayList);
    }

}
