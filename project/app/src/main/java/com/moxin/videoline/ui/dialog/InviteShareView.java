package com.moxin.videoline.ui.dialog;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.moxin.videoline.R;
import com.moxin.videoline.widget.CuckooShareDialogView;

public class InviteShareView extends CuckooShareDialogView {
    public InviteShareView(Context context) {
        super(context);
    }

    public InviteShareView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InviteShareView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public int setRes() {
        return R.layout.view_share_dialog2;
    }


    public void showSaveBt(boolean b){
        findViewById(R.id.ll_qrcode).setVisibility(b? View.VISIBLE:View.GONE);
    }
}
