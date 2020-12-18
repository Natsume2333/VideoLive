package com.moxin.videoline.ui;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.moxin.videoline.R;
import com.moxin.videoline.api.Api;
import com.moxin.videoline.base.BaseFragment;
import com.moxin.videoline.manage.SaveData;
import com.moxin.videoline.modle.ConfigModel;
import com.moxin.videoline.modle.HintBean;
import com.moxin.videoline.modle.InviteBean;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.Gson;
import com.lzy.okgo.callback.StringCallback;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

import static com.moxin.videoline.ui.WealthDetailedActivity.TYPE_DRAWING_LIST;

/**
 * 主播提现
 */
public class DrawingFragment extends BaseFragment {
    @BindView(R.id.hint_top)
    TextView hint_t;

    @BindView(R.id.edit)
    EditText edit;

    @BindView(R.id.hint_b)
    TextView hint_b;

    @BindView(R.id.wealth_invite_rv)
    RecyclerView wealthInviteRv;

    private BaseQuickAdapter rechageAdapter;
    private int nowSelRecharge = -1;
    private List<InviteBean.ListBean> rulesList;

    @Override
    protected View getBaseView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_normal_drawing, container, false);
    }

    @Override
    protected void initView(View view) {
        wealthInviteRv.setLayoutManager(new GridLayoutManager(getContext(), 3));

        getRulesData();
    }

    private void getRulesData() {
        Api.getRulesData(SaveData.getInstance().id, SaveData.getInstance().token, new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                Log.e("getRulesData", s);
                InviteBean bean = new Gson().fromJson(s, InviteBean.class);
                if (bean.getCode() == 1) {

                    hint_t.setText("当前可提现积分为: " + bean.getIncome());

                    String earnings_withdrawal_rules = ConfigModel.getInitData().getEarnings_withdrawal_rules();
                    hint_b.setText(earnings_withdrawal_rules);

                    rulesList = bean.getList();
                    wealthInviteRv.setAdapter(rechageAdapter = new BaseQuickAdapter<InviteBean.ListBean, BaseViewHolder>(R.layout.recharge_buy_item, rulesList) {
                        @Override
                        protected void convert(BaseViewHolder helper, InviteBean.ListBean item) {
                            helper.setText(R.id.top, item.getMoney() + "元");
                            helper.setText(R.id.bottom, item.getCoin() + "积分");

                            if (nowSelRecharge == helper.getAdapterPosition()) {
                                helper.setVisible(R.id.sel_icon, true);
                                helper.getView(R.id.bg).setBackgroundResource(R.drawable.bg_guardbuy_item);
                            } else {
                                helper.setVisible(R.id.sel_icon, false);
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

                } else {
                    ToastUtils.showShort(bean.getMsg());
                }


            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                Log.e("getRulesData", e.toString());
            }
        });
    }


    @OnClick(R.id.to_drawing)
    public void toD() {
        requestCash();
    }

    private void requestCash() {
        if(nowSelRecharge == -1){
            ToastUtils.showLong("请选择提现规则");
            return;
        }
        Api.toDrawal(rulesList.get(nowSelRecharge).getId(), new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                HintBean hint = new Gson().fromJson(s, HintBean.class);
                if (hint.getCode() == 1) {
                    ToastUtils.showLong(hint.getMsg());
                    getRulesData();
                } else {
                    ToastUtils.showLong(hint.getMsg());
                }

            }
        });
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

    @OnClick(R.id.his)
    public void toHis() {
        WealthDetailedActivity.start(getContext(), TYPE_DRAWING_LIST);
    }
}
