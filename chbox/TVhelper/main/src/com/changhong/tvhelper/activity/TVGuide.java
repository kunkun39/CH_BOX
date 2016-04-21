package com.changhong.tvhelper.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.changhong.tvhelper.R;
import com.changhong.tvhelper.view.PagerIndicator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yves Yang on 2016/4/20.
 */
public class TVGuide extends Activity implements ViewPager.OnPageChangeListener{

    int[] guides = new int[]{R.drawable.guide1,R.drawable.guide2,R.drawable.guide3};
    String[] guidesTitle = new String[]{"Mobile Remote","Cast Media","Screen Mirroring"};
    String[] guidesContent = new String[]{"Use you phone as a remote","Cast local photos,music and \nvideos from your phone to TV","Mirror phone screen on \nyour TV Screen"};
    ViewPager mViewpager;
    PagerIndicator mIndicator;
    Button mSkip,mNext,mStart;
    TextView mTitle,mContent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_command);
        mViewpager =((ViewPager)findViewById(R.id.viewpager));
        mSkip = (Button)findViewById(R.id.btn_skip);
        mStart = (Button)findViewById(R.id.btn_start);
        mNext = (Button)findViewById(R.id.btn_next);
        mTitle = (TextView)findViewById(R.id.discription_title);
        mContent = (TextView)findViewById(R.id.discription_content);
        mSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewpager.setCurrentItem(mViewpager.getCurrentItem() + 1);
            }
        });
        mTitle.setText(guidesTitle[0]);
        mContent.setText(guidesContent[0]);
        mViewpager.setAdapter(new GuideAdapter());
        mIndicator = new PagerIndicator(this);
        mViewpager.setOnPageChangeListener(this);
        mIndicator.setIndicatorCount(mViewpager.getAdapter().getCount());
    }

    class GuideAdapter extends PagerAdapter{
        @Override
        public int getCount() {
            return guides.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView = new ImageView(TVGuide.this);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            imageView.setImageResource(guides[position]);
            container.addView(imageView);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View)object);
        }
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {
        mIndicator.setCurrent(i);
        mTitle.setText(guidesTitle[i]);
        mContent.setText(guidesContent[i]);
        if (i == mViewpager.getAdapter().getCount() - 1){
            mNext.setVisibility(View.GONE);
            mSkip.setVisibility(View.GONE);
            mStart.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }
}
