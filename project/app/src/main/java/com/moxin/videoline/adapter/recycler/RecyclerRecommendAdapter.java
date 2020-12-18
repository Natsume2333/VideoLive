package com.moxin.videoline.adapter.recycler;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.moxin.videoline.R;
import com.moxin.videoline.api.ApiUtils;
import com.moxin.videoline.json.jsonmodle.TargetUserData;
import com.moxin.videoline.modle.ConfigModel;
import com.moxin.videoline.utils.StringUtils;
import com.moxin.videoline.utils.Utils;
import com.moxin.videoline.widget.BGLevelTextView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * RecyclerView-recommendPage适配器
 * Created by wp on 2017/12/28 0028.
 */
public class RecyclerRecommendAdapter extends BaseQuickAdapter<TargetUserData, BaseViewHolder> {
    private Context context;
    private int itemWidth;
    private int itemHeight;
    private int dp1;
    private int margin;
    //private final int margin;

    //构造方法,用于传入数据参数
    public RecyclerRecommendAdapter(Context context, @Nullable List<TargetUserData> data) {
        super(R.layout.adapter_user, data);
        this.context = context;

        dp1 = ConvertUtils.dp2px(1);
        margin = dp1 * 6;
        itemWidth = ScreenUtils.getScreenWidth() / 2 - dp1 * 8;
    }

    @Override
    protected void convert(BaseViewHolder helper, TargetUserData item) {
        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, itemWidth + (itemWidth / 3));
        params.setMargins(margin, margin, margin, margin);
        helper.getConvertView().setLayoutParams(params);

        //初始化数据显示
        if (ApiUtils.isTrueUrl(item.getAvatar())) {
            Utils.loadImg(Utils.getCompleteImgUrl(item.getAvatar()), (ImageView) helper.getView(R.id.pagemsg_background));
        }
        helper.setImageResource(R.id.pagemsg_view_dian, StringUtils.toInt(item.getIs_online()) == 1 ? R.mipmap.on_line : R.mipmap.not_online);
        helper.setImageResource(R.id.pagemsg_view_isvip, StringUtils.toInt(item.getIs_vip()) == 1 ? R.mipmap.vip_image_bac : 0);
        helper.setText(R.id.pagemsg_view_name, item.getUser_nickname());

        if (StringUtils.toInt(item.getSex()) == 2) {
            helper.setText(R.id.pagemsg_view_nice, item.getCharging_coin() + ConfigModel.getInitData().getCurrency_name() + "/分钟");
        }

        if (!TextUtils.isEmpty(item.getSign())) {
            helper.setText(R.id.pagemsg_view_sign, item.getSign());
        } else {
            helper.setText(R.id.pagemsg_view_sign, "暂未设置签名");
        }

        ((BGLevelTextView) helper.getView(R.id.tv_level)).setLevelInfo(item.getSex(), item.getLevel());
    }

}