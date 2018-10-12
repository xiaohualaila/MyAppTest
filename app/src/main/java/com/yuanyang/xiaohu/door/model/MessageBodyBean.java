package com.yuanyang.xiaohu.door.model;

import java.util.List;

public class MessageBodyBean {
    private List<String> deletedcards;
    private List<String> addedcards;

    public List<String> getDeletedcards() {
        return deletedcards;
    }

    public void setDeletedcards(List<String> deletedcards) {
        this.deletedcards = deletedcards;
    }

    public List<String> getAddedcards() {
        return addedcards;
    }

    public void setAddedcards(List<String> addedcards) {
        this.addedcards = addedcards;
    }
}
