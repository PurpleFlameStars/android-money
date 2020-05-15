package com.dzfd.gids.baselibs.listener;

import android.content.Context;
import android.text.TextUtils;

import com.dzfd.gids.baselibs.network.HttpLoader;
import com.dzfd.gids.baselibs.stat.StatEntity;
import com.dzfd.gids.baselibs.stat.StatHelper;

/**
 * Created by zheng on 2019/3/15.
 * 表示在网络请求过程中在调用和回调之间传调的数据集
 */

public class RequestCell {
   public RequestCell(Context cxt){
      this();
      this._context=cxt;
   }
   public RequestCell(Context cxt,Runnable runnable){
      this(cxt);
      this._runable=runnable;
   }
   public RequestCell(){
      this(HttpLoader.HttpLoadPos.LOAD_POSTION_NONE,HttpLoader.ItemInsertMode.INSERT_MODE);
   }
   public RequestCell(HttpLoader.HttpLoadPos pos){
      this(pos,HttpLoader.ItemInsertMode.INSERT_MODE);
   }
   public RequestCell(HttpLoader.ItemInsertMode mode){
      this(HttpLoader.HttpLoadPos.POSTION_UP_REFRESH,mode);
   }
   public RequestCell(HttpLoader.HttpLoadPos pos,HttpLoader.ItemInsertMode mode){
      addmodel= mode;
      loadpos= pos;
      _Report_Refresh_id="";
      _Report_LoadMore_id="";
      _Report_LoadNone_id="";
      _Report_Refer="";
   }
   public RequestCell SetReportEventId(String refreshid,String loadmoreid){
      if(refreshid!=null){
         _Report_Refresh_id=refreshid;
      }
      if(loadmoreid!=null){
         _Report_LoadMore_id=loadmoreid;
      }
      return this;
   }
   public RequestCell SetReportEventId(String loadNoneId){
      if(loadNoneId!=null){
         _Report_LoadNone_id=loadNoneId;
      }
      return this;

   }
   public RequestCell setReportRefer(String refer){
      if(refer!=null){
         _Report_Refer=refer;
      }
      return this;
   }

   public void DoRequstReport(){
     DoReport("request","","","");
      tmStart=System.currentTimeMillis();
   }
   public void DoResultReport(boolean sucess,int code,String msg){
      long time=System.currentTimeMillis();
      long tmoffset=time-tmStart;
      DoReport("result",sucess?"sucess":"failed",String.valueOf(tmoffset),""+code+msg);
   }
   public void DoLastPageReport(){
      DoReport("nomoredata","","","");

   }
   private void DoReport(String action,String lable,String extra,String extra1){
      //BUNSTAT: zheng 2019/4/14 request
      String eventid="";
      if(loadpos == HttpLoader.HttpLoadPos.POSTION_UP_REFRESH){
         eventid=_Report_Refresh_id;
      }else if(loadpos == HttpLoader.HttpLoadPos.POSTION_BOTTOM_LOADMORE){
         eventid=_Report_LoadMore_id;
      }else if(loadpos == HttpLoader.HttpLoadPos.LOAD_POSTION_NONE){
         eventid=_Report_LoadNone_id;
      }
      if(!TextUtils.isEmpty(eventid)){
         StatEntity entity=new StatEntity(action,lable,_Report_Refer,extra,extra1);
         StatHelper.onEvent(eventid,entity);
      }
   }
   public void setContext(Context cxt){
      _context=cxt;
   }
   public Context getContext(){
      return _context;
   }
   public Runnable getRunable(){
      return _runable;
   }
   public HttpLoader.HttpLoadPos loadpos;
   public HttpLoader.ItemInsertMode addmodel;
   private Context _context;
   private Runnable _runable;
   private long tmStart;
   private String _Report_Refresh_id;
   private String _Report_LoadMore_id;
   private String _Report_LoadNone_id;
   private String _Report_Refer;
}
