package com.moxin.videoline.modle;

import com.stx.xhb.xbanner.entity.SimpleBannerInfo;

public class BannerImgBean extends SimpleBannerInfo {

    private String url;
    private String title;
    private String image;//路径

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public Object getXBannerUrl() {
        return image;
    }
}
