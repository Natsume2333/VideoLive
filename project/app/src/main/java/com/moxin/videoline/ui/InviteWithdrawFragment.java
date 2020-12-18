package com.moxin.videoline.ui;

import android.text.TextUtils;
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
import com.moxin.videoline.modle.ConfigModel;
import com.moxin.videoline.modle.DrawalBean;
import com.moxin.videoline.modle.HintBean;
import com.google.gson.Gson;
import com.lzy.okgo.callback.StringCallback;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

import static com.moxin.videoline.ui.WealthDetailedActivity.TYPE_DRAWING_LIST;

/**
 * 邀请提现
 */
public class InviteWithdrawFragment extends BaseFragment {
    @BindView(R.id.hint_top)
    TextView hint_t;

    @BindView(R.id.edit)
    EditText edit;

    @BindView(R.id.hint_b)
    TextView hint_b;

    @BindView(R.id.invite_money_et)
    EditText moneyEt;

    @Override
    protected View getBaseView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_invite_drawing, container, false);
    }

    @Override
    protected void initView(View view) {
        getData();
    }

    private void getData() {
        Api.getWithdrawal(new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                Log.e("getWithdrawal", s);
                DrawalBean bean = new Gson().fromJson(s, DrawalBean.class);
                if (bean.getCode() == 1) {
                    hint_t.setText("当前可提现金额为: " + bean.getData().getInvitation_coin() + " 元");

                    String invitation_withdrawal_rules = ConfigModel.getInitData().getInvitation_withdrawal_rules();
                    hint_b.setText(invitation_withdrawal_rules);
                } else {
                    ToastUtils.showShort(bean.getMsg());
                }
            }
        });
    }

    @OnClick(R.id.to_drawing)
    public void toD() {
        String trim = moneyEt.getText().toString().trim();
        if (TextUtils.isEmpty(trim)) {
            ToastUtils.showShort("请输入提现金额");
            return;
        }

        Api.toWinthDraw(trim, new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                HintBean hint = new Gson().fromJson(s, HintBean.class);
                if (hint.getCode() == 1) {
//                    getData();
                    ToastUtils.showLong(hint.getMsg());
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
