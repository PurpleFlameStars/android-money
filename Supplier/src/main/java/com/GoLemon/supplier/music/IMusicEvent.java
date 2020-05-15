package com.GoLemon.supplier.music;

import com.GoLemon.supplier.player.BunRepeatModel;
import com.GoLemon.supplier.player.IPlayEvent;

/**
 * Created by zheng on 2019/9/24.
 */

public interface IMusicEvent extends IPlayEvent {
    void onProgressChange(long position, long duration, int secondaryProgress);
    void OnBufferStart();
    void OnBuffering(int progress);
    void OnBufferStop();
    void OnMusicInfo(IMusicItem item);
    void OnChangePlayItem(int newindex);
    void OnPlayerReady(IMusicPlayer player);
    void OnRepeatModelChange(BunRepeatModel model);
}
