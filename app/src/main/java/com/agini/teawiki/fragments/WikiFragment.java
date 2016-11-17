package com.agini.teawiki.fragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.agini.teawiki.R;
import com.agini.teawiki.adapter.WikiPagerAdapter;
import com.agini.teawiki.bean.Wiki;
import com.agini.teawiki.ui.DetailsActivity;
import com.agini.teawiki.utils.NetworkUtils;
import com.agini.teawiki.utils.SdCardUtils;
import com.alibaba.fastjson.JSON;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

import static com.agini.teawiki.utils.HttpUtils.getByteFromUrl;

/**
 * A simple {@link Fragment} subclass.
 */
public class WikiFragment extends Fragment {


    String url = "http://sns.maimaicha.com/api?apikey=b4f4ee31a8b9acc866ef2afb754c33e6&format=json&method=news.getListByType&type=16&rows=15&page=&d";


    private int index = 1;
    private PullToRefreshListView mListView;
    private BaseAdapter mAdapter;
    List<Wiki.DataBean> mData = new ArrayList<>();





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
                    String fileName = "wiki" + index;
                    SdCardUtils.saveFile(bytes, root, fileName);
                    Wiki wiki = JSON.parseObject(new String(bytes), Wiki.class);
                    Log.d("flag", "-------------->handleMessage: top.toString()" + wiki.toString());
                    List<Wiki.DataBean> datas = wiki.getData();
                    Log.d("flag", "-------------->handleMessage: datas.size()" + datas.size());
                    mData.addAll(datas);
                    Log.d("flag", "-------------->handleMessage: mData.size()" + mData.size());
                    mAdapter.notifyDataSetChanged();
                    break;

            }
        }
    };

    public WikiFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View ret = inflater.inflate(R.layout.fragment_wiki, container, false);
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

        }

    }


    private void initListView() {
        mAdapter = new WikiPagerAdapter(getContext(), mData);
        final ListView listView = mListView.getRefreshableView();
        mListView.setAdapter(mAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(getContext(), DetailsActivity.class);
                Bundle bundle=new Bundle();
                bundle.putString("id",mData.get(position-1).getId());
                bundle.putString("title",mData.get(position-1).getTitle());
                Log.d("flag", "-------------->onItemClick: " +mData.get(0).getTitle());
                bundle.putString("source",mData.get(position-1).getSource());
                bundle.putString("time",mData.get(position-1).getCreate_time());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                builder.setIcon(android.R.drawable.btn_star);
                builder.setTitle("删除条目");
                builder.setMessage("是否删除");
                builder.setNegativeButton("取消",null);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mData.remove(position-1);
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











    private void initView(View ret) {
        mListView = (PullToRefreshListView) ret.findViewById(R.id.wikiListView);

    }

}
