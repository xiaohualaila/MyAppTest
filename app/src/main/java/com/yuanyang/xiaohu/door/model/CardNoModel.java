package com.yuanyang.xiaohu.door.model;

import cn.com.library.event.IBus;

public class CardNoModel implements IBus.IEvent{

    public String value;

    public int scanBox;

    public AccessModel accessModel;


    public CardNoModel(String value, int scanBox,AccessModel model) {

        this.value = value;
        this.scanBox = scanBox;
        this.accessModel = model;
    }

    @Override
    public int getTag() {
        return 10;
    }
}
