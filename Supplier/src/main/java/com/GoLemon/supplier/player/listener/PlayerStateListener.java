package com.GoLemon.supplier.player.listener;

import com.GoLemon.supplier.player.IPlayEvent;

/**
 * Created by zheng on 2018/12/28.
 */

public interface PlayerStateListener extends IPlayEvent {

    void onFullScreenChange(boolean isfull);
    void OnVideoBufferStart();
    void OnVideoBuffering(int progress);
    void OnVideoBufferStop();
    void onVideoSizeChange(int width, int height);
    void onVideoError(int code,int extra);
    void onVideoError(int code,String url,String extra);
}
