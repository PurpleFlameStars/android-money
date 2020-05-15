package com.dzfd.gids.baselibs.utils;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import java.util.HashMap;

public final class MetaDataManager {
    private HashMap<String, String> metaMap;

    public static final String KEY_APP_FLYER = "KEY_APP_FLYER";
    public static final String KEY_FLURRY_AGENT = "KEY_FLURRY_AGENT";
    public static final String KEY_BUGLY = "KEY_BUGLY";
    public static final String KEY_GOOGLE_SERVER = "KEY_GOOGLE_SERVER";
    public static final String KEY_FB_APP_ID = "KEY_FB_APP_ID";

    private volatile static MetaDataManager INSTANCE;

    public static MetaDataManager getInstance() {
        if (INSTANCE == null) {
            synchronized (MetaDataManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new MetaDataManager();
                }
            }
        }
        return INSTANCE;
    }

    private MetaDataManager() {
        metaMap = new HashMap<>();
        ApplicationInfo appInfo = null;
        try {
            appInfo = ContextUtils.getApplicationContext().getPackageManager()
                    .getApplicationInfo(ContextUtils.getApplicationContext().getPackageName(),
                            PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (appInfo != null) {
            metaMap.put(KEY_APP_FLYER, appInfo.metaData.getString("AF_DEV_KEY"));
            metaMap.put(KEY_FLURRY_AGENT, appInfo.metaData.getString("FLURRY_AGENT_KEY"));
            metaMap.put(KEY_BUGLY, appInfo.metaData.getString("BUGLY_KEY"));
            metaMap.put(KEY_GOOGLE_SERVER, appInfo.metaData.getString("GOOGLE_SERVER"));
            String fb_value = appInfo.metaData.getString("FB_APP_ID_VALUE");
            if (!TextUtils.isEmpty(fb_value)) {
                metaMap.put(KEY_FB_APP_ID, fb_value.substring(0, fb_value.length() - 1));
            }
        }
    }

    public String getTargetValue(String targetKey) {
        String value = metaMap.get(targetKey);
        if (TextUtils.isEmpty(value)) {
            value = "";
        }
        return value;
    }
}
