package com.moxin.videoline.modle;

public class GuildUserModel {


    /**
     * user_nickname : 好现实啊啊
     * avatar : http://videoline.qiniu.bugukj.com/80859201902100053156346.jpg
     * id : 100186
     * status : 0
     */

    private String user_nickname;
    private String avatar;
    private int id;
    private int status;
    private String guild_id;
    private int level;
    private String income_total;

    public String getIncome_total() {
        return income_total;
    }

    public void setIncome_total(String income_total) {
        this.income_total = income_total;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getGuild_id() {
        return guild_id;
    }

    public void setGuild_id(String guild_id) {
        this.guild_id = guild_id;
    }

    public String getUser_nickname() {
        return user_nickname;
    }

    public void setUser_nickname(String user_nickname) {
        this.user_nickname = user_nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
