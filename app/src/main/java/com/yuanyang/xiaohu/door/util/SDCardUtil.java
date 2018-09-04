package com.yuanyang.xiaohu.door.util;

import android.content.Context;
import android.os.storage.StorageManager;
import android.util.Log;

import java.lang.reflect.Method;

public class SDCardUtil {

    /**获取SD卡路径*/
    public static String getStoragePath(Context context) {
        try {
            StorageManager sm = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
            Method getVolumePathsMethod = StorageManager.class.getMethod("getVolumePaths", (Class<?>[]) null);
            String[] paths = (String[]) getVolumePathsMethod.invoke(sm, (Object[]) null);
            return paths.length > 1 ? paths[0] : paths[0];
        } catch (Exception e) {
            Log.e("info", "getStoragePath() failed", e);
        }
        return null;
    }

}
