package com.yuanyang.xiaohu.door.model;

import com.yuanyang.xiaohu.door.event.IBus;

public class NetStateModel  implements IBus.IEvent{
    public boolean state;

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public NetStateModel(boolean state) {
        this.state = state;
    }

    @Override
    public int getTag() {
        return 0;
    }
}
