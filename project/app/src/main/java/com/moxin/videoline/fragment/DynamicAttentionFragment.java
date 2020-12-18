package com.moxin.videoline.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;

import com.moxin.videoline.R;
import com.moxin.videoline.adapter.DynamicAdapter;
import com.moxin.videoline.api.Api;
import com.moxin.videoline.base.BaseListFragment;
import com.moxin.videoline.event.RefreshMessageEvent;
import com.moxin.videoline.json.JsonRequestBase;
import com.moxin.videoline.json.JsonRequestDoGetDynamicList;
import com.moxin.videoline.manage.SaveData;
import com.moxin.videoline.modle.DynamicListModel;
import com.moxin.videoline.ui.DynamicDetailActivity;
import com.moxin.videoline.ui.common.Common;
import com.moxin.videoline.utils.DialogHelp;
import com.moxin.videoline.utils.StringUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.callback.StringCallback;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 动态 关注
 */
public class DynamicAttentionFragment extends BaseListFragment<DynamicListModel> implements BaseQuickAdapter.OnItemClickListener, BaseQuickAdapter.OnItemChildClickListener {

    @Override
    protected void initSet(View view) {

    }

    @Override
    protected void initDisplayData(View view) {

    }

    @Override
    protected boolean isRegEvent() {
        return true;
    }

    @Override
    protected BaseQuickAdapter getBaseQuickAdapter() {
        return new DynamicAdapter(getContext(), dataList);
    }

    @Override
    public void onResume() {
        super.onResume();
        requestGetData(false);
    }


    @Override
    protected void requestGetData(boolean isCache) {
        Api.doRequestGetLoveDynamicList(SaveData.getInstance().getId(), SaveData.getInstance().getToken(), page, new StringCallback() {

            @Override
            public void onSuccess(String s, Call call, Response response) {
                JsonRequestDoGetDynamicList data = (JsonRequestDoGetDynamicList) JsonRequestBase.getJsonObj(s, JsonRequestDoGetDynamicList.class);
                if (StringUtils.toInt(data.getCode()) == 1) {
                    onLoadDataResult(data.getList());
                } else {
                    mSwRefresh.setRefreshing(false);
                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                mSwRefresh.setRefreshing(false);
            }
        });
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        Intent intent = new Intent(getContext(), DynamicDetailActivity.class);
        intent.putExtra(DynamicDetailActivity.DYNAMIC_DATA, dataList.get(position));
        startActivity(intent);
    }

    @Override
    public void onItemChildClick(final BaseQuickAdapter adapter, View view, final int position) {
        if (view.getId() == R.id.item_iv_like_count) {
            Api.doRequestDynamicLike(SaveData.getInstance().getId(), SaveData.getInstance().getToken(), dataList.get(position).getId(), new StringCallback() {

                @Override
                public void onSuccess(String s, Call call, Response response) {

                    JsonRequestBase data = JsonRequestBase.getJsonObj(s, JsonRequestBase.class);
                    if (StringUtils.toInt(data.getCode()) == 1) {
                        if (StringUtils.toInt(dataList.get(position).getIs_like()) == 1) {
                            dataList.get(position).setIs_like("0");
                            dataList.get(position).decLikeCount(1);
                        } else {
                            dataList.get(position).setIs_like("1");
                            dataList.get(position).plusLikeCount(1);
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
            });
        } else if (view.getId() == R.id.item_del) {

            DialogHelp.getConfirmDialog(getContext(), "确定要删除动态？", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    clickDelDynamic(position);
                }
            }).show();
        } else if (view.getId() == R.id.item_tv_chat) {
            Common.startPrivatePage(getContext(), dataList.get(position).getUserInfo().getId());
        } else if (view.getId() == R.id.item_iv_avatar) {
            Common.jumpUserPage(getContext(), dataList.get(position).getUserInfo().getId());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(RefreshMessageEvent messageEvent) {
        if (messageEvent.getMessage().equals("refresh_dynamic_list")) {
            page = 1;
            requestGetData(false);
        }
    }

    private void clickDelDynamic(final int position) {

        showLoadingDialog("正在操作...");
        Api.doRequestDelDynamic(SaveData.getInstance().getId(), SaveData.getInstance().getToken(), dataList.get(position).getId(), new StringCallback() {

            @Override
            public void onSuccess(String s, Call call, Response response) {

                hideLoadingDialog();
                JsonRequestBase data = JsonRequestBase.getJsonObj(s, JsonRequestBase.class);
                if (StringUtils.toInt(data.getCode()) == 1) {

                    dataList.remove(position);
                    baseQuickAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                hideLoadingDialog();
            }
        });
    }
}