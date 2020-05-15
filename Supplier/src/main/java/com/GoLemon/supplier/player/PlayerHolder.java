package com.GoLemon.supplier.player;

import android.content.Context;

/**
 * Created by zheng on 2018/12/27.
 */

public interface PlayerHolder {
    PlayerView createPlayerView(Context context, String uid);
    PlayerView getPlayerView(Context context);
    void destroy(Context context);
    void destroyAll();
}
