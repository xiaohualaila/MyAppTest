package com.yuanyang.xiaohu.door.activity;

import android.app.smdt.SmdtManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import com.yuanyang.xiaohu.door.R;
import com.yuanyang.xiaohu.door.present.StartPresent;

import cn.com.library.kit.ToastManager;
import cn.com.library.mvp.XActivity;
import cn.com.library.net.NetError;

public class StartActivity  extends XActivity<StartPresent> {

    private Handler handler = new Handler();
    private SmdtManager smdt;
    private String mac = "";
    @Override
    public void initData(Bundle savedInstanceState) {
        smdt = SmdtManager.create(this);
        mac = smdt.smdtGetEthMacAddress();
        String banzi = Build.MODEL;
        String ip = smdt.smdtGetEthIPAddress();
        if(mac != null && ip != null){
            getP().initDate(mac,banzi);
        }else {
            ToastManager.showShort(context,"网络异常,请检查网络！");
            toAccessDoorAct();
        }
    }

    private void toAccessDoorAct() {
        handler.postDelayed(() -> {
            startActivity(new Intent(StartActivity.this,AccessDoorActivity2.class));
            finish();
        },4000);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_start;
    }

    @Override
    public StartPresent newP() {
        return new StartPresent();
    }

    public void showError(NetError error) {
        ToastManager.showShort(context, error.getMessage());
    }
}
