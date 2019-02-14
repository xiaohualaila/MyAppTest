package com.yuanyang.xiaohu.door.activity.start;

import android.content.Context;

import com.yuanyang.xiaohu.door.base.IBasePresenter;
import com.yuanyang.xiaohu.door.base.IBaseView;

public class StartContract {
    interface View extends IBaseView<Presenter> {
        void showError(String error);
        void toAccessDoorActivity();
    }

    interface Presenter extends IBasePresenter {
        void initDate(String mac,Context context);

    }
}
