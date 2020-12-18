package com.moxin.videoline.ui;

import android.app.DatePickerDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import com.moxin.videoline.R;
import com.moxin.videoline.api.Api;
import com.moxin.videoline.base.BaseListFragment;
import com.moxin.videoline.modle.RewardDetailBean;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.gson.Gson;
import com.lzy.okgo.callback.StringCallback;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

public class RewardDetailPager extends BaseListFragment<RewardDetailBean.DataBean> {

    @BindView(R.id.tv_all)
    TextView all;

    @BindView(R.id.tv_date)
    TextView tv_date;

    private int mYear;
    private int mMonth;
    private int mDay;
    private String date="";

    @Override
    protected View getBaseView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_reward_detail, container, false);
    }

    @Override
    protected BaseQuickAdapter getBaseQuickAdapter() {
        BaseQuickAdapter<RewardDetailBean.DataBean, BaseViewHolder> adapter = new BaseQuickAdapter<RewardDetailBean.DataBean, BaseViewHolder>(R.layout.wealth_sr_details_item) {
            @Override
            protected void convert(BaseViewHolder helper, RewardDetailBean.DataBean item) {
                //'type':'0注册 1主播收益 2充值奖励',
                String hint = "用户 " + item.getUser_nickname() + item.getType();
                helper.setText(R.id.sr_title, hint)
                        .setText(R.id.sr_time, item.getCreate_time())
                        .setText(R.id.sr_num, "+" + item.getMoney());
                helper.setVisible(R.id.sr_type, false);
            }
        };

        return adapter;
    }


    @Override
    protected RecyclerView.LayoutManager getLayoutManage() {
        return new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
    }

    @Override
    protected void requestGetData(boolean isCache) {
        super.requestGetData(isCache);
        Api.getReward(date, new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                Log.i("邀请明细", "onError: "+s);

                mSwRefresh.setRefreshing(false);
                RewardDetailBean bean = new Gson().fromJson(s, RewardDetailBean.class);
                if(bean.getCode()==1){
                    onLoadDataResult(bean.getData());
                    log(s);
                    all.setText("累计奖励: "+bean.getMoney());
                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                Log.i("邀请明细", "onError: "+e.toString());
            }
        });

    }


    @Override
    protected void init(View view) {
        super.init(view);
        Calendar ca = Calendar.getInstance();
        mYear = ca.get(Calendar.YEAR);
        mMonth = ca.get(Calendar.MONTH);
        mDay = ca.get(Calendar.DAY_OF_MONTH);
        tv_date.setText("全部记录");

        requestGetData(false);

    }

    @OnClick(R.id.sel_date)
    public void showDate(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),DatePickerDialog.THEME_HOLO_LIGHT,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        mYear = year;
                        mMonth = month;
                        mDay = dayOfMonth;
                        date = ""+year+"-"+(month+1);
                        tv_date.setText(date);
                        page=1;
                        requestGetData(false);
                    }
                },
                mYear, mMonth, mDay);
        datePickerDialog.show();
        View view = datePickerDialog.getDatePicker().getCalendarView().getChildAt(2);
        if(view!=null)view.setVisibility(View.GONE);

    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

    }
}
