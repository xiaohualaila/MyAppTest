package com.yuanyang.xiaohu.door.serialPortUtil;

import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import android_serialport_api.SerialPort;

/**
 * Created by WangChaowei on 2017/12/7.
 */

public class SerialPortUtils {

    private final String TAG = "SerialPortUtils";
    private String path = "/dev/ttyS1";
    private String path1 = "/dev/ttyS2";
    private String path2 = "/dev/ttyS3";
    private int baudrate = 9600;
    public boolean serialPortStatus = false; //是否打开串口标志
    public String data_;
    public boolean threadStatus; //线程状态，为了安全终止线程

    public SerialPort serialPort = null;
    public InputStream inputStream = null;
    public OutputStream outputStream = null;
    public ChangeTool changeTool = new ChangeTool();
    private List<String> devicies = new ArrayList<>();
    private String deviceName = "", openTime = "";

    /**
     * 打开串口
     *
     * @return serialPort串口对象
     */
    public SerialPort openSerialPort(String path) {
        try {
            serialPort = new SerialPort(new File(path), baudrate, 0);
            this.serialPortStatus = true;
            threadStatus = false; //线程状态
            //获取打开的串口中的输入输出流，以便于串口数据的收发
            inputStream = serialPort.getInputStream();
            outputStream = serialPort.getOutputStream();

//            new SendThread().start(); //开始线程监控发送指令
            new ReadThread().start(); //开始线程监控是否有数据要接收
        } catch (IOException e) {
            Log.e(TAG, "openSerialPort: 打开串口异常：" + e.toString());
            return serialPort;
        }
        Log.d(TAG, "openSerialPort: 打开串口");
        return serialPort;
    }

    /**
     * 关闭串口
     */
    public void closeSerialPort() {
        try {
            inputStream.close();
            outputStream.close();

            this.serialPortStatus = false;
            this.threadStatus = true; //线程状态
            serialPort.close();
        } catch (IOException e) {
            Log.e(TAG, "closeSerialPort: 关闭串口异常：" + e.toString());
            return;
        }
        Log.d(TAG, "closeSerialPort: 关闭串口成功");
    }

    /**
     * 发送串口指令（字符串）
     *
     * @param data String数据指令
     */
    public void sendSerialPort(String data) {
        Log.d(TAG, "sendSerialPort: 发送数据");

        try {
            byte[] sendData = data.getBytes(); //string转byte[]
            this.data_ = new String(sendData); //byte[]转string
            if (sendData.length > 0) {
                outputStream.write(sendData);
                outputStream.write('\n');
                //outputStream.write('\r'+'\n');
                outputStream.flush();
                Log.d(TAG, "sendSerialPort: 串口数据发送成功");
            }
        } catch (IOException e) {
            Log.e(TAG, "sendSerialPort: 串口数据发送失败：" + e.toString());
        }

    }

    /**
     * 发送串口指令（字节数组）
     *
     * @param data String数据指令
     */
    public void sendSerialPort(byte[] data) {
        Log.d(TAG, "sendSerialPort: 发送数据");

        try {
            if (data.length > 0) {
                outputStream.write(data);
                outputStream.write('\n');
                //outputStream.write('\r'+'\n');
                outputStream.flush();
                Log.d(TAG, "sendSerialPort: 串口数据发送成功");
            }
        } catch (IOException e) {
            Log.e(TAG, "sendSerialPort: 串口数据发送失败：" + e.toString());
        }

    }

    private class SendThread extends Thread {

        @Override
        public void run() {
            super.run();
            while (!threadStatus) {
//                Log.e("发送取值指令");
//                sendSerialPort("0133052123000000000000000000000000000303000000000000060101001000000301010010000003B304");
                while (true) {
                    for (int i=0; i<5;i++) {
                        int j = i + 1;
                        Log.i("ss","发送指令s" + j);
                        sendSerialPort("01330" + j + "2123000000000000000000000000000303000000000000060101001000000301010010000003" + (j == 1 ? "AF" : j == 2 ? "B0" : j == 3 ? "B1" : j == 4 ? "B2" : "B3") + "04");
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

    }

    /**
     * 单开一线程，来读数据
     */
    private class ReadThread extends Thread {
        @Override
        public void run() {
            super.run();
            //判断进程是否在运行，更安全的结束进程
            while (!threadStatus) {
                Log.d(TAG, "进入线程run");
                //64   1024
//                byte[] buffer = new byte[64];
//                    new DoorData().execute(inputStream);
                int size; //读取数据的大小
                try {
                    byte[] buffer = new byte[inputStream.available()];
                    size = inputStream.read(buffer);
                    if (size > 0) {
                        Log.i("ss","run: 接收到了数据：" + changeTool.ByteArrToHex(buffer));
                        Log.i("ss","run: 接收到了数据字符：" + new String(buffer));
                        Log.i("ss","run: 接收到了数据大小：" + String.valueOf(size));
                        onDataReceiveListener.onDataReceive(buffer, size);
                    }
                } catch (IOException e) {
                    Log.i("ss","run: 数据读取异常：" + e.toString());
                }
            }
        }
    }

    public OnDataReceiveListener onDataReceiveListener = null;

    public static interface OnDataReceiveListener {
        public void onDataReceive(byte[] buffer, int size);
    }

    public void setOnDataReceiveListener(OnDataReceiveListener dataReceiveListener) {
        onDataReceiveListener = dataReceiveListener;
    }

    class DoorData extends AsyncTask<InputStream, Integer, String> {

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.i("ss","s======" + s);
        }

        @Override
        protected String doInBackground(InputStream... inputStreams) {
            String data = new Scanner(inputStream).useDelimiter("\\Z").next();
            Log.i("ss","data======" + data);
            return data;
        }
    }


}
