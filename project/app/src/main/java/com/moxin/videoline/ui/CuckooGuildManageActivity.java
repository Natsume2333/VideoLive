package com.moxin.videoline.ui;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.moxin.videoline.R;
import com.moxin.videoline.api.Api;
import com.moxin.videoline.base.BaseActivity;
import com.moxin.videoline.json.JsonGetGuildInfo;
import com.moxin.videoline.json.JsonRequestBase;
import com.moxin.videoline.modle.GuildInfoModel;
import com.moxin.videoline.utils.UIHelp;
import com.moxin.videoline.utils.Utils;
import com.lzy.okgo.callback.StringCallback;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Response;

public class CuckooGuildManageActivity extends BaseActivity {

    @BindView(R.id.tv_introduce)
    TextView tv_introduce;

    @BindView(R.id.tv_name)
    TextView tv_name;

    @BindView(R.id.iv_logo)
    CircleImageView iv_logo;

    @BindView(R.id.tv_num)
    TextView tv_num;

    @BindView(R.id.tv_day_total)
    TextView tv_day_total;

    @BindView(R.id.tv_total)
    TextView tv_total;

    private GuildInfoModel guildInfoModel;

    @Override
    protected Context getNowContext() {
        return this;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.cuckoo_act_guild_manage;
    }

    @Override
    protected void initView() {
        getTopBar().setTitle("公会管理");
    }

    @Override
    protected void initSet() {

    }

    @Override
    protected void initData() {
        requestGetData();
    }

    private void requestGetData() {

        Api.doRequestGetGuildInfo(uId, uToken, new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                JsonGetGuildInfo data = (JsonGetGuildInfo) JsonRequestBase.getJsonObj(s, JsonGetGuildInfo.class);
                if (data.getCode() == 1) {
                    guildInfoModel = data.getData();

                    Utils.loadHttpImg(guildInfoModel.getLogo(), iv_logo);
                    tv_introduce.setText(guildInfoModel.getIntroduce());
                    tv_name.setText(guildInfoModel.getName());

                    tv_num.setText(guildInfoModel.getNum());
                    tv_total.setText(guildInfoModel.getTotal_profit());
                    tv_day_total.setText(guildInfoModel.getDay_total_profit());
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
    protected void initPlayerDisplayData() {
    }

    @OnClick({R.id.ll_manage, R.id.ll_apply,R.id.ll_select_income})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_manage:
                clickManage();
                break;
            case R.id.ll_apply:
                clickApply();
                break;
            case R.id.ll_select_income:
                UIHelp.showSelectIncomeLog(this);
                break;
            default:
                break;
        }
    }

    private void clickApply() {
        UIHelp.showGuildApplyUserManage(this, guildInfoModel.getId());
    }

    private void clickManage() {
        UIHelp.showGuildUserManage(this, guildInfoModel.getId());
    }

    @Override
    protected boolean hasTopBar() {
        return true;
    }
}
