package com.moxin.videoline.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.moxin.videoline.R;
import com.moxin.videoline.modle.DynamicListModel;
import com.moxin.videoline.utils.Utils;
import com.moxin.videoline.widget.GlideImageEngine;
import com.maning.imagebrowserlibrary.ImageEngine;
import com.maning.imagebrowserlibrary.MNImageBrowser;
import com.maning.imagebrowserlibrary.listeners.OnPageChangeListener;
import com.maning.imagebrowserlibrary.model.ImageBrowserConfig;

import java.util.ArrayList;
import java.util.List;

public class DynamicImgAdapter extends RecyclerView.Adapter {

    public ImageBrowserConfig.TransformType transformType = ImageBrowserConfig.TransformType.Transform_Default;
    public ImageBrowserConfig.IndicatorType indicatorType = ImageBrowserConfig.IndicatorType.Indicator_Number;

    private ImageEngine imageEngine = new GlideImageEngine();
    private int openAnim = R.anim.mn_browser_enter_anim;
    private int exitAnim = R.anim.mn_browser_exit_anim;
    public ImageBrowserConfig.ScreenOrientationType screenOrientationType = ImageBrowserConfig.ScreenOrientationType.Screenorientation_Default;
    //显示自定义遮盖层
    private boolean showCustomShadeView = false;
    //显示ProgressView
    private boolean showCustomProgressView = false;
    //是不是全屏模式
    private boolean isFulScreenMode = false;

    private int imgWidth = 0;

    private Context context;
    private DynamicListModel dynamicListModel;
    private final List<String> thumbnailPicUrls;

    public DynamicImgAdapter(Context context, DynamicListModel data) {
        this.context = context;
        dynamicListModel = data;
        thumbnailPicUrls = dynamicListModel.getThumbnailPicUrls();
        imgWidth = (ScreenUtils.getScreenWidth() - ConvertUtils.dp2px(60)) / 3;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_dynamic_img, parent, false);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(imgWidth, imgWidth);
       // params.setMargins(10, 10, 10, 10);
        view.setLayoutParams(params);

        return new DynamicImgAdapter.RecommendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final DynamicImgAdapter.RecommendViewHolder recommendViewHolder = (RecommendViewHolder) holder;

        Utils.loadCropRadius(thumbnailPicUrls.get(position), recommendViewHolder.imageView, 5);

        recommendViewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageView(dynamicListModel, position, recommendViewHolder);
            }
        });


    }

    @Override
    public int getItemCount() {
        return thumbnailPicUrls.size();
    }

    class RecommendViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;

        public RecommendViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.item_iv_img);
        }
    }


    private void showImageView(DynamicListModel item, int position, RecommendViewHolder recommendViewHolder) {
        ArrayList<String> originalPicUrls = (ArrayList<String>) item.getOriginalPicUrls();

        //获取一个自定义遮盖层View
        View customView = LayoutInflater.from(context).inflate(R.layout.layout_custom_view, null);
        ImageView ic_close = (ImageView) customView.findViewById(R.id.iv_close);
        final TextView tv_number_indicator = (TextView) customView.findViewById(R.id.tv_number_indicator);
        ic_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //关闭图片浏览
                MNImageBrowser.finishImageBrowser();
            }
        });

        tv_number_indicator.setText((position + 1) + "/" + originalPicUrls.size());


        MNImageBrowser.with(context)
                //页面切换效果
                .setTransformType(transformType)
                //指示器效果
                .setIndicatorType(indicatorType)
                //设置隐藏指示器
                .setIndicatorHide(false)
                //设置自定义遮盖层，定制自己想要的效果，当设置遮盖层后，原本的指示器会被隐藏
                .setCustomShadeView(showCustomShadeView ? customView : null)
                //当前位置
                .setCurrentPosition(position)
                //图片引擎
                .setImageEngine(imageEngine)
                //图片集合
                .setImageList(originalPicUrls)
                //方向设置
                .setScreenOrientationType(screenOrientationType)
                //页面切换监听
                .setOnPageChangeListener(new OnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {

                        if (tv_number_indicator != null) {
                            tv_number_indicator.setText((position + 1) + "/" + MNImageBrowser.getImageList().size());
                        }
                    }
                })
                //全屏模式
                .setFullScreenMode(isFulScreenMode)
                //打开动画
                .setActivityOpenAnime(openAnim)
                //关闭动画
                .setActivityExitAnime(exitAnim)
                //关闭动画
                //显示：传入当前View
                .show(recommendViewHolder.imageView);
    }
}
