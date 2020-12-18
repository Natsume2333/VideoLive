package com.moxin.videoline.adapter.recycler;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.moxin.videoline.R;
import com.moxin.videoline.modle.RankModel;
import com.moxin.videoline.utils.Utils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * RecyclerView-new people page适配器
 */
public class RecyclerRankingAdapter extends BaseQuickAdapter<RankModel, BaseViewHolder> {

    private int type = 0;//1财气,2魅力
    private Context context;

    public RecyclerRankingAdapter(@Nullable List<RankModel> data, Context context, int type) {
        super(R.layout.rank_main_item, data);
        this.context = context;
        this.type = type;
    }

    @Override
    protected void convert(BaseViewHolder helper, RankModel charmData) {


        //数据绑定
        Utils.loadUserIcon(context, Utils.getCompleteImgUrl(charmData.getAvatar()), (ImageView) helper.getView(R.id.icon));
        helper.setImageResource(R.id.online_state, "1".equals(charmData.getIs_online()) ? R.mipmap.on_line : R.mipmap.not_online);
        helper.setText(R.id.name, charmData.getUser_nickname());
        TextView man = helper.getView(R.id.vip_man);
        TextView woman = helper.getView(R.id.vip_woman);
        if ("2".equals(charmData.getSex())) {//女
            man.setVisibility(View.GONE);
            woman.setVisibility(View.VISIBLE);
            woman.setText(charmData.getLevel());
        } else {
            woman.setVisibility(View.GONE);
            man.setVisibility(View.VISIBLE);
            man.setText(charmData.getLevel());
        }
        helper.setText(R.id.address, charmData.getAddress());
        helper.setText(R.id.coin, charmData.getTotal());
        helper.setText(R.id.num, charmData.getOrder_num());
//        //数据绑定
//        if (ApiUtils.isTrueUrl(charmData.getAvatar())){
//            Utils.loadUserIcon(Utils.getCompleteImgUrl(charmData.getAvatar()), (ImageView) helper.getView(R.id.people_img));
//
//        }
//
//        helper.setBackgroundRes(R.id.newpeople_bar_isonLine,SelectResHelper.getOnLineRes(StringUtils.toInt(charmData.getIs_online())));
//        helper.setText(R.id.newpeople_bar_title,charmData.getUser_nickname());
//        helper.setVisible(R.id.people_img_masking, StringUtils.toInt(charmData.getUser_status()) == 1);
//        helper.setText(R.id.text_number,charmData.getOrder_num());
//        helper.setText(R.id.money_new_text,charmData.getTotal());
//        helper.setText(R.id.newpeople_bar_location_text,charmData.getAddress());
//        helper.setVisible(R.id.money_new_img,true);
//
//        //((FrameLayout)helper.getView(R.id.newpeople_bar_nowgrade)).addView(new GradeShowLayout(helper.getView(R.id.newpeople_bar_nowgrade).getContext(),charmData.getLevel(),Integer.valueOf(charmData.getSex())));
//
//        if (type == 1){
//            //财气
//            helper.setImageResource(R.id.money_new_img,R.drawable.chat_coins);
//            helper.setTextColor(R.id.money_new_text,context.getResources().getColor(R.color.orange));
//            helper.setImageResource(R.id.newpeople_bar_location,R.drawable.location_hint_male);
//
//            helper.setBackgroundRes(R.id.tv_level,R.drawable.bg_org_num);
//            helper.setText(R.id.tv_level,"V " + charmData.getLevel());
//        }
//        if (type == 2){
//            //魅力
//            helper.setImageResource(R.id.money_new_img,R.drawable.integral);
//            helper.setTextColor(R.id.money_new_text,context.getResources().getColor(R.color.admin_color));
//            helper.setImageResource(R.id.newpeople_bar_location,R.drawable.location_hint_female);
//
//            helper.setBackgroundRes(R.id.tv_level,R.drawable.bg_main_color_num);
//            helper.setText(R.id.tv_level,"M " + charmData.getLevel());
//        }

    }
}