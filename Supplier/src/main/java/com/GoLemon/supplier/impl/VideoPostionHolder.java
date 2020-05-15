package com.GoLemon.supplier.impl;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zheng on 2018/12/31.
 */

public class VideoPostionHolder {

    private VideoPostionHolder(){}
    private static volatile  VideoPostionHolder ginst;
    public static VideoPostionHolder getIns(){
        if(ginst !=null){
            return ginst;
        }
        synchronized (VideoPostionHolder.class){
            if(ginst !=null){
                return ginst;
            }
            ginst=new VideoPostionHolder();
        }
        return ginst;
    }
    private Map<String,Integer> mVideoPostions=new HashMap<>();
    public static void setVideoPostion(String video,int pos){
        VideoPostionHolder ins=getIns();
        if(ins == null){
            return;
        }
        ins.mVideoPostions.put(video,pos);
    }
    public static int getVideoPostion(String vid){
        VideoPostionHolder ins=getIns();
        if(ins == null){
            return 0;
        }
        if(ins.mVideoPostions.containsKey(vid)){
            return   ins.mVideoPostions.get(vid);
        }
        return 0;
    }


}
