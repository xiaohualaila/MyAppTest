package com.yuanyang.xiaohu.door.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class CardBean {
    @Id
    private String id;

    private String name;

    private String num;

    @Generated(hash = 961368415)
    public CardBean(String id, String name, String num) {
        this.id = id;
        this.name = name;
        this.num = num;
    }

    @Generated(hash = 516506924)
    public CardBean() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }
}
