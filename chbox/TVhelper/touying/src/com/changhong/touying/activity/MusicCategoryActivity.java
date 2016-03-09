package com.changhong.touying.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.changhong.common.widgets.BoxSelecter;
import com.changhong.touying.R;
import com.changhong.touying.adapter.FragmentAdapter;
import com.changhong.touying.service.MusicService;
import com.changhong.touying.service.MusicServiceImpl;
import com.changhong.touying.tab.MusicCategoryAllTab;
import com.changhong.touying.tab.MusicCategoryPlaylistTab;
import com.changhong.touying.tab.MusicCategorySpecialTab;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jack Wang
 */
public class MusicCategoryActivity extends AppCompatActivity {

	/************************************************** IP连接部分 *******************************************************/
	
    private DrawerLayout mDrawerLayout;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
	private BoxSelecter ipSelecter;

	public static MusicCategoryActivity musicCategory = null;

	public static MusicCategoryActivity getInstance() {
		if (null == musicCategory) {
			musicCategory = new MusicCategoryActivity();
		}
		return musicCategory;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		initEvent();

		/**
		 * 启动歌词扫描服务
		 */
		MusicService musicService = new MusicServiceImpl(
				MusicCategoryActivity.this);
		musicService.findAllMusicLrc();
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
//		getMenuInflater().inflate(R.menu.touying, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == android.R.id.home) {

			finish();
		} else if (item.getItemId() == R.id.ipbutton) {
			mDrawerLayout.openDrawer(GravityCompat.START);
		}
		return true;
	}
	
	
    private void setupViewPager() {
        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        List<String> titles = new ArrayList<String>();
        titles.add("  单曲  ");
        titles.add("  歌手  ");
        titles.add("  歌单  ");
        mTabLayout.addTab(mTabLayout.newTab().setText(titles.get(0)));
        mTabLayout.addTab(mTabLayout.newTab().setText(titles.get(1)));
        mTabLayout.addTab(mTabLayout.newTab().setText(titles.get(2)));
        List<Fragment> fragments = new ArrayList<Fragment>();
        
        fragments.add(new MusicCategoryAllTab());
        fragments.add(new MusicCategorySpecialTab());
        fragments.add(new MusicCategoryPlaylistTab());
        
		
        FragmentAdapter adapter =
                new FragmentAdapter(getSupportFragmentManager(), fragments, titles);
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabsFromPagerAdapter(adapter);
    }

	private void initView() {
		/**
		 * IP连接部分
		 */
		setContentView(R.layout.activity_category);
		
		mDrawerLayout = (DrawerLayout) findViewById(R.id.touying_drawer);
		Toolbar toolbar = (Toolbar) this.findViewById(R.id.toolbar);
		toolbar.setTitle(" ");
		setSupportActionBar(toolbar);

		final ActionBar ab = getSupportActionBar();
		ab.setDisplayHomeAsUpEnabled(true);
		
		
		 mViewPager = (ViewPager) findViewById(R.id.viewpager);
	     setupViewPager();

	}

	private void initEvent() {

		/**
		 * IP连接部分
		 */
		ipSelecter = new BoxSelecter(this, (TextView)findViewById(R.id.title), (ListView)findViewById(R.id.clients), new Handler(getMainLooper()));


	}


	/********************************************** 系统发发重载 *********************************************************/

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			finish();
			break;
		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (ipSelecter != null) {
			ipSelecter.release();
		}

	}
	@Override
	protected void onSaveInstanceState(Bundle outState) {
	}
}
