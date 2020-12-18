package com.moxin.videoline.json;

import com.moxin.videoline.modle.EvaluateModel;

import java.util.List;

public class JsonRequestDoGetEvaluate extends JsonRequestBase{
    private List<EvaluateModel> list;

    public List<EvaluateModel> getList() {
        return list;
    }

    public void setList(List<EvaluateModel> list) {
        this.list = list;
    }
}
