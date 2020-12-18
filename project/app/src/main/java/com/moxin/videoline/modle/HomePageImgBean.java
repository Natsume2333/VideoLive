package com.moxin.videoline.modle;

import com.stx.xhb.xbanner.entity.SimpleBannerInfo;

public class HomePageImgBean extends SimpleBannerInfo {
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public Object getXBannerUrl() {
        return url;
    }
}
