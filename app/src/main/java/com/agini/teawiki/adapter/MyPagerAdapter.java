package com.agini.teawiki.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by Yamato on 2016/11/10.
 */
public class MyPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragmentList;
    private List<String> tabName;
    public MyPagerAdapter(FragmentManager fm, List<Fragment> fragmentList, List<String> tabName) {
        super(fm);
        this.fragmentList=fragmentList;
        this.tabName=tabName;

    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return tabName.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabName.get(position%tabName.size());
    }
}
