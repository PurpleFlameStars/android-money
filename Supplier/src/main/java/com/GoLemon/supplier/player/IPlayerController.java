package com.GoLemon.supplier.player;

import android.content.Context;
import android.view.View;

import com.GoLemon.supplier.video.VideoItem;

/**
 * Created by zheng on 2019/4/27.
 */

public interface IPlayerController extends IPlayEvent {
    void setPlayerEntity(IPlayerEntity entity);
    void setVideo(VideoItem item);
    void showCtrlPanel();
    void showCtrlPanel(boolean show);
    void onProgressChange(long position, long duration, int secondaryProgress);
    void onFullScreenChange(boolean isfull);
    boolean isPrePared();
    boolean OnReset();
    View getLaoutView(Context cxt);

}
