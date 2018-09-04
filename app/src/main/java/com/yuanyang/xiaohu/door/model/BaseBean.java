package com.yuanyang.xiaohu.door.model;

public class BaseBean<T> extends BaseModel {

    private T messageBody;
    private int code;
    private String describe;

    public T getMessageBody() {
        return messageBody;
    }

    public int getCode() {
        return code;
    }

    public String getDescribe() {
        return describe;
    }

    /**
     * 请求是否成功
     *
     * @return
     */
    public boolean isSuccess() {
        if (code== 1000) {
            return true;
        } else {
            return false;
        }
    }

}
