package com.GoLemon.supplier.music;

import android.content.Context;

import com.GoLemon.supplier.player.BunRepeatModel;

import java.util.List;

/**
 * Created by zheng on 2019/9/18.
 */

public interface IMusicPlayer {
    void doPause();
    void doResume();
    void doStop();
    void doReplay();
    void doPlayNext();
    void doPlayPrevious();
    void setMute(boolean mute);
    void seekTo(int pos);
    boolean isPlaying();
    boolean isPaused();
    boolean isMute();
    boolean StartPlay(Context cxt,IMusicItem item,long pos);
    boolean onDestroy();
    boolean AddMusicToList(Context cxt,List<IMusicItem> items);
    boolean clearPlayList();
    boolean PlayMusic(Context cxt,int index);
    boolean setRepeatModel(BunRepeatModel repeatModel);
    BunRepeatModel getRepeatModel();

}
