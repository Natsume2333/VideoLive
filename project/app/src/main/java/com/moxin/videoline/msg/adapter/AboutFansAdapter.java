package com.moxin.videoline.msg.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.moxin.videoline.R;
import com.moxin.videoline.api.ApiUtils;
import com.moxin.videoline.json.jsonmodle.AboutAndFans;
import com.moxin.videoline.utils.StringUtils;
import com.moxin.videoline.utils.Utils;
import com.moxin.videoline.widget.BGLevelTextView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * 关注和粉丝页适配器适配器
 */

public class AboutFansAdapter extends BaseQuickAdapter<AboutAndFans, BaseViewHolder> {
    List<AboutAndFans> aboutAndFans = new ArrayList<>();
    int type = 0;//状态,0默认关注,1粉丝
    private Context context;

    public AboutFansAdapter(Context context, int type, @Nullable List<AboutAndFans> data) {
        super(R.layout.adapter_aboutandfanse, data);
        this.aboutAndFans = data;
        this.type = type;
        this.context = context;
    }


    @Override
    protected void convert(BaseViewHolder helper, AboutAndFans item) {

        if (ApiUtils.isTrueUrl(item.getAvatar())) {
            Utils.loadHttpImg(Utils.getCompleteImgUrl(item.getAvatar()), (ImageView) helper.getView(R.id.aboutandans_img));
        }

        TextView fansLoveTv = helper.getView(R.id.aboutandfans_loveme);
        fansLoveTv.setVisibility(View.VISIBLE);
        if (StringUtils.toInt(item.getFocus()) == 1) {
            fansLoveTv.setText("取消关注");
            fansLoveTv.setBackgroundResource(R.drawable.yellow_white_fifty_corners_bac);
            fansLoveTv.setTextColor(context.getResources().getColor(R.color.black));
        } else {
            fansLoveTv.setText("  关注  ");
            fansLoveTv.setBackgroundResource(R.drawable.yellow_fifty_corners_bac);
            fansLoveTv.setTextColor(context.getResources().getColor(R.color.white));
        }
        //((ViewGroup)helper.getView(R.id.aboutandans_nowgrade)).addView(new GradeShowLayout(context,item.getLevel(), StringUtils.toInt(item.getSex())));
        BGLevelTextView tv_level = helper.getView(R.id.tv_level);
        tv_level.setLevelInfo(StringUtils.toInt(item.getSex()), item.getLevel());

        helper.addOnClickListener(R.id.aboutandans_img);
        helper.addOnClickListener(R.id.aboutandfans_loveme);
        helper.setText(R.id.aboutandans_text, item.getUser_nickname());
    }


    //获取数据的数量
    @Override
    public int getItemCount() {
        return aboutAndFans.size();
    }
}
