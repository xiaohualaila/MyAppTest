package com.yuanyang.xiaohu.door.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;

/**
 * 3288板子控制IO的工具类
 */

public class IOUtil {


    public static void door_io_1(int num){
        String strTemp = "busybox echo 1 > /sys/class/gpio_xrm/gpio" + num + "/data";
        executer(strTemp);
    }

    public static void door_io_0(int num){
        String strTemp = "busybox echo 0 > /sys/class/gpio_xrm/gpio" + num + "/data";
        executer(strTemp);
    }

    public static String executer(String command) {

        StringBuffer output = new StringBuffer();
        Process process = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(command + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = "";
            while ((line = reader.readLine())!= null) {
                output.append(line + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null)
                    os.close();
                process.destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (output.toString().equals(""))
        {
            return "";
        }
        String response = output.toString().trim().substring(0, output.length() - 1);
        return response;
    }
}
