package com.moxin.videoline.ui;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.blankj.utilcode.util.ToastUtils;
import com.moxin.videoline.R;
import com.moxin.videoline.adapter.CuckooGiftCabinetAdapter;
import com.moxin.videoline.api.Api;
import com.moxin.videoline.base.BaseActivity;
import com.moxin.videoline.json.JsonRequestBase;
import com.moxin.videoline.json.JsonRequestGiftCabinetList;
import com.moxin.videoline.modle.CuckooGiftCabinetModel;
import com.moxin.videoline.utils.StringUtils;
import com.lzy.okgo.callback.StringCallback;
import com.maning.imagebrowserlibrary.utils.StatusBarUtil;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Response;

public class CuckooGiftCabinetGiftListActivity extends BaseActivity {

    @BindView(R.id.rv_content_list)
    RecyclerView rv_content_list;

    public static final String TO_USER_ID = "TO_USER_ID";

    private String toUserId;

    private List<CuckooGiftCabinetModel> list = new ArrayList<>();
    private CuckooGiftCabinetAdapter cuckooGiftCabinetAdapter;

    @Override
    protected Context getNowContext() {
        return this;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_cuckoo_gift_cabinet_gift_list;
    }

    @Override
    protected void initView() {
        StatusBarUtil.setColor(this,getResources().getColor(R.color.admin_color),0);
        QMUIStatusBarHelper.setStatusBarLightMode(this);
        getTopBar().setVisibility(View.VISIBLE);
        getTopBar().setTitle("礼物柜");

        GridLayoutManager gridLayoutManager =  new GridLayoutManager(this,3);
        rv_content_list.setLayoutManager(gridLayoutManager);

        cuckooGiftCabinetAdapter = new CuckooGiftCabinetAdapter(list);
        rv_content_list.setAdapter(cuckooGiftCabinetAdapter);
    }

    @Override
    protected void initSet() {
    }

    @Override
    protected void initData() {
        toUserId = getIntent().getStringExtra(TO_USER_ID);
        requestGetDataList();
    }

    @Override
    protected void initPlayerDisplayData() {

    }

    private void requestGetDataList(){
        Api.doRequestGetGiftCabinetList(toUserId,new StringCallback(){

            @Override
            public void onSuccess(String s, Call call, Response response) {

                JsonRequestGiftCabinetList data = (JsonRequestGiftCabinetList) JsonRequestBase.getJsonObj(s,JsonRequestGiftCabinetList.class);
                if(StringUtils.toInt(data.getCode()) == 1){
                    list.clear();
                    list.addAll(data.getGift_list());
                    cuckooGiftCabinetAdapter.notifyDataSetChanged();
                }else{
                    ToastUtils.showLong("获取信息失败！");
                }
            }
        });
    }
}