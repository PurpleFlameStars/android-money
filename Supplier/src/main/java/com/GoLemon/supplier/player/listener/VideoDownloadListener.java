package com.GoLemon.supplier.player.listener;

/**
 * Created by zheng on 2019/6/22.
 */

public interface VideoDownloadListener {
    void onStart(String rid);
    void onProgress(String rid, long position, long total, double speed);
    void onSuccess(String rid,String filepath);
    void onFailed(String rid, int errCode, String errMsg);
}
