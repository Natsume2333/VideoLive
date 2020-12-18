package com.moxin.videoline.fragment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.moxin.videoline.R;

public class EmptFootView extends FrameLayout {
    public EmptFootView(@NonNull Context context) {
        super(context);
        init();
    }

    private void init() {
        View empt = LayoutInflater.from(getContext()).inflate(R.layout.recy_empt_foot, null, false);
        addView(empt,new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        TextView hint=empt.findViewById(R.id.hint);

        hint.setText("无更多");
    }

    public EmptFootView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public EmptFootView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
