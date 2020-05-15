package com.dzfd.gids.baselibs.UI.recyclerview.bean;

import com.GoLemon.supplier.BunItem;
import com.dzfd.gids.baselibs.UI.recyclerview.lisenter.ILoadRetry;
import com.dzfd.gids.baselibs.network.HttpLoader;

public class LoadErrorBean implements BunItem {
    public int code;
    public String msg;
    public HttpLoader.HttpLoadPos pos;
    public ILoadRetry iretry;
    public LoadErrorBean(int code, String msg, HttpLoader.HttpLoadPos pos,ILoadRetry iretry){
        this.code=code;
        this.msg=msg;
        this.pos=pos;
        this.iretry=iretry;
    }
    public LoadErrorBean(int code,String msg){
        this(code,msg, HttpLoader.HttpLoadPos.LOAD_POSTION_NONE,null);
    }

    public static final int SYSERROR_NONET=50001;

}
