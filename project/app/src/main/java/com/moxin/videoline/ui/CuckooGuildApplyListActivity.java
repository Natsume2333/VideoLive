package com.moxin.videoline.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.blankj.utilcode.util.ToastUtils;
import com.moxin.videoline.R;
import com.moxin.videoline.adapter.CuckooGuildUserApplyManageAdapter;
import com.moxin.videoline.api.Api;
import com.moxin.videoline.base.BaseActivity;
import com.moxin.videoline.json.JsonGetGuildUserList;
import com.moxin.videoline.json.JsonRequestBase;
import com.moxin.videoline.modle.GuildUserModel;
import com.moxin.videoline.utils.DialogHelp;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Response;

public class CuckooGuildApplyListActivity extends BaseActivity implements BaseQuickAdapter.OnItemChildClickListener, BaseQuickAdapter.RequestLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {

    public static final String GUILD_ID = "GUILD_ID";

    @BindView(R.id.rv_content_list)
    RecyclerView rv_content_list;

    @BindView(R.id.sw_refresh)
    SwipeRefreshLayout sw_refresh;

    private CuckooGuildUserApplyManageAdapter cuckooGuildUserManageAdapter;
    private List<GuildUserModel> list = new ArrayList<>();

    private String guildId;
    private int page = 1;

    @Override
    protected Context getNowContext() {
        return this;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.cuckoo_act_guild_apply_list;
    }

    @Override
    protected void initView() {
        rv_content_list.setLayoutManager(new LinearLayoutManager(getNowContext()));
        cuckooGuildUserManageAdapter = new CuckooGuildUserApplyManageAdapter(list);
        rv_content_list.setLayoutManager(new LinearLayoutManager(getNowContext()));
        rv_content_list.setAdapter(cuckooGuildUserManageAdapter);

        cuckooGuildUserManageAdapter.setOnItemChildClickListener(this);
        cuckooGuildUserManageAdapter.setOnLoadMoreListener(this, rv_content_list);
        cuckooGuildUserManageAdapter.disableLoadMoreIfNotFullPage();
        sw_refresh.setOnRefreshListener(this);

    }

    @Override
    protected void initSet() {
        getTopBar().setTitle("申请列表");
    }

    @Override
    protected void initData() {

        guildId = getIntent().getStringExtra(GUILD_ID);
        requestGetData();
    }

    private void requestGetData() {
        Api.doRequestGetGuildApplyUser(uId, uToken, guildId, page, new StringCallback() {

            @Override
            public void onSuccess(String s, Call call, Response response) {
                sw_refresh.setRefreshing(false);

                JsonGetGuildUserList data = (JsonGetGuildUserList) JsonRequestBase.getJsonObj(s, JsonGetGuildUserList.class);
                if (data.getCode() == 1) {

                    if (page == 1) {
                        list.clear();
                    }

                    if (data.getList().size() == 0) {
                        cuckooGuildUserManageAdapter.loadMoreEnd();
                    } else {
                        cuckooGuildUserManageAdapter.loadMoreComplete();
                    }

                    list.addAll(data.getList());
                    cuckooGuildUserManageAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                sw_refresh.setRefreshing(false);
            }
        });
    }

    @Override
    protected void initPlayerDisplayData() {

    }

    @Override
    protected boolean hasTopBar() {
        return true;
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter,final View view,final int position) {
        DialogHelp.getConfirmDialog(this, "是否确定该操作？", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (view.getId() == R.id.item_btn_agree) {
                    clickAgreeOrRefuse(list.get(position).getGuild_id(), "agree");
                } else if (view.getId() == R.id.item_btn_refuse) {
                    clickAgreeOrRefuse(list.get(position).getGuild_id(), "refuse");
                }
            }
        }).show();
    }

    private void clickAgreeOrRefuse(String id, String agree) {
        showLoadingDialog("正在操作...");
        Api.doRequestAudition(uId, uToken, id, agree, new StringCallback() {

            @Override
            public void onSuccess(String s, Call call, Response response) {
                hideLoadingDialog();
                JsonRequestBase data = JsonRequestBase.getJsonObj(s, JsonRequestBase.class);
                if (data.getCode() == 1) {
                    ToastUtils.showLong("操作成功！");
                    requestGetData();
                } else {
                    ToastUtils.showLong(data.getMsg());
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
    public void onLoadMoreRequested() {
        page++;
        requestGetData();
    }

    @Override
    public void onRefresh() {
        page = 1;
        requestGetData();
    }
}
