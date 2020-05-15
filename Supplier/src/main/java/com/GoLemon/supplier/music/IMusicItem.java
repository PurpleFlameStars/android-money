package com.GoLemon.supplier.music;

/**
 * Created by zheng on 2019/9/18.
 */

public interface  IMusicItem  {
    String getId();
    String getdlId();
    String getTitle();
    String getImage();
    int getDuration();
    int getSize();
    String getPath();
    String getAuthor();
    String getAlbumId();
}
