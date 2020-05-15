package com.GoLemon.supplier.impl;

import com.GoLemon.supplier.player.listener.VideoDownloadListener;

/**
 * Created by zheng on 2019/6/23.
 */

public class VideoDownloadListenerWrapper implements VideoDownloadListener {
    private VideoDownloadListener _listener;
    public VideoDownloadListenerWrapper(VideoDownloadListener listener){
        _listener=listener;
    }
    @Override
    public void onStart(String rid) {
        if(_listener!=null){
            _listener.onStart(rid);
        }
    }

    @Override
    public void onProgress(String rid, long position, long total, double speed) {
        if(_listener!=null){
            _listener.onProgress(rid,position,total,speed);
        }
    }

    @Override
    public void onSuccess(String rid,String filepath) {
        if(_listener!=null){
            _listener.onSuccess(rid,filepath);
        }
    }

    @Override
    public void onFailed(String rid, int errCode, String errMsg) {
        if(_listener!=null){
            _listener.onFailed(rid,errCode,errMsg);
        }
    }
}
