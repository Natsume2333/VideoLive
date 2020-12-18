package com.moxin.videoline.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.moxin.videoline.R;
import com.moxin.videoline.adapter.CuckooGuildUserManageAdapter;
import com.moxin.videoline.api.Api;
import com.moxin.videoline.base.BaseActivity;
import com.moxin.videoline.json.JsonGetGuildUserList;
import com.moxin.videoline.json.JsonRequestBase;
import com.moxin.videoline.modle.GuildUserModel;
import com.moxin.videoline.ui.common.Common;
import com.moxin.videoline.utils.DialogHelp;
import com.moxin.videoline.utils.UIHelp;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Response;

public class CuckooGuildUserManageActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener, BaseQuickAdapter.RequestLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {

    public static final String GUILD_ID = "GUILD_ID";

    @BindView(R.id.rv_content_list)
    RecyclerView rv_content_list;

    @BindView(R.id.sw_refresh)
    SwipeRefreshLayout sw_refresh;

    private CuckooGuildUserManageAdapter cuckooGuildUserManageAdapter;
    private List<GuildUserModel> list = new ArrayList<>();

    private String guildId;
    private int page = 1;

    @Override
    protected Context getNowContext() {
        return this;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.cuckoo_act_guild_user_manage;
    }

    @Override
    protected void initView() {
        cuckooGuildUserManageAdapter = new CuckooGuildUserManageAdapter(list);
        rv_content_list.setLayoutManager(new LinearLayoutManager(getNowContext()));
        rv_content_list.setAdapter(cuckooGuildUserManageAdapter);

        cuckooGuildUserManageAdapter.setOnItemClickListener(this);
        cuckooGuildUserManageAdapter.setOnLoadMoreListener(this, rv_content_list);
        cuckooGuildUserManageAdapter.disableLoadMoreIfNotFullPage();

        sw_refresh.setOnRefreshListener(this);

    }

    @Override
    protected void initSet() {
        getTopBar().setTitle("成员管理");
    }

    @Override
    protected void initData() {
        guildId = getIntent().getStringExtra(GUILD_ID);
        requestGetData();
    }


    @Override
    protected void initPlayerDisplayData() {

    }

    private void requestGetData() {

        Api.doRequestGetGuildUser(uId, uToken, guildId, page, new StringCallback() {

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
    protected boolean hasTopBar() {
        return true;
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, final int position) {

        DialogHelp.getSelectDialog(this, new String[]{"修改提成比例", "查看主页"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    UIHelp.showChangeUserRatioPage(CuckooGuildUserManageActivity.this, list.get(position).getId());
                } else if (i == 1) {
                    Common.jumpUserPage(CuckooGuildUserManageActivity.this, String.valueOf(list.get(position).getId()));
                }
            }
        }).show();
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
