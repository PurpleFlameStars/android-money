package com.GoLemon.supplier.music;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by zheng on 2019/9/24.
 */

public class MusicItemBean implements IMusicItem,Parcelable{
    private String id;
    private String dlid;
    private String name;
    private String img;
    private int duration;
    private int size;
    private String src;
    private String author;
    private String albumid;

   public  MusicItemBean(IMusicItem item){
       if(item==null){
           return;
       }
       id=item.getId();
       dlid=item.getdlId();
       name=item.getTitle();
       img=item.getImage();
       duration=item.getDuration();
       size=item.getSize();
       src=item.getPath();
       author=item.getAuthor();
       albumid=item.getAlbumId();

   }

    protected MusicItemBean(Parcel in) {
        id = in.readString();
        dlid = in.readString();
        name = in.readString();
        img = in.readString();
        duration = in.readInt();
        size = in.readInt();
        src = in.readString();
        author=in.readString();
        albumid=in.readString();
    }



    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getdlId() {
        return id;
    }

    @Override
    public String getTitle() {
        return name;
    }

    @Override
    public String getImage() {
        return img;
    }

    @Override
    public int getDuration() {
        return duration;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public String getPath() {
        return src;
    }

    @Override
    public String getAuthor() {
        return author;
    }

    @Override
    public String getAlbumId() {
        return albumid;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if(dest == null){
            return;
        }
        dest.writeString(id);
        dest.writeString(dlid);
        dest.writeString(name);
        dest.writeString(img);
        dest.writeInt(duration);
        dest.writeInt(size);
        dest.writeString(src);
        dest.writeString(author);
        dest.writeString(albumid);
    }
    public static final Creator<MusicItemBean> CREATOR = new Creator<MusicItemBean>() {
        @Override
        public MusicItemBean createFromParcel(Parcel in) {
            return new MusicItemBean(in);
        }

        @Override
        public MusicItemBean[] newArray(int size) {
            return new MusicItemBean[size];
        }
    };
}
