package com.yuanyang.xiaohu.door.broadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.yuanyang.xiaohu.door.event.BusProvider;
import com.yuanyang.xiaohu.door.model.EventModel;
import com.yuanyang.xiaohu.door.model.NetStateModel;

public class ConnectReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if(networkInfo!= null && networkInfo.getState().equals(NetworkInfo.State.CONNECTED)){
            Toast.makeText(context,"网路已连接", Toast.LENGTH_SHORT).show();
            BusProvider.getBus().post(new NetStateModel(true));
        }else {
            Toast.makeText(context,"没有网络", Toast.LENGTH_SHORT).show();
        }
    }
}
