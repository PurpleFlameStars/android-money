package com.GoLemon.supplier.video;

import android.os.Parcelable;

import com.GoLemon.supplier.BunItem;

/**
 * Created by zheng on 2018/12/11.
 */

public abstract class VideoItem implements BunItem {

    abstract public String getId();
    abstract public String getdlId();
    abstract public String getTitle();
    abstract public String getImage();
    abstract public int getDuration();
    abstract public int getSize();
    abstract public VideoAuthor getAuthor();
    abstract public String getVideoUrl();
    abstract public String getDownLoadUrl();
    abstract public String getFrom();
    abstract public Parcelable getParcelObject();
    abstract public boolean enableDownload();
}
