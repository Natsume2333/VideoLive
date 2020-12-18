package com.moxin.videoline.ui;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.moxin.videoline.R;
import com.moxin.videoline.adapter.CuckooSearchListAdapter;
import com.moxin.videoline.api.Api;
import com.moxin.videoline.base.BaseActivity;
import com.moxin.videoline.json.JsonDoRequestGetSearchHistoryList;
import com.moxin.videoline.json.JsonDoRequestGetSearchList;
import com.moxin.videoline.json.JsonRequestBase;
import com.moxin.videoline.manage.SaveData;
import com.moxin.videoline.modle.HistoryModel;
import com.moxin.videoline.modle.UserModel;
import com.moxin.videoline.ui.common.Common;
import com.moxin.videoline.utils.StringUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.callback.StringCallback;
import com.maning.imagebrowserlibrary.utils.StatusBarUtil;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

public class CuckooSearchActivity extends BaseActivity implements BaseQuickAdapter.OnItemClickListener {

    @BindView(R.id.et_key_word)
    EditText et_key_word;

    @BindView(R.id.rv_content_list)
    RecyclerView rv_content_list;

    @BindView(R.id.search_history_tf)
    TagFlowLayout historyTflayout;

    private ArrayList<UserModel> list = new ArrayList<>();
    private CuckooSearchListAdapter cuckooSearchListAdapter;
    private ArrayList<HistoryModel> historyList;

    @Override
    protected Context getNowContext() {
        return this;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_cuckoo_search;
    }

    @Override
    protected void initView() {
        StatusBarUtil.setColor(this, getResources().getColor(R.color.admin_color), 0);
        StatusBarUtil.setLightMode(this);

        rv_content_list.setLayoutManager(new LinearLayoutManager(this));
        cuckooSearchListAdapter = new CuckooSearchListAdapter(list);
        rv_content_list.setAdapter(cuckooSearchListAdapter);
        cuckooSearchListAdapter.setOnItemClickListener(this);

        et_key_word.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    clickSearch();
                    return true;
                }
                return false;
            }
        });
    }


    @Override
    protected void initSet() {

    }

    @Override
    protected void initData() {
        getSearchHistory();
    }

    private void getSearchHistory() {
        Api.getSearchHistory(SaveData.getInstance().getId(), new StringCallback() {

            @Override
            public void onSuccess(String s, Call call, Response response) {

                JsonDoRequestGetSearchHistoryList data = (JsonDoRequestGetSearchHistoryList) JsonRequestBase.getJsonObj(s, JsonDoRequestGetSearchHistoryList.class);
                if (StringUtils.toInt(data.getCode()) == 1) {
                    historyList = data.getList();

                    historyTflayout.setAdapter(new TagAdapter(historyList) {
                        @Override
                        public View getView(FlowLayout parent, int position, Object o) {
                            TextView tv = (TextView) LayoutInflater.from(getNowContext()).inflate(R.layout.view_evaluate_label,
                                    historyTflayout, false);
                            tv.setText(historyList.get(position).getKey_word());
                            tv.setTextSize(ConvertUtils.dp2px(5));
                            tv.setTextColor(getResources().getColor(R.color.white));
                            tv.setBackgroundResource(R.drawable.bg_evaluate_un_select);
                            return tv;
                        }


                        @Override
                        public void onSelected(int position, View view) {
                            et_key_word.setText(historyList.get(position).getKey_word());
                            clickSearch();
                        }

                        @Override
                        public void unSelected(int position, View view) {
                        }
                    });


                } else {
                    ToastUtils.showLong(data.getMsg());
                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                ToastUtils.showLong(R.string.search_error);
            }
        });
    }

    @Override
    protected void initPlayerDisplayData() {

    }

    @OnClick({R.id.tv_search, R.id.iv_back, R.id.tv_back})
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.tv_search:

                clickSearch();
                break;
            case R.id.iv_back:
            case R.id.tv_back:

                finish();
                break;
            default:
                break;
        }
    }

    private void clickSearch() {
        historyTflayout.setVisibility(View.GONE);
        rv_content_list.setVisibility(View.VISIBLE);

        String keyWord = et_key_word.getText().toString();
        if (TextUtils.isEmpty(keyWord)) {
            ToastUtils.showLong(R.string.plase_input_search_content);
            return;
        }

        Api.doRequestSearch(SaveData.getInstance().getId(), keyWord, new StringCallback() {

            @Override
            public void onSuccess(String s, Call call, Response response) {

                JsonDoRequestGetSearchList data = (JsonDoRequestGetSearchList) JsonRequestBase.getJsonObj(s, JsonDoRequestGetSearchList.class);
                if (StringUtils.toInt(data.getCode()) == 1) {
                    list.clear();
                    list.addAll(data.getList());
                    cuckooSearchListAdapter.notifyDataSetChanged();
                } else {
                    ToastUtils.showLong(data.getMsg());
                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                ToastUtils.showLong(R.string.search_error);
            }
        });
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

        Common.jumpUserPage(CuckooSearchActivity.this, list.get(position).getId());
    }
}
