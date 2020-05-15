package com.cashLoan.money.network;

import android.content.Context;

import com.GoLemon.supplier.BunItem;
import com.dzfd.gids.baselibs.listener.IRemoteData;

import java.util.List;

/**
 * Created by zheng on 2019/3/6.
 */

public class LemonRemoteData implements IRemoteData {
    private int code;
    private String msg;
    private String version;
    private long timestamp;

    @Override
    public int getErrCode() {
        return code;
    }

    @Override
    public String getErrMsg() {
        return msg;
    }

    @Override
    public boolean dataIsValid() {
        return code == 0;
    }

    @Override
    public boolean isLastPage() {
        return false;
    }

    @Override
    public boolean doTaskAward(Context cxt, final Runnable runnable) {
        return false;
    }

    public long getTimestamp() {
        return timestamp;
    }

    //============================================

    public List<BunItem> getItems() {
        return null;
    }

    public String getVersion() {
        return version;
    }

    public void setCode(int code) {
        this.code = code;
    }
    public void setErrMsg(String msg) {
        this.msg = msg;
    }


}
