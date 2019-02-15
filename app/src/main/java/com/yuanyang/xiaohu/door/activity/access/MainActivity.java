package com.yuanyang.xiaohu.door.activity.access;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.bjw.utils.SerialHelper;
import com.yuanyang.xiaohu.door.R;
import com.yuanyang.xiaohu.door.event.BusProvider;
import com.yuanyang.xiaohu.door.model.EventModel;
import com.yuanyang.xiaohu.door.util.Constants;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private SerialHelper serialHelper;
    private Button button1,button2,button3,button4;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        button1 =findViewById(R.id.button);
        button2 =findViewById(R.id.button2);
        button3 =findViewById(R.id.button3);
        button4 =findViewById(R.id.button4);

        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);
        serialHelper = new SerialHelper() {
            @Override
            protected void onDataReceived(final com.bjw.bean.ComBean comBean) {

            }
        };
        serialHelper.setPort(Constants.PORT_ttyS2);
        serialHelper.setBaudRate(Constants.BAUDRATE);
        try {
            serialHelper.open();
        } catch (IOException e) {
            e.printStackTrace();
            BusProvider.getBus().post(new EventModel("ttyS2串口打开失败"));
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button:
                openDoor("11","10");
                break;
            case R.id.button2:
                openDoor("21","20");
                break;
            case R.id.button3:
                openDoor("31","30");
                break;
            case R.id.button4:
                openDoor("41","40");
                break;

        }

    }

    /**
     * 刷二维码开门
     */
    private void openDoor(String open,String close) {
        if (serialHelper.isOpen()) {
            //serialHelper.send(getArrOpenDoor(model.getRelay()));
            serialHelper.sendHex(open);
            Log.i("sss","门已打开！");
        }else {
            Log.i("sss","串口没有打开！");
        }

        Observable.timer(500, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread()).subscribe(new Observer<Long>() {
            @Override
            public void onSubscribe(Disposable d) {
                //XLog.e("对继电器复位");
            }

            @Override
            public void onNext(Long value) {
                if (serialHelper.isOpen()) {
//                    serialHelper.send(getArrCloseDoor(model.getRelay()));
                    serialHelper.sendHex(close);
                } else {
                    BusProvider.getBus().post(new EventModel("串口都没打开"));
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }
}
