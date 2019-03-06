package com.yuanyang.xiaohu.door.activity.start;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import com.yuanyang.xiaohu.door.R;
import com.yuanyang.xiaohu.door.activity.access.AccessDoorActivity2;
import com.yuanyang.xiaohu.door.util.NetUtil;

import butterknife.ButterKnife;


public class StartActivity  extends AppCompatActivity implements StartContract.View {
    private StartContract.Presenter presenter;
    private Handler handler = new Handler();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        ButterKnife.bind(this);
        new StartPresent(this);

        String mac = NetUtil.smdtGetEthMacAddress();
        mac="1C:CA:E3:35:8B:98";
        String ip = NetUtil.smdtGetEthIPAddress();

        if(mac != null && ip != null){
            presenter.initDate(mac,this);
        }else {
            showError("网络异常,请检查网络！");
            toAccessDoorAct();
        }
    }


    private void toAccessDoorAct() {
        handler.postDelayed(() -> {
            startActivity(new Intent(StartActivity.this,AccessDoorActivity2.class));
            finish();
        },4000);
    }

    public int getLayoutId() {
        return R.layout.activity_start;
    }

    public void showError(String error) {
        Toast.makeText(this,error,Toast.LENGTH_LONG).show();
    }

    @Override
    public void toAccessDoorActivity() {
        startActivity(new Intent(this,AccessDoorActivity2.class));
        finish();
    }

    @Override
    public void setPresenter(StartContract.Presenter presenter) {
        this.presenter = presenter;
    }
}
