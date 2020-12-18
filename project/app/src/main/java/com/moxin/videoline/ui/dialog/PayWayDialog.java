package com.moxin.videoline.ui.dialog;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import com.moxin.videoline.R;

import butterknife.BindView;
import butterknife.OnClick;

public class PayWayDialog extends BaseDialog {

    public PayWayDialog(Context context) {
        super(context);
    }

    @Override
    public int setRes() {
        return R.layout.payway_dialog;
    }

    @OnClick(R.id.cancel)
    @Override
    public void hide() {
        super.hide();
    }

    @BindView(R.id.pay_way_rv)
    RecyclerView rv;
    public RecyclerView getList(){
        return rv;
    }
    @Override
    public void init() {
        super.init();
        setTrans();
        setBottomPop();

    }
}
