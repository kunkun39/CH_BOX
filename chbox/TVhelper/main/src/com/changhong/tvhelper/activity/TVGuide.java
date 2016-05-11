package com.changhong.tvhelper.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
    String[] guidesContent = new String[]{"Use your phone as a remote","Cast local photos,music and \nvideos from your phone to TV","Mirror phone screen on \nyour TV Screen"};
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
        mIndicator = (PagerIndicator)findViewById(R.id.indicator);
        mViewpager.setOnPageChangeListener(this);
        mIndicator.setIndicatorCount(mViewpager.getAdapter().getCount());
        mIndicator.setCurrent(0);
        mViewpager.setOffscreenPageLimit(3);
    }

    class GuideAdapter extends PagerAdapter{
        List<ImageView> items = new ArrayList<ImageView>();
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
            ImageView imageView;
            if (items.size() > position){
                imageView = items.get(position);
            }else {
                imageView = new ImageView(TVGuide.this);
                imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                imageView.setImageResource(guides[position]);
                items.add(imageView);
            }
            if (imageView.getParent() != null){
                ((ViewGroup)imageView.getParent()).removeView(imageView);
            }
            container.addView(imageView);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View)object);
        }

        @Override
        public int getItemPosition(Object object) {
            return items.indexOf(object);
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
        Animation animationOut = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
        Animation animationIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        if (i == mViewpager.getAdapter().getCount() - 1){
            mNext.setAnimation(animationOut);
            mSkip.setAnimation(animationOut);
            mStart.setAnimation(animationIn);
            mNext.setVisibility(View.GONE);
            mSkip.setVisibility(View.GONE);
            mStart.setVisibility(View.VISIBLE);
        }else {
            mNext.setAnimation(animationIn);
            mSkip.setAnimation(animationIn);
            mStart.setAnimation(animationOut);
            mNext.setVisibility(View.VISIBLE);
            mSkip.setVisibility(View.VISIBLE);
            mStart.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }
}
