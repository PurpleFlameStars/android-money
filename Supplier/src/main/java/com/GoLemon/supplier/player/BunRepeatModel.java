package com.GoLemon.supplier.player;

/**
 * Created by zheng on 2019/9/26.
 */

public enum BunRepeatModel {
   REPEAT_UNSUPPORT(-1),REPEAT_LIST(0),REPEAT_ONE(1),REPEAT_ALL(2),REPEAT_RANDOM(3);
    private int _Index;
    BunRepeatModel(int index){
        _Index=index;
    }
    public static BunRepeatModel getNextModel(BunRepeatModel model){
        int index=model._Index;
        if(index == -1){
            return REPEAT_LIST;
        }
        int next=index+1;
        next%=4;
        return fromInt(next);
    }
    static public BunRepeatModel fromInt(int index){
        for (BunRepeatModel e : BunRepeatModel.values()) {
            if(index == e._Index){
                return e;
            }
        }
        return REPEAT_UNSUPPORT;
    }

}
