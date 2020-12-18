package com.moxin.videoline.ui;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.blankj.utilcode.util.ToastUtils;
import com.moxin.videoline.R;
import com.moxin.videoline.adapter.UserAdapter;
import com.moxin.videoline.api.Api;
import com.moxin.videoline.base.BaseActivity;
import com.moxin.videoline.modle.FriendsBean;
import com.moxin.videoline.widget.swipe.SwipeFlingAdapterView;
import com.google.gson.Gson;
import com.lzy.okgo.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 觅友
 */
public class FriendsActivity extends BaseActivity implements UserAdapter.MatchmakerClickListener {

    @BindView(R.id.friend_center_swf)
    SwipeFlingAdapterView flingContainer;

    private Intent intent;
    private int page = 1;
    private boolean isLastData = false;
    private boolean isLoading = false;

    private List<FriendsBean.DataBean> backListUser = new ArrayList<>();
    private List<FriendsBean.DataBean> mListUser = new ArrayList<>();
    private UserAdapter mUserAdapter;

    @Override
    protected Context getNowContext() {
        return this;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_friends;
    }

    @Override
    protected void initView() {
        //初始化滑动控件
        initFlingListener();
    }

    @Override
    protected void initSet() {

    }

    @Override
    protected void initData() {
        getFriendsData();
    }


    @Override
    protected void initPlayerDisplayData() {

    }


    private void initFlingListener() {


        mUserAdapter = new UserAdapter(FriendsActivity.this, mListUser);
        flingContainer.setAdapter(mUserAdapter);
        mUserAdapter.setMatchmakerClickListener(this);

        //滑动控件监听
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {

                if (mListUser != null && mListUser.size() > 0) {
                    backListUser.clear();
                    backListUser.addAll(mListUser);
                    mListUser.remove(0);

                    //在这里也刷新下，因为有个先后顺序问题
                    refresh(-1);
                }

                mUserAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
//                toLeftPass((UserInfoBean) dataObject);
            }

            @Override
            public void onRightCardExit(Object dataObject) {
//                toRightLike((UserInfoBean) dataObject);
            }

            @Override
            public void onTopCardExit(Object dataObject) {
//                toTopSuperLikeLike((UserInfoBean) dataObject);
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                if (isLoading || isLastData) {
                    return;
                }
                page++;
                if (page == 1) {
                    getFriendsData();
                } else {
                    getFriendsData();
                }

                mUserAdapter.notifyDataSetChanged();
            }

            @Override
            public void onScroll(float scrollProgressPercent, String direction) {
            }

        });

        //首页控件点击监听
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {

            }


        });
    }

    private void refresh(int type) {

        if (mListUser.size() == 0) {

//            UIHelp.showRadarActivityNoPeople(MainActivity.this);
        }

        //-1只判断数据等于0时的情况，不用做刷新操作
        if (-1 == type) return;
        mUserAdapter.notifyDataSetChanged();
    }

    private void getFriendsData() {
        Api.getFriendsData(new StringCallback() {
            @Override
            public void onSuccess(String s, Call call, Response response) {
                FriendsBean bean = new Gson().fromJson(s, FriendsBean.class);
                Log.e("getFriendsData", s);
                if (bean.getCode() == 1) {
                    List<FriendsBean.DataBean> listData = bean.getData();

                    if (listData == null) {
                        return;
                    }
                    if (page == 1) {
                        mListUser.clear();
                    }

                    if (listData.isEmpty()) {
                        page = page - 1;
                        isLastData = true;

                    } else {
                        mListUser.addAll(listData);
                        isLastData = false;
                    }

                    mUserAdapter.notifyDataSetChanged();

                    isLoading = false;

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

    @OnClick({R.id.friend_bottom_btn_return, R.id.friend_bottom_btn_skip, R.id.friend_bottom_btn_like, R.id.friend_bottom_btn_vip,
            R.id.friend_title_news, R.id.friend_title_edit, R.id.friend_top_bar_back})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.friend_bottom_btn_return:


                break;
            case R.id.friend_bottom_btn_skip:


                break;

            case R.id.friend_bottom_btn_like:


                break;
            case R.id.friend_bottom_btn_vip:


                break;
            case R.id.friend_title_news:
                intent = new Intent(this, PairingActivity.class);
                startActivity(intent);

                break;

            case R.id.friend_title_edit:
                intent = new Intent(this, FriendEditActivity.class);
                startActivity(intent);
                break;

            case R.id.friend_top_bar_back:
                finish();
                break;

            default:
                break;
        }
    }


    @Override
    public void onMatchmakerClickListener(int position) {

    }
}
