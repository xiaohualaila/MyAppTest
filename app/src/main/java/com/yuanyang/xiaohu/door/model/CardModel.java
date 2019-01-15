package com.yuanyang.xiaohu.door.model;


import com.yuanyang.xiaohu.door.event.IBus;

public class CardModel implements IBus.IEvent{

    public  String card_no;

    public AccessModel model;

    public CardModel(String card_no, AccessModel model) {
        this.card_no = card_no;
        this.model = model;
    }

    @Override
    public int getTag() {
        return 13;
    }
}
