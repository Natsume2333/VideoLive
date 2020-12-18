package com.moxin.videoline.ui;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blankj.utilcode.util.ToastUtils;
import com.moxin.videoline.R;
import com.moxin.videoline.base.BaseFragment;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.OnClick;

public class WealthDrawingFragment extends BaseFragment {


    @BindView(R.id.recharge_list)
    RecyclerView recy_recharge;
    private BaseQuickAdapter<String, BaseViewHolder> rechageAdapter;


    @Override
    protected View getBaseView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_drawing, container, false);
    }


    int nowSelRecharge = 0;

    @Override
    protected void initView(View view) {
        recy_recharge.setLayoutManager( new GridLayoutManager(getContext(),3));
        String[] list = new String[6];
        recy_recharge.setAdapter(rechageAdapter=new BaseQuickAdapter<String, BaseViewHolder>(R.layout.recharge_buy_item, Arrays.asList(list)) {
            @Override
            protected void convert(BaseViewHolder helper, String item) {
                helper.setText(R.id.top,"200元");
                helper.setText(R.id.bottom,"2000积分");

                if(nowSelRecharge==helper.getAdapterPosition()){
                    helper.setVisible(R.id.sel_icon,true);
                    helper.getView(R.id.bg).setBackgroundResource(R.drawable.bg_guardbuy_item);
                }else {
                    helper.setVisible(R.id.sel_icon,false);
                    helper.getView(R.id.bg).setBackgroundResource(R.drawable.bg_guardbuy_item_y);
                }
            }
        });
        rechageAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                nowSelRecharge = position;
                adapter.notifyDataSetChanged();
            }
        });



    }


    @OnClick(R.id.to_drawing)
    void checkBind(){
        ToastUtils.showShort("请先点击右上角图标\n绑定账户");
    }

    @Override
    protected void initDate(View view) {

    }

    @Override
    protected void initSet(View view) {

    }

    @Override
    protected void initDisplayData(View view) {

    }

}
