package com.changhong.tvhelper.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.changhong.common.db.sqlite.DatabaseContainer;
import com.changhong.common.system.MyApplication;
import com.changhong.tvhelper.R;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.Telephony.Mms.Addr;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class SearchPageDefault extends Fragment{
	
	private static final int MAX_ITEM_COUNT = 6;
	
	
	View view = null;
	LinearLayout contentView = null;
	DatabaseContainer database =  MyApplication.databaseContainer;
	
	private Map<String,Integer> values = new HashMap<String,Integer>();
	private List<LinearLayout> layouts = new LinkedList<LinearLayout>();
	private Map<View,LinearLayout> views = new HashMap<View, LinearLayout>();
	Rect viewRect = new Rect();
	Activity activity = null;
	@Override
	public void onAttach(Activity activity) {
		this.activity = activity;
		View view = activity.findViewById(R.id.search_page_content);
		view.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			
			@Override
			public void onGlobalLayout() {
				// TODO 自动生成的方法存根
				if(SearchPageDefault.this.activity != null && !SearchPageDefault.this.view.isLayoutRequested())
				{
//					View view = SearchPageDefault.this.activity.findViewById(R.id.search_page_content);
//					view.getLocalVisibleRect(viewRect);	
					addViews(SearchPageDefault.this.contentView);	
				}
				getView().getViewTreeObserver().removeGlobalOnLayoutListener(this);
				
			}
		});
		
		
		super.onAttach(activity);
		
	}
		
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
		return view;//super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onResume() {
		// TODO 自动生成的方法存根
		super.onResume();
		
	}
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
	
	
	
	private boolean addItemIntoLayout(View v)
	{
		LinearLayout layout;
		LinearLayout view = contentView;
		if (layouts.size() == 0) {
			layout = generalLayout(activity);
			layout.addView(v);			
			views.put(v, layout);
			layouts.add(layout);
			this.contentView.addView(layout);
			
			return true;
		}
		
		layout =  layouts.get(layouts.size() -1);
		int spaceLeft = view.getWidth();
		for (Entry<View, LinearLayout> entry : views.entrySet()) {
			if (entry.getValue() == layout) {
				spaceLeft = spaceLeft - getViewRect(entry.getKey()).width();
			}
		}

		boolean isSuccess = spaceLeft > getViewRect(v).width();
		if (isSuccess) {
			layout.addView(v);
			views.put(v, layout);
			return true;			
		}
		else {
			layout = generalLayout(activity);
			layout.addView(v);
			layouts.add(layout);
			views.put(v, layout);
			this.contentView.addView(layout);
			
		}		
		return true;
	}
	
	private Rect getViewRect(View v)
	{
		Rect rect = new Rect();//v.getClipBounds();
		v.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		rect.set(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
		return rect;
	}
	
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
	
	private LinearLayout generalLayout(Context context)
	{		
		
		LinearLayout layout = new LinearLayout(context);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER;			
		layout.setLayoutParams(params);
		layout.setOrientation(LinearLayout.HORIZONTAL);
		return layout;
	}
	
	private View generalItem(Context context,String text) {
		TextView view = new TextView(context);
		MarginLayoutParams params = new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.setMargins(15, 10, 15, 10);				
		view.setLayoutParams(params);
		view.setText(text);
		view.setPadding(0, 10, 20, 10);
		view.setBackgroundResource(R.drawable.recommend);
		return view;
	}
	
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
						+ " SET ( search_time=DATE('NOW'), search_count=(search_count+1))" 
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
			cursor.moveToNext();			
		}
		
		
		
		return values;
	}
	
	public void updateDatabase(Activity activity)
	{
		String sqlString = "DELETE FROM " + DatabaseContainer.TABLE_NAME_SEARCH_HEAT + " WHERE search_time BETWEEN 'NOW' AND DATE('-30 DAY')";
		if (database == null) {
			database = new DatabaseContainer(activity);
		}
		database.getReadableDatabase().execSQL(sqlString, new Object[]{});
	}

}
