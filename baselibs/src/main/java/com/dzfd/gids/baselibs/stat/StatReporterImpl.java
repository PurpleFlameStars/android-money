package com.dzfd.gids.baselibs.stat;

import java.util.HashMap;

public interface StatReporterImpl {
    void onEvent(String eventId, StatEntity statEntity, HashMap<String, String> baseParams);

    boolean validKey(String key);
}
