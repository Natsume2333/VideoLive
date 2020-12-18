package com.moxin.videoline.modle;

import com.moxin.videoline.json.JsonRequestBase;

public class JsonDoGetVideoCallInfoModel extends JsonRequestBase{
    private String ext;

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }
}
