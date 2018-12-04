package com.yuanyang.xiaohu.door.broadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.yuanyang.xiaohu.door.activity.AccessDoorActivity;
import com.yuanyang.xiaohu.door.activity.AccessDoorActivity2;

import cn.com.library.kit.ToastManager;


/**
 * Created by admin on 2017/11/2.
 */

public class BootBroadcastReceiver extends BroadcastReceiver {
    public static final String ACTION = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION)) {
          //  Intent in = new Intent(context, AccessDoorActivity.class);
            Intent in = new Intent(context, AccessDoorActivity2.class);
            in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(in);
        }
        ToastManager.showShort(context, "开机启动成功");
    }
}
