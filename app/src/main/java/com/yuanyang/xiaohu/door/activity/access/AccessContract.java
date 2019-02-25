package com.yuanyang.xiaohu.door.activity.access;

import android.content.Context;

import com.yuanyang.xiaohu.door.base.IBasePresenter;
import com.yuanyang.xiaohu.door.base.IBaseView;
import com.yuanyang.xiaohu.door.model.AccessModel;


/**
 * Created by Administrator on 2017/6/3.
 */

public interface AccessContract {
    interface View extends IBaseView<Presenter> {
        void showError(String error);
        void updateVersion(String apkurl, String s_ver);
    }

    interface Presenter extends IBasePresenter {
        void uploadLog(Context context, String[] strings, String mac, AccessModel model);

        void uploadCardLog(Context context,String cardNo, String mac, AccessModel model);

        void queryServer(Context context,String mac, String cardNo,int box,AccessModel accessModel);

        void sendState(String mac, String ip);

        void uploadRecordLog();
    }
}
