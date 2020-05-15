package com.GoLemon.supplier.impl;

import com.GoLemon.supplier.player.listener.VideoDownloadListener;

/**
 * Created by zheng on 2019/6/23.
 */

public class VideoDownloadListenerImpl implements VideoDownloadListener {
    @Override
    public void onStart(String rid) {

    }

    @Override
    public void onProgress(String rid, long position, long total, double speed) {

    }

    @Override
    public void onSuccess(String rid,String filepath) {

    }

    @Override
    public void onFailed(String rid, int errCode, String errMsg) {

    }
}
