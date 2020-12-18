package com.moxin.videoline.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.blankj.utilcode.util.ToastUtils;
import com.moxin.videoline.R;
import com.moxin.videoline.adapter.GuildListAdapter;
import com.moxin.videoline.api.Api;
import com.moxin.videoline.base.BaseActivity;
import com.moxin.videoline.json.JsonGetGuildList;
import com.moxin.videoline.json.JsonRequestBase;
import com.moxin.videoline.modle.GuildModel;
import com.moxin.videoline.utils.DialogHelp;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Response;

public class CuckooGuildListActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener {

    @BindView(R.id.rv_content_list)
    RecyclerView rv_content_list;

    private List<GuildModel> guildModelList = new ArrayList<>();
    private GuildListAdapter guildListAdapter;

    private int page = 1;

    @Override
    protected Context getNowContext() {
        return this;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.cuckoo_act_guild_list;
    }

    @Override
    protected void initView() {
        rv_content_list.setLayoutManager(new LinearLayoutManager(getNowContext()));
        guildListAdapter = new GuildListAdapter(guildModelList);
        rv_content_list.setAdapter(guildListAdapter);
        guildListAdapter.setOnItemClickListener(this);
    }

    @Override
    protected void initSet() {
        getTopBar().setTitle("公会列表");
        getTopBar().setBackgroundColor(getResources().getColor(R.color.admin_color));
    }

    @Override
    protected void initData() {
        requestGetData();
    }

    @Override
    protected void initPlayerDisplayData() {


    }

    private void requestGetData() {
        Api.doRequestGetGuildList(uId, uToken, page, new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                JsonGetGuildList data = (JsonGetGuildList) JsonRequestBase.getJsonObj(s, JsonGetGuildList.class);
                if (data.getCode() == 1) {
                    guildModelList.clear();
                    guildModelList.addAll(data.getList());
                    guildListAdapter.notifyDataSetChanged();
                } else {
                    ToastUtils.showLong(data.getMsg());
                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
            }
        });
    }

    @Override
    protected boolean hasTopBar() {
        return true;
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, final int position) {

        DialogHelp.getConfirmDialog(this, "是否申请加入公会？", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dia1logInterface, int i) {
                clickJoinGuild(guildModelList.get(position).getId());
            }
        }).show();

    }

    private void clickJoinGuild(int id) {

        showLoadingDialog("正在提交申请...");
        Api.doJoinGuild(uId, uToken, id, new StringCallback() {

            @Override
            public void onSuccess(String s, Call call, Response response) {
                hideLoadingDialog();
                JsonRequestBase data = JsonRequestBase.getJsonObj(s, JsonRequestBase.class);
                if (data.getCode() == 1) {
                    ToastUtils.showLong("申请成功，等待会长审核！");
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
}
