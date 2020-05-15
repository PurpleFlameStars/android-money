package com.dzfd.gids.baselibs.stat;

import android.app.Activity;

import com.dzfd.gids.baselibs.utils.MetaDataManager;
import com.flurry.android.FlurryAgent;

import java.util.HashMap;

import static com.dzfd.gids.baselibs.stat.StatEntity.ACTIVE_FROM;
import static com.dzfd.gids.baselibs.stat.StatEntity.PROCESS;

public class FlurryStatReporter implements StatReporterImpl, ActivitySessionCallback {

    public FlurryStatReporter() {
    }

    @Override
    public void onEvent(String eventId, StatEntity statEntity, HashMap<String, String> baseParams) {
        if (statEntity == null || statEntity.getParamsMap() == null || baseParams == null) {
            return;
        }

        if (statEntity.getParamsMap().containsKey(PROCESS)) {
            baseParams.put(PROCESS, statEntity.getParamsMap().get(PROCESS));
        }

        if (statEntity.getParamsMap().containsKey(ACTIVE_FROM)) {
            baseParams.put(ACTIVE_FROM, statEntity.getParamsMap().get(ACTIVE_FROM));
        }

        FlurryAgent.logEvent(eventId, baseParams);
    }

    @Override
    public boolean validKey(String key) {
        return true;
    }

    @Override
    public void onActivityStarted(Activity activity) {
        FlurryAgent.onStartSession(activity, MetaDataManager.getInstance().getTargetValue(MetaDataManager.KEY_FLURRY_AGENT));
    }

    @Override
    public void onActivityStopped(Activity activity) {
        FlurryAgent.onEndSession(activity);
    }
}
