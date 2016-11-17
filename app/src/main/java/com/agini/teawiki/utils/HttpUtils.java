package com.agini.teawiki.utils;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by softpo on 2016/11/4.
 */
public class HttpUtils {
    public static void getByteFromUrl(final String path, final Handler handler) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                InputStream is = null;
                ByteArrayOutputStream baos = null;
                try {
                    URL url = new URL(path);

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                    conn.setConnectTimeout(5000);

                    if(conn.getResponseCode() == 200){

                        is = conn.getInputStream();

                        baos = new ByteArrayOutputStream();

                        int len = 0;

                        byte[] buf = new byte[1024*8];

                        while ((len = is.read(buf))!=-1){
                            baos.write(buf,0,len);
                        }

                        //数据获取成功
                        byte[] data = baos.toByteArray();
                        Log.d("flag", "-------------->run: " +data);
                        //子线程，需要Handler发送数据给主线程

                        Message msg = Message.obtain();

                        msg.what = 1;

                        msg.obj = data;

                        handler.sendMessage(msg);
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (baos != null) {
                        try {
                            baos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }

    public static byte[] getByteFromUrl(String path) {

        InputStream is = null;
        ByteArrayOutputStream baos = null;
        try {
            URL url = new URL(path);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setConnectTimeout(5000);

            if(conn.getResponseCode() ==200){
                is = conn.getInputStream();
                baos = new ByteArrayOutputStream();
                int len = 0;
                byte[] buf = new byte[1024*8];

                while ((len = is.read(buf))!=-1){
                    baos.write(buf,0,len);
                }
                return baos.toByteArray();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (baos != null) {
                try {
                    baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
        return null;
    }
}
