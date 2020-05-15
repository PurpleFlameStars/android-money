package com.GoLemon.supplier;

import android.content.Context;

import com.GoLemon.supplier.config.SysMediaPath;
import com.GoLemon.supplier.player.listener.VideoDownloadListener;
import com.GoLemon.supplier.video.VideoItem;


public interface ILocalServer {

    boolean initPlayer(Context cxt, String ver, String uid);
    void setDebugEnable(boolean enable);
    boolean InitLocalServer(Context cxt);
    boolean StopLocalServer();
    boolean cacheVideo(String vid, String url, int size);//单位字节
    boolean DownloadVideo(VideoItem videoItem, String appname , SysMediaPath path, VideoDownloadListener callback);
    boolean CancelDownLoad(VideoItem item);
    String getLocalUrl(String vid,String url);

}
