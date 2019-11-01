package com.iflytek.speex;

/**
 * 文 件 名：Speex
 * 创 建 人：魏海锋
 * 创建日期：2019/10/30 14:42
 * 邮    箱：hfwei@iflytek.com
 * 功    能：pcm原始音频压缩解压jni类（压缩比为16）
 * 修 改 人：
 * 修改时间：
 * 修改备注：
 */
public class Speex {

    // 默认压缩类型（压缩包为16）
    private static final int DEFAULT_COMPRESSION = 4;

    static {
        try {
            System.loadLibrary("speex");
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public Speex() {
        open(DEFAULT_COMPRESSION);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        close();
    }

    public native int open(int compression);

    public native int getFrameSize();

    public native int decode(byte encoded[], short lin[], int size);

    public native int encode(short lin[], int offset, byte encoded[], int size);

    public native void close();
}
