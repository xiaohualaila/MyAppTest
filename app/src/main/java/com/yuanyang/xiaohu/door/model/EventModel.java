package com.yuanyang.xiaohu.door.model;

import cn.com.library.event.IBus;

public class EventModel implements IBus.IEvent{

    public String value;

    public EventModel(String value) {

        this.value = value;
    }


    @Override
    public int getTag() {
        return 10;
    }
}
