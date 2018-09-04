package com.yuanyang.xiaohu.door.serialPortUtil;



public class ComBean {

    public byte[] bRec=null;
    public String sRecTime="";
    public String sComPort="";
    public String scmd ="";
    public String sflag ="";
    public ComBean(String sPort, byte[] buffer, int size){

        sComPort = sPort;
        bRec=new byte[size];
        for (int i = 0; i < size; i++)
        {
            bRec[i]=buffer[i];
        }
        sRecTime = AppDateMgr.todayYyyyMmDdHhMmSs();
    }
}
