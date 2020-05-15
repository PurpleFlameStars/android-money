package com.GoLemon.supplier.player;

/**
 * Created by zheng on 2019/9/3.
 */

public interface IPlayEvent {
    void onPlayStart();
    void OnPlayPaused();
    void OnPlayResume();
    void OnPlayStoped();
    void OnPlayComplete();
    void OnReplay();
    void OnPrepared(boolean value);
}
