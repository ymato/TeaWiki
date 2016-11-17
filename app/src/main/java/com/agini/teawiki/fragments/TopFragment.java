package com.agini.teawiki.fragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.agini.teawiki.R;
import com.agini.teawiki.adapter.HeadPagerAdapter;
import com.agini.teawiki.adapter.TopPagerAdapter;
import com.agini.teawiki.bean.HeadLine;
import com.agini.teawiki.bean.Top;
import com.agini.teawiki.callback.ImageCallback;
import com.agini.teawiki.net.ImageAsyncTask;
import com.agini.teawiki.ui.DetailsActivity;
import com.agini.teawiki.utils.HttpUtils;
import com.agini.teawiki.utils.NetworkUtils;
import com.agini.teawiki.utils.SdCardUtils;
import com.alibaba.fastjson.JSON;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.agini.teawiki.utils.HttpUtils.getByteFromUrl;

/**
 * A simple {@link Fragment} subclass.
 */
public class TopFragment extends Fragment {

    String url = "http://sns.maimaicha.com/api?apikey=b4f4ee31a8b9acc866ef2afb754c33e6&format=json&method=news.getHeadlines&rows=15&page=%d";
    String headUrl = "http://sns.maimaicha.com/api?apikey=b4f4ee31a8b9acc866ef2afb754c33e6&format=json&method=news.getSlideshow";

    private int index = 1;
    private PullToRefreshListView mListView;

    private BaseAdapter mAdapter;
    private List<ImageView> mImage = new ArrayList<>();
    private PagerAdapter   headAdapter = new HeadPagerAdapter(getContext(), mImage);;


    List<Top.Data> mData = new ArrayList<>();


    List<HeadLine.DataBean> headData = new ArrayList<>();

    private int currentPosition = 1;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                // TODO: 2016/11/12
                case 1:
                    //接收下载的数据
                    byte[] bytes = (byte[]) msg.obj;

                    Log.d("flag", "-------------->handleMessage: " + bytes.length);
                    //保存数据到sdCard
                    String root = getContext().getExternalCacheDir().getAbsolutePath();
                    String fileName = "top" + index;
                    SdCardUtils.saveFile(bytes, root, fileName);
                    Top top = JSON.parseObject(new String(bytes), Top.class);
                    Log.d("flag", "-------------->handleMessage: top.toString()" + top.toString());
                    List<Top.Data> datas = top.getData();
                    Log.d("flag", "-------------->handleMessage: datas.size()" + datas.size());
                    mData.addAll(datas);
                    Log.d("flag", "-------------->handleMessage: mData.size()" + mData.size());
                    mAdapter.notifyDataSetChanged();


                    break;
                case 2:
                    //接收下载的数据
                    byte[] headBytes = (byte[]) msg.obj;
                    Log.d("flag", "-------------->handleMessage: headBytes.length) " + headBytes.length);
                    //保存数据到sdCard
                    String root1 = getContext().getExternalCacheDir().getAbsolutePath();
                    String headFileName = "head" + index;
                    SdCardUtils.saveFile(headBytes, root1, headFileName);
                    HeadLine headLine = JSON.parseObject(new String(headBytes), HeadLine.class);
                    Log.d("flag", "-------------->handleMessage: headLine.toString()" + headLine.toString());
                    List<HeadLine.DataBean> headDatas = headLine.getData();
                    Log.d("flag", "-------------->handleMessage:headDatas.size() " + headDatas.size());
                    headData.addAll(headDatas);
                    Log.d("flag", "-------------->handleMessage:headData " + headData.get(0).getImage());


                    for (int i = 0; i < headData.size(); i++) {
                        final ImageView imageView = new ImageView(getContext());
                        Log.d("flag", "-------------->initViewPager: " + headData.get(i).getImage_s());
                        new ImageAsyncTask(new ImageCallback() {
                            @Override
                            public void callback(byte[] data) {
                                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                imageView.setImageBitmap(bitmap);
                                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                mImage.add(imageView);
                                headAdapter.notifyDataSetChanged();
                            }
                        }).execute(headData.get(i).getImage());
                    }


                    break;

            }
        }
    };

    public TopFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View ret = inflater.inflate(R.layout.fragment_top, container, false);
        initView(ret);
        initData(index);
        initListView();


        return ret;
    }

    private void initData(int index) {
        if (NetworkUtils.isConnected(getContext())) {
            //如果有网络从网上下载数据
            String path = String.format(url, index);
            //String path=headUrl;
            Log.d("flag", "-------------->initData: " + path);
            getByteFromUrl(path, mHandler);
        } else {
            //无网络从本地读取
            String root = getContext().getExternalCacheDir().getAbsolutePath();
            String fileName = root + File.separator + "top" + index;
            byte[] bytes = SdCardUtils.getbyteFromFile(fileName);
            if (bytes != null) {
                Top top = JSON.parseObject(new String(bytes), Top.class);
                List<Top.Data> datas = top.getData();
                mData.addAll(datas);
                mAdapter.notifyDataSetChanged();

            }

        }
    }


    private void initListView() {
        mAdapter = new TopPagerAdapter(getContext(), mData);
        final ListView listView = mListView.getRefreshableView();
        View headerView = LayoutInflater.from(getContext()).inflate(R.layout.header_view, listView, false);
        initViewPager(headerView);

        listView.addHeaderView(headerView);
        mListView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), DetailsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("id", mData.get(position - 2).getId());
                bundle.putString("title", mData.get(position - 2).getTitle());
                Log.d("flag", "-------------->onItemClick: " + mData.get(0).getTitle());
                bundle.putString("source", mData.get(position - 2).getSource());
                bundle.putString("time", mData.get(position - 2).getCreate_time());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setIcon(android.R.drawable.btn_star);
                builder.setTitle("删除条目");
                builder.setMessage("是否删除");
                builder.setNegativeButton("取消", null);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mData.remove(position - 2);
                        mAdapter.notifyDataSetChanged();
                    }
                });
                builder.create().show();
                return true;

            }
        });


        mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                mData.removeAll(mData);
                initData(index);
                mAdapter.notifyDataSetChanged();
                mListView.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        mListView.onRefreshComplete();
                    }
                }, 1000);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                index++;
                Log.d("flag", "-------------->onPullUpToRefresh: " + index);
                initData(index);
                mListView.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        mListView.onRefreshComplete();
                    }
                }, 1000);

                // mListView.onRefreshComplete();

            }

        });
        mListView.setMode(PullToRefreshBase.Mode.BOTH);

    }

    private void initViewPager(View headerView) {
        ViewPager viewPager = (ViewPager) headerView.findViewById(R.id.viewPager);
        if (NetworkUtils.isConnected(getContext())) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    byte[] datas = HttpUtils.getByteFromUrl(headUrl);
                    Log.d("flag", "-------------->run: datas.toString()" + datas.toString());
                    Message msg = Message.obtain();
                    msg.obj = datas;
                    msg.what = 2;
                    mHandler.sendMessage(msg);
                }
            }).start();
        } else {
            //无网络从本地读取
            String root = getContext().getExternalCacheDir().getAbsolutePath();
            String fileName = root + File.separator + "head" + index;
            byte[] datas = SdCardUtils.getbyteFromFile(fileName);
            if (datas != null) {
                HeadLine headLine = JSON.parseObject(new String(datas), HeadLine.class);
                List<HeadLine.DataBean> headDatas = headLine.getData();
                headData.addAll(headDatas);
                headAdapter.notifyDataSetChanged();
            }
        }
            for (int i = 0; i < headData.size(); i++) {

                ImageView imageView = new ImageView(getContext());
                imageView.setImageResource(R.mipmap.ic_launcher);
                mImage.add(imageView);
            }



            viewPager.setAdapter(headAdapter);


        }


    private void initView(View ret) {
        mListView = (PullToRefreshListView) ret.findViewById(R.id.topListView);

    }


}
