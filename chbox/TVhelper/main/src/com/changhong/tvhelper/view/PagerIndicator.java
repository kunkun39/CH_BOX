package com.changhong.tvhelper.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.changhong.common.utils.Utils;
import com.changhong.tvhelper.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yves Yang on 2016/4/21.
 */
public class PagerIndicator extends LinearLayout{
    List<ImageView> mIndicators = new ArrayList<ImageView>();

    public PagerIndicator(Context context) {
        super(context);
    }

    public PagerIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PagerIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setCurrent(int index){
        clearStatus();
        View view = this.getChildAt(index);
        if (view == null)
            return;

        view.setSelected(true);
        LinearLayout.LayoutParams  layoutParams = (LinearLayout.LayoutParams)view.getLayoutParams();
        layoutParams.height = layoutParams.width = Utils.dip2px(getContext(), 15);
        view.setLayoutParams(layoutParams);
    }

    public void setIndicatorCount(int count){
        mIndicators.clear();
        this.removeAllViews();

        for(int i  = 0 ; i < count; i ++ ){
            ImageView indicator = new ImageView(getContext());
            indicator.setImageResource(R.drawable.indicator_item);
            LinearLayout.LayoutParams linearLayout = new LinearLayout.LayoutParams(Utils.dip2px(getContext(), 10),Utils.dip2px(getContext(), 10));
            linearLayout.setMargins(Utils.dip2px(getContext(), 10),0,0,0);
            indicator.setLayoutParams(linearLayout);

            mIndicators.add(indicator);
            this.addView(indicator);
        }
        if (count > 0){
            mIndicators.get(0).setSelected(true);
        }
        requestLayout();
    }

    public int getIndicatorCount(){
        return mIndicators.size();
    }

    public void clearStatus(){
        for (ImageView view : mIndicators){
            view.setSelected(false);
            LinearLayout.LayoutParams  layoutParams = (LinearLayout.LayoutParams)view.getLayoutParams();
            layoutParams.height = layoutParams.width = Utils.dip2px(getContext(), 10);
            view.setLayoutParams(layoutParams);
        }
    }
}
