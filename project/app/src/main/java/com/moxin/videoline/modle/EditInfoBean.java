package com.moxin.videoline.modle;

public class EditInfoBean {

    /**
     * code : 1
     * msg :
     * data : {"phone":"","height":"","weight":"","constellation":"","city":"","image_label":"","introduce":"","self_label":""}
     */

    private int code;
    private String msg;
    private DataBean data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * phone :
         * height :
         * weight :
         * constellation :
         * city :
         * image_label :
         * introduce :
         * self_label :
         */

        private String phone;
        private String height;
        private String weight;
        private String constellation;
        private String city;
        private String image_label;
        private String introduce;
        private String self_label;

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getHeight() {
            return height;
        }

        public void setHeight(String height) {
            this.height = height;
        }

        public String getWeight() {
            return weight;
        }

        public void setWeight(String weight) {
            this.weight = weight;
        }

        public String getConstellation() {
            return constellation;
        }

        public void setConstellation(String constellation) {
            this.constellation = constellation;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getImage_label() {
            return image_label;
        }

        public void setImage_label(String image_label) {
            this.image_label = image_label;
        }

        public String getIntroduce() {
            return introduce;
        }

        public void setIntroduce(String introduce) {
            this.introduce = introduce;
        }

        public String getSelf_label() {
            return self_label;
        }

        public void setSelf_label(String self_label) {
            this.self_label = self_label;
        }
    }
}
