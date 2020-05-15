package com.dzfd.gids.baselibs.stat;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;

import com.dzfd.gids.baselibs.utils.ContextUtils;

import java.util.HashMap;

public class AliYunStatReporter implements StatReporterImpl {

    @Override
    public void onEvent(String eventId, StatEntity statEntity, HashMap<String, String> baseParams) {
        if (statEntity == null) {
            return;
        }
        reportStat(eventId, statEntity);
    }

    @Override
    public boolean validKey(String key) {
        return true;
    }

    /**
     * 上报服务端打点
     *
     * @param eventId
     * @param statEntity
     */
    private static void reportStat(String eventId, StatEntity statEntity) {
        statEntity.setKey(eventId);
        Intent intent = new Intent("com.golemon.wegoo.stat");
        intent.putExtra("statEntity", statEntity);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ContextUtils.getApplicationContext().sendBroadcast(intent);
        } else {
            intent.setComponent(new ComponentName(ContextUtils.getAppContext().getPackageName(), "com.GoLemon.stat.StatService"));
            try {
                ContextUtils.getApplicationContext().startService(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
