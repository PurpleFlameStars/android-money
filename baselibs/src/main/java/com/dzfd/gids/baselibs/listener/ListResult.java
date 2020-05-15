package com.dzfd.gids.baselibs.listener;

import com.dzfd.gids.baselibs.network.HttpLoader;

import java.util.List;

/**
 * Created by zheng on 2019/3/15.
 */
public class ListResult<T> {
    public List<T> data;
    public HttpLoader.ItemInsertMode Addmodel;
    public int arg1;
    public int arg2;
    public Object object;
    public ListResult(){
        this(0,HttpLoader.ItemInsertMode.INSERT_MODE);
    }
    public ListResult(HttpLoader.ItemInsertMode addmodel){
        this(0,addmodel);
    }
    public ListResult(int insertPos,HttpLoader.ItemInsertMode addmodel){
        this.arg1=insertPos;
        Addmodel=addmodel;
    }
    public ListResult(List<T> items,HttpLoader.ItemInsertMode addmodel){
        this(addmodel);
        data=items;
    }
    public ListResult(List<T> items){
        this(HttpLoader.ItemInsertMode.INSERT_MODE);
        data=items;
    }
    public  ListResult setInsertPos(int pos){
        this.arg1=pos;
        return this;
    }
    public  ListResult setInsertModel(HttpLoader.ItemInsertMode addmodel){
        this.Addmodel=addmodel;
        return this;
    }
}
