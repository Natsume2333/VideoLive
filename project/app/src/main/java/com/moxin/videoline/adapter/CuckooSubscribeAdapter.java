package com.moxin.videoline.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.moxin.videoline.R;
import com.moxin.videoline.helper.SelectResHelper;
import com.moxin.videoline.modle.CuckooSubscribeModel;
import com.moxin.videoline.utils.StringUtils;
import com.moxin.videoline.utils.Utils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

public class CuckooSubscribeAdapter extends BaseQuickAdapter<CuckooSubscribeModel, BaseViewHolder> {
    private int action = 1;

    public CuckooSubscribeAdapter(@Nullable List<CuckooSubscribeModel> data, int action) {
        super(R.layout.item_subscribe_list, data);
        this.action = action;
    }

    @Override
    protected void convert(BaseViewHolder helper, final CuckooSubscribeModel item) {
        Utils.loadHttpImg(item.getAvatar(), (ImageView) helper.getView(R.id.item_iv_avatar));
        helper.setText(R.id.item_tv_name, item.getUser_nickname());
        helper.setText(R.id.item_tv_time, item.getCreate_time());
        helper.setText(R.id.item_tv_status, item.getStatus_msg());

        helper.setImageResource(R.id.pagemsg_view_dian, SelectResHelper.getOnLineRes(StringUtils.toInt(item.getIs_online())));
        helper.setText(R.id.pagemsg_view_isonline, StringUtils.toInt(item.getIs_online()) == 1 ? "在线" : "离线");

        //取消预约布局
        LinearLayout cancelSubScribeLl = helper.getView(R.id.subscribe_cancel_ll);
        //是客户 并且超时才会显示按钮
        if (action == 1 && item.getType() == 1) {
            cancelSubScribeLl.setVisibility(View.VISIBLE);

            cancelSubScribeLl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new MaterialDialog.Builder(mContext)
                            .content("是否取消预约？")
                            .positiveText(R.string.agree)
                            .negativeText(R.string.disagree)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    cancelSubScribeListener.onCancelSubScribeListener(item.getId());
                                }
                            })
                            .show();


                }
            });
        } else {
            cancelSubScribeLl.setVisibility(View.GONE);
        }

    }


    private CancelSubScribeListener cancelSubScribeListener;

    public void setCancelSubScribeListener(CancelSubScribeListener listener) {
        cancelSubScribeListener = listener;
    }

    public interface CancelSubScribeListener {
        void onCancelSubScribeListener(String id);
    }
}
