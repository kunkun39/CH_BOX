package com.changhong.faq.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import com.changhong.faq.domain.Examination;
import com.changhong.faq.view.DataLoader;
import com.changhong.faq.view.VoteSubmitAdapter;
import com.changhong.faq.view.VoteSubmitItem;
import com.changhong.faq.view.VoteSubmitViewPager;
import com.changhong.faq.R;

import java.util.ArrayList;
import java.util.List;

public class VoteSubmitActivity extends Activity {

    /**
     * 显示问题的页面
     */
    private VoteSubmitViewPager viewPager;

    /**
     * 处理问题的适配器
     */
    private VoteSubmitAdapter pagerAdapter;

    /**
     * 每一个问题VIEW
     */
    private List<View> viewItems = new ArrayList<View>();

    /**
     * 每一个问题
     */
    private ArrayList<VoteSubmitItem> dataItems = new ArrayList<VoteSubmitItem>();

    /**
     * 问卷的Title
     */
    private TextView examinationTitle;

    /**
     * 用户选择的问卷
     */
    private Examination examination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();

        initData();
    }

    private void initView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_vote_submit);
        examinationTitle = (TextView) findViewById(R.id.vote_submit_tabbar_title);
    }

    /**
     * 页面初始化
     */
    private void initData() {
        Intent intent = getIntent();
        examination = (Examination) intent.getSerializableExtra("examination");

        /**
         * 设置问卷答题的每一个View
         */
        for (int i = 0; i < examination.getQuestions().size(); i++) {
            viewItems.add(getLayoutInflater().inflate(R.layout.vote_submit_viewpager_item, null));
        }
        viewPager = (VoteSubmitViewPager) findViewById(R.id.vote_submit_viewpager);
        dataItems = new DataLoader(examination).getData();
        pagerAdapter = new VoteSubmitAdapter(this, examination, viewItems, dataItems);
        viewPager.setAdapter(pagerAdapter);
        viewPager.getParent().requestDisallowInterceptTouchEvent(false);

        /**
         * 设置TITLE
         */
        examinationTitle.setText(examination.getTitle());
    }

    /**
     * @param index 根据索引值切换页面
     */
    public void setCurrentView(int index) {
        viewPager.setCurrentItem(index);
    }
}
