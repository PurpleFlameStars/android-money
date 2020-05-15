package com.dzfd.gids.baselibs.stat;

import android.app.Activity;

public interface ActivitySessionCallback {
    void onActivityStarted(Activity activity);

    void onActivityStopped(Activity activity);
}
