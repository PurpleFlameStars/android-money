package com.GoLemon.supplier.player;

/**
 * Created by zheng on 2018/12/29.
 */

public interface IPlayerEntity {
    void doPause();
    void doResume();
    void doStop();
    void doReplay();
    void doPlayNext();
    void doPlayPrevious();
    void doFullScreen();
    void setMute(boolean mute);
    void seekTo(int pos);
    boolean isPortrait();
    boolean isLandscape();
    boolean isHasPrevious();
    boolean isPlaying();
    boolean isPaused();
    boolean isMute();

}
