package com.moxin.videoline.fragment;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.moxin.videoline.adapter.recycler.RecyclerRecommendAdapter;
import com.moxin.videoline.api.Api;
import com.moxin.videoline.base.BaseListFragment;
import com.moxin.videoline.inter.JsonCallback;
import com.moxin.videoline.json.JsonRequestBase;
import com.moxin.videoline.json.JsonRequestsPeople;
import com.moxin.videoline.json.jsonmodle.TargetUserData;
import com.moxin.videoline.ui.common.Common;
import com.chad.library.adapter.base.BaseQuickAdapter;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 新人
 * Created by wp on 2017/12/28 0028.
 */
public class NewPeopleFragment extends BaseListFragment<TargetUserData> {

    @Override
    protected void initDate(View view) {

    }

    @Override
    public void fetchData() {

        //加载数据源
        requestGetData(false);
    }

    @Override
    protected void initSet(View view) {

    }

    @Override
    protected void initDisplayData(View view) {

    }

    @Override
    protected RecyclerView.LayoutManager getLayoutManage() {
        return new GridLayoutManager(getContext(),2);
    }

    @Override
    protected BaseQuickAdapter getBaseQuickAdapter() {
        return new RecyclerRecommendAdapter(getContext(),dataList);
    }

    @Override
    protected void requestGetData(boolean isCache) {
        Api.getNewPeoplePageList(
                uId,
                uToken,
                page,
                new JsonCallback() {
                    @Override
                    public Context getContextToJson() {
                        return getContext();
                    }

                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        Log.e("getNewPeoplePageList",s);
                        JsonRequestsPeople requestObj = (JsonRequestsPeople) JsonRequestBase.getJsonObj(s,JsonRequestsPeople.class);
                        if (requestObj.getCode() == 1){
                            onLoadDataResult(requestObj.getData());
                        }else{
                            onLoadDataError();
                            showToastMsg(getContext(),requestObj.getMsg());
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        onLoadDataError();
                    }
                }
        );
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

        Common.jumpUserPage(getContext(),dataList.get(position).getId());
    }

}
