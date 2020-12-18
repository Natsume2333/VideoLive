package com.moxin.videoline.modle;

public class GuardRulesBean {

    /**
     * code : 1
     * msg :
     * data : {"time":"守护到期时间；空不是本主播的守护","list":[{"id":"购买守护规则id","title":"购买名称","day":"时间（天）","coin":"消费的金币"}]}
     */

    private String published_time;
    private String post_title;
    private String post_content;

    public String getPublished_time() {
        return published_time;
    }

    public void setPublished_time(String published_time) {
        this.published_time = published_time;
    }

    public String getPost_title() {
        return post_title;
    }

    public void setPost_title(String post_title) {
        this.post_title = post_title;
    }

    public String getPost_content() {
        return post_content;
    }

    public void setPost_content(String post_content) {
        this.post_content = post_content;
    }
}
