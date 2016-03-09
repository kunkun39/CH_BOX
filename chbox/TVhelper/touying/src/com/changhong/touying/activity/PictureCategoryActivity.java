package com.changhong.touying.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.changhong.common.service.ClientSendCommandService;
import com.changhong.common.system.AppConfig;
import com.changhong.common.system.MyApplication;
import com.changhong.common.utils.StringUtils;
import com.changhong.common.widgets.BoxSelecter;
import com.changhong.touying.R;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jack Wang
 */
public class PictureCategoryActivity extends AppCompatActivity {

    /**************************************************IP连接部分*******************************************************/

	private BoxSelecter ipSelecter;
	DrawerLayout mDrawerLayout;

    /**************************************************图片部分*********************************************************/

    /**
     * 顺序记录文件夹名称, 初始化容量，避免空间分配造成的性能影响
     */
    private static List<String> packageNames = new ArrayList<String>(30);
    /**
     * 本地文件夹缓存对象
     */
    public static Map<String, List<String>> packageList = new HashMap<String, List<String>>(30);
    /**
     * 文件夹选项适配器
     */
    /**
     * 本地文宽夹显示VIEW
     */
	private RecyclerView listPackageView;

    /**
     * 处理主线程消息
     */
    public static Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();

        initEvent();

        initData();
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
    private void initView() {
        setContentView(R.layout.activity_picture_category);

        /**
         * IP连接部分
         */
		mDrawerLayout = (DrawerLayout) findViewById(R.id.pic_main_drawer);
		Toolbar toolbar = (Toolbar) this.findViewById(R.id.toolbar);
		toolbar.setTitle(" ");
		setSupportActionBar(toolbar);

		final ActionBar ab = getSupportActionBar();
		ab.setDisplayHomeAsUpEnabled(true);

        /**
         * 图片部分
         */
		listPackageView = (RecyclerView) findViewById(R.id.select_data);

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
					
					listPackageView.setLayoutManager(new GridLayoutManager(PictureCategoryActivity.this,2));
					listPackageView.setAdapter(new RecyclerViewAdapter(PictureCategoryActivity.this));
	
                        break;
                    default:
                        break;
                }
                super.handleMessage(msg);
            }
        };
    }

    private void initEvent() {
        /**
         * IP连接部分
         */
		ipSelecter = new BoxSelecter(this, (TextView) findViewById(R.id.title),
				(ListView) findViewById(R.id.clients), new Handler(
						getMainLooper()));

	}

    private void initData() {
        /**
         * 利用ContentProvider扫描手机中的图片，此方法在运行在子线程中
         */
        getPackageFromLocal();
    }

    private void getPackageFromLocal() {
        packageNames.clear();
        packageList.clear();

        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, getResources().getString(R.string.no_sdcard), Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(new Runnable() {

            @Override
            public void run() {
                Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver mContentResolver = PictureCategoryActivity.this.getContentResolver();

                // 只查询jpeg和png的图片
                Cursor mCursor = mContentResolver.query(mImageUri, null, MediaStore.Images.Media.MIME_TYPE + "=? or "
                        + MediaStore.Images.Media.MIME_TYPE + "=?or "
                        + MediaStore.Images.Media.MIME_TYPE + "=?", new String[]{"image/jpeg","image/jpg", "image/png"}, MediaStore.Images.Media.DATE_MODIFIED + " desc");
                while (mCursor.moveToNext()) {
                    String imagePath = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));

                    // 获取该图片的父路径名
                    String[] tokens = StringUtils.delimitedListToStringArray(imagePath, File.separator);
                    String packageName = tokens[tokens.length - 2];

                    //组装相同路径下的package
                    List<String> files = packageList.get(packageName);
                    if (files == null) {
                        files = new ArrayList<String>();
                        packageNames.add(packageName);
                    }
                    files.add(imagePath);
                    packageList.put(packageName, files);
                }
                mCursor.close();

                //把拍照文件夹放到首位，在兄配置中，配置了有哪些默认的拍照文件夹
                List<String> newPackageNames = new ArrayList<String>();
                for (String packageName : packageNames) {
                    if (AppConfig.MOBILE_CARMERS_PACKAGE.contains(packageName.toLowerCase())) {
                        newPackageNames.add(0, packageName);
                    } else {
                        newPackageNames.add(packageName);
                    }
                }
                packageNames = newPackageNames;

                // 通知Handler扫描图片完成
                mHandler.sendEmptyMessage(0);
            }
        }).start();

    }

    /***********************************************图片部分************************************************************/


    private void displayImage(ImageView imageView, String path) {
        MyApplication.imageLoader.displayImage("file://" + path, imageView, MyApplication.viewOptions);
    }

    /**********************************************系统发发重载*********************************************************/

    @Override
    protected void onResume() {
        super.onResume();
//        if (ClientSendCommandService.titletxt != null) {
//            title.setText(ClientSendCommandService.titletxt);
//        }
    }
    
    @Override
    protected void onDestroy() {    
    	super.onDestroy();
    	if (ipSelecter != null) {
			ipSelecter.release();
		}
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
	public class RecyclerViewAdapter extends
			RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

		private Context mContext;

		public RecyclerViewAdapter(Context mContext) {
			this.mContext = mContext;
		}

		@Override
		public RecyclerViewAdapter.ViewHolder onCreateViewHolder(
				ViewGroup parent, int viewType) {
			View view = LayoutInflater.from(parent.getContext()).inflate(
					R.layout.activity_picture_category_item, parent, false);
			return new ViewHolder(view);
		}

		@Override
		public void onBindViewHolder(
				final RecyclerViewAdapter.ViewHolder holder, final int position) {

			// 准备数据
			String packageName = packageNames.get(position);
			List<String> images = packageList.get(packageName);
			int size = images.size();

			// 设置文字
			holder.packageName.setText(StringUtils.getShortString(packageName,
					12) + "    " + size + "张");

			// 设置图片
			try {
				if (images.size() == 1) {
					displayImage(holder.imageView1, images.get(0));
					holder.imageView2.setBackground(getResources().getDrawable(
							R.drawable.ic_stub));
					holder.imageView3.setBackground(getResources().getDrawable(
							R.drawable.ic_stub));
					holder.imageView4.setBackground(getResources().getDrawable(
							R.drawable.ic_stub));
				}

				if (images.size() == 2) {
					displayImage(holder.imageView1, images.get(0));
					displayImage(holder.imageView2, images.get(1));
					holder.imageView3.setBackground(getResources().getDrawable(
							R.drawable.ic_stub));
					holder.imageView4.setBackground(getResources().getDrawable(
							R.drawable.ic_stub));
				}

				if (images.size() == 3) {
					displayImage(holder.imageView1, images.get(0));
					displayImage(holder.imageView2, images.get(1));
					displayImage(holder.imageView3, images.get(2));
					holder.imageView4.setBackground(getResources().getDrawable(
							R.drawable.ic_stub));

				}

				if (images.size() >= 4) {
					displayImage(holder.imageView1, images.get(0));
					displayImage(holder.imageView2, images.get(1));
					displayImage(holder.imageView3, images.get(2));
					displayImage(holder.imageView4, images.get(3));
				}
			} catch (Exception e) {
			}

			final View view = holder.mView;
			view.setOnClickListener(new View.OnClickListener() {

				public void onClick(View v) {
					
					MyApplication.vibrator.vibrate(100);
					String packageName = (String) packageNames.get(position);
					List<String> imagePaths = packageList.get(packageName);

					// 跳转到每个文件夹下面的图片
					Intent intent = new Intent();
					intent.setClass(PictureCategoryActivity.this,
							PictureViewActivity.class);
					Bundle bundle = new Bundle();
					bundle.putStringArrayList("imagePaths", new ArrayList<String>(
							imagePaths));
					intent.putExtras(bundle);
					startActivity(intent);
				}
			});

		}

		@Override
		public int getItemCount() {
			
			return packageNames.size();
			
		}

		public class ViewHolder extends RecyclerView.ViewHolder {

			TextView packageName;
			ImageView imageView1;
			ImageView imageView2;
			ImageView imageView3;
			ImageView imageView4;

			public final View mView;

			public ViewHolder(View view) {
				super(view);
				mView = view;
				packageName = (TextView) view.findViewById(R.id.package_name);
				imageView1 = (ImageView) view
						.findViewById(R.id.package_picture_1);
				imageView2 = (ImageView) view
						.findViewById(R.id.package_picture_2);
				imageView3 = (ImageView) view
						.findViewById(R.id.package_picture_3);
				imageView4 = (ImageView) view
						.findViewById(R.id.package_picture_4);

			}
		}

	}
	
}
