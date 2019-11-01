package com.charein.speex.lib.speex;

/**
 * 文 件 名：Speex
 * 创 建 人：魏海锋
 * 创建日期：2019/10/30 10:27
 * 邮    箱：hfwei@iflytek.com
 * 功    能：
 * 修 改 人：
 * 修改时间：
 * 修改备注：
 */
public class Speex {
    private static final int DEFAULT_COMPRESSION = 4;

    static {
        System.loadLibrary("speex");
    }

    public Speex() {
        this.open(DEFAULT_COMPRESSION);
    }

    public native int open(int var1);

    public native int getFrameSize();

    public native int decode(byte[] var1, short[] var2, int var3);

    public native int encode(short[] var1, int var2, byte[] var3, int var4);

    public native void close();
}
