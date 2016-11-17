package com.agini.teawiki.net;

import android.os.AsyncTask;
import android.util.Log;

import com.agini.teawiki.callback.ImageCallback;
import com.agini.teawiki.utils.HttpUtils;

/**
 * Created by Yamato on 2016/11/14.
 */

public class ImageAsyncTask extends AsyncTask<String ,Void,byte[]> {
    private ImageCallback mCallback;

    public ImageAsyncTask(ImageCallback callback) {
        mCallback = callback;
    }

    @Override
    protected byte[] doInBackground(String... params) {
        byte[] ret = null;

        ret = HttpUtils.getByteFromUrl(params[0]);

        return ret;
    }

    @Override
    protected void onPostExecute(byte[] bytes) {
        super.onPostExecute(bytes);
        if (bytes != null) {
            mCallback.callback(bytes);
        }else {
            Log.d("flag", "----------------->onPostExecute: 图片的异步任务失败");
        }
    }
}
