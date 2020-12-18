package com.moxin.videoline.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.moxin.videoline.R;

import java.util.ArrayList;
import java.util.List;

public class GiftInfoAdapter extends RecyclerView.Adapter {
    private List<String> guardInfoList = new ArrayList<>();
    private Context context;

    public GiftInfoAdapter(List<String> list, Context context) {
        guardInfoList.addAll(list);
        this.context = context;
    }

    public void setData(List<String> list) {
        guardInfoList.clear();
        guardInfoList.addAll(list);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new GuardClassViewHolder(View.inflate(context, R.layout.item_live_chat, null));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        GuardClassViewHolder viewHolder = (GuardClassViewHolder) holder;
        viewHolder.giftInfo.setText(guardInfoList.get(position) + "");
    }

    @Override
    public int getItemCount() {
        return guardInfoList.size();
    }


    public class GuardClassViewHolder extends RecyclerView.ViewHolder {

        private final TextView giftInfo;

        public GuardClassViewHolder(View itemView) {
            super(itemView);

            giftInfo = itemView.findViewById(R.id.gift_info);
        }
    }


}
