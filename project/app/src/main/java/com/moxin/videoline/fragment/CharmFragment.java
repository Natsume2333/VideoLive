package com.moxin.videoline.fragment;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.moxin.videoline.R;
import com.moxin.videoline.adapter.recycler.RecyclerRankingAdapter;
import com.moxin.videoline.api.Api;
import com.moxin.videoline.api.ApiUtils;
import com.moxin.videoline.base.BaseFragment;
import com.moxin.videoline.helper.SelectResHelper;
import com.moxin.videoline.inter.JsonCallback;
import com.moxin.videoline.json.JsonRequestRank;
import com.moxin.videoline.modle.RankModel;
import com.moxin.videoline.ui.common.Common;
import com.moxin.videoline.utils.StringUtils;
import com.moxin.videoline.utils.Utils;
import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 魅力
 */
public class CharmFragment extends BaseFragment implements RadioGroup.OnCheckedChangeListener {
    private RadioGroup radioGroup;//按钮组容器
    private TextView textMyRanking;//我的排行
    private SwipeRefreshLayout rankingFresh;//刷新组件
    private RecyclerView rankingRecycler;//数据列表
    private RankingHeaderView headView;//头部布局
    //适配器
    private RecyclerRankingAdapter recyclerRankingAdapter;
    //数据
    private List<RankModel> charmDatas = new ArrayList<>();


    boolean isML = true;

    public CharmFragment setMl(boolean b) {
        isML = b;
        return this;
    }

    @BindView(R.id.bg)
    View bg;


    //////////////////////////////////////////初始化方法//////////////////////////////////////////////
    @Override
    protected View getBaseView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_ranking_list, container, false);
    }

    @Override
    protected void initView(View view) {
        //主页信息
        radioGroup = view.findViewById(R.id.ranking_radio_group);
        textMyRanking = view.findViewById(R.id.text_my_ranking);
        rankingFresh = view.findViewById(R.id.ranking_fresh);
        rankingRecycler = view.findViewById(R.id.ranking_recyclerview);
        headView = new RankingHeaderView(getContext());

        bg.setBackgroundResource(isML ? R.mipmap.meili_bc : R.mipmap.caiqi_bc);
        Utils.initTransP(bg, 60);
    }

    @Override
    protected void initDate(View view) {

    }

    @Override
    public void fetchData() {

        //初始化加载数据源
        requestMoneyData("");
    }

    View footView;
    View foot2;

    @Override
    protected void initSet(View view) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        rankingRecycler.setHasFixedSize(true);
        rankingRecycler.setLayoutManager(linearLayoutManager);
        //选择监听
        radioGroup.setOnCheckedChangeListener(this);
        //刷新监听
        rankingFresh.setOnRefreshListener(this);

        if (recyclerRankingAdapter != null) {
            recyclerRankingAdapter.removeAllHeaderView();
            recyclerRankingAdapter.removeAllFooterView();
        }
        //创建并设置Adapter
        footView = new EmptFootView(getContext());
        foot2 = LayoutInflater.from(getContext()).inflate(R.layout.recy_empt_foot2, null, false);
        recyclerRankingAdapter = new RecyclerRankingAdapter(charmDatas, getContext(), 2);
        recyclerRankingAdapter.addHeaderView(headView);
        recyclerRankingAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                Common.jumpUserPage(getContext(), charmDatas.get(position).getId());
            }
        });
        addFoot();
        //设置添加适配器
        rankingRecycler.setAdapter(recyclerRankingAdapter);

    }

    @Override
    protected void initDisplayData(View view) {

    }

    //////////////////////////////////////////监听方法///////////////////////////////////////////////
    @Override
    public void onClick(View v) {
        super.onClick(v);
    }

    @Override
    public void onRefresh() {

        // 刷新数据-初始位置/刷新数量
        recyclerRankingAdapter.notifyItemRangeInserted(recyclerRankingAdapter.getItemCount(), charmDatas.size());
        rankingFresh.setRefreshing(false);
    }

    @Override
    public void onLoadMoreRequested() {

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId == R.id.radio_days) {
            //日榜
            requestMoneyData("day");
        } else if (checkedId == R.id.radio_weeks) {
            //周榜
            requestMoneyData("week");
        } else {
            //总榜
            requestMoneyData("");
        }
    }

    //////////////////////////////////////////本地工具方法////////////////////////////////////////////
    private void refreshAdapter() {
        addFoot();
        recyclerRankingAdapter.notifyDataSetChanged();
        LogUtils.i("设置添加适配器");
    }

    private void addFoot() {
        recyclerRankingAdapter.removeAllFooterView();
        if (charmDatas.size() < 4) recyclerRankingAdapter.addFooterView(footView);
        else recyclerRankingAdapter.addFooterView(foot2);
    }

    /**
     * 加载魅力排行榜数据
     */
    private void requestMoneyData(String type) {
        if (isML) {
            Api.getCharmListData(
                    uId,
                    uToken,
                    type,
                    0,
                    new JsonCallback() {
                        @Override
                        public Context getContextToJson() {
                            return getContext();
                        }

                        @Override
                        public void onSuccess(String s, Call call, Response response) {
                            Log.e("requestMoneyData-1", s);
                            JsonRequestRank requestObj = JsonRequestRank.getJsonObj(s);
                            if (requestObj.getCode() == 1) {
                                List<RankModel> charmArray = requestObj.getList();
                                textMyRanking.setText(String.format(Locale.CHINA, "我的排名：%s", requestObj.getOrder_num()));
                                charmDatas.clear();
                                charmDatas.addAll(charmArray);
                                //设置其他
                                headView.setData(charmDatas);
                                refreshAdapter();
                            }
                        }
                    }
            );

        } else {
            Api.getMoneyListData(
                    uId,
                    uToken,
                    type,
                    0,
                    new JsonCallback() {
                        @Override
                        public Context getContextToJson() {
                            return getContext();
                        }

                        @Override
                        public void onSuccess(String s, Call call, Response response) {
                            Log.e("requestMoneyData-2", s);
                            JsonRequestRank requestObj = JsonRequestRank.getJsonObj(s);
                            if (requestObj.getCode() == 1) {
                                List<RankModel> charmArray = requestObj.getList();
                                textMyRanking.setText(String.format(Locale.CHINA, "我的排名：%s", requestObj.getOrder_num()));
                                charmDatas.clear();
                                charmDatas.addAll(charmArray);
                                //设置其他
                                headView.setData(charmDatas);
                                refreshAdapter();
                            }
                        }
                    }
            );
        }
    }

    /**
     * 设置资源
     *
     * @param charmData 资源对象
     * @param headImg   头像
     * @param nickname  昵称
     * @param money     钱数
     * @param location  地址
     * @param isOnline  是否在线
     * @param grid      等级
     */
    private void setRes(final RankModel charmData, CircleImageView headImg, TextView nickname, TextView money, TextView location, ImageView isOnline, TextView grid) {
        if (ApiUtils.isTrueUrl(charmData.getAvatar())) {
            Utils.loadUserIcon(getContext(), Utils.getCompleteImgUrl(charmData.getAvatar()), headImg);
        }
        nickname.setText(charmData.getUser_nickname());
        money.setText(charmData.getTotal());
        //设置跳转监听
        headImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.jumpUserPage(getContext(), charmData.getId());
            }
        });
        location.setText(charmData.getAddress());

        isOnline.setBackgroundResource(SelectResHelper.getOnLineRes(StringUtils.toInt(charmData.getIs_online())));
        //grid.addView(new GradeShowLayout(grid.getContext(),charmData.getLevel(),Integer.valueOf(charmData.getSex())));

        grid.setBackgroundResource(R.drawable.bg_main_color_num);
        grid.setText("M " + charmData.getLevel());
    }
}
