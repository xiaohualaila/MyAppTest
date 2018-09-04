package com.yuanyang.xiaohu.door.model;

import cn.com.library.event.IBus;

public class MusicModel implements IBus.IEvent{

    public int num;

    public MusicModel(int num) {
        this.num = num;
    }

    @Override
    public int getTag() {
        return 12;
    }
}
