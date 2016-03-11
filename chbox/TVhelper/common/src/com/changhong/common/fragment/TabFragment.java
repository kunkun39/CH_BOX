package com.changhong.common.fragment;

import android.os.Bundle;

/**
 * Created by Yves Yang on 2016/3/10.
 */
public class TabFragment extends RecycleViewFragment{
    public TabFragment(){
        super();
    }
    public static TabFragment newInstance( RecycleViewAdapter adapter, String param) {
        TabFragment fragment = new TabFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1,adapter);
        args.putString(ARG_PARAM2, param);
        fragment.setArguments(args);
        fragment.setName(param);
        fragment.setmAdapter(adapter);
        return fragment;
    }

    public static TabFragment newInstance( RecycleViewAdapter adapter, String param,int col) {
        TabFragment fragment = newInstance(adapter,param);
        fragment.getArguments().putInt(ARG_PARAM3, col);
        return fragment;
    }
    public String getName() {
        return super.getmParam2();
    }

    public void setName(String name) {
        super.setmParam2(name);
    }
}
