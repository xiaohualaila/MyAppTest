package com.yuanyang.xiaohu.door.model;

import cn.com.library.event.IBus;

public class UploadModel implements IBus.IEvent{

    public  String[] strings;

    public AccessModel model;

    public UploadModel(String[] strings, AccessModel model) {
        this.strings = strings;
        this.model = model;
    }

    @Override
    public int getTag() {
        return 13;
    }
}
