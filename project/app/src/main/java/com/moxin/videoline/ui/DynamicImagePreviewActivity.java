package com.moxin.videoline.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.moxin.videoline.R;
import com.moxin.videoline.base.BaseActivity;
import com.moxin.videoline.modle.DynamicListModel;
import com.qmuiteam.qmui.widget.QMUIPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class DynamicImagePreviewActivity extends BaseActivity {

//    @BindView(R.id.photo_view)
//    PhotoView photoView;

    public static final String IMAGE_PATH = "IMAGE_PATH";
    public static final String IMAGE_PATH_POSI = "IMAGE_PATH_POSI";
    private DynamicListModel imagePath;
    private ViewPager viewPager;
    private List<String> originalPicUrls;
    private List<View> mImageViews = new ArrayList<>();
    private int posi;
    private QMUIPagerAdapter qmuiPagerAdapter;

    @Override
    protected Context getNowContext() {
        return this;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_dynamic_image_preview;
    }

    @Override
    protected void initView() {
        ButterKnife.bind(this);

        viewPager = (ViewPager) findViewById(R.id.viewPager);



    }

    @Override
    protected void initSet() {

    }

    @Override
    protected void initData() {
        imagePath = getIntent().getParcelableExtra(IMAGE_PATH);
        posi = getIntent().getIntExtra(IMAGE_PATH_POSI, 0);



//        originalPicUrls = imagePath.getOriginalPicUrls();
//        for (int i = 0; i < originalPicUrls.size(); i++) {
//            ImageView imageView = new ImageView(this);
//            Utils.loadHttpImg(this, originalPicUrls.get(i), imageView, 0);
//
//            mImageViews.add(imageView);
//        }
//
//        newAdaper();
//
//        //设置Adapter
//        viewPager.setAdapter(qmuiPagerAdapter);


//        Utils.loadHttpImg(this,imagePath,photoView,0);
    }

    private QMUIPagerAdapter newAdaper() {


        qmuiPagerAdapter = new QMUIPagerAdapter() {
            @Override
            protected Object hydrate(ViewGroup container, int position) {

                return mImageViews.get(position);
            }

            @Override
            protected void populate(ViewGroup container, Object item, int position) {
                View v = mImageViews.get(position);
                ViewGroup parent = (ViewGroup) v.getParent();
                if (parent != null) {
                    parent.removeAllViews();
                }
                container.addView(mImageViews.get(position));
            }

            @Override
            protected void destroy(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }

            @Override
            public int getCount() {
                return mImageViews.size();
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                return view == object;
            }
        };

        return qmuiPagerAdapter;
    }

    @Override
    protected void initPlayerDisplayData() {

    }

    @OnClick({R.id.iv_back})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:

                finish();
                break;
            default:
                break;
        }
    }


}
