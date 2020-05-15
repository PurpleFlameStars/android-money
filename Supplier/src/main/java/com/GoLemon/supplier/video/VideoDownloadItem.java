package com.GoLemon.supplier.video;

import com.GoLemon.supplier.player.listener.VideoDownloadListener;

/**
 * Created by zheng on 2019/6/23.
 */

public class VideoDownloadItem {
   public String rid;
    public String filepath;
    public VideoDownloadListener callback;
    public VideoDownloadItem(String id,String path,VideoDownloadListener listener){
        this.rid=id;
        this.filepath=path;
        this.callback=listener;
    }

}
