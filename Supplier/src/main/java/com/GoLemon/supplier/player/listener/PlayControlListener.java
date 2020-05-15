package com.GoLemon.supplier.player.listener;

/**
 * Created by zheng on 2019/1/3.
 */

public interface PlayControlListener {
    void OnPlayNextVideo();
    void OnPlayPreviousVideo();
    boolean isHasPrevious();
}
