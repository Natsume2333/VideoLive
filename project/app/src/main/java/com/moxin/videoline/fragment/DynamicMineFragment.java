package com.moxin.videoline.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blankj.utilcode.util.ToastUtils;
import com.moxin.videoline.ApiConstantDefine;
import com.moxin.videoline.R;
import com.moxin.videoline.adapter.DynamicAdapter;
import com.moxin.videoline.api.Api;
import com.moxin.videoline.base.BaseFragment;
import com.moxin.videoline.dialog.ShowPayPhotoDialog;
import com.moxin.videoline.event.RefreshMessageEvent;
import com.moxin.videoline.json.JsonRequestBase;
import com.moxin.videoline.json.JsonRequestDoGetDynamicList;
import com.moxin.videoline.json.JsonRequestSelectPic;
import com.moxin.videoline.manage.SaveData;
import com.moxin.videoline.modle.DynamicListModel;
import com.moxin.videoline.ui.DynamicDetailActivity;
import com.moxin.videoline.ui.DynamicImagePreviewActivity;
import com.moxin.videoline.utils.DialogHelp;
import com.moxin.videoline.utils.StringUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 动态 关注
 */
public class DynamicMineFragment extends BaseFragment implements BaseQuickAdapter.OnItemClickListener, BaseQuickAdapter.OnItemChildClickListener, DynamicAdapter.OnImgClickListener {
    private RecyclerView mRvContentList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private int page = 1;
    private List<DynamicListModel> list = new ArrayList<>();
    private DynamicAdapter dynamicAdapter;

    @Override
    protected View getBaseView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_dynamic, container, false);
    }

    @Override
    protected void initView(View view) {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        mRvContentList = view.findViewById(R.id.rv_content_list);
        swipeRefreshLayout = view.findViewById(R.id.sw_refresh);

        //layoutManger
        mRvContentList.setLayoutManager(new LinearLayoutManager(getContext()));
        swipeRefreshLayout.setOnRefreshListener(this);

        dynamicAdapter = new DynamicAdapter(getContext(), list);
        mRvContentList.setAdapter(dynamicAdapter);
        dynamicAdapter.setOnImgClickListener(this);
        dynamicAdapter.setOnItemClickListener(this);
        dynamicAdapter.setOnItemChildClickListener(this);
        dynamicAdapter.setOnLoadMoreListener(this, mRvContentList);
        dynamicAdapter.disableLoadMoreIfNotFullPage();
        dynamicAdapter.setEmptyView(R.layout.empt_data_layout);


    }

    @Override
    protected void initDate(View view) {
        requestGetData();
    }

    @Override
    protected void initSet(View view) {

    }

    @Override
    protected void initDisplayData(View view) {

    }

    @Override
    public void onRefresh() {
        page = 1;
        requestGetData();
    }

    @Override
    public void onLoadMoreRequested() {
        page++;
        requestGetData();
    }


    private void requestGetData() {

        Api.doRequestGetMyDynamicList(SaveData.getInstance().getId(), SaveData.getInstance().getToken(), page, new StringCallback() {

            @Override
            public void onSuccess(String s, Call call, Response response) {
                swipeRefreshLayout.setRefreshing(false);

                JsonRequestDoGetDynamicList data = (JsonRequestDoGetDynamicList) JsonRequestBase.getJsonObj(s, JsonRequestDoGetDynamicList.class);
                if (StringUtils.toInt(data.getCode()) == 1) {
                    if (page == 1) {
                        list.clear();
                    }

                    list.addAll(data.getList());
                    if (data.getList().size() == 0) {
                        dynamicAdapter.loadMoreEnd();
                    } else {
                        dynamicAdapter.loadMoreComplete();
                    }

                    dynamicAdapter.notifyDataSetChanged();
                } else {
                    ToastUtils.showLong(data.getMsg());
                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        Intent intent = new Intent(getContext(), DynamicDetailActivity.class);
        intent.putExtra(DynamicDetailActivity.DYNAMIC_DATA, list.get(position));
        startActivity(intent);
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, final int position) {
        if (view.getId() == R.id.item_iv_like_count) {
            Api.doRequestDynamicLike(SaveData.getInstance().getId(), SaveData.getInstance().getToken(), list.get(position).getId(), new StringCallback() {

                @Override
                public void onSuccess(String s, Call call, Response response) {

                    JsonRequestBase data = JsonRequestBase.getJsonObj(s, JsonRequestBase.class);
                    if (StringUtils.toInt(data.getCode()) == 1) {
                        if (StringUtils.toInt(list.get(position).getIs_like()) == 1) {
                            list.get(position).setIs_like("0");
                            list.get(position).decLikeCount(1);
                        } else {
                            list.get(position).setIs_like("1");
                            list.get(position).plusLikeCount(1);
                        }
                        dynamicAdapter.notifyDataSetChanged();
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
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        requestGetData(false);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(RefreshMessageEvent messageEvent) {
        if (messageEvent.getMessage().equals("refresh_dynamic_list")) {
            page = 1;
            requestGetData();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void clickDelDynamic(final int position) {

        showLoadingDialog("正在操作...");
        Api.doRequestDelDynamic(SaveData.getInstance().getId(), SaveData.getInstance().getToken(), list.get(position).getId(), new StringCallback() {

            @Override
            public void onSuccess(String s, Call call, Response response) {

                hideLoadingDialog();
                JsonRequestBase data = JsonRequestBase.getJsonObj(s, JsonRequestBase.class);
                if (StringUtils.toInt(data.getCode()) == 1) {

                    list.remove(position);
                    dynamicAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                hideLoadingDialog();
            }
        });
    }

    @Override
    public void onItemClickListener(final String imgUrl, final String pid) {
        if (TextUtils.isEmpty(pid)) {
            Intent intent = new Intent(getContext(), DynamicImagePreviewActivity.class);
            intent.putExtra(DynamicImagePreviewActivity.IMAGE_PATH, imgUrl);
            getContext().startActivity(intent);
            return;
        }

        Api.doRequestSelectPic(SaveData.getInstance().getId(), SaveData.getInstance().getToken(), pid, new StringCallback() {

            @Override
            public void onSuccess(String s, Call call, Response response) {

                JsonRequestSelectPic jsonObj = (JsonRequestSelectPic) JsonRequestSelectPic.getJsonObj(s, JsonRequestSelectPic.class);
                if (jsonObj.getCode() == 1) {

                    Intent intent = new Intent(getContext(), DynamicImagePreviewActivity.class);
                    intent.putExtra(DynamicImagePreviewActivity.IMAGE_PATH, imgUrl);
                    getContext().startActivity(intent);

                } else if (jsonObj.getCode() == ApiConstantDefine.ApiCode.PHOTO_NOT_PAY) {

                    ShowPayPhotoDialog showPayPhotoDialog = new ShowPayPhotoDialog(getContext(), pid);
                    showPayPhotoDialog.show();
                } else {
                    ToastUtils.showShort(jsonObj.getMsg());
                }
            }
        });

    }
}
