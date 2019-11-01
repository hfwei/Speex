package com.iflytek.speex;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 文 件 名：PcmUtil
 * 创 建 人：魏海锋
 * 创建日期：2019/10/30 15:08
 * 邮    箱：hfwei@iflytek.com
 * 功    能：pcm原始音频压缩解压工具类
 * 修 改 人：
 * 修改时间：
 * 修改备注：
 */
public class PcmUtil {

    // 日志TAG
    private static final String TAG = PcmUtil.class.getSimpleName();

    // pcm原始音频压缩解压jni类实例
    private static Speex speex = new Speex();

    // pcm压缩线程池
    private static Executor encodeExecutor = Executors.newSingleThreadExecutor();

    // pcm解压线程池
    private static Executor decodeExecutor = Executors.newSingleThreadExecutor();

    /**
     * 原始pcm数据通过speex算法加密（压缩）
     *
     * @param srcFile  原始pcm文件
     * @param listener 加密（压缩）监听
     */
    public static void speexEncode(final String srcFile, final OnEncodeListener listener) {
        Log.i(TAG, "speexEncode, srcFile:" + srcFile);
        encodeExecutor.execute(new EncodeRunnable(srcFile, listener));
    }

    /**
     * 加密（压缩）后的pcm数据通过speex算法解密（解压）
     *
     * @param srcFile  pcm加密（压缩）文件
     * @param listener 解密（解压）监听
     */
    public static void speexDecode(final String srcFile, final OnDecodeListener listener) {
        Log.i(TAG, "speexDecode, srcFile:" + srcFile);
        decodeExecutor.execute(new DecodeRunnable(srcFile, listener));
    }

    /**
     * byte类型数组转换为short类型数组
     *
     * @param bytes byte类型数组
     * @return short类型数组
     */
    public static short[] bytesToShort(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        short[] shorts = new short[bytes.length / 2];
        ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts);
        return shorts;
    }


    /**
     * short类型数组转换为byte类型数组
     *
     * @param shorts short类型数组
     * @return byte类型数组
     */
    public static byte[] shortToBytes(short[] shorts) {
        if (shorts == null) {
            return null;
        }
        byte[] bytes = new byte[shorts.length * 2];
        ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().put(shorts);

        return bytes;
    }

    // pcm编码（压缩）监听接口
    public interface OnEncodeListener {
        void onEncodeData(byte[] data, int len);

        void onEncodeError(String errReason);

        void onEncodeFinish();
    }

    // pcm编码（压缩）Runnable
    static class EncodeRunnable implements Runnable {

        // 原始pcm文件
        private String srcFile;
        // 加密（压缩）监听
        private OnEncodeListener listener;

        EncodeRunnable(final String srcFile, final OnEncodeListener listener) {
            this.srcFile = srcFile;
            this.listener = listener;
        }

        @Override
        public void run() {
            Log.i(TAG, "speex encode begin");
            byte[] ret = new byte[1024];
            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(srcFile))) {
                int len = 1280;
                byte[] buffer = new byte[len];
                while (-1 != (len = bis.read(buffer))) {
                    int size = speex.encode(bytesToShort(buffer), 0, ret, len / 2);
                    if (null != listener) listener.onEncodeData(ret, size);
                }
                if (null != listener) listener.onEncodeFinish();
            } catch (IOException e) {
                Log.e(TAG, "speex encode err, msg:" + e.getMessage());
                if (null != listener) listener.onEncodeError(e.getMessage());
            }
            Log.i(TAG, "speex encode end");
        }
    }

    // pcm解码（解压）监听接口
    public interface OnDecodeListener {
        void onDecodeData(byte[] data, int len);

        void onDecodeError(String errReason);

        void onDecodeFinish();
    }

    // pcm解码（解压）Runnable
    static class DecodeRunnable implements Runnable {

        // pcm加密（压缩）文件
        private String srcFile;

        // 解密（解压）监听
        private OnDecodeListener listener;

        DecodeRunnable(final String srcFile, final OnDecodeListener listener) {
            this.srcFile = srcFile;
            this.listener = listener;
        }

        @Override
        public void run() {
            Log.i(TAG, "speex decode begin");
            short[] ret = new short[160];
            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(srcFile))) {
                int len = 20;
                byte[] buffer = new byte[len];
                while (-1 != (len = bis.read(buffer))) {
                    int size = speex.decode(buffer, ret, len);
                    if (null != listener) listener.onDecodeData(shortToBytes(ret), size * 2);
                }
                if (null != listener) listener.onDecodeFinish();
            } catch (IOException e) {
                Log.e(TAG, "speex decode err, msg:" + e.getMessage());
                if (null != listener) listener.onDecodeError(e.getMessage());
            }
            Log.i(TAG, "speex decode end");
        }
    }
}
