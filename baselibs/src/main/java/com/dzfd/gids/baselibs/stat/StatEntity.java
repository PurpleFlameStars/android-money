package com.dzfd.gids.baselibs.stat;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.dzfd.gids.baselibs.helper.ProcessHelper;
import com.dzfd.gids.baselibs.network.NetUtils;
import com.dzfd.gids.baselibs.utils.StringUtils;

import java.util.HashMap;


public class StatEntity implements Parcelable {
    public final static String ACTION = "action";
    public final static String LABEL = "label";
    public final static String REFER = "refer";
    public final static String EXTRA = "extra";
    public final static String EXTRA_1 = "extra1";
    public final static String EXTRA_2 = "extra2";
    public final static String ACTIVE_FROM = "activitefrom";
    public static final String EVENT_ID = "event_id";
    public static final String CREATE_TIME = "createTime";
    public static final String PROCESS = "process";
    public static final String NETWORK = "network";
    public static final String UID = "uid";
    public static final String KEY = "key";
    public final static String M2 = "m2";

    public static final String APP = "app";
    public static final String GCM = "gcm";
    public static final String YX = "yx";
    public static final String YXGCM = "yxgcm";
    public static final String BROWSER = "browser";
    public static String activitefrom = "";
    public static String uid = "";

    private HashMap<String, String> paramsMap;
    private String id;

    public StatEntity() {
        this("", "", "", "", "");
    }

    public StatEntity(Parcel dest) {
        paramsMap = dest.readHashMap(HashMap.class.getClassLoader());
        id = dest.readString();
    }

    public StatEntity(String action) {
        this(action, "", "", "", "");
    }

    public StatEntity(String action, String label) {
        this(action, label, "", "", "");
    }

    public StatEntity(String action, String label, String refer) {
        this(action, label, refer, "", "");
    }

    public StatEntity(String action, String label, String refer, String extra) {
        this(action, label, refer, extra, "");
    }

    public StatEntity(String action, String label, String refer, String extra, String extra1) {
        this(action, label, refer, extra, extra1, "");
    }

    public StatEntity(String action, String label, String refer, String extra, String extra1, String extra2) {
        paramsMap = new HashMap<>();
        if (!TextUtils.isEmpty(action)) {
            paramsMap.put(ACTION, action);
        }
        if (!TextUtils.isEmpty(label)) {
            paramsMap.put(LABEL, label);
        }
        if (!TextUtils.isEmpty(refer)) {
            paramsMap.put(REFER, refer);
        }
        if (!TextUtils.isEmpty(extra)) {
            paramsMap.put(EXTRA, extra);
        }
        if (!TextUtils.isEmpty(extra1)) {
            paramsMap.put(EXTRA_1, extra1);
        }
        if (!TextUtils.isEmpty(extra2)) {
            paramsMap.put(EXTRA_2, extra2);
        }
        if (!TextUtils.isEmpty(activitefrom)) {
            paramsMap.put(ACTIVE_FROM, activitefrom);
        }
        if (!TextUtils.isEmpty(uid)) {
            paramsMap.put(UID, uid);
        }

        id = StringUtils.getRandomString(32);
        paramsMap.put(EVENT_ID, id);
        paramsMap.put(M2, NetUtils.getIns().getUid());
        paramsMap.put(CREATE_TIME, String.valueOf(System.currentTimeMillis()));
        paramsMap.put(PROCESS, ProcessHelper.getProcessName());
        paramsMap.put(NETWORK, NetUtils.isWiFI(true) ? "wifi" : "mobile");
    }

    public StatEntity setAction(String action) {
        if (paramsMap != null && !TextUtils.isEmpty(action)) {
            paramsMap.put(ACTION, action);
        }
        return this;
    }

    public StatEntity setLabel(String label) {
        if (paramsMap != null && !TextUtils.isEmpty(label)) {
            paramsMap.put(LABEL, label);
        }
        return this;
    }

    public StatEntity setRefer(String refer) {
        if (paramsMap != null && !TextUtils.isEmpty(refer)) {
            paramsMap.put(REFER, refer);
        }
        return this;
    }

    public StatEntity setExtra(String extra) {
        if (paramsMap != null && !TextUtils.isEmpty(extra)) {
            paramsMap.put(EXTRA, extra);
        }
        return this;
    }

    public StatEntity setExtra1(String extra1) {
        if (paramsMap != null && !TextUtils.isEmpty(extra1)) {
            paramsMap.put(EXTRA_1, extra1);
        }
        return this;
    }

    public StatEntity setExtra2(String extra2) {
        if (paramsMap != null && !TextUtils.isEmpty(extra2)) {
            paramsMap.put(EXTRA_2, extra2);
        }
        return this;
    }

    public StatEntity setKey(String key) {
        if (paramsMap != null && !TextUtils.isEmpty(key)) {
            paramsMap.put(KEY, key);
        }
        return this;
    }

    public static void setActivitefrom(String activitefrom) {
        if (TextUtils.isEmpty(StatEntity.activitefrom)) {
            StatEntity.activitefrom = activitefrom;
        }
    }

    public HashMap<String, String> getParamsMap() {
        return paramsMap;
    }

    public static final Creator<StatEntity> CREATOR = new Creator<StatEntity>() {
        @Override
        public StatEntity createFromParcel(Parcel parcel) {
            return new StatEntity(parcel);
        }

        @Override
        public StatEntity[] newArray(int size) {
            return new StatEntity[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeMap(paramsMap);
        parcel.writeString(id);
    }

    @Override
    public boolean equals(Object obj) {
        boolean equals;
        if (!(obj instanceof StatEntity) || id == null) {
            equals = false;
        } else if (obj == this) {
            equals = true;
        } else {
            equals = id.equals(((StatEntity) obj).id);
        }

        return equals;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        if (paramsMap != null) {
            return paramsMap.toString();
        }
        return super.toString();
    }
}
