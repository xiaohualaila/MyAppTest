package com.yuanyang.xiaohu.door.broadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import cn.com.library.kit.ToastManager;

/**
 * Created by Carson_Ho on 16/10/31.
 */
public class NetWorkStateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = connMgr.getActiveNetworkInfo();
        if (activeNetwork != null) {
           if (activeNetwork.isConnected()) {
                 //  Log.i("sss", "当前移动网络连接可用 ");
               ToastManager.showShort(context, "网络连接可用！ ");
           } else {
               Log.i("sss", "当前没有网络连接，请确保你已经打开网络 ");
               ToastManager.showShort(context, "当前网络未连接！ ");
           }
        } else {
             Log.i("sss", "当前没有网络连接，请确保你已经打开网络 ");
            ToastManager.showShort(context, "当前网络未连接");
        }
    }
}
