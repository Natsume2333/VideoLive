package com.moxin.videoline.ui;

import android.util.Log;
import android.view.View;

import com.moxin.chat.utils.TimeUtil;
import com.moxin.videoline.R;
import com.moxin.videoline.api.Api;
import com.moxin.videoline.base.BaseListFragment;
import com.moxin.videoline.modle.DrawingInfoBean;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.Gson;
import com.lzy.okgo.callback.StringCallback;

import okhttp3.Call;
import okhttp3.Response;

public class InviteDrawingListFragment extends BaseListFragment<DrawingInfoBean.DataBean> {


    @Override
    protected BaseQuickAdapter getBaseQuickAdapter() {
        return new BaseQuickAdapter<DrawingInfoBean.DataBean,BaseViewHolder>(R.layout.wealth_sr_details_item) {
            @Override
            protected void convert(BaseViewHolder helper, DrawingInfoBean.DataBean item) {
                helper.setText(R.id.sr_title,"提现"+item.getCoin()+"元")
                        .setText(R.id.sr_time, TimeUtil.format(item.getAddtime()))
                        .setText(R.id.sr_num,"1".equals(item.getType())?"支付宝":"微信");
                //0审核中1审核通过2拒绝提现
                String status="";
                switch (item.getStatus()){
                    case "0": status="审核中"; break;
                    case "1": status="已提现"; break;
                    case "2": status="拒绝提现"; break;
                }
                helper.setText(R.id.sr_type,status);
            }
        };
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

    }



    @Override
    protected void requestGetData(boolean isCache) {
        super.requestGetData(isCache);
        Api.getDrawingInfo(page, new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                Log.i("提现记录", "onSuccess: "+s);
                DrawingInfoBean bean = new Gson().fromJson(s,DrawingInfoBean.class);
                if(bean.getCode()==1){
                    onLoadDataResult(bean.getData());
                }
            }
        });
    }

    @Override
    protected void init(View view) {
        super.init(view);
        requestGetData(false);
    }
}
