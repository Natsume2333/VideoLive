package com.moxin.videoline.json;

import com.moxin.videoline.modle.SelectIncomeLogModel;

import java.util.List;

public class JsonGetSelectIncomeLog extends JsonRequestBase {
    private List<SelectIncomeLogModel> list;

    public List<SelectIncomeLogModel> getList() {
        return list;
    }

    public void setList(List<SelectIncomeLogModel> list) {
        this.list = list;
    }
}
