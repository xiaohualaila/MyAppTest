package com.yuanyang.xiaohu.door;

import android.app.Application;
import android.content.Context;

import com.yuanyang.xiaohu.door.util.SoundPoolUtil;


/**
 * Created by wanglei on 2016/12/31.
 */

public class App extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        SoundPoolUtil.getInstance(this);

    }

    public static Context getContext() {
        return context;
    }
}
