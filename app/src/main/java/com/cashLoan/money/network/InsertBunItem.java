package com.cashLoan.money.network;

import androidx.annotation.NonNull;

import com.GoLemon.supplier.BunItem;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zheng on 2019/7/18.
 */

public class InsertBunItem implements BunItem,Serializable,Comparable<InsertBunItem> {
    public int _InsertPos;
    public BunItem _CoreItem;
   public InsertBunItem(int pos, BunItem item){
       _InsertPos=pos;
       _CoreItem=item;
   }

    @Override
    public int compareTo(@NonNull InsertBunItem o) {
        Integer myorder=_InsertPos;
        Integer otherorder=o._InsertPos;
        int comparevalue=myorder.compareTo(otherorder);
        return (-1*comparevalue);
    }
    public boolean InsertTo(List<BunItem> srcitems){
        if(srcitems == null){
            return false;
        }

        int size=srcitems.size();
        if(srcitems.isEmpty()){
            srcitems.add(_InsertPos,_CoreItem);
        } else if(_InsertPos>=0 &&  _InsertPos<size){
            srcitems.add(_InsertPos,_CoreItem);
        }
        int beforsize=srcitems.size();
        return (beforsize>size);
    }

}
