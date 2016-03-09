package com.changhong.touying.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.changhong.common.system.MyApplication;
import com.changhong.common.utils.StringUtils;
import com.changhong.common.widgets.BoxSelecter;
import com.changhong.touying.R;
import com.changhong.touying.vedio.Vedio;
import com.nostra13.universalimageloader.cache.disc.utils.DiskCacheFileManager;

import java.util.List;

/**
 * Created by Jack Wang
 */
public class VedioViewActivity extends AppCompatActivity {

	DrawerLayout mDrawerLayout;

    /**
     * server ip part
     */
    private BoxSelecter ipSelecter;

    /**
     * 视频浏览部分
     */
	private RecyclerView listView;

    /**
     * 从上个Activity传过来的veidos
     */
    private List<Vedio> vedios;


    @Override
    protected void onResume() {
        super.onResume();
//        if (ClientSendCommandService.titletxt != null) {
//            title.setText(ClientSendCommandService.titletxt);
//        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initData();

        initViews();

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
    private void initData() {
        vedios = (List<Vedio>) getIntent().getSerializableExtra("vedios");
    }

    private void initViews() {
		setContentView(R.layout.activity_vedio_category);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.pic_main_drawer);
		Toolbar toolbar = (Toolbar) this.findViewById(R.id.toolbar);
		toolbar.setTitle(" ");
		setSupportActionBar(toolbar);

		final ActionBar ab = getSupportActionBar();

		ab.setDisplayHomeAsUpEnabled(true);

		/**
		 * 图片容器
		 */
		listView = (RecyclerView) findViewById(R.id.select_data);
		listView.setLayoutManager(new GridLayoutManager(VedioViewActivity.this,
				3));
		listView.setAdapter(new RecyclerViewAdapter(VedioViewActivity.this,
				vedios));

		ipSelecter = new BoxSelecter(this, (TextView) findViewById(R.id.title),
				(ListView) findViewById(R.id.clients), new Handler(
						getMainLooper()));
    }

    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	if (ipSelecter != null) {
			ipSelecter.release();
		}
    }

    /**********************************************数据适配器**********************************************************/

	public class RecyclerViewAdapter extends
			RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

		private Context mContext;
		private List<Vedio> mvedios;

		public RecyclerViewAdapter(Context mContext, List<Vedio> vedios) {
			this.mContext = mContext;
			mvedios = vedios;
		}

		@Override
		public RecyclerViewAdapter.ViewHolder onCreateViewHolder(
				ViewGroup parent, int viewType) {
			View view = LayoutInflater.from(parent.getContext()).inflate(
					R.layout.vedio_category_item, parent, false);
			return new ViewHolder(view);
		}

		@Override
		public void onBindViewHolder(
				final RecyclerViewAdapter.ViewHolder holder, final int position) {

			try {
				Vedio vedio = vedios.get(position);
				String displayName = StringUtils.hasLength(vedio
						.getDisplayName()) ? StringUtils.getShortString(
						vedio.getDisplayName(), 20) : vedio.getTitle();
				holder.vedioName.setText(displayName);
				holder.fullPath.setText(String.valueOf(position));

				String vedioPath = vedio.getPath();
				String vedioImagePath = DiskCacheFileManager
						.isSmallImageExist(vedioPath);
				if (!vedioImagePath.equals("")) {
					MyApplication.imageLoader.displayImage("file://"
							+ vedioImagePath, holder.vedioImage,
							MyApplication.viewOptions);
					holder.vedioImage.setScaleType(ImageView.ScaleType.FIT_XY);
				} else {
					synchronizImageLoad(holder.vedioImage, vedioPath);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			final View view = holder.mView;
			view.setOnClickListener(new View.OnClickListener() {

				public void onClick(View v) {

					MyApplication.vibrator.vibrate(100);
					Intent intent = new Intent();
					intent.setClass(VedioViewActivity.this,
							VedioDetailsActivity.class);
					Bundle bundle = new Bundle();
					Vedio vedio = vedios.get(position);
					bundle.putSerializable("selectedVedio", vedio);
					intent.putExtras(bundle);
					startActivity(intent);
				}

			});

		}

		@Override
		public int getItemCount() {

			return mvedios.size();

		}
		
		

		public class ViewHolder extends RecyclerView.ViewHolder {

			ImageView vedioImage = null;
			TextView vedioName = null;
			TextView fullPath = null;
			public final View mView;

			public ViewHolder(View view) {
				super(view);
				mView = view;

				vedioImage = (ImageView) view
						.findViewById(R.id.vedio_item_image);
				vedioName = (TextView) view.findViewById(R.id.vedio_item_name);
				fullPath = (TextView) view.findViewById(R.id.vedio_item_path);

			}
		}

	}

        private void synchronizImageLoad(final ImageView imageView, final String path) {
            ImageAsyncTask task = new ImageAsyncTask(imageView);
            task.execute(path);
        }

        private final class ImageAsyncTask extends AsyncTask<String, Integer, Bitmap> {
            ImageView imageView;

            private ImageAsyncTask(ImageView imageView) {
                this.imageView = imageView;
            }

            @Override
            protected Bitmap doInBackground(String... params) {
                Bitmap bitmap = null;
                try {
                    String path = params[0];
                    bitmap = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Images.Thumbnails.MINI_KIND);
                    DiskCacheFileManager.saveSmallImage(bitmap, path);
                    return bitmap;
                } catch (Exception e) {
                    e.printStackTrace();
                    return bitmap;
                }
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                if (bitmap != null && imageView != null) {
                    try {
                        imageView.setImageBitmap(bitmap);
                        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
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
}
