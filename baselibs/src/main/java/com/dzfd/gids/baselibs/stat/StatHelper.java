package com.dzfd.gids.baselibs.stat;

import java.util.ArrayList;
import java.util.HashMap;

import static com.dzfd.gids.baselibs.stat.StatEntity.ACTION;
import static com.dzfd.gids.baselibs.stat.StatEntity.ACTIVE_FROM;
import static com.dzfd.gids.baselibs.stat.StatEntity.EXTRA;
import static com.dzfd.gids.baselibs.stat.StatEntity.EXTRA_1;
import static com.dzfd.gids.baselibs.stat.StatEntity.EXTRA_2;
import static com.dzfd.gids.baselibs.stat.StatEntity.LABEL;
import static com.dzfd.gids.baselibs.stat.StatEntity.M2;
import static com.dzfd.gids.baselibs.stat.StatEntity.REFER;

public class StatHelper {
    private static ArrayList<StatReporterImpl> statReporters = new ArrayList<>();
    private static final String[] baseParams = new String[]{ACTION, LABEL, REFER, EXTRA, EXTRA_1, EXTRA_2, ACTIVE_FROM, M2};

    public static void initStatReporter(StatReporterImpl... reporters) {
        if (reporters != null) {
            for (StatReporterImpl reporter : reporters) {
                statReporters.add(reporter);
            }
        }
    }

    public static void onEvent(String eventId, StatEntity statEntity) {
        if (statEntity == null) {
            return;
        }

        HashMap<String, String> paramsMap = statEntity.getParamsMap();
        if (paramsMap == null) {
            return;
        }

        HashMap<String, String> reportMap = new HashMap<>();

        for (String params : baseParams) {
            if (paramsMap.containsKey(params)) {
                reportMap.put(params, paramsMap.get(params));
            }
        }

        for (StatReporterImpl reporter : statReporters) {
            if (reporter.validKey(eventId)) {
                reporter.onEvent(eventId, statEntity, reportMap);
            }
        }
    }

}
