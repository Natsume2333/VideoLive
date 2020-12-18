package com.moxin.videoline.ui.dialog;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.moxin.videoline.R;
import com.moxin.videoline.ui.RechargeActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class CallNoMuchDialog extends BaseDialog{


    String msg;
    public CallNoMuchDialog(Context context,String hint) {
        super(context);
        msg = hint;
    }

    @Override
    public void init() {
        super.init();
        setTrans();
        hint.setText(msg);
    }

    @BindView(R.id.hint)
    TextView hint;

    @Override
    public int setRes() {
        return R.layout.dialog_call_nomuch;
    }

    @Override
    public void show() {
        super.show();
        hint.setText(msg);
    }

    @OnClick({R.id.cancel,R.id.cz})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.cancel:
                hide();
                break;

            case R.id.cz:
                RechargeActivity.startRechargeActivity(context);
                break;
        }
    }
}
