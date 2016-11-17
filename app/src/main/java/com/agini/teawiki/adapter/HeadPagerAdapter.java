package com.agini.teawiki.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by Yamato on 2016/11/15.
 */
public class HeadPagerAdapter extends PagerAdapter {
    private Context mContext;
    private List<ImageView> mImageViews;
    public HeadPagerAdapter(Context context, List<ImageView> imageViews) {
        Log.d("flag", "-------------->HeadPagerAdapter: " +imageViews.size());
        mContext=context;
        mImageViews=imageViews;


    }

    @Override
    public int getCount() {
        return mImageViews!=null?mImageViews.size():0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;

    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {



       View ret = mImageViews.get(position);

        container.addView(ret);
        //add listeners here if necessary

        return ret;
    }
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//                super.destroyItem(container, position, object);
        container.removeView(mImageViews.get(position));
    }
}
