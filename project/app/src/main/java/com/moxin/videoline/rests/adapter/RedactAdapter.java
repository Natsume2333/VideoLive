package com.moxin.videoline.rests.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.moxin.videoline.R;
import com.moxin.videoline.modle.UserImgModel;
import com.moxin.videoline.utils.StringUtils;
import com.moxin.videoline.utils.Utils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * 编辑资料页图片适配器
 */

public class RedactAdapter extends BaseQuickAdapter<UserImgModel,BaseViewHolder> {

    private Context context;

    public RedactAdapter(Context context,@Nullable List<UserImgModel> data) {
        super(R.layout.adapter_redact_list, data);
        this.context = context;
    }


    @Override
    protected void convert(BaseViewHolder helper, UserImgModel item) {

        helper.setVisible(R.id.redact_imgbtn,false);

        if(StringUtils.toInt(item.getId()) != 0){

            Utils.loadHttpImg(context,item.getImg(), (ImageView) helper.getView(R.id.redact_imglist));
        }else{
            ImageView imageView = helper.getView(R.id.redact_imglist);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            helper.setImageResource(R.id.redact_imglist,R.drawable.add_image_icon);
        }

    }
}
