package com.dzfd.gids.baselibs.LAN;

/**
 * Created by zheng on 2019/2/26.
 */

public interface OnScanListener {
    void onFound(Device device);

    void onFinished();

    void onBeginLoading();
}
