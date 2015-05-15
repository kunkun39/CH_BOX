/**
 * 
 */
package com.changhong.touying.music;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import com.changhong.common.utils.UnicodeReader;
import com.changhong.touying.R;

/**
 * @author yves.yang
 *
 */
public class M3UPlayList {
	
	/**
	 * 播放列表后缀
	 */
	public static final String SUFFIX = ".m3u";
	
	public static List<String> loadPlayListToStringList(Context context,String path)
	{
		FileInputStream stream = null;		
		BufferedReader bffReader = null;			
		List<String> playlistString = new ArrayList<String>();
		boolean isResult = true;
		
		try {
			//创建列表
			File file = new File(path);
			
			if(!file.exists())
			{
				return null;			
			}
			
			//读取列表文件内容
			stream = new FileInputStream(path);										
			bffReader = new BufferedReader(new UnicodeReader(stream,"UTF-8"));
			
			String temp;
			while((temp = bffReader.readLine()) != null)
			{
				if (temp.startsWith("#")) {
					continue;
				}
				
				playlistString.add(temp);
			}
			
			List<String> pDeList = new ArrayList<String>();
			
			// 确认文件是否存在
			for (String songPath : playlistString) {
				if (!new File(songPath).exists()) {
					pDeList.add(songPath);					
				}
			}
			
			for (String string : pDeList) {
				playlistString.remove(string);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			isResult = false;
		} catch (IOException e) {
			e.printStackTrace();
			isResult = false;
		} finally
		{
			try {
				
				if (bffReader != null)
					bffReader.close();
				
				if(stream != null)
					stream.close();
				
			} catch (IOException e) {
				e.printStackTrace();
				isResult = false;
			}	
		}
		if (!isResult) {
			return null;
		}
		
		return playlistString;
	}

	public static List<Music> loadPlayListToMusicList(Context context,String path)
	{		
			
		List<Music> playList = new ArrayList<Music>();
		
		List<String> playlistString = loadPlayListToStringList(context,path);				
		
		List<Music> musics = (List<Music>)new MusicProvider(context).getList();
		
		for (String musicPath : playlistString) {
			for (Music music : musics) {
				if (music.getPath().equals(musicPath)) {
					playList.add(music);
					break;
				}
			}
		}
		
		return playList;
	}
	
	public static boolean savePlayList(Context context,String path,List<String> playList)
	{
		boolean isResult = true;
		
		if(!path.endsWith(SUFFIX))
		{
			path += SUFFIX;
		}		
		
		FileOutputStream stream = null;
		try {
			stream = new FileOutputStream(path);
			for (String item : playList) {				
				stream.write(item.getBytes(Charset.forName("UTF-8")));
				stream.write("\n".getBytes());
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();			
			isResult = false;
		} catch (IOException e) {
			e.printStackTrace();
			isResult = false;
		}
		finally
		{
			
			try {
				if (stream != null) 
					stream.close();
			} catch (IOException e1) {
				e1.printStackTrace();
				isResult = false;
			}					
		}	
		
		return isResult;
	}
	
	public static  MusicPlayList generalplaylist(String name)
    {

    	if(!Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED))
    		return null;
    	
    	//判断目录存在性
    	File dirfile = Environment.getExternalStoragePublicDirectory(SUFFIX);
    	if (!dirfile.exists()) {
    		dirfile.mkdirs();
		}
    	
    	//建立列表对象
    	MusicPlayList list = new MusicPlayList();
    	String path = dirfile.getPath()
    			+ "/" 
    			+ name 
    			+ SUFFIX;
    	
    	//判断名字是否存在
    	File f = new File(path);
    	if (f.exists())
    		return null;
    	
    	list.setName(name);
    	list.setPath(path);

    	return list;
    }     
}
