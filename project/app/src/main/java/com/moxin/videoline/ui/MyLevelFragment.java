package com.moxin.videoline.ui;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.moxin.videoline.R;
import com.moxin.videoline.api.Api;
import com.moxin.videoline.base.BaseFragment;
import com.moxin.videoline.modle.MyLevelBean;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.Gson;
import com.lzy.okgo.callback.StringCallback;

import java.util.List;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Response;

public class MyLevelFragment extends BaseFragment {

    @Override
    protected View getBaseView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_my_level, container, false);
    }


    @BindView(R.id.top_level)
    TextView top_l;
    @BindView(R.id.hint_level)
    TextView hint_level;
    @BindView(R.id.v_l)
    TextView v_l;
    @BindView(R.id.v_r)
    TextView v_r;

    @BindView(R.id.level_pb)
    ProgressBar pb;

    @BindView(R.id.list)
    RecyclerView list;

    @Override
    protected void initView(View view) {

        list.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));

        Api.getLevel(new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                MyLevelBean bean = new Gson().fromJson(s, MyLevelBean.class);
                MyLevelBean.LevelMyBean me = bean.getLevel_my();
                top_l.setText("V"+me.getLevel_name());
                hint_level.setText("当前等级 V"+me.getLevel_name()+" 距离 V"+me.getDown_name()+" 还差 "+me.getSpread());
                v_l.setText("V"+me.getLevel_name());
                v_r.setText("V"+me.getDown_name());
                pb.setProgress(Integer.parseInt(me.getProgress().replace("%","")));
                log("等级"+s);
                showList(bean.getLevel());
            }
        });

    }

    private void showList(List<MyLevelBean.LevelBean> level) {
        list.setAdapter(new BaseQuickAdapter<MyLevelBean.LevelBean,BaseViewHolder>(R.layout.level_item,level) {
            @Override
            protected void convert(BaseViewHolder helper, MyLevelBean.LevelBean item) {
                helper.setText(R.id.left,"V"+item.getLevel_name())
                        .setText(R.id.right,item.getLevel_up_female());
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
}
