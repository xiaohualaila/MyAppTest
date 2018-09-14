package com.yuanyang.xiaohu.door.util;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

import com.yuanyang.xiaohu.door.R;


/**
 * Created by Administrator on 2017/12/27.
 */

public class SoundPoolUtil {
    private static SoundPoolUtil soundPoolUtil;
    private static SoundPool soundPool;


    //单例模式
    public static SoundPoolUtil getInstance(Context context) {
        if (soundPoolUtil == null)
            soundPoolUtil = new SoundPoolUtil(context);
        return soundPoolUtil;
    }

    private SoundPoolUtil(Context context) {
        soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 10);
        //加载音频文件
        soundPool.load(context, R.raw.dingdong, 1);
        soundPool.load(context, R.raw.success, 2);
        soundPool.load(context, R.raw.fail, 3);

    }

    public static void play(int number) {
        Log.d("tag", "number " + number);
        //播放音频
        soundPool.play(number, 1.0f, 1.0f, 0, 0, 1);
    }
}
