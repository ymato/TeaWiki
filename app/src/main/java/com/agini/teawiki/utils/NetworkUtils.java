package com.agini.teawiki.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

/**
 * Created by softpo on 2016/11/4.
 */

public class NetworkUtils {
    public static boolean isConnected(Context context){
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //获取当前活跃网络
        NetworkInfo activeNetworkInfo =
                connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo == null) {
            Toast.makeText(context, "当前无网络", Toast.LENGTH_LONG).show();
            return false;

        }else{
            switch (activeNetworkInfo.getType()){
                case ConnectivityManager.TYPE_WIFI:
                    Toast.makeText(context,"当前网络是WIFI", Toast.LENGTH_LONG).show();
                    return true;
                case ConnectivityManager.TYPE_MOBILE:
                    Toast.makeText(context,"当前网络是移动网络",Toast.LENGTH_LONG).show();
                    return true;
            }
        }


        return  false;
    }
}
