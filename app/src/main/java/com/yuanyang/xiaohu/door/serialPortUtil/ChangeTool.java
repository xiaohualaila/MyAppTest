package com.yuanyang.xiaohu.door.serialPortUtil;

/**
 * Created by WangChaowei on 2017/12/11.
 */

public class ChangeTool {
    //-------------------------------------------------------
    // 判断奇数或偶数，位运算，最后一位是1则为奇数，为0是偶数
    public static int isOdd(int num) {
        return num & 1;
    }

    //-------------------------------------------------------
    //Hex字符串转int
    public static int HexToInt(String inHex) {
        return Integer.parseInt(inHex, 16);
    }

    //-------------------------------------------------------
    //Hex字符串转byte
    public static byte HexToByte(String inHex) {
        return (byte) Integer.parseInt(inHex, 16);
    }

    //-------------------------------------------------------
    //1字节转2个Hex字符
    public static String Byte2Hex(Byte inByte) {
        return String.format("%02x", new Object[]{inByte}).toUpperCase();
    }

    //-------------------------------------------------------
    //字节数组转转hex字符串
    public static String ByteArrToHex(byte[] inBytArr) {
        StringBuilder strBuilder = new StringBuilder();
        for (byte valueOf : inBytArr) {
            strBuilder.append(Byte2Hex(Byte.valueOf(valueOf)));
            strBuilder.append(" ");
        }
        return strBuilder.toString();
    }

    //-------------------------------------------------------
    //字节数组转转hex字符串，可选长度
    public static String ByteArrToHex(byte[] inBytArr, int offset, int byteCount) {
        StringBuilder strBuilder = new StringBuilder();
        int j = byteCount;
        for (int i = offset; i < j; i++) {
            strBuilder.append(Byte2Hex(Byte.valueOf(inBytArr[i])));
        }
        return strBuilder.toString();
    }

    //-------------------------------------------------------
    //把hex字符串转字节数组
    public static byte[] HexToByteArr(String inHex) {
        byte[] result;
        int hexlen = inHex.length();
        if (isOdd(hexlen) == 1) {
            hexlen++;
            result = new byte[(hexlen / 2)];
            inHex = "0" + inHex;
        } else {
            result = new byte[(hexlen / 2)];
        }
        int j = 0;
        for (int i = 0; i < hexlen; i += 2) {
            result[j] = HexToByte(inHex.substring(i, i + 2));
            j++;
        }
        return result;
    }

    /**
     * 将Hex编码字符串转换成普通字符串
     *
     * @param hexStr Hex编码字符串
     * @return 普通字符串
     */
    public static String decodeHexStr(String hexStr) {
        String byteStr = new String(HexToByteArr(hexStr));
        return byteStr;
    }
    /**
     * 将Hex编码字符串求和
     * @param hexStr Hex编码字符串
     * @return 普通字符串
     */
    public static String makeChecksum(String hexStr) {
        if (hexStr == null || hexStr.equals("")) {
            return "";
        }
        int total = 0;
        int len = hexStr.length();
        int num = 0;
        while (num < len) {
            String s = hexStr.substring(num, num + 2);
//		   System.out.println(s);
            total += Integer.parseInt(s, 16);
            num = num + 2;
        }
        /**
         * 用256求余最大是255，即16进制的FF
         */
        int mod = total % 256;
        String hex = Integer.toHexString(mod);
        len = hex.length();
        // 如果不够校验位的长度，补0,这里用的是两位校验
        if (len < 2) {
            hex = "0" + hex;
        }
        return hex;
    }

    /**
     * 将Hex编码字符串求和
     * @param hexStr Hex编码字符串
     * @return 普通字符串(获取数据的指令)
     */
    public static String makeDataChecksum(String hexStr) {
        return hexStr + makeChecksum(hexStr) + "04";
    }

}
