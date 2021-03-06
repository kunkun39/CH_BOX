/**
 *
 */
package com.changhong.touying.tab;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.changhong.common.utils.DialogUtil;
import com.changhong.common.utils.DialogUtil.DialogBtnOnClickListener;
import com.changhong.common.utils.DialogUtil.DialogMessage;
import com.changhong.touying.R;
import com.changhong.touying.activity.MusicPlayListActivity;
import com.changhong.touying.activity.MusicViewActivity;
import com.changhong.touying.adapter.DividerItemDecoration;
import com.changhong.touying.music.M3UPlayList;
import com.changhong.touying.music.Music;
import com.changhong.touying.music.MusicPlayList;
import com.changhong.touying.music.MusicProvider;
import com.changhong.touying.service.M3UListProviderService;
import com.changhong.touying.service.MusicService;
import com.changhong.touying.service.MusicServiceImpl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yves.yang
 */
public class MusicCategoryPlaylistTab extends Fragment {

    public static final String TAG = "MusicCategoryPlaylistTab";

    private static final int RETURN_ACTIVITY_ADD = 1;
    private RecyclerView mRecyclerView = null;
    PlayListRecyclerViewAdapter RecyclerViewAdapter = null;
    /**
     * 添加列表按钮
     */
    // private Button mAddNewListBtn;
    private FloatingActionButton mAddNewListBtn;

    /**
     * 播放列表View
     */
    private ListView mPlayListView;

    View view = null;

    /**
     * 播放列表的列表
     */
    List<MusicPlayList> musicPlayLists = new ArrayList<MusicPlayList>();

    /*recyclerView*/
    private int mPreviousVisibleItem;

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

        mAddNewListBtn = (FloatingActionButton) v.findViewById(R.id.music_list_add);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.music_list_list);

        RecyclerViewAdapter = new PlayListRecyclerViewAdapter();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mRecyclerView
                .getContext()));
        mRecyclerView.setAdapter(RecyclerViewAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
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

        // 更新视图
        if (mRecyclerView != null && RecyclerViewAdapter != null)
            RecyclerViewAdapter.notifyDataSetChanged();
    }

    private void addPlayList() {
        Dialog dialog = DialogUtil.showEditDialog(getActivity(), "请输入播放列表名字", "确    认", "取    消", new DialogBtnOnClickListener() {

            @Override
            public void onSubmit(DialogMessage dialogMessage) {
                String playListName = dialogMessage.msg;
                if (playListName == null
                        || playListName.isEmpty()) {
                    Toast.makeText(getActivity(), "未输入任何信息", Toast.LENGTH_SHORT).show();
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
        if (mRecyclerView != null && RecyclerViewAdapter != null)
            RecyclerViewAdapter.notifyDataSetChanged();


    }


    public class PlayListRecyclerViewAdapter extends
            RecyclerView.Adapter<PlayListRecyclerViewAdapter.ViewHolder> {


        public PlayListRecyclerViewAdapter() {

        }

        @Override
        public PlayListRecyclerViewAdapter.ViewHolder onCreateViewHolder(
                ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.playlist_list_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(
                final PlayListRecyclerViewAdapter.ViewHolder holder,
                final int position) {
            final View view = holder.mView;

            MusicPlayList item = (MusicPlayList) getItem(position);

            if (item != null) {

                holder.mIndexText.setText(String.valueOf(position + 1));
                holder.mPlayListName.setText(item.getName());
                holder.mPlayListComment.setText(item.getComment());

                holder.mIndexText.setText(String.valueOf(position + 1));
                holder.mPlayListName.setText(item.getName());
                holder.mPlayListComment.setText(item.getComment());

            }

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String path = musicPlayLists.get(position).getPath();
                    String name = path.substring(path.lastIndexOf("/") + 1,
                            path.length() - M3UPlayList.SUFFIX.length());

                    Intent intent = new Intent();
                    intent.setClass(getActivity(), MusicViewActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("musics", playPlayList(position));
                    bundle.putString("name", name);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });

            // 长按弹出播放，删除操作
            view.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                    final Dialog dialog = new Dialog(getActivity(),
                            R.style.Dialog_nowindowbg);
                    View dialogView = LayoutInflater.from(getActivity()).inflate(
                            R.layout.dialog_playlist_modify, null);
                    dialog.setContentView(dialogView);
                    Button bt_modify = (Button) dialogView
                            .findViewById(R.id.bt_modifydia_modify);
                    Button bt_delete = (Button) dialogView
                            .findViewById(R.id.bt_modifydia_delete);
                    Button bt_cancel = (Button) dialogView
                            .findViewById(R.id.bt_modifydia_cancel);
                    bt_modify.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            modifyPlayList(position);
                            if (dialog != null && dialog.isShowing()) {
                                dialog.dismiss();
                            }
                        }
                    });
                    bt_delete.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            delPlayList(position);
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
                    param.width = (int) getActivity().getResources().getDimension(
                            R.dimen.dialog_modify_width);
                    param.height = (int) getActivity().getResources().getDimension(
                            R.dimen.dialog_modify_height);
                    dialog.getWindow().setAttributes(param);
                    dialog.show();

                    return true;
                }
            });

        }

        @Override
        public int getItemCount() {
            return musicPlayLists.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView mIndexText;
            TextView mPlayListName;
            TextView mPlayListComment;
            public final View mView;

            public ViewHolder(View view) {

                super(view);
                mView = view;
                mIndexText = (TextView) view
                        .findViewById(R.id.playlist_listitem_index);
                mPlayListName = (TextView) view
                        .findViewById(R.id.playlist_listitem_name);
                mPlayListComment = (TextView) view
                        .findViewById(R.id.playlist_listitem_comment);

            }
        }

        public Object getItem(int position) {
            return musicPlayLists.get(position);
        }
    }

}
