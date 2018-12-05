package com.yuanyang.xiaohu.door.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import com.yuanyang.xiaohu.door.R;

public class StartActivity extends AppCompatActivity {

    private Handler handler = new Handler();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                /**
                 * 两个版本在此处切换
                 */
                startActivity(new Intent(StartActivity.this,AccessDoorActivity.class));
               //startActivity(new Intent(StartActivity.this,AccessDoorActivity2.class));
            }
        },4000);

    }
}
