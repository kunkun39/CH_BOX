/**
 * 
 */
package com.changhong.touying.dialog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.changhong.common.service.ClientSendCommandService;
import com.changhong.common.system.MyApplication;
import com.changhong.common.utils.DateUtils;
import com.changhong.common.utils.MobilePerformanceUtils;
import com.changhong.common.utils.NetworkUtils;
import com.changhong.common.utils.StringUtils;
import com.changhong.common.utils.WebUtils;
import com.changhong.touying.R;
import com.changhong.touying.music.JsonMusicObject;
import com.changhong.touying.music.Music;
import com.changhong.touying.music.MusicLrc;
import com.changhong.touying.nanohttpd.NanoHTTPDService;
import com.changhong.touying.service.MusicService;
import com.changhong.touying.service.MusicServiceImpl;
import com.changhong.common.service.ClientSendCommandService;



/**
 * @author yves.yang
 *
 */
public class MusicPlayer extends DialogFragment{

	public final static String TAG = "MusicPlayer";
	/**
	 * messages
	 */
	
	private final static String CMD_TAG = "music:";
	
	private final static String CMD_PAUSE = CMD_TAG + "pause";
	private final static String CMD_STOP = CMD_TAG + "stop";
	private final static String CMD_START = CMD_TAG + "start";
	private final static String CMD_PLAY = CMD_TAG + "play";
	private final static String CMD_SEEK = CMD_TAG + "seekto";
	
	//就算播放结束,播放界面仍然保持,不退出
	private final static String CMD_AUTO = CMD_TAG + "auto";	
	
	private final static int AUTO_EXCUTE_DELAY_TIME = 2000;
	/**
	 * 
	 */
	View view;
    /**
     * 消息处理
     */
    public static Handler handler;

    /**
     * 被选中的音乐文件
     */
    private List<Music> musics = new ArrayList<Music>();
    
    private Music music;

    /**
     * 歌曲名
     */
    private TextView musicName;    
    
    /**
     * 
     */
    private View seekbarContainer;
    
    private SeekBar seekBar;

    /**
     * 时间显示信息
     */
    private TextView showTimeGoing;
    private TextView showTimeTotal;

    /**
     * 判断是否正在播放，可以用来防止用户连续点击播放按钮，导致系统创建信的线程
     */
    public boolean isPlaying = false;

    /**
     * 是否为暂停状态
     */
    public boolean isPausing = false;

    /**
     * 是否拖动过，因为，拖动后，手机端的进度条刚更新后，服务端还未跟新就已经发送的进度消息，手机端会返回拖动前状态
     * 一旦发生拖动，就手机缓冲2秒再更新进度条
     */
    private int isSeeking = 0;   

    /**
    /**
     * 播放暂停按钮
     */
    private ImageButton controlButton;

    /**
     * 音量控制按钮
     */
    private ImageView volUpBtn;
    private ImageView volDownBtn;

    /**
     *  歌词服务
     */
    private MusicService musicService;
    
    /**
     * 不显示标题
     */
    boolean isTitleHide = false;
    
    /**
     * 简洁模式
     */
    boolean isSimpleStyle = false;
    
    /**
     * 自动播放
     */
    boolean isAutoPlaying = false;
    Runnable autoPlayRunnable;
    
    /**
     * 当前正在播放的歌曲名字 
     */
    String playingMusic;    
    
    String playlistName = null;

    /**
     * 回调监听者
     */
    OnPlayListener listener;   

/**====================================================回调函数===================*/
    public interface OnPlayListener
	{
    	public void OnPlayBegin(String path,String name,String artist);    	
    	public void OnPlayFinished();
	}
    
    public void setOnPlayListener(OnPlayListener listener)
    {
    	this.listener = listener;
    }
    
    /**============================================播放列表属性设置==========================*/   
    public void hideTitle()
    {
    	isTitleHide = true;      	
    }
    
    public void simpleStyle()
    {
    	isSimpleStyle = true;    
    }
    
    public void autoPlaying(boolean isAuto)
    {
    	isAutoPlaying = isAuto;   
    	
    	autoPlayRunnable = new Runnable() {			
			@Override
			public void run() {				
				if (playingMusic != null) {
					
					if(playlistName != null
							&& !playingMusic.contains(playlistName)) 
						return ;
					
					if (!musics.isEmpty()) {
						for (Music m : musics) {
							if (playingMusic.contains(m.getTitle())
									&& playingMusic.contains(m.getArtist())) {
								music = m;	
								autoPlayRunnable = null;
								return ;
							}
						}
					}											
				}
				else {
					if(!musics.isEmpty())
					{
						//playMusics(null);
					}
					else if(music != null){
						//playMusic(music);
					}
				}	
								
				autoPlayRunnable = null;
			}
			
		};
		
    	if (handler != null) {
    		
    		if (isAutoPlaying) {
    			handler.postDelayed(autoPlayRunnable, AUTO_EXCUTE_DELAY_TIME); 
			}else {
	    		handler.removeCallbacks(autoPlayRunnable);
			}	   
		}
    		
    }
    /**===========================================================公用函数，供公共调用========*/
    /**
     * 首先创建play,然后add fragment,接着attachmusic,然后才是play
     * @param musics
     * @param playListName
     * @return
     */
    public MusicPlayer attachMusics(List<Music> musics,String playListName)
    {
    	attachMusics(musics);
    	
    	playlistName = playListName;
    	return this;    	
    }
    
    public MusicPlayer attachMusics(List<Music> musics)
    {
    	if (musics == null
    			|| musics.size() == 0)
    		return this;		
    	
    	this.playlistName = null;
    	this.musics.clear();
    	this.musics.addAll(musics);
    	music = this.musics.get(0);
    	
    	return this;
    }
    
    public MusicPlayer attachMusic(Music music)
    {
    	if (musics == null) {
    		return this;
		}

    	this.playlistName = null;
    	this.musics.clear();
    	
    	this.music = music;
    	
    	return this;
    }
    
    
    /**
     * 播放歌曲列表
     * 参数:music为列表里的歌曲，如果参数存在，那么就会从选择了的歌曲处播放  
     */    
    
    public boolean playMusics(Music music)
    {       		    	
    	if (musics == null
    			|| musics.isEmpty()) {
			return false;
		}
    	
    	if (getActivity() == null) {
			return false;
		}
    	
    	if (listener != null) {
        	try {
        		listener.OnPlayBegin(music.getPath(), music.getTitle(), music.getArtist());
			} catch (Exception e) {
				e.printStackTrace();    				
			}			
		}
    	
    	if (music != null
    			&& musics.contains(music)) {
    		this.music = music;

            List<Music> list = new ArrayList<Music>();
            for(int i = musics.indexOf(music);i < musics.size();i ++)
            	list.add(musics.get(i));
            touYing(list,playlistName);
    		return true;
    		
		}
    	else if (music == null){
    		if (this.music != null) {
    			List<Music> list = new ArrayList<Music>();
                for(int i = musics.indexOf(this.music);i < musics.size();i ++)
                	list.add(musics.get(i));
                touYing(list,playlistName);
			}
    		
		}
    	
    	return false;
    }
    public boolean playMusic(Music music)
    {
    	if (music == null) {
			return false;
		}
    	this.music = music;
        
        if (listener != null) {
        	try {
        		listener.OnPlayBegin(music.getPath(), music.getTitle(), music.getArtist());
			} catch (Exception e) {
				e.printStackTrace();				
			}			
		}
        
        touYing(music.getPath(), music.getTitle(), music.getArtist());
        return true;
    }
      
    public boolean OnKeyPress(int keyCode, KeyEvent event)
    {
    	if (!isPlaying) {
			return false;
		}
    	
    	switch (keyCode) {
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			ClientSendCommandService.sendMessage("key:volumedown");				
			return true;
		case KeyEvent.KEYCODE_VOLUME_UP:
			ClientSendCommandService.sendMessage("key:volumeup");		
			return true;

		default:
			break;
		}
    	
    	return false;
    }
   
    public boolean nextMusic()
    {    	
        
    	int index = musics.indexOf(music);
    	
    	if (++index < musics.size()) {
    		music = musics.get(index);
		}
    	else {    		
			return false;
		}    	    	
        
    	return playMusics(music);    	 
    }
    
    public void stopTVPlayer()
    {
    	
    	//ClientSendCommandService.sendMessage(CMD_STOP);
    	if (view.getVisibility() == View.VISIBLE) {
    		Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.music_seekbar_out);
            view.startAnimation(animation);
            view.setVisibility(View.INVISIBLE);
		}
    	
    }
    
    
/**====================================================系统重写函数===================*/    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	musicService = new MusicServiceImpl(this.getActivity());
    	setCancelable(false);    	
    	setStyle(DialogFragment.STYLE_NO_TITLE, 0);
    }


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		if (isSimpleStyle) {
			view = inflater.inflate(R.layout.dialog_music_player_simple, container, false);
		}
		else {
			view = inflater.inflate(R.layout.dialog_music_player, container, false);
		}
		initialViews(view);		
		initialEvents();
		
		return view;
	}
	
	@Override
    public void onHiddenChanged(boolean hidden) {
    	super.onHiddenChanged(hidden);
    	if (view != null) {
    		if (hidden) {  
    			//ClientSendCommandService.sendMessage(CMD_AUTO + ":" + "1"); 	
    			if (isAutoPlaying && handler != null) {
					handler.removeCallbacks(autoPlayRunnable);
				}
			}
    		else {
    			autoPlaying(isAutoPlaying);
    			//ClientSendCommandService.sendMessage(CMD_AUTO + ":" + "0"); 	        
			}			
		}
    }
	
	@Override
	public void onPause() {
		super.onPause();
		//ClientSendCommandService.sendMessage(CMD_AUTO + ":" + "1"); 	
		if (view.getVisibility() == View.VISIBLE) {
			//Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.music_seekbar_out);
	        //view.startAnimation(animation);
	        view.setVisibility(View.INVISIBLE);
		}
		
	}

	@Override
    public void onResume() {
    	super.onResume();
    	
    	handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
	                //进度条同步服务端的更新
            		if(getActivity() == null)
            			return ;
            		
            		String[] content = StringUtils.delimitedListToStringArray(((String) msg.obj), "|");
            		
	                if (msg.what == 0) {
	                    //HTTPD的使用状态
	                    MobilePerformanceUtils.httpServerUsing = true;
	
	                    String key = null;
	                    playingMusic = content[0];
	                    if(playlistName == null)
                    	{
	                    	key = (music != null) ? music.getTitle() + "-" +music.getArtist() : null;
                    		                    	
                    	}
	                    else {
	                    	
	                    	key = (music != null) ? playlistName + "-" + music.getTitle() + "-" +music.getArtist() : null;
						}
	                    
	                    
	                    if (isAutoPlaying) {
	                    	if(autoPlayRunnable != null)	              
	                    	{
	                    		handler.removeCallbacks(autoPlayRunnable);
	                    		handler.post(autoPlayRunnable);
	                    	}
						}		
	                    //判断当前的页面是否为在播放的歌曲
	                    if (key != null && key.equals(WebUtils.convertHttpURLToLocalFile(content[0]))) {
	                        int progress = Integer.parseInt(content[1]);
	                        if (view.getVisibility() == View.INVISIBLE 
	                        		&& progress > 0
	                        		&& seekBar.getMax() - seekBar.getProgress() > 5) {
	                            Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.music_seekbar_in);
	                            view.startAnimation(animation);
	                            view.setVisibility(View.VISIBLE);
	
	                            int totalTime = music.getDuration() / 1000;
	                            seekBar.setMax(totalTime);
	                            String musicTotalTime = DateUtils.getTimeShow(totalTime);
	                            showTimeGoing.setText("00:00");
	                            showTimeTotal.setText( musicTotalTime);
	                        }
	                        if (progress > 0 && isSeeking == 0) {
	                            seekBar.setProgress(progress);
	                        }
	                        if (isSeeking > 0) {
	                            isSeeking = isSeeking - 1;
	                        }
	                    String status = content[2];
	                    if ("true".equals(status)) {
	                        isPlaying = true;
	                        isPausing = false;
	                    } else {
	                        isPlaying = false;
	                        isPausing = true;
	                    }                   	
	                }
                    else {
                    	// 当finish UDP包掉了后，实现自动检测歌曲播放，切换到正确的进度        
                    	if(handler != null && autoPlayRunnable != null)
                    		handler.postAtFrontOfQueue(autoPlayRunnable);
						
					}
	            }
                if ((msg.what == 1 && seekBar.getMax() - seekBar.getProgress() <= 5)) {
                		Log.e("MusicViewActivity", "music stop play");
                		playFinish();	                                                                    
                }
	        }
        };
        autoPlaying(true);
        
    }


	@Override
	public void onDestroy() {

		super.onDestroy();
		//ClientSendCommandService.sendMessage(CMD_AUTO + ":" + "1");

	}   

/**=======================================================私有函数=====================*/    
    private void initialViews(View v) {        	            	
        seekBar  = (SeekBar)v.findViewById(R.id.music_seek);
        controlButton = (ImageButton)v.findViewById(R.id.music_control_button);        
        showTimeGoing = (TextView) v.findViewById(R.id.music_showtime_going);
        showTimeGoing.setText("00:00");
        showTimeTotal = (TextView) v.findViewById(R.id.music_showtime_total);
        showTimeTotal.setText("00:00");
        
        musicName = (TextView)v.findViewById(R.id.music_name_singer);   
        volUpBtn = (ImageView)v.findViewById(R.id.control_volume_bigger);
        volDownBtn = (ImageView)v.findViewById(R.id.control_volume_small);
        seekbarContainer = v.findViewById(R.id.music_seek_container);
        if (isTitleHide) {
        	if (musicName != null) {
        		musicName.setVisibility(View.GONE);
			}        	
        	view.setMinimumHeight(30);
		}
        else {
        	view.setMinimumHeight(100);        	
		}
        view.invalidate();
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        view.requestFocus();          
    }
    
    private void initialEvents() {

    	if (seekbarContainer != null) {
        	seekbarContainer.setOnTouchListener(new OnTouchListener() {
    			@Override
    			public boolean onTouch(View v, MotionEvent event) {
    				switch (event.getAction()) {
    				case MotionEvent.ACTION_DOWN:
    				case MotionEvent.ACTION_MOVE:
    				case MotionEvent.ACTION_UP:
    				{
    					return seekBar.onTouchEvent(event);					
    				}				    				
    				default:
    					break;
    				}
    				return false;
    			}
    		});
		}            	
    	seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String currentime = DateUtils.getTimeShow(seekBar.getProgress());
                showTimeGoing.setText(currentime);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            	int progress = seekBar.getProgress();            	
            		ClientSendCommandService.sendMessage(CMD_SEEK + ":" + progress);
                                    	
                    isPlaying = true;
                    isPausing = false;
                    isSeeking = 2;				
            }
        });

        controlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.vibrator.vibrate(100);
                if (isPausing) {
                	controlButton.setBackgroundResource(R.drawable.control_pause);
                	ClientSendCommandService.sendMessage(CMD_PLAY);
                    isPausing = false;
                    isPlaying = true;
                } else {
                	controlButton.setBackgroundResource(R.drawable.control_play);
                	ClientSendCommandService.sendMessage(CMD_PAUSE);
                    isPausing = true;
                    isPlaying = false;
                }
            }
        });
		
		/**
         * 视频投影音量控制
         */
		if(volUpBtn != null)
		{
	        volUpBtn.setOnClickListener(new View.OnClickListener() {
	            @Override
	            public void onClick(View v) {
	                MyApplication.vibrator.vibrate(100);
	                ClientSendCommandService.sendMessage("key:volumeup");
	            }
	        });
		}
		if(volDownBtn != null)
		{
	        volDownBtn.setOnClickListener(new View.OnClickListener() {
	            @Override
	            public void onClick(View v) {
	                MyApplication.vibrator.vibrate(100);
	                ClientSendCommandService.sendMessage("key:volumedown");	                
	            }
	        }); 
		} 
				
		view.setOnKeyListener(new View.OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				return OnKeyPress(keyCode, event);
			}
		});
    }
 
    
    private void playFinish()
    {
        
        if (listener != null) {
        	try {
        		listener.OnPlayFinished();
			} catch (Exception e) {
				e.printStackTrace();
			}        	
		}
        else {
			if(!nextMusic())
			{						        
		        stopTVPlayer();			 
		        return ;
			}
		}

        //音乐停止播放
        isPlaying = false;
        isPausing = false;

        seekBar.setProgress(0);
    }               
    
    private void touYing(List<Music> musics,String playlistName)
    {
    	List<JsonMusicObject> objectsList = new ArrayList<JsonMusicObject>();
    	
    	try {
            if (NetworkUtils.isWifiConnected(getActivity())) {
                if (!StringUtils.hasLength(ClientSendCommandService.serverIP)) {
                    Toast.makeText(getActivity(), "手机未连接电视，请确认后再投影", Toast.LENGTH_SHORT).show();
                    return;
                }
  				/**
                 * 设置播放滚动条的状态
                 */
                //if (view.getVisibility() == View.INVISIBLE) {
                //	view.setVisibility(View.VISIBLE);
                //    Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.music_seekbar_in);
                //    view.startAnimation(animation);
                //}

                int  totalTime = music.getDuration() / 1000;
                seekBar.setMax(totalTime);
                String musicTotalTime = DateUtils.getTimeShow(totalTime);
                showTimeGoing.setText("00:00");
                showTimeTotal.setText(musicTotalTime);
                seekBar.setProgress(0);              
                /**
                 * 有时候用户在进入投影页面，但是确没有投影动作，http服务关闭，但是用户现在点击投影，所以这里需要先检查有没有HTTP服务
                 */
                if (NanoHTTPDService.httpServer == null) {
                    Intent http = new Intent(getActivity(), NanoHTTPDService.class);   
                    
                    getActivity().startService(http);

                    //Sleep 1s is used for let http service started fully
                    SystemClock.sleep(1000);
                }
                

                MyApplication.vibrator.vibrate(100);

                /**
                 * 开始投影播放
                 */
                String musicSelectedPath = null;
                String ipAddress = NetworkUtils.getLocalHostIp();
                for (Music m : musics) {
					JsonMusicObject obj = new JsonMusicObject();
					obj.setMusicPath(m.getPath());
					obj.setArtist(m.getArtist());
					obj.setMusicName(m.getTitle());
					
					if (obj.getMusicPath().startsWith(NanoHTTPDService.defaultHttpServerPath)) {
	                    musicSelectedPath = obj.getMusicPath().replace(NanoHTTPDService.defaultHttpServerPath, "");
	                } else {
	                    for (String otherHttpServerPath : NanoHTTPDService.otherHttpServerPaths) {
	                        if (obj.getMusicPath().startsWith(otherHttpServerPath)) {
	                            musicSelectedPath = obj.getMusicPath().replace(otherHttpServerPath, "");
	                        }
	                    }
	                }
	                
	                String httpAddress = "http://" + ipAddress + ":" + NanoHTTPDService.HTTP_PORT;
	                obj.setMusicPath(httpAddress + WebUtils.convertLocalFileToHttpURL(musicSelectedPath));
	                
	                try {
	                    MusicLrc lrc = musicService.findMusicLrc(obj.getArtist(), obj.getMusicName());
	                    if (lrc != null) {
	                        String lrcPath = lrc.getPath();
	                        String lrcHttpAddress = null;
	                        if (lrcPath.startsWith(NanoHTTPDService.defaultHttpServerPath)) {
	                            lrcHttpAddress = lrcPath.replace(NanoHTTPDService.defaultHttpServerPath, "");
	                        } else {
	                            for (String otherHttpServerPath : NanoHTTPDService.otherHttpServerPaths) {
	                                if (lrcPath.startsWith(otherHttpServerPath)) {
	                                    lrcHttpAddress = obj.getMusicPath().replace(otherHttpServerPath, "");
	                                }
	                            }
	                        }
	                        obj.setMusicLrcPath(httpAddress + WebUtils.convertLocalFileToHttpURL(lrcHttpAddress));	                        
	                    }
	                } catch (Exception e) {
	                    e.printStackTrace();
	                }	                
	                objectsList.add(obj);
				}
               
                
                JSONObject o = new JSONObject();
                o.put("music_play", "music_play");                
                o.put("objects_list", JSON.toJSONString(objectsList));                

                if(playlistName != null)
                	o.put("playlistName", playlistName);
                
                //发送播放地址
                ClientSendCommandService.sendMessageNew(o.toString());                

                //HTTPD的使用状态
                MobilePerformanceUtils.openPerformance(getActivity());
            } else {
                Toast.makeText(getActivity(), "请链接无线网络", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
            	Toast.makeText(getActivity(), "歌曲获取出错", Toast.LENGTH_SHORT).show();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
            
        }
    }
    private void touYing(String musicPath, String musicName, String artist) {
        try {
            if (NetworkUtils.isWifiConnected(getActivity())) {
                if (!StringUtils.hasLength(ClientSendCommandService.serverIP)) {
                    Toast.makeText(getActivity(), "手机未连接电视，请确认后再投影", Toast.LENGTH_SHORT).show();
                    return;
                }
  				/**
                 * 设置播放滚动条的状态
                 */
                //if (view.getVisibility() == View.INVISIBLE) {
                //	view.setVisibility(View.VISIBLE);
                //    Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.music_seekbar_in);
                //    view.startAnimation(animation);
                //}

                int  totalTime = music.getDuration() / 1000;
                seekBar.setMax(totalTime);
                String musicTotalTime = DateUtils.getTimeShow(totalTime);
                showTimeGoing.setText("00:00");
                showTimeTotal.setText(musicTotalTime);
                seekBar.setProgress(0);              
                /**
                 * 有时候用户在进入投影页面，但是确没有投影动作，http服务关闭，但是用户现在点击投影，所以这里需要先检查有没有HTTP服务
                 */
                if (NanoHTTPDService.httpServer == null) {
                    Intent http = new Intent(getActivity(), NanoHTTPDService.class);   
                    
                    getActivity().startService(http);

                    //Sleep 1s is used for let http service started fully
                    SystemClock.sleep(1000);
                }
                

                MyApplication.vibrator.vibrate(100);

                /**
                 * 开始投影播放
                 */
                String musicSelectedPath = null;
                if (musicPath.startsWith(NanoHTTPDService.defaultHttpServerPath)) {
                    musicSelectedPath = musicPath.replace(NanoHTTPDService.defaultHttpServerPath, "");
                } else {
                    for (String otherHttpServerPath : NanoHTTPDService.otherHttpServerPaths) {
                        if (musicPath.startsWith(otherHttpServerPath)) {
                            musicSelectedPath = musicPath.replace(otherHttpServerPath, "");
                        }
                    }
                }

                String ipAddress = NetworkUtils.getLocalHostIp();

                String httpAddress = "http://" + ipAddress + ":" + NanoHTTPDService.HTTP_PORT;
                
                String musicHttpAddress = httpAddress + WebUtils.convertLocalFileToHttpURL(musicSelectedPath);
                JSONObject o = new JSONObject();
                o.put("music_play", "music_play");
                o.put("path", musicHttpAddress);
                o.put("musicName", musicName);
                o.put("artist", artist);

                try {
                    MusicLrc lrc = musicService.findMusicLrc(artist, musicName);
                    if (lrc != null) {
                        String lrcPath = lrc.getPath();
                        String lrcHttpAddress = null;
                        if (lrcPath.startsWith(NanoHTTPDService.defaultHttpServerPath)) {
                            lrcHttpAddress = lrcPath.replace(NanoHTTPDService.defaultHttpServerPath, "");
                        } else {
                            for (String otherHttpServerPath : NanoHTTPDService.otherHttpServerPaths) {
                                if (lrcPath.startsWith(otherHttpServerPath)) {
                                    lrcHttpAddress = musicPath.replace(otherHttpServerPath, "");
                                }
                            }
                        }
                        o.put("musicLrcPath", httpAddress + WebUtils.convertLocalFileToHttpURL(lrcHttpAddress));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }                

                //发送播放地址
                ClientSendCommandService.sendMessage(o.toString());                

                //HTTPD的使用状态
                MobilePerformanceUtils.openPerformance(getActivity());
            } else {
                Toast.makeText(getActivity(), "请链接无线网络", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
            	Toast.makeText(getActivity(), "歌曲获取出错", Toast.LENGTH_SHORT).show();	
			} catch (Exception e2) {
				e2.printStackTrace();
			}
            
        }
    }
    
    
    
 
}
