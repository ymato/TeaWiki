package com.agini.teawiki.ui;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.agini.teawiki.R;
import com.agini.teawiki.adapter.MyPagerAdapter;
import com.agini.teawiki.animation.TabletTransformer;
import com.agini.teawiki.fragments.BusinessFragment;
import com.agini.teawiki.fragments.DataFragment;
import com.agini.teawiki.fragments.ReferFragment;
import com.agini.teawiki.fragments.TopFragment;
import com.agini.teawiki.fragments.WikiFragment;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private SlidingMenu mSlidingMenu;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private List<String> tabName;
    private List<Fragment> mFragmentList;
    private FragmentPagerAdapter adapter;
    private ImageView mImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initTabLayout();
        initViewPager();
        initSlidingMenu();


    }

    private void initSlidingMenu() {
        //实例化
        mSlidingMenu=new SlidingMenu(this);
        //添加到activity
        mSlidingMenu.attachToActivity(this,SlidingMenu.SLIDING_CONTENT);
        //设置Mode
        mSlidingMenu.setMode(SlidingMenu.RIGHT);
        //添加布局到slidingMenu
        mSlidingMenu.setMenu(R.layout.searchmenu);
        mSlidingMenu.bringToFront();
        mSlidingMenu.setBehindOffset(getResources().getDisplayMetrics().widthPixels/3);
        mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);










    }

    private void initViewPager() {
        mFragmentList=new ArrayList<>();
        mFragmentList.add(new TopFragment());
        mFragmentList.add(new WikiFragment());
        mFragmentList.add(new ReferFragment());
        mFragmentList.add(new BusinessFragment());
        mFragmentList.add(new DataFragment());
        adapter=new MyPagerAdapter(getSupportFragmentManager(),mFragmentList,tabName);
       mViewPager.setPageTransformer(true,new TabletTransformer());




        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);





    }
    private void initView() {
        mTabLayout= (TabLayout) findViewById(R.id.tab_layout);
        mViewPager= (ViewPager) findViewById(R.id.view_pager);
        mImageView= (ImageView) findViewById(R.id.imageView);
    }
    private void initTabLayout() {
        tabName=new ArrayList<>();
        tabName.add("头条");
        tabName.add("百科");
        tabName.add("咨询");
        tabName.add("经营");
        tabName.add("数据");
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        for (int i = 0; i < tabName.size(); i++) {
            mTabLayout.addTab(mTabLayout.newTab().setText(tabName.get(i)));
        }
    }
    private boolean isContinued = false;
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if ( isContinued) {
            finish();
        } else {
            Toast.makeText(this, "再按一次退出茶百科", Toast.LENGTH_SHORT).show();
            isContinued = true;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2 * 1000);
                        isContinued = false;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    public void showMenu(View view) {
        mSlidingMenu.showMenu();
    }
}
