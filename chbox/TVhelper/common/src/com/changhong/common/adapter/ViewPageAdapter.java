package com.changhong.common.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.changhong.common.fragment.TabFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yves Yang on 2016/2/24.
 */
public class ViewPageAdapter extends FragmentPagerAdapter {
    List<TabFragment> list = new ArrayList<TabFragment>();

    public ViewPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return list.get(position).getName();
    }

    public ViewPageAdapter addItem(TabFragment fragment){
        list.add(fragment);
        notifyDataSetChanged();
        return this;
    }
}
