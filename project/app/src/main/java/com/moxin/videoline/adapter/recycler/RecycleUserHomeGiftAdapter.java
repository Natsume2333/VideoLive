package com.moxin.videoline.adapter.recycler;

import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.moxin.videoline.R;
import com.moxin.videoline.json.jsonmodle.TargetUserData;
import com.moxin.videoline.utils.Utils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * Created by weipeng on 2018/2/9.
 */

public class RecycleUserHomeGiftAdapter extends BaseQuickAdapter<TargetUserData.GiftBean,BaseViewHolder> {

    private Context context;//上下文

    public RecycleUserHomeGiftAdapter(Context context,@Nullable List<TargetUserData.GiftBean> data) {
        super(R.layout.item_user_home_gift,data);

        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, TargetUserData.GiftBean item) {

        //加载礼物图片
        Utils.loadHttpImg(context,item.getImg(), (ImageView) helper.getView(R.id.item_iv_gift_icon));
    }
}
