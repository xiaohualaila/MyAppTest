package com.yuanyang.xiaohu.door.present;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.util.Log;

import com.yuanyang.xiaohu.door.R;
import com.yuanyang.xiaohu.door.activity.AccessDoorActivity;
import com.yuanyang.xiaohu.door.model.AccessModel;
import com.yuanyang.xiaohu.door.model.BaseBean;
import com.yuanyang.xiaohu.door.net.BillboardApi;
import com.yuanyang.xiaohu.door.net.UserInfoKey;
import com.yuanyang.xiaohu.door.util.AppSharePreferenceMgr;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.com.library.encrpt.Base64Utils;
import cn.com.library.encrpt.TDESUtils;
import cn.com.library.kit.ToastManager;
import cn.com.library.log.XLog;
import cn.com.library.mvp.XPresent;
import cn.com.library.net.ApiSubscriber;
import cn.com.library.net.NetError;
import cn.com.library.net.XApi;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class AccessPresent extends XPresent<AccessDoorActivity> {

    private MediaPlayer mediaPlayer;

    public void initMusic() {
        mediaPlayer = new MediaPlayer();
        startMusic(1);
    }

    /**
     * 播放音乐
     */
    public void startMusic(int select) {
        mediaPlayer = new MediaPlayer();
        AssetFileDescriptor file = getV().getResources().openRawResourceFd(select == 1 ? R.raw.dingdong : select == 2 ? R.raw.success : R.raw.fail);
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(),
                    file.getLength());
            mediaPlayer.prepare();
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.start();
    }


    /**
     * 上传门禁日志
     */
    public void uploadLog(String[] strings, AccessModel model) {
        String directionDoor = AppSharePreferenceMgr.get(getV(), UserInfoKey.OPEN_DOOR_DIRECTION_ID, "").toString();
        BillboardApi.getDataService().uploadLog(strings[4], strings[5], strings[1], strings[2], strings[3], directionDoor, model.getAccessible(),
                "", "", "").compose(XApi.<BaseBean>getApiTransformer())
                .compose(XApi.<BaseBean>getScheduler())
                .compose(getV().<BaseBean>bindToLifecycle())
                .subscribe(new ApiSubscriber<BaseBean>() {
                    @Override
                    protected void onFail(NetError error) {
                        ToastManager.showShort(getV(), "上传日志失败！");
                    }

                    @Override
                    public void onNext(BaseBean model) {
                        if (model.isSuccess()) {
                            ToastManager.showShort(getV(), "上传日志成功！");
                        } else {
                            ToastManager.showShort(getV(), model.getDescribe());
                        }
                    }
                });
    }


    /**
     * 退出页面销毁
     */
    public void onDestroy() {
        if (mediaPlayer != null)
            mediaPlayer.release();
    }


}
