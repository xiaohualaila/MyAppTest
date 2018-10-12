package com.yuanyang.xiaohu.door.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class CardBean {
    @Id(autoincrement = true)
    private Long id;

    private String num;

    @Generated(hash = 532224635)
    public CardBean(Long id, String num) {
        this.id = id;
        this.num = num;
    }

    @Generated(hash = 516506924)
    public CardBean() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNum() {
        return this.num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    
}
