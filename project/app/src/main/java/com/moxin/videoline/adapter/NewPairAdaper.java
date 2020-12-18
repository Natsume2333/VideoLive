package com.moxin.videoline.adapter;

import android.content.Context;

import com.moxin.videoline.R;
import com.moxin.videoline.modle.PairBean;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

public class NewPairAdaper extends BaseQuickAdapter<PairBean.ListBean, BaseViewHolder> {
    private Context mContext;

    public NewPairAdaper(Context context, List<PairBean.ListBean> data) {
        super(R.layout.item_pair_layout, data);
        this.mContext = context;
    }

    @Override
    protected void convert(final BaseViewHolder helper, final PairBean.ListBean item) {
        helper.setText(R.id.right_name, item.getUser_nicename() + "");

//        Utils.loadImageForImgView(mContext, (CircleImageView) helper.getView(R.id.im_left_menu_avatar), item.getAvatar(), 0);
//
//        String str = StringUtils.timeStamp2Date(item.getEdittime(), "yyyy-MM-dd HH:mm:ss");
//        String time = StringUtils.friendly_time(str);
//
//        if (!TextUtils.isEmpty(item.getLastMessage())) {
//            helper.setText(R.id.tv_item_last_msg, item.getLastMessage());
//        } else {
//            helper.setText(R.id.tv_item_last_msg, time + "");
//        }
//
//
//        helper.getView(R.id.pair_item_rl).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                itemClickListener.onItenClickListener(helper.getPosition());
//            }
//        });
//
//        helper.getView(R.id.im_left_menu_avatar).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(AppContext.context(), OtherUserCenterActivity.class);
//                intent.putExtra("touid", item.getUid());
//                AppContext.context().startActivity(intent);
//            }
//        });



    }


    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener listener) {
        itemClickListener = listener;
    }

    public interface ItemClickListener {
        void onItenClickListener(int position);

    }

}
