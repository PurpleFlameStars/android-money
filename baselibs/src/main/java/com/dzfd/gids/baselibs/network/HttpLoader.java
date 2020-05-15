package com.dzfd.gids.baselibs.network;

/**
 * Created by zheng on 2019/1/28.
 */

public   class HttpLoader {
    public enum HttpLoadPos{
        POSTION_UP_REFRESH(-1),LOAD_POSTION_NONE(0),POSTION_BOTTOM_LOADMORE(1);
        HttpLoadPos(int index){
            mIndex=index;
        }
        public int mIndex;
        public static HttpLoadPos fromInt(int Index){
            if(Index == POSTION_BOTTOM_LOADMORE.mIndex){
                return POSTION_BOTTOM_LOADMORE;
            }else if(Index == POSTION_UP_REFRESH.mIndex){
                return POSTION_UP_REFRESH;
            }else if(Index == LOAD_POSTION_NONE.mIndex){
                return LOAD_POSTION_NONE;
            }
            return LOAD_POSTION_NONE;
        }
    }
    public enum ItemInsertMode{
        INSERT_MODE,REPLACE_MOEE,REPLACE_HOLDER_MODEL,INSERT_AND_REPLACE;
    }

}
