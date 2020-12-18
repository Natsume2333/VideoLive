package com.moxin.videoline.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.moxin.videoline.LiveConstant;
import com.moxin.videoline.R;
import com.moxin.videoline.api.Api;
import com.moxin.videoline.base.BaseActivity;
import com.moxin.videoline.modle.LablesBean;
import com.google.gson.Gson;
import com.lzy.okgo.callback.StringCallback;
import com.maning.imagebrowserlibrary.utils.StatusBarUtil;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.List;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 形象标签
 */
public class CuckooSelectLabelActivity extends BaseActivity {

    @BindView(R.id.tab_flow)
    TagFlowLayout tab_flow;
    private List<LablesBean.DataBean> lableList;


    @Override
    protected Context getNowContext() {
        return this;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_cuckoo_select_label;
    }

    @Override
    protected void initView() {
        StatusBarUtil.setColor(this, getResources().getColor(R.color.admin_color), 0);// 沉浸式状态栏
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        getTopBar().setTitle(getString(R.string.plase_select_label));

        findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickSubmit();
            }
        });
    }

    private void clickSubmit() {

        Integer pos[] = new Integer[tab_flow.getSelectedList().size()];
        tab_flow.getSelectedList().toArray(pos);

        if (pos.length == 0) {
            finish();
            return;
        }

        if (pos.length > LiveConstant.DATA_DEFINE.MAX_SELECT_SELF_LABEL_NUM) {
            ToastUtils.showLong(getString(R.string.max_select) + LiveConstant.DATA_DEFINE.MAX_SELECT_SELF_LABEL_NUM + getString(R.string.a_label));
            return;
        }


        StringBuilder stringBuilder = new StringBuilder();
//        if (i == pos.length - 1) {
//            stringBuilder.append(lableList.get(pos[i]).getName());
//        } else {
//            stringBuilder.append(lableList.get(pos[i]).getName() + " - ");
//        }
        for (int i = 0; i < pos.length; i++) {
            if (i == pos.length - 1) {
                stringBuilder.append(lableList.get(pos[i]).getId());
            } else {
                stringBuilder.append(lableList.get(pos[i]).getId() + ",");
            }

        }

        Log.e("updateLableInfo", stringBuilder.toString());
        Api.updateLableInfo(stringBuilder.toString(), new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                Log.e("updateLableInfo", s);
                LablesBean bean = new Gson().fromJson(s, LablesBean.class);
                if (bean.getCode() == 1) {
                    String image_label = bean.getImage_label();
                    Intent intent = new Intent();
                    intent.putExtra(CuckooAuthFormActivity.USER_LABEL, image_label);
                    setResult(CuckooAuthFormActivity.RESULT_SELF_LABEL, intent);
                    finish();
                } else {
                    ToastUtils.showShort(bean.getMsg());
                }
            }



            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                Log.e("updateLableInfo", e.toString());
            }
        });


    }

    @Override
    protected void initSet() {

    }

    @Override
    protected void initData() {
        getLablesData();
    }

    private void getLablesData() {
        Api.getLablesData(new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                LablesBean bean = new Gson().fromJson(s, LablesBean.class);
                if (bean.getCode() == 1) {
                    lableList = bean.getData();

                    tab_flow.setAdapter(new TagAdapter(lableList) {
                        @Override
                        public View getView(FlowLayout parent, int position, Object o) {
                            TextView tv = (TextView) LayoutInflater.from(getNowContext()).inflate(R.layout.view_evaluate_label,
                                    tab_flow, false);
                            tv.setText(lableList.get(position).getName());
                            tv.setTextSize(ConvertUtils.dp2px(5));
                            tv.setTextColor(getResources().getColor(R.color.white));
                            tv.setBackgroundResource(R.drawable.bg_evaluate_un_select);
                            return tv;
                        }


                        @Override
                        public void onSelected(int position, View view) {

                            view.setBackgroundResource(R.drawable.bg_evaluate_select);
                            TextView tv = (TextView) view;
                            tv.setTextColor(Color.parseColor("#FC5808"));

                            lableList.get(position).setChecked("1");
                        }

                        @Override
                        public void unSelected(int position, View view) {
                            view.setBackgroundResource(R.drawable.bg_evaluate_un_select);
                            TextView tv = (TextView) view;
                            tv.setTextColor(getResources().getColor(R.color.white));

                            lableList.get(position).setChecked("0");
                        }
                    });


                } else {
                    ToastUtils.showShort(bean.getMsg());
                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
            }
        });
    }

    @Override
    protected void initPlayerDisplayData() {

    }

    @Override
    protected boolean hasTopBar() {
        return true;
    }
}
