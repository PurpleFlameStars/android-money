package com.GoLemon.supplier.player;

import android.content.Context;

import com.GoLemon.supplier.config.SysMediaPath;
import com.GoLemon.supplier.player.listener.VideoDownloadListener;
import com.GoLemon.supplier.video.VideoItem;

import androidx.annotation.NonNull;

/**
 * Created by zheng on 2018/12/11.
 */

public interface BunPlayer {
    boolean initPlayer(Context cxt,  boolean isdubug, String ver, String uid,String appname);
    void unInitPlayer();
    void setDebugEnable(boolean enable);
    PlayerHolder getPlayerHolder();
    boolean cacheVideo(String vid, String url, int size);
    boolean DownloadVideo(VideoItem videoItem, String appname, @NonNull SysMediaPath path, VideoDownloadListener callback);
    boolean CancelDownLoad(VideoItem videoItem);

}
