package com.moxin.videoline.ui.dialog;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.moxin.videoline.R;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

public class QuickDialog extends BaseDialog {

    public QuickDialog(Context context) {
        super(context);
    }

    @BindView(R.id.recy)
    RecyclerView recy;
    BaseQuickAdapter adapter;
    ArrayList<String> list;

    @Override
    public void init() {
        super.init();
        list = new ArrayList<>();
        setTrans();
        recy.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false));
        recy.setAdapter(adapter=new BaseQuickAdapter<String, BaseViewHolder>(R.layout.quick_dialog_item,list) {
            @Override
            protected void convert(BaseViewHolder helper, String item) {
                    helper.setText(R.id.tv,item);
                    //helper.setVisible(R.id.split,helper.getAdapterPosition()==list.size()-1);
            }
        });
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                callBack.onClick(list.get(position),position);
            }
        });
    }

    public QuickDialog add(String item){
        list.add(item);
        return this;
    }

    CallBack callBack;
    public QuickDialog addCallBack(CallBack call){
        callBack = call;
        return this;
    }

    public interface CallBack{
        void onClick(String item,int pos);
    }


    @Override
    public void show() {
        super.show();
        adapter.notifyDataSetChanged();
        setWith(0.8f);
    }

    @OnClick(R.id.close)
    @Override
    public void hide() {
        super.hide();
    }


    @Override
    public int setRes() {
        return R.layout.quick_dialog_layout;
    }
}
