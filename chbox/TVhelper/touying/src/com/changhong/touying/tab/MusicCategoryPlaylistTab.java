/**
 *
 */
package com.changhong.touying.tab;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.changhong.common.utils.DialogUtil;
import com.changhong.common.utils.DialogUtil.DialogBtnOnClickListener;
import com.changhong.common.utils.DialogUtil.DialogMessage;
import com.changhong.touying.R;
import com.changhong.touying.activity.MusicPlayListActivity;
import com.changhong.touying.activity.MusicViewActivity;
import com.changhong.touying.music.M3UPlayList;
import com.changhong.touying.music.Music;
import com.changhong.touying.music.MusicPlayList;
import com.changhong.touying.music.MusicProvider;
import com.changhong.touying.music.PlayListAdatper;
import com.changhong.touying.service.M3UListProviderService;
import com.changhong.touying.service.MusicService;
import com.changhong.touying.service.MusicServiceImpl;

/**
 * @author yves.yang
 */
public class MusicCategoryPlaylistTab extends Fragment {

    public static final String TAG = "MusicCategoryPlaylistTab";

    private static final int RETURN_ACTIVITY_ADD = 1;

    /**
     * 添加列表按钮
     */
//    private Button mAddNewListBtn;
    private ImageButton mAddNewListBtn;

    /**
     * 播放列表View
     */
    private ListView mPlayListView;

    View view = null;

    /**
     * 播放列表的列表
     */
    List<MusicPlayList> musicPlayLists = new ArrayList<MusicPlayList>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * 启动歌词扫描服务
         */
        MusicService musicService = new MusicServiceImpl(getActivity());
        musicService.findAllMusicLrc();
        getActivity().sendBroadcast(new Intent(M3UListProviderService.UPDATE_INTENT));
        loadPlayLists();
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (view == null) {
            view = inflater.inflate(R.layout.tab_playlist, container, false);
            initView(view);
            initEvent();
        } else {
            ViewGroup v = (ViewGroup) view.getParent();
            if (v != null)
                v.removeView(view);
        }
        return view;
    }

    private void initView(View v) {

//        mAddNewListBtn = (Button) v.findViewById(R.id.music_list_add);
        mAddNewListBtn = (ImageButton) v.findViewById(R.id.music_list_add);
        mPlayListView = (ListView) v.findViewById(R.id.music_list_list);
        mPlayListView.setLongClickable(true);
        mPlayListView.setAdapter(new PlayListAdatper(getActivity(), musicPlayLists));
    }

    private void initEvent() {
        initPlayListEvent();
    }

    /**
     * *******************************************系统发发重载********************************************************
     */

    @Override
    public void onResume() {
        super.onResume();
        updateView();
    }

    /**
     * *******************************************播放列表********************************************************
     */

    private void initPlayListEvent() {

        mAddNewListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPlayList();
            }
        });

        mPlayListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                String path = musicPlayLists.get(arg2).getPath();
                String name = path.substring(path.lastIndexOf("/") + 1, path.length() - M3UPlayList.SUFFIX.length());

                Intent intent = new Intent();
                intent.setClass(getActivity(), MusicViewActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("musics", playPlayList(arg2));
                bundle.putString("name", name);
                intent.putExtras(bundle);
                startActivity(intent);

            }
        });

        //长按弹出播放，删除操作
        mPlayListView.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(final AdapterView<?> adapterView, final View v,
                                           final int arg2, long arg3) {
                final Dialog dialog = new Dialog(getActivity(), R.style.Dialog_nowindowbg);
                View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_playlist_modify_pan, null);
                dialog.setContentView(dialogView);
                Button bt_modify = (Button) dialogView.findViewById(R.id.bt_modifydia_modify);
                Button bt_delete = (Button) dialogView.findViewById(R.id.bt_modifydia_delete);
                Button bt_cancel = (Button) dialogView.findViewById(R.id.bt_modifydia_cancel);
                bt_modify.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        modifyPlayList(arg2);
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }
                });
                bt_delete.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        delPlayList(arg2);
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }
                });
                bt_cancel.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }
                });

                LayoutParams param = dialog.getWindow().getAttributes();
                param.gravity = Gravity.CENTER;
                param.width = (int) getActivity().getResources().getDimension(R.dimen.dialog_modify_width);
                param.height = (int) getActivity().getResources().getDimension(R.dimen.dialog_modify_height);
                dialog.getWindow().setAttributes(param);
                dialog.show();


                return true;
            }
        });
    }

    private ArrayList<Music> playPlayList(int index) {
        //重新组装列表，然后拿去播放
        ArrayList<Music> musicsPlay = new ArrayList<Music>();
        MusicPlayList list = musicPlayLists.get(index);
        MusicProvider provider = new MusicProvider(getActivity());
        ArrayList<Music> musics = (ArrayList<Music>) provider.getList();

        for (String musicPath : list.getPlayList()) {

            for (Music music : musics) {
                if (music.getPath().equals(musicPath)) {
                    musicsPlay.add(music);
                    break;
                }
            }
        }

        return musicsPlay;
    }

    private void delPlayList(int index) {
        MusicPlayList list = musicPlayLists.get(index);

        try {
            File file = new File(list.getPath());
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        musicPlayLists.remove(list);
        updateView();
    }

    private void updateView() {
        getActivity().sendBroadcast(new Intent(M3UListProviderService.UPDATE_INTENT));

        //加载列表
        if (musicPlayLists != null) {
            List<String> tempList;
            for (MusicPlayList l : musicPlayLists) {
                tempList = M3UPlayList.loadPlayListToStringList(getActivity(), l.getPath());
                if (tempList == null) {
                    continue;
                }
                l.getPlayList().clear();
                l.getPlayList().addAll(tempList);
                l.setComment(getActivity().getString(R.string.playlistcomment_pre) + l.getPlayList().size() + getActivity().getString(R.string.playlistcomment_end));
            }
        }

        //更新视图
        if (mPlayListView != null
                && mPlayListView.getAdapter() != null)
            ((BaseAdapter) mPlayListView.getAdapter()).notifyDataSetChanged();
    }

    private void addPlayList() {
        Dialog dialog = DialogUtil.showEditDialog(getActivity(),getResources().getString(R.string.create_playlist), getResources().getString(R.string.confirm_space), getResources().getString(R.string.cancel_space), new DialogBtnOnClickListener() {

            @Override
            public void onSubmit(DialogMessage dialogMessage) {
                String playListName = dialogMessage.msg;
                if (playListName == null
                        || playListName.isEmpty()) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.didnot_enter_any_information), Toast.LENGTH_SHORT).show();
                    return;
                }
                MusicPlayList list = M3UPlayList.generalplaylist(MusicCategoryPlaylistTab.this.getActivity(), playListName);
                if (list == null) {
                    Toast.makeText(getActivity(), R.string.playlist_createfile_failed, Toast.LENGTH_SHORT).show();
                    return;
                }
                musicPlayLists.add(list);

                Intent intent = new Intent();
                intent.setClass(getActivity(), MusicPlayListActivity.class);
                intent.putExtra(MusicPlayListActivity.SERIALIZABLE_OBJECT, list);

                startActivityForResult(intent, RETURN_ACTIVITY_ADD);
                if (dialogMessage.dialog != null && dialogMessage.dialog.isShowing()) {
                	dialogMessage.dialog.cancel();
				}
            }

            @Override
            public void onCancel(DialogMessage dialogMessage) {
            	if (dialogMessage.dialog != null && dialogMessage.dialog.isShowing()) {
                	dialogMessage.dialog.cancel();
				}
            }

        });
    }

    private void modifyPlayList(int index) {
        MusicPlayList list = musicPlayLists.get(index);

        Intent intent = new Intent();
        intent.setClass(getActivity(), MusicPlayListActivity.class);
        intent.putExtra(MusicPlayListActivity.SERIALIZABLE_OBJECT, list);

        startActivity(intent);
    }

    private synchronized void loadPlayLists() {


        //更新视图，之后再获取到文件的时候会重新填充
        updateView();

        // 收索sdcard，找到播放列表文件，并加入，然后更新列表
        List<String> list = M3UListProviderService.getList();

        if (list == null
                || list.size() == 0) {
            return;
        }

        // 删除列表
        musicPlayLists.clear();

        List<String> tempList;
        for (String s : list) {
            MusicPlayList item = new MusicPlayList();
            item.setPath(s);
            item.setName(s.substring(s.lastIndexOf("/") + 1, s.length() - M3UPlayList.SUFFIX.length()));

            //加载列表内容，并与歌曲关联
            tempList = M3UPlayList.loadPlayListToStringList(getActivity(), item.getPath());
            if (tempList == null) {
                continue;
            }
            item.getPlayList().clear();
            item.getPlayList().addAll(tempList);
            item.setComment(getActivity().getString(R.string.playlistcomment_pre) + item.getPlayList().size() + getString(R.string.playlistcomment_end));

            musicPlayLists.add(item);
        }

        //更新列表
        if (mPlayListView != null
                && mPlayListView.getAdapter() != null)
            ((BaseAdapter) mPlayListView.getAdapter()).notifyDataSetChanged();
    }
}
