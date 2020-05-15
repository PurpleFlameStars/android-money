package com.dzfd.gids.baselibs.listener;

import android.content.Context;

import java.io.Serializable;

/**
 * Created by zheng on 2019/1/28.
 */

public interface IRemoteData extends Serializable {
    int getErrCode();
    String getErrMsg();
    boolean dataIsValid();
    boolean isLastPage();
    boolean doTaskAward(Context cxt,Runnable runnable);


}
