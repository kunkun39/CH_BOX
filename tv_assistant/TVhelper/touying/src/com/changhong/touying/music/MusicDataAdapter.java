package com.changhong.touying.music;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.changhong.touying.R;

import java.util.List;
import java.util.Map;

/**
 * Created by Jack Wang
 */
public class MusicDataAdapter extends BaseAdapter {

    private Context context;

    private LayoutInflater inflater;

    private static List<?> musics;

    private static List<String> musicList;

    private static Map<String, List<Music>> model;

    public MusicDataAdapter(Context context) {
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        MusicProvider provider = new MusicProvider(context);
        musics = provider.getList();
        model = provider.getMapStructure(musics);
        musicList = provider.getMusicList(model);
        Log.i("mmmm", "musics="+musics+"     model="+model+"    musicList="+musicList);
    }

    public int getCount() {
        return musicList.size();
    }

    public Object getItem(int item) {
        return item;
    }

    public long getItemId(int id) {
        return id;
    }

    //创建View方法
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView musicImage = null;

        TextView musicName = null;

        TextView fullPath = null;

        if (convertView == null) {
            //获得view
            convertView = inflater.inflate(R.layout.music_category_item, null);
            musicImage = (ImageView) convertView.findViewById(R.id.music_item_image);
            musicName = (TextView) convertView.findViewById(R.id.music_item_name);
            fullPath = (TextView) convertView.findViewById(R.id.music_item_path);

            //组装view
            DataWapper wapper = new DataWapper(musicImage, musicName, fullPath);
            convertView.setTag(wapper);
        } else {
            DataWapper wapper = (DataWapper) convertView.getTag();
            musicImage = wapper.getMusicImage();
            musicName = wapper.getMusicName();
            fullPath = wapper.getFullPath();
        }

        String key = musicList.get(position);
        List<Music> list = model.get(key);
        Music music = (Music) list.get(0);

        musicName.setText(key + "\n" + list.size() + "首歌曲");
        fullPath.setText("");
        synchronizImageLoad(musicImage, position);

        return convertView;
    }

    private void synchronizImageLoad(final ImageView imageView, final int position) {
        int musicImageSource = (position + 1) % 12;
        switch (musicImageSource) {
            case 1:
                imageView.setBackground(context.getResources().getDrawable(R.drawable.music_bg1));
                break;
            case 2:
                imageView.setBackground(context.getResources().getDrawable(R.drawable.music_bg2));
                break;
            case 3:
                imageView.setBackground(context.getResources().getDrawable(R.drawable.music_bg3));
                break;
            case 4:
                imageView.setBackground(context.getResources().getDrawable(R.drawable.music_bg4));
                break;
            case 5:
                imageView.setBackground(context.getResources().getDrawable(R.drawable.music_bg5));
                break;
            case 6:
                imageView.setBackground(context.getResources().getDrawable(R.drawable.music_bg6));
                break;
            case 7:
                imageView.setBackground(context.getResources().getDrawable(R.drawable.music_bg7));
                break;
            case 8:
                imageView.setBackground(context.getResources().getDrawable(R.drawable.music_bg8));
                break;
            case 9:
                imageView.setBackground(context.getResources().getDrawable(R.drawable.music_bg9));
                break;
            case 10:
                imageView.setBackground(context.getResources().getDrawable(R.drawable.music_bg10));
                break;
            case 11:
                imageView.setBackground(context.getResources().getDrawable(R.drawable.music_bg11));
                break;
            case 12:
                imageView.setBackground(context.getResources().getDrawable(R.drawable.music_bg12));
                break;
            default:
                imageView.setBackground(context.getResources().getDrawable(R.drawable.music_bg1));
                break;
        }
    }

    private final class DataWapper {

        //视频的图标
        private ImageView musicImage;

        //视频的名字
        private TextView musicName;

        //视屏的全路径
        private TextView fullPath;


        private DataWapper(ImageView musicImage, TextView musicName, TextView fullPath) {
            this.musicImage = musicImage;
            this.musicName = musicName;
            this.fullPath = fullPath;
        }

        public ImageView getMusicImage() {
            return musicImage;
        }

        public void setMusicImage(ImageView musicImage) {
            this.musicImage = musicImage;
        }

        public TextView getMusicName() {
            return musicName;
        }

        public void setMusicName(TextView musicName) {
            this.musicName = musicName;
        }

        public TextView getFullPath() {
            return fullPath;
        }

        public void setFullPath(TextView fullPath) {
            this.fullPath = fullPath;
        }
    }

    public static Music getPositionMusic(int position) {
        return model.get(musicList.get(position)).get(0);
    }

    public static List<Music> getPositionMusics(int position) {
        return model.get(musicList.get(position));
    }
}
