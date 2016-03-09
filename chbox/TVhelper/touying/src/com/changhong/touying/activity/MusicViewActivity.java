package com.changhong.touying.activity;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.changhong.common.service.ClientSendCommandService;
import com.changhong.common.system.MyApplication;
import com.changhong.common.utils.DateUtils;
import com.changhong.common.widgets.BoxSelecter;
import com.changhong.touying.R;
import com.changhong.touying.dialog.MusicPlayer;
import com.changhong.touying.dialog.MusicPlayer.OnPlayListener;
import com.changhong.touying.music.Music;
import com.changhong.touying.music.SetDefaultImage;
import com.changhong.touying.music.SingleMusicAdapter;
import com.nostra13.universalimageloader.cache.disc.utils.DiskCacheFileManager;

/**
 * Created by Jack Wang
 */
public class MusicViewActivity extends AppCompatActivity {

    /**************************************************IP连接部分*******************************************************/

    private BoxSelecter ipBoxSelecter; 
	DrawerLayout mDrawerLayout;
	private RecyclerView listPackageView;

    /************************************************music basic related info******************************************/

    /**
     * 从上个Activity传过来的musics
     */
    private List<Music> musics;

    private String playlistName;
  
    private MusicPlayer player;
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.touying, menu);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initData();

        initViews();

        initEvents();
    }

    private void initData() {
        musics = (List<Music>) getIntent().getSerializableExtra("musics");
        playlistName = getIntent().getStringExtra("name");
        if (playlistName == null) {
        	playlistName = musics.get(0).getArtist();
		}          
    }

    private void initViews() {
    	
        setContentView(R.layout.activity_music_view_list);
        
        mDrawerLayout = (DrawerLayout) findViewById(R.id.pic_main_drawer);
		Toolbar toolbar = (Toolbar) this.findViewById(R.id.toolbar);
		toolbar.setTitle(" ");
		setSupportActionBar(toolbar);

		final ActionBar ab = getSupportActionBar();
		ab.setDisplayHomeAsUpEnabled(true);

		initPlayer();
		 
		listPackageView = (RecyclerView) findViewById(R.id.select_data);
        
		listPackageView.setLayoutManager(new LinearLayoutManager(listPackageView
				.getContext()));
		listPackageView.setAdapter(new RecyclerViewAdapter(MusicViewActivity.this,musics,player));
        
    }
    
    private void initPlayer(){
    	player = new MusicPlayer();
        getSupportFragmentManager().beginTransaction().add(R.id.music_seek_layout,player,MusicPlayer.TAG).show(player).commitAllowingStateLoss();
        player.setOnPlayListener(new OnPlayListener() {
			boolean isLastSong = false;
			@Override
			public void OnPlayFinished() {
				if (isLastSong) {
					player.stopTVPlayer();
					isLastSong = false;
				}
				else {
						player.nextMusic();					
				}
			}
			
			@Override
			public void OnPlayBegin(String path, String name, String artist) {
				if (musics.get(musics.size() -1).getPath().equals(path)) {
					isLastSong = true;
				}
				else {
					isLastSong = false;
				}
				
			}
		});
    }

    private void initEvents() {
    	
        /**
         * IP part
         */
        
    	ipBoxSelecter = new BoxSelecter(this, (TextView) findViewById(R.id.title), (ListView) findViewById(R.id.clients), new Handler(getMainLooper()));        
  
    }
	public class RecyclerViewAdapter extends
			RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

		private Context mContext;
		private List<Music> mMusics;
		private MusicPlayer mplayer;

		public RecyclerViewAdapter(Context Context, List<Music> musics,
				MusicPlayer player) {
			this.mContext = Context;
			this.mMusics = musics;
			this.mplayer = player;
		}

		@Override
		public RecyclerViewAdapter.ViewHolder onCreateViewHolder(
				ViewGroup parent, int viewType) {
			View view = LayoutInflater.from(parent.getContext()).inflate(
					R.layout.music_list_item, parent, false);
			return new ViewHolder(view);
		}

		@Override
		public void onBindViewHolder(
				final RecyclerViewAdapter.ViewHolder holder, final int position) {

			final View view = holder.mView;
			
			final Music music = mMusics.get(position);
			holder.musicName.setText(music.getTitle());
			holder.artist.setText(music.getArtist() + "  ["
					+ DateUtils.getTimeShow(music.getDuration() / 1000) + "]");

			holder.fullPath.setText(music.getPath());

			String musicImagePath = DiskCacheFileManager
					.isSmallImageExist(music.getPath());
			if (!musicImagePath.equals("")) {
				MyApplication.imageLoader.displayImage("file://"
						+ musicImagePath, holder.defaultImage,
						MyApplication.musicPicOptions);
				holder.defaultImage.setScaleType(ImageView.ScaleType.FIT_XY);
			} else {
				SetDefaultImage.getInstance().startExecutor(
						holder.defaultImage, music);
			}

			holder.playBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					playMusics(mMusics, music);
				}
			});

			view.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {

	                MyApplication.vibrator.vibrate(100);
	                Intent intent = new Intent();
	                Music music = musics.get(position);
	                Bundle bundle = new Bundle();
	                bundle.putSerializable("selectedMusic", music);
	                intent.putExtras(bundle);
	                intent.setClass(MusicViewActivity.this, MusicDetailsActivity.class);
	                startActivity(intent);

				}
			});

		}

		@Override
		public int getItemCount() {

			return mMusics.size();

		}

		public class ViewHolder extends RecyclerView.ViewHolder {

			public TextView musicName;
			public TextView fullPath;
			public ImageView playBtn;
			public ImageView defaultImage;
			public TextView artist;

			public final View mView;

			public ViewHolder(View view) {

				super(view);

				mView = view;
				musicName = (TextView) view.findViewById(R.id.music_item_name);
				fullPath = (TextView) view.findViewById(R.id.music_item_path);
				artist = (TextView) view
						.findViewById(R.id.music_item_artist_duration);
				playBtn = (ImageView) view.findViewById(R.id.music_list_play);
				defaultImage = (ImageView) view
						.findViewById(R.id.music_list_image);

			}
		}

		private void playMusics(List<Music> musics, Music music) {
			((FragmentActivity) mContext).getSupportFragmentManager()
					.beginTransaction().show(mplayer).commitAllowingStateLoss();
			if (music != null) {
				mplayer.playMusics(music);
			} else {
				mplayer.autoPlaying(true);
			}
		}

	}
	

    /**
     * *******************************************系统发发重载********************************************************
     */

    @Override
    protected void onResume() {
        super.onResume();

		player.attachMusics(musics,playlistName).autoPlaying(true);
		
		
    }
    @Override
    protected void onDestroy() {
    
    	super.onDestroy();
    	if (ipBoxSelecter != null) {
			ipBoxSelecter.release();
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
