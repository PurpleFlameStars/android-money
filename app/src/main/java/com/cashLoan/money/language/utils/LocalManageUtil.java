package com.cashLoan.money.language.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.util.DisplayMetrics;

import com.cashLoan.money.language.Language;
import com.dzfd.gids.baselibs.utils.LogUtils;

import java.util.Locale;

public class LocalManageUtil {

    private static final String TAG = "LocalManageUtil";

    public static String getCurrentLanguage(Context context) {
        Locale locale = getSetLanguageLocale(context);
        if (locale == null) {
            return "empty";
        } else {
            return locale.getLanguage() + "-" + locale.getCountry();
        }
    }

    public static String getCurrentLanguageWithoutCountry(Context context) {
        Locale locale = getSetLanguageLocale(context);
        if (locale == null) {
            return "empty";
        } else {
            return locale.getLanguage();
        }
    }

    /**
     * 获取app默认适配Locale
     * @return Locale
     */
    static Locale getDefaultLocale() {
        return new Locale("en", "US");
    }

    /**
     * 获取系统的locale
     *
     * @return Locale对象
     */
    public static Locale getSystemLocale(Context context) {
        return SPUtil.getInstance(context).getSystemCurrentLocal();
    }

    /**
     * 获取已经选中语言的代码
     * @param context context
     * @return 选中语言的代码
     */
    public static int getSelectLanguageCode(Context context) {
        return SPUtil.getInstance(context).getSelectLanguage();
    }

    /**
     * 获取选择的语言设置
     *
     * @param context context
     * @return
     */
    private static Locale getSetLanguageLocale(Context context) {
        switch (SPUtil.getInstance(context).getSelectLanguage()) {
            case Language.AUTO:
                return getSystemLocale(context);
            case Language.CHINESE:
                return Locale.SIMPLIFIED_CHINESE;
            case Language.SPANISH:
                return new Locale("es", "MX");
            case Language.PORTUGUESE:
                return new Locale("pt", "BR");
            case Language.ENGLISH:
            default:
                return new Locale("en", "US");
        }
    }

    public static void saveSelectLanguage(Context context, int select) {
        SPUtil.getInstance(context).saveLanguage(select);
        setApplicationLanguage(context);
    }

    public static Context setLocal(Context context) {
        return updateResources(context, getSetLanguageLocale(context));
    }

    private static Context updateResources(Context context, Locale locale) {
        Locale.setDefault(locale);

        Resources res = context.getResources();

        Configuration config = res.getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(locale);
        } else {
            config.locale = locale;
        }
        res.updateConfiguration(config, res.getDisplayMetrics());
        return context;
    }

    /**
     * 设置语言类型
     */
    public static void setApplicationLanguage(Context context) {
        Resources resources = context.getApplicationContext().getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        Locale locale = getSetLanguageLocale(context);
        config.locale = locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            LocaleList localeList = new LocaleList(locale);
            LocaleList.setDefault(localeList);
            config.setLocales(localeList);
            context.getApplicationContext().createConfigurationContext(config);
            Locale.setDefault(locale);
        }
        resources.updateConfiguration(config, dm);
    }

    public static void saveSystemCurrentLanguage(Context context) {
        Locale locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = LocaleList.getDefault().get(0);
        } else {
            locale = Locale.getDefault();
        }
        LogUtils.d(TAG, locale.getLanguage());
        SPUtil.getInstance(context).setSystemCurrentLocal(locale);
    }

    public static void onConfigurationChanged(Context context){
        saveSystemCurrentLanguage(context);
        setLocal(context);
        setApplicationLanguage(context);
    }
}
