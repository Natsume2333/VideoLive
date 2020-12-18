package com.moxin.videoline.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

import com.moxin.videoline.R;

import butterknife.BindView;

public class TransTitleBar extends BaseView{
    public TransTitleBar(@NonNull Context context) {
        super(context);
    }

    public TransTitleBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TransTitleBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public int setRes() {
        return R.layout.title_trans;
    }

    @BindView(R.id.title_tv)
    TextView ti;
    @Override
    public void init() {

    }
}
