package com.changhong.tvhelper.view;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils.TruncateAt;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.changhong.common.db.sqlite.DatabaseContainer;
import com.changhong.common.system.MyApplication;
import com.changhong.tvhelper.R;

public class SearchPageDefault extends Fragment{
	
	// 总共的条数
	private static final int MAX_ITEM_COUNT = 6;
	
	//用来测试的短语
	private static final String TEST_WORDS = "abcd...";
	
	//当前的View
	View view = null;
	
	// 动态创建的内部View
	LinearLayout contentView = null;
	
	// 相关数据库
	DatabaseContainer database =  MyApplication.databaseContainer;
	
	// 短语数据集
	private Map<String,Integer> values = new LinkedHashMap<String, Integer>();	
	
	// 每一行布局
	private List<LinearLayout> layouts = new LinkedList<LinearLayout>();
	
	// item的集合
	private Map<View,LinearLayout> views = new HashMap<View, LinearLayout>();
	
	// 绑定的activity
	Activity activity = null;
	
	/**
	 * 函数重写
	 * =================================================================================================================
	 */
	// 当绘制View的时候才将界面构建出来
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = activity;
		View view = activity.findViewById(R.id.search_page_content);
		view.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {		
			@Override
			public void onGlobalLayout() {
				if(SearchPageDefault.this.activity != null && !SearchPageDefault.this.view.isLayoutRequested())
				{
					addViews(SearchPageDefault.this.contentView);	
				}
				getView().getViewTreeObserver().removeGlobalOnLayoutListener(this);				
			}
		});					
	}
		
	//初始化View
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		if(view == null)
		{
			view = inflater.inflate(R.layout.search_page_idle, container,false);
		}
		else {
			ViewGroup v = (ViewGroup)view.getParent();
			if (v != null) 
				v.removeView(view);
		}
		
		contentView  = (LinearLayout)view.findViewById(R.id.search_idle_content);		
		values.clear();
		updateDatabase(activity);				
		return view;
	}
	
	// 添加View到根目录，只调用这个函数就能构建界面
	private void addViews(LinearLayout view)
	{
		view.removeAllViews();
		layouts.clear();		
		
		loadSentences(activity);		
		for (String name : values.keySet()) {
			View v = addSentence(name);
			if (v != null) {
				addItemIntoLayout(v);
			}			
		}
		
	}
	
	/**
	 * 数据库操作
	 * ===========================================================================================================
	 */
	//保存一个短语
	public boolean saveSentences(Activity activity,String text)
	{
		boolean isSuccess = true;
		if (database == null) {
			database = new DatabaseContainer(activity);
		}
		
		SQLiteDatabase sqLiteDatabase = database.getWritableDatabase();
		sqLiteDatabase.beginTransaction();
		try {		
			String sqlString = "SELECT * FROM " + DatabaseContainer.TABLE_NAME_SEARCH_HEAT + " WHERE search_name = ?";
			
			Cursor cursor = sqLiteDatabase.rawQuery(sqlString, new String[]{text});
			if (cursor.isAfterLast()) {
				sqlString = "INSERT INTO  " + DatabaseContainer.TABLE_NAME_SEARCH_HEAT 
						+ " ( search_name, search_time, search_count )" 
						+ " VALUES( ?, DATE('NOW'), 1)";				
				sqLiteDatabase.execSQL(sqlString, new String[]{text});
			}
			else {
				sqlString = "UPDATE " + DatabaseContainer.TABLE_NAME_SEARCH_HEAT 
						+ " SET search_time=DATE('NOW'), search_count=search_count+1" 
						+ " WHERE search_name=?";
				sqLiteDatabase.execSQL(sqlString, new String[]{text});
			}
			sqLiteDatabase.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();	
			isSuccess = false;
		}finally
		{
			sqLiteDatabase.endTransaction();
		}
		
		return isSuccess;
	}
	
	//加载短语
	public Map<String,Integer> loadSentences(Activity activity)
	{
		String sqlString = "SELECT * FROM " 
				+ DatabaseContainer.TABLE_NAME_SEARCH_HEAT 
				+ " ORDER BY search_count DESC";
		
		values.clear();
		if (database == null) {
			database = new DatabaseContainer(activity);
		}
		Cursor cursor = database.getReadableDatabase().rawQuery(sqlString, null);
		int count = MAX_ITEM_COUNT;		
		
		while (cursor.moveToNext()
				&&((count--) > 0)) {
			values.put(cursor.getString(0), cursor.getInt(2));					
		}
		return values;
	}
	
	//删除一条短语
	public void deleteSentences(Activity activity,String sentence)
	{
		String sqlString = "DELETE FROM " + DatabaseContainer.TABLE_NAME_SEARCH_HEAT + " WHERE search_name=?";
		if (database == null) {
			database = new DatabaseContainer(activity);
		}
		database.getReadableDatabase().execSQL(sqlString, new Object[]{sentence});		
	}
	
	//删除大于一个月的短语
	public void updateDatabase(Activity activity)
	{
		String sqlString = "DELETE FROM " + DatabaseContainer.TABLE_NAME_SEARCH_HEAT + " WHERE search_time BETWEEN 'NOW' AND DATE('-30 DAY')";
		if (database == null) {
			database = new DatabaseContainer(activity);
		}
		database.getReadableDatabase().execSQL(sqlString, new Object[]{});
	}
	
	
	/** 
	 * 私有函数
	 * ========================================================================================================
	 */
	private boolean addItemIntoLayout(View v)
	{
		LinearLayout layout;
		LinearLayout view = contentView;
		View testView = generalItem(activity, TEST_WORDS);
		int testWidth = getViewRect(testView).width();
		
		//当是第一条的时候直接添加进去
		if (layouts.size() == 0) {
			layout = generalLayout(activity);
			if(getViewRect(v).width() + testWidth > view.getWidth())
			{
				android.view.ViewGroup.LayoutParams params = v.getLayoutParams();
				params.width = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
				v.setLayoutParams(params);
			}
			
			layout.addView(v);			
			views.put(v, layout);
			layouts.add(layout);
			this.contentView.addView(layout);
			
			return true;
		}
		
		//获取一条已有的行，然后把现在的view加进去，否则就添加一条新行，加进去
		layout =  layouts.get(layouts.size() -1);
		int spaceLeft = view.getWidth();
		for (Entry<View, LinearLayout> entry : views.entrySet()) {
			if (entry.getValue() == layout) {
				spaceLeft = spaceLeft - getViewRect(entry.getKey()).width();
			}
		}

		boolean isSuccess = spaceLeft > getViewRect(v).width();
		if (isSuccess) {
			if (spaceLeft > getViewRect(v).width() + testWidth) {
				layout.addView(v);
				views.put(v, layout);
			}
			else {
				layout.addView(v);
				android.view.ViewGroup.LayoutParams params = v.getLayoutParams();
				params.width = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
				views.put(v, layout);
				v.setLayoutParams(params);
			}
			
			return true;			
		}
		else {
			if(spaceLeft > testWidth)
			{
				layout.addView(v);
				views.put(v, layout);
			}
			else {
				layout = generalLayout(activity);
				layout.addView(v);
				if(getViewRect(v).width() + testWidth > view.getWidth())
				{
					android.view.ViewGroup.LayoutParams params = v.getLayoutParams();
					params.width = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
					v.setLayoutParams(params);
				}
				layouts.add(layout);
				views.put(v, layout);
				this.contentView.addView(layout);
			}			
		}		
		return true;
	}
	
	//获取一个view的Rect
	private Rect getViewRect(View v)
	{
		Rect rect = new Rect();//v.getClipBounds();
		v.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		rect.set(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
		return rect;
	}
	
	// 添加一个短语
	private View addSentence(String Sentence)
	{
		String temp = Sentence;
		View view = contentView;
		View v = generalItem(activity, Sentence);
		
		Rect vRect = new Rect(0, 0, view.getWidth(), view.getHeight());//view.getClipBounds();		
		
		if (vRect.contains(getViewRect(v))) {
			return v;
		}
		
		temp = Sentence.substring(0, Sentence.length() >> 1);
		temp += "...";
		v = generalItem(activity, temp);

		if (vRect.contains(getViewRect(v))) {
			return v;
		}
		
		temp = Sentence.substring(0, Sentence.length() >> 2);
		temp += "...";
		v = generalItem(activity, temp);		
		
		if (vRect.contains(getViewRect(v))) {
			return v;
		}
		
		return null;
	}
	
	//生成一个layout
	private LinearLayout generalLayout(Context context)
	{		
		
		LinearLayout layout = new LinearLayout(context);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER;			
		layout.setLayoutParams(params);
		layout.setOrientation(LinearLayout.HORIZONTAL);
		return layout;
	}
	
	// 生成一个item
	private View generalItem(Context context,String text) {
		final TextView view = new TextView(context);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.setMargins(0, 10, 15, 10);				
		view.setLayoutParams(params);
		view.setText(text);
		view.setSingleLine(true);
		view.setEllipsize(TruncateAt.END);
		view.setPadding(20, 10, 20, 10);
		view.setGravity(Gravity.CENTER);
		//点击的时候搜索
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Button searchButton = (Button) activity.findViewById(R.id.btn_search);
				TextView textView = (TextView)activity.findViewById(R.id.searchstring);
				textView.setText(view.getText());
				searchButton.performClick();
			}
		});
		//长按删除
		view.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				
				AlertDialog dialog = new AlertDialog.Builder(activity)
						.setMessage("纭畾瑕佸垹闄n" + view.getText() + "?")
						.setPositiveButton(android.R.string.yes, new Dialog.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								deleteSentences(activity,String.valueOf(view.getText()));
								addViews(SearchPageDefault.this.contentView);
							}
						})
						.setNegativeButton(android.R.string.cancel, new Dialog.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO 鑷姩鐢熸垚鐨勬柟娉曞瓨鏍�
								dialog.dismiss();
							}
						})	
						.create();
				dialog.show();
				
				return true;
			}
		});
		view.setBackgroundResource(R.drawable.recommend);
		return view;
	}
	
	

}
