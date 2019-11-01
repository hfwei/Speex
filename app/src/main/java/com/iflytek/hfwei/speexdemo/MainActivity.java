package com.iflytek.hfwei.speexdemo;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.iflytek.speex.PcmUtil;
import com.iflytek.speex.Speex;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class MainActivity extends AppCompatActivity {

    private Speex speex = new Speex();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    encodeNew();
                    Thread.sleep(5);
                    decodeNew();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void encode() {
        byte[] ret = new byte[1024];
        try {
            Log.i("hfwei90", "encode begin");
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream("/sdcard/hfwei/3.pcm"));
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream("/sdcard/hfwei/3-encode.speex"));
            int len = 1280;
            byte[] buffer = new byte[len];
            while (-1 != (len = bis.read(buffer))) {
                int size = speex.encode(bytesToShort(buffer), 0, ret, len / 2);
                bos.write(ret, 0, size);
//                Thread.sleep(40);
            }
            bos.flush();
            Log.i("hfwei90", "encode end");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void decode() {
        short[] ret = new short[160];
        try {
            Log.i("hfwei90", "decode begin");
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream("/sdcard/hfwei/3-encode.speex"));
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream("/sdcard/hfwei/3-decode.pcm"));
            int len = 20;
            byte[] buffer = new byte[len];
            while (-1 != (len = bis.read(buffer))) {
                int size = speex.decode(buffer, ret, len);
                bos.write(shortToBytes(ret), 0, size * 2);
//                Thread.sleep(40);
            }
            bos.flush();
            Log.i("hfwei90", "decode end");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static short[] bytesToShort(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        short[] shorts = new short[bytes.length / 2];
        ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts);
        return shorts;
    }

    public static byte[] shortToBytes(short[] shorts) {
        if (shorts == null) {
            return null;
        }
        byte[] bytes = new byte[shorts.length * 2];
        ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().put(shorts);

        return bytes;
    }

    public void encodeNew() throws IOException {
        final BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream("/sdcard/hfwei/3-encode-new.speex"));
        PcmUtil.speexEncode("/sdcard/hfwei/3.pcm", new PcmUtil.OnEncodeListener() {
            @Override
            public void onEncodeData(byte[] data, int len) {
                try {
                    bos.write(data, 0, len);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onEncodeError(String errReason) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onEncodeFinish() {
                try {
                    bos.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void decodeNew() throws IOException {
        final BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream("/sdcard/hfwei/3-decode.pcm"));
        PcmUtil.speexDecode("/sdcard/hfwei/3-encode.speex", new PcmUtil.OnDecodeListener() {
            @Override
            public void onDecodeData(byte[] data, int len) {
                try {
                    bos.write(data, 0, len);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onDecodeError(String errReason) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onDecodeFinish() {
                try {
                    bos.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
