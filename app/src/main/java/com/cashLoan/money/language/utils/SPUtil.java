package com.cashLoan.money.language.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.cashLoan.money.BuildConfig;
import com.cashLoan.money.language.Language;

import java.util.Locale;

public class SPUtil {

    private final String SP_NAME = "language_setting";
    private final String TAG_LANGUAGE = "language_select";
    private static volatile SPUtil instance;

    private final SharedPreferences mSharedPreferences;

    private Locale systemCurrentLocal = LocalManageUtil.getDefaultLocale();


    public SPUtil(Context context) {
        mSharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
    }


    public void saveLanguage(int select) {
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        edit.putInt(TAG_LANGUAGE, select);
        edit.commit();
    }

    public int getSelectLanguage() {
        switch (BuildConfig.LANGUAGE) {
            case "en":
                return mSharedPreferences.getInt(TAG_LANGUAGE, Language.ENGLISH);
            case "es":
                return mSharedPreferences.getInt(TAG_LANGUAGE, Language.SPANISH);
            case "zh":
                return mSharedPreferences.getInt(TAG_LANGUAGE, Language.CHINESE);
            case "pt":
                return mSharedPreferences.getInt(TAG_LANGUAGE, Language.PORTUGUESE);
            default:
                return mSharedPreferences.getInt(TAG_LANGUAGE, Language.ENGLISH);
        }
    }


    public Locale getSystemCurrentLocal() {
        return systemCurrentLocal;
    }

    public void setSystemCurrentLocal(Locale local) {
        systemCurrentLocal = local;
    }

    public static SPUtil getInstance(Context context) {
        if (instance == null) {
            synchronized (SPUtil.class) {
                if (instance == null) {
                    instance = new SPUtil(context);
                }
            }
        }
        return instance;
    }
}
