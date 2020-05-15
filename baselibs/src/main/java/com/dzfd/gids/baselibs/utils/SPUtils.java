package com.dzfd.gids.baselibs.utils;

import java.util.Map;
import java.util.Set;

public class SPUtils {
    private static final String TAG = "SPUtils";

    /**
     * 保存在手机里面的文件名o
     */
    private static final String FILE_NAME = "share_data";


    /**
     * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     */

    public static void put(String key, Object object) {
        put(FILE_NAME, key, object);
    }

    public static void put(String fileName, String key, Object object) {
        if (object instanceof String) {
            NewSPUtils.getInstance(fileName).put(key, (String) object);
        } else if (object instanceof Integer) {
            NewSPUtils.getInstance(fileName).put(key, (Integer) object);
        } else if (object instanceof Boolean) {
            NewSPUtils.getInstance(fileName).put(key, (Boolean) object);
        } else if (object instanceof Float) {
            NewSPUtils.getInstance(fileName).put(key, (Float) object);
        } else if (object instanceof Long) {
            NewSPUtils.getInstance(fileName).put(key, (Long) object);
        } else if (object instanceof Set) {
            NewSPUtils.getInstance(fileName).put(key, (Set) object);
        } else if (object == null) {
            NewSPUtils.getInstance(fileName).remove(key);
        }
    }

    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     */

    public static Object get(String key, Object defaultObject) {
        return get(FILE_NAME, key, defaultObject);
    }

    public static Object get(String fileName, String key, Object defaultObject) {
        if (defaultObject instanceof String) {
            return NewSPUtils.getInstance(fileName).getString(key, (String) defaultObject);
        } else if (defaultObject instanceof Integer) {
            return NewSPUtils.getInstance(fileName).getInt(key, (Integer) defaultObject);
        } else if (defaultObject instanceof Boolean) {
            return NewSPUtils.getInstance(fileName).getBoolean(key, (Boolean) defaultObject);
        } else if (defaultObject instanceof Float) {
            return NewSPUtils.getInstance(fileName).getFloat(key, (Float) defaultObject);
        } else if (defaultObject instanceof Long) {
            return NewSPUtils.getInstance(fileName).getLong(key, (Long) defaultObject);
        } else if (null == defaultObject) {
            return NewSPUtils.getInstance(fileName).getString(key, null);
        }

        return null;
    }

    public static boolean getBoolean(String fileName, String key, boolean defValue) {
        return (Boolean) get(fileName, key, defValue);
    }

    public static void setBoolean(String fileName, String key, boolean defValue) {
        put(fileName, key, defValue);
    }

    public static String getString(String fileName, String key, String defValue) {
        return (String) get(fileName, key, defValue);
    }

    public static void setString(String fileName, String key, String defValue) {
        put(fileName, key, defValue);
    }

    public static int getInt(String fileName, String key, int defValue) {
        return (int) get(fileName, key, defValue);
    }

    public static void setInt(String fileName, String key, int defValue) {
        put(fileName, key, defValue);
    }

    public static int getInt(String key, int defValue) {
        return getInt(FILE_NAME, key, defValue);
    }

    public static void setInt(String key, int defValue) {
        setInt(FILE_NAME, key, defValue);
    }

    public static long getLong(String fileName, String key, long defValue) {
        Object val = get(fileName, key, defValue);
        long ret = 0;
        if (val != null) {
            if (val instanceof Long) {
                ret = (long) val;
            }
        }
        return ret;
    }

    public static void setLong(String fileName, String key, long defValue) {
        put(fileName, key, defValue);
    }

    public static void remove(String key) {
        remove(FILE_NAME, key);
    }

    public static void remove(String fileName, String key) {
        NewSPUtils.getInstance(fileName).remove(key);
    }

    /**
     * 清除所有数据
     */
    public static void clear() {
        clear(FILE_NAME);
    }

    public static void clear(String fileName) {
        NewSPUtils.getInstance(fileName).clear();
    }

    /**
     * 查询某个key是否已经存在
     */

    public static boolean contains(String key) {
        return contains(FILE_NAME, key);
    }


    public static boolean contains(String fileName, String key) {
        return NewSPUtils.getInstance(fileName).contains(key);
    }

    /**
     * 返回所有的键值对
     */
    public static Map<String, ?> getAll() {
        return getAll(FILE_NAME);
    }

    public static Map<String, ?> getAll(String fileName) {
        return NewSPUtils.getInstance(fileName).getAll();
    }

    public static boolean clearKey(String key) {
        return clearKey(FILE_NAME, key);
    }

    public static boolean clearKey(String fileName, String key) {
        NewSPUtils.getInstance(fileName).remove(key);
        return true;
    }
}
