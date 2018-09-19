package com.yuanyang.xiaohu.door.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.bjw.utils.SerialHelper;
import com.yuanyang.xiaohu.door.model.AccessModel;
import com.yuanyang.xiaohu.door.model.CardBean;
import com.yuanyang.xiaohu.door.model.EventModel;
import com.yuanyang.xiaohu.door.model.UploadModel;
import com.yuanyang.xiaohu.door.util.Constants;
import com.yuanyang.xiaohu.greendaodemo.greendao.gen.CardBeanDao;
import com.yuanyang.xiaohu.greendaodemo.greendao.gen.GreenDaoManager;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import cn.com.library.event.BusProvider;
import cn.com.library.log.XLog;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;


public class CardService extends Service {
    
    public MyThread thread; //发送获取数据指令线程
    private boolean flag = true;
    private SerialHelper serialHelper;
    private StringBuffer stringBuffer;
    
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        init();
        thread = new MyThread();
        thread.start();
    }

    /**
     * 初始化串口
     */
    private void init() {
        serialHelper = new SerialHelper() {
            @Override
            protected void onDataReceived(final com.bjw.bean.ComBean comBean) {

            }
        };
        serialHelper.setPort(Constants.PORT);
        serialHelper.setBaudRate(Constants.BAUDRATE);
        stringBuffer = new StringBuffer();
        try {
            serialHelper.open();
        
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("sss", e.getMessage());
            BusProvider.getBus().post(new EventModel("串口打开失败"));
        }
    }



    /**
     * 发送取值命令
     */
    private class MyThread extends Thread {
        @Override
        public void run() {
            while (flag) {


                try {
                    sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //判断卡号是否存在
    private boolean findCardInfo(String card_no){
        boolean isRightCard = false;
       CardBean cardBean = GreenDaoManager.getInstance().getSession().getCardBeanDao().queryBuilder()
                .where(CardBeanDao.Properties.Num.eq(card_no)).build().unique();
       if(cardBean!=null){
           isRightCard = true;
       }
       return isRightCard;
    }

    /**
     * 开门代码
     */
    private void openDoor(final String[] strings, final AccessModel model) {
        int num = model.getRelay();//那个门
        /**四个继电器的*/
        byte[] sendArr = new byte[5];//打开继电器指令
        sendArr[0] = (byte) 0xFF;
        sendArr[1] = (byte) (num == 1 ? 0x01 : num == 2 ? 0x02 : num == 3 ? 0x03 : num == 4 ? 0x04 : 0x01); //0x25全开
        sendArr[2] = 0x01;
        sendArr[3] = (byte) (num == 1 ? 0x02 : num == 2 ? 0x03 : num == 3 ? 0x04 : num == 4 ? 0x05 : 0x02); //0x26全开
        sendArr[4] = (byte) 0xEE;
        final byte[] sendArr_ = new byte[5];//复位继电器指令
        sendArr_[0] = (byte) 0xFF;
        sendArr_[1] = (byte) (num == 1 ? 0x01 : num == 2 ? 0x02 : num == 3 ? 0x03 : num == 4 ? 0x04 : 0x01);//0x25全关
        sendArr_[2] = 0x00;
        sendArr_[3] = (byte) (num == 1 ? 0x01 : num == 2 ? 0x02 : num == 3 ? 0x03 : num == 4 ? 0x04 : 0x01);//0x25全关
        sendArr_[4] = (byte) 0xEE;
        serialHelper.send(sendArr);
        Observable.timer(300, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread()).subscribe(new Observer<Long>() {
            @Override
            public void onSubscribe(Disposable d) {
                XLog.e("对继电器复位");
            }

            @Override
            public void onNext(Long value) {
                serialHelper.send(sendArr_);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {
                BusProvider.getBus().post(new EventModel(strings[0].trim().equals("001") ? "Success!开门成功！" : "预约开门成功"));
                BusProvider.getBus().post(new UploadModel(strings, model));
            }
        });
    }




    @Override
    public void onDestroy() {
        super.onDestroy();
        flag = false;
        serialHelper.close();
    }


}
