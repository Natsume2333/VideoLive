package com.moxin.videoline.ui;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.moxin.videoline.R;
import com.moxin.videoline.adapter.CuckooSelectIncomeLogAdapter;
import com.moxin.videoline.api.Api;
import com.moxin.videoline.base.BaseActivity;
import com.moxin.videoline.json.JsonGetSelectIncomeLog;
import com.moxin.videoline.json.JsonRequestBase;
import com.moxin.videoline.modle.SelectIncomeLogModel;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.callback.StringCallback;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

public class CuckooSelectIncomeLogActivity extends BaseActivity implements BaseQuickAdapter.RequestLoadMoreListener {

    @BindView(R.id.tv_start_time)
    TextView tv_start_time;

    @BindView(R.id.tv_end_time)
    TextView tv_end_time;

    @BindView(R.id.rv_content_list)
    RecyclerView rv_content_list;

    @BindView(R.id.et_id)
    EditText et_id;

    private int page = 1;
    private Date startDate;
    private Date endDate;

    private List<SelectIncomeLogModel> list = new ArrayList<>();
    private CuckooSelectIncomeLogAdapter cuckooSelectIncomeLogAdapter;

    @Override
    protected Context getNowContext() {
        return this;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.cuckoo_act_select_income_log;
    }

    @Override
    protected void initView() {
        getTopBar().setTitle("查询收益");
        cuckooSelectIncomeLogAdapter = new CuckooSelectIncomeLogAdapter(list);
        rv_content_list.setLayoutManager(new LinearLayoutManager(getNowContext()));
        rv_content_list.setAdapter(cuckooSelectIncomeLogAdapter);

        cuckooSelectIncomeLogAdapter.setOnLoadMoreListener(this, rv_content_list);
        cuckooSelectIncomeLogAdapter.disableLoadMoreIfNotFullPage();
    }

    @Override
    protected void initSet() {

    }

    @Override
    protected void initData() {
        requestGetData();
    }


    @Override
    protected void initPlayerDisplayData() {

    }

    private void requestGetData() {

    }

    @OnClick({R.id.ll_start_time, R.id.ll_end_time, R.id.btn_submit})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_start_time:
                clickSelectStartTime();
                break;
            case R.id.ll_end_time:
                clickSelectEndTime();
                break;
            case R.id.btn_submit:
                clickSubmit();
            default:
                break;
        }
    }

    private void clickSubmit() {

        String toUserId = et_id.getText().toString();
        if (startDate == null || endDate == null) {
            ToastUtils.showLong("请选择时间!");
            return;
        }
        long startTime = startDate.getTime() / 1000;
        long endTime = endDate.getTime() / 1000;

        Api.doSelectIncomeLog(uId, uToken, startTime, endTime, toUserId, page, new StringCallback() {

            @Override
            public void onSuccess(String s, Call call, Response response) {
                JsonGetSelectIncomeLog data = (JsonGetSelectIncomeLog) JsonRequestBase.getJsonObj(s, JsonGetSelectIncomeLog.class);
                if (data.getCode() == 1) {

                    if (page == 1) {
                        list.clear();
                    }

                    list.addAll(data.getList());
                    if (data.getList().size() == 0) {
                        cuckooSelectIncomeLogAdapter.loadMoreEnd();
                    } else {
                        cuckooSelectIncomeLogAdapter.loadMoreComplete();
                    }
                }
            }
        });
    }

    private void clickSelectStartTime() {
        //时间选择器
        TimePickerView pvTime = new TimePickerBuilder(CuckooSelectIncomeLogActivity.this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                startDate = date;
                tv_start_time.setText(TimeUtils.date2String(date));
            }
        }).setType(new boolean[]{true, true, true, true, true, true}).build();
        pvTime.show();
    }

    private void clickSelectEndTime() {
        //时间选择器
        TimePickerView pvTime = new TimePickerBuilder(CuckooSelectIncomeLogActivity.this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                endDate = date;
                tv_end_time.setText(TimeUtils.date2String(date));
            }
        }).setType(new boolean[]{true, true, true, true, true, true}).build();
        pvTime.show();
    }

    @Override
    protected boolean hasTopBar() {
        return true;
    }

    @Override
    public void onLoadMoreRequested() {
        page++;
        clickSubmit();
    }
}
