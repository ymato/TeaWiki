package com.agini.teawiki.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.agini.teawiki.R;
import com.agini.teawiki.bean.Top;
import com.agini.teawiki.callback.ImageCallback;
import com.agini.teawiki.net.ImageAsyncTask;
import com.agini.teawiki.utils.MyLruCache;
import com.agini.teawiki.utils.SdCardUtils;

import java.io.File;
import java.util.List;

/**
 * Created by Yamato on 2016/11/12.
 */
public class TopPagerAdapter extends BaseAdapter {
    private Context mContext;
    private List<Top.Data> mData;
    private MyLruCache myLruCache;
    public TopPagerAdapter(Context context, List<Top.Data> data) {
        Log.d("flag", "-------------->TopPagerAdapter: " +data.size());
        mContext=context;
        mData=data;
        int maxSize = (int) (Runtime.getRuntime().maxMemory()/8);
        //就是一个链表，相当于List
        //分配了内存了空间的八分之一
        myLruCache = new MyLruCache(maxSize);
    }

    @Override
    public int getCount() {
        if(mData.size()==0)
            return 0;

        return mData!=null?mData.size():0;
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View ret=null;
        ViwHolder holder;
        if(convertView!=null){
            ret=convertView;
            holder= (ViwHolder) ret.getTag();

        }else{
            ret= LayoutInflater.from(mContext).inflate(R.layout.top_list_item,parent,false);
            holder=new ViwHolder();
            holder.title= (TextView) ret.findViewById(R.id.title);
            holder.source= (TextView) ret.findViewById(R.id.source);
            holder.author= (TextView) ret.findViewById(R.id.author);
            holder.date= (TextView) ret.findViewById(R.id.date);
            holder.image= (ImageView) ret.findViewById(R.id.pic);
            ret.setTag(holder);
        }
        holder.title.setText(mData.get(position).getTitle());
        holder.source.setText(mData.get(position).getSource());
        Log.d("flag", "-------------->getView: " +mData.size());
        holder.date.setText(mData.get(position).getCreate_time());
        //对图片进行赋值
        //缓存是LruCache：Latest，Recently，Used
        //最近最少使用原则
        //LruCache 是内存缓存

        String imageUrl=mData.get(position).getWap_thumb();
        if (imageUrl.equals("")){
            holder.image.setVisibility(View.GONE);
        }else{
            Bitmap cacheBitmap=getCache(imageUrl);
            if (cacheBitmap != null) {
                holder.image.setImageBitmap(cacheBitmap);
            }else {//第三级获取数据---->网络获取数据
                //设置标志，防止图片错位
                holder.image.setTag(imageUrl);
                getNetImage(imageUrl,holder.image);
            }
        }


        return ret;
    }

    private void getNetImage(final String imageUrl, final ImageView image) {
        new ImageAsyncTask(new ImageCallback() {
            @Override
            public void callback(byte[] data) {
                String tag = (String)image.getTag();

                if(tag.equals(imageUrl)){
                    //设置显示图片
                    Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);
                    image.setImageBitmap(bm);

                    //存起来
                    //存到内存中,第一级缓存
                    myLruCache.put(imageUrl.replaceAll("/",""),bm);

                    //存入磁盘中，第二级缓存
                    String root = mContext.getExternalCacheDir().getAbsolutePath();
                    SdCardUtils.saveFile(data,root,imageUrl.replaceAll("/",""));

                }

            }
        }).execute(imageUrl);

    }

    private Bitmap getCache(String imageUrl) {
        //从缓存中获取数据
        //从内存中获取数据---->myLruCache---->第一级
        imageUrl=imageUrl.replaceAll("/","");
        Bitmap bitmap = myLruCache.get(imageUrl);
        if (bitmap != null) {
            Log.d("flag", "----------------->getCache: 从内存LruCache中获取数据");
            return bitmap;
        }else {//内存中没有该图片的数据
            //从磁盘获取这个数据----->第二级
            String root = mContext.getExternalCacheDir().getAbsolutePath();
            String fileName = root+ File.separator+imageUrl;
            byte[] bytes = SdCardUtils.getbyteFromFile(fileName);
            if (bytes != null) {
                Bitmap bitmapSd = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                //将磁盘中数据保存到内存中
                myLruCache.put(imageUrl,bitmapSd);
                Log.d("flag", "----------------->getCache: 从磁盘获取数据");
                //从Sd卡获取了该图片
                return bitmapSd;
            }
        }





        return null;
    }

    public static class ViwHolder{
    private TextView title,source,author,date;
    private ImageView image;
}
}
