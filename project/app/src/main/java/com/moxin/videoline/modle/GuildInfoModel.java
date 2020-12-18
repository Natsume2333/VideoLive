package com.moxin.videoline.modle;

public class GuildInfoModel {

    /**
     * id : 1
     * user_id : 100186
     * name : 测试公会
     * logo : http://videoline.qiniu.bugukj.com/video_cover_img/1551062367544_headImage.jpg
     * introduce : 工会介绍
     * create_time : 1551062367
     * status : 0
     */

    private String id;
    private int user_id;
    private String name;
    private String logo;
    private String introduce;
    private int create_time;
    private int status;
    private String total_profit;
    private String day_total_profit;
    private String num;

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getTotal_profit() {
        return total_profit;
    }

    public void setTotal_profit(String total_profit) {
        this.total_profit = total_profit;
    }

    public String getDay_total_profit() {
        return day_total_profit;
    }

    public void setDay_total_profit(String day_total_profit) {
        this.day_total_profit = day_total_profit;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public int getCreate_time() {
        return create_time;
    }

    public void setCreate_time(int create_time) {
        this.create_time = create_time;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
