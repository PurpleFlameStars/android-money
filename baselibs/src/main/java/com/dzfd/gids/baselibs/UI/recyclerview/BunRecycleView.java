package com.dzfd.gids.baselibs.UI.recyclerview;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.GoLemon.supplier.BunItem;
import com.dzfd.gids.baselibs.R;
import com.dzfd.gids.baselibs.UI.helper.AnimalHelper;
import com.dzfd.gids.baselibs.UI.pullrefresh.BunRefreshLayout;
import com.dzfd.gids.baselibs.UI.recyclerview.bean.LoadErrorBean;
import com.dzfd.gids.baselibs.UI.recyclerview.lisenter.ILoadRetry;
import com.dzfd.gids.baselibs.UI.recyclerview.wrapper.BunFooterView;
import com.dzfd.gids.baselibs.UI.recyclerview.wrapper.HeaderAndFooterWrapper;
import com.dzfd.gids.baselibs.UI.recyclerview.wrapper.IBunLoadMore;
import com.dzfd.gids.baselibs.UI.widgets.NoNetWorkView;
import com.dzfd.gids.baselibs.listener.ListResult;
import com.dzfd.gids.baselibs.listener.ListUpdataListener;
import com.dzfd.gids.baselibs.network.HttpLoader;
import com.dzfd.gids.baselibs.utils.Utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.functions.Predicate;

/**
 * Created by zheng on 2019/3/5.
 */

public class BunRecycleView extends RelativeLayout implements ListUpdataListener<BunItem>, RecyclerScrollListener._LoadMoreData, ILoadRetry {
    private ViewStub loadingview;
    private BunDataListener mcallback;

    protected RecyclerView mCoreView;
    private BunRefreshLayout mRefreshView;
    private TextView txtTipView;

    private HeaderAndFooterWrapper mAdpter;
    private BunHolderFactory _factory;
    private RecyclerItemScroll _checktop;
    private boolean isLoading=false;
    private boolean isLastPage=false;

    private int offset_loadmore=1;
    private boolean hasLoadmoreUI=true;
    private boolean canBottomLoadmore =true;
    private boolean showRefreshTips=true;
    private boolean TopLoadMore=false;
    private int refresh_bk,recycel_bk,load_bk;
    private boolean showLoadEnd=true;
    private boolean showCenterLoading=true;
    private boolean showuprefresh=true;

    private final int MSG_LOADLIST_SUCESS =1,MSG_WHAT_LOADING=2,MSG_WHAT_LASTPAGE=3,MSG_LOADLIST_FAILED=4;

    private Handler uiHandler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case MSG_LOADLIST_SUCESS: {
                    handleSucessResultUI(msg);
                    break;
                }
                case MSG_LOADLIST_FAILED:{
                    handleFailedResultUI(msg);
                    break;
                }
                case MSG_WHAT_LOADING:{
                    handleDataLoading(msg);
                    break;
                }
                case MSG_WHAT_LASTPAGE:{
                    handleLastPage();
                }

            }
            return false;
        }
    });

    public BunRecycleView(Context context) {
        this(context,null);
    }

    public BunRecycleView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public BunRecycleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        InitView(context,attrs);
    }
    private void InitView(Context context, AttributeSet attrs){
        LayoutInflater layoutInflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View bunlistview=layoutInflater.inflate(R.layout.bun_recycleview,this, true);
        TypedArray types=context.obtainStyledAttributes(attrs,R.styleable.BunListView);
        if(types != null){
            showuprefresh=types.getBoolean(R.styleable.BunListView_canrefresh,true);
            canBottomLoadmore =types.getBoolean(R.styleable.BunListView_canloadmore,true);
            offset_loadmore=types.getInteger(R.styleable.BunListView_offset_loadmore,1);
            hasLoadmoreUI=types.getBoolean(R.styleable.BunListView_has_loadmore_ui,true);
            showRefreshTips=types.getBoolean(R.styleable.BunListView_refresh_tip,true);
            TopLoadMore=types.getBoolean(R.styleable.BunListView_top_loadmore,false);
            showLoadEnd=types.getBoolean(R.styleable.BunListView_show_load_end,true);
            showCenterLoading=types.getBoolean(R.styleable.BunListView_use_center_loading,true);
            recycel_bk=types.getColor(R.styleable.BunListView_recycel_bk,0xFFFFFFFF);
            refresh_bk=types.getColor(R.styleable.BunListView_refresh_bk,0xFFFFFFFF);
            load_bk=types.getColor(R.styleable.BunListView_load_color,Utils.getColorRes(getResources(), R.color.themeColorRed));

            types.recycle();
        }

        initRefreshView(showuprefresh);
        initLoadingView();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mCoreView=findViewById(R.id.bun_recycler);
        if(mCoreView!=null){
            mCoreView.setBackgroundColor(recycel_bk);
            _checktop=new RecyclerItemScroll();
            mCoreView.addOnScrollListener(_checktop);
            mCoreView.addOnScrollListener(getOnScrollListener());

        }
        txtTipView= findViewById(R.id.bun_fresh_tips);

    }
    protected RecyclerView.OnScrollListener getOnScrollListener(){
        return new RecyclerScrollListener(this,offset_loadmore,hasLoadmoreUI);
    }

    public void initLoadingView(){
        ViewStub loadview=findViewById(R.id.common_loading);
        if(loadview !=null){
            if(!showCenterLoading){
                removeView(loadview);
                return;
            }
            loadingview=loadview;
            loadingview.inflate();
            loadingview.setVisibility(GONE);
        }
    }
    private void initRefreshView(boolean enableRefresh){
        mRefreshView = findViewById(R.id.refreshlayout);
        if (mRefreshView != null) {
            mRefreshView.setEnabled(enableRefresh);
            mRefreshView.setBackgroundColor(refresh_bk);
            mRefreshView.setColorSchemeColors(load_bk);
            mRefreshView.setTipColor(Utils.getColorRes(getResources(), R.color.transparent), Utils.getColorRes(getResources(), R.color.themeColorRed));
            mRefreshView.setOnRefreshListener(new BunRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    LoadNewData(HttpLoader.HttpLoadPos.POSTION_UP_REFRESH,true);
                }
            });
        }
    }
    public void setRefreshTipEnable(boolean value){
        showRefreshTips=value;
    }
    public void enableUpRefresh(boolean enable) {
        if (mRefreshView != null) {
            mRefreshView.setEnabled(enable);
        }
    }
    public void stopRefresh() {
        if (mRefreshView != null)
            mRefreshView.setRefreshing(false);
    }
    public void setListener(BunDataListener listener){
        mcallback=listener;
    }
    public void setHolderFactory(BunHolderFactory factory){
        _factory=factory;
    }
    public void RefreshDataLoad(){
        LoadNewData(HttpLoader.HttpLoadPos.POSTION_UP_REFRESH,false);

    }
    public void LoadMoreData(boolean Manually){
        LoadNewData(HttpLoader.HttpLoadPos.POSTION_BOTTOM_LOADMORE,Manually);
    }
    public void beginFirstDataLoad(BunDataListener listener,BunHolderFactory factory){
        setListener(listener);
        setHolderFactory(factory);
        LoadNewData(HttpLoader.HttpLoadPos.POSTION_UP_REFRESH,false);
    }

    public void AddHeaderView(View view){
        if(!checkAdpter()){
            return;
        }
        if(view!=null){
            mAdpter.addHeaderView(view);
        }
    }
    public void RemoveHeaderView(View view){
        if(view == null){
            return;
        }
        if(!checkAdpter()){
            return;
        }
        mAdpter.RemoveHeader(view);
    }

    public void AddFooterView(View view){
        if(!checkAdpter()){
            return;
        }
        if(view!=null){
            mAdpter.addFootView(view);
        }
    }
    public int getHeaderCount(){
        if(mAdpter!=null){
            return mAdpter.getHeadersCount();
        }
        return 0;
    }

    public void notifyDataSetChanged() {
        if (mAdpter == null) {
            return;
        }
        mAdpter.notifyDataSetChanged();
    }

    public void RefreshItem(int position){
        RefreshItem(position, null);
    }
    public void RefreshItem(int position, BunItem bunItem) {
        if(mAdpter==null){
            return;
        }
        int itemcount=mAdpter.getItemCount();
        if(position<0 || position>=itemcount){
            return;
        }
        if (bunItem == null) {
            mAdpter.notifyItemChanged(position+mAdpter.getHeadersCount());
        } else {
            mAdpter.notifyItemChanged(position+mAdpter.getHeadersCount(),bunItem);

        }
    }
    public void notifyItemChanged(int postion,BunItem item){
        if(mAdpter!=null){
            mAdpter._notifyItemChanged(postion,item);
        }
    }
    public void notifyItemMoved(int fromPosition, int toPosition){
        if(mAdpter!=null){
            mAdpter._notifyItemMoved(fromPosition,toPosition);
        }
    }

    public void notifyItemRemoved(int postion){
        notifyItemRemovedAndChanged(postion);
    }

    public void notifyItemRemovedAndChanged(int postion) {
        if (mAdpter != null) {
            mAdpter._notifyItemRemovedAndChanged(postion);
        }
    }

    public void SetRefreshBackgroundColor(int color){
        if(mRefreshView!=null){
            mRefreshView.setBackgroundColor(color);
        }
    }
    public void SetViewBackgroundColor(int color){
        if(mCoreView!=null){
            mCoreView.setBackgroundColor(color);
        }
    }
    public void SetLoadViewBackgroundColor(int color){
        if(mRefreshView!=null){
            mRefreshView.setColorSchemeColors(color);
        }
    }

    public void setLayoutManager(RecyclerView.LayoutManager layout){
        if(mCoreView!=null){
            if(layout!=null && layout instanceof LinearLayoutManager){
                ((LinearLayoutManager) layout).setReverseLayout(TopLoadMore);
            }
            mCoreView.setLayoutManager(layout);
        }
    }
    public RecyclerView.LayoutManager getLayoutManager(){
        if(mCoreView!=null){
            return mCoreView.getLayoutManager();
        }
        return null;
    }
    public void addItemDecoration(RecyclerView.ItemDecoration decor){
        if(mCoreView!=null){
            mCoreView.addItemDecoration(decor);
        }
    }
    public void scrollToPosition(int posion){
        if(mCoreView!=null){
            mCoreView.scrollToPosition(posion);
        }
    }
    public void smoothScrollToPosition(int postion){
        if(mCoreView!=null){
            mCoreView.smoothScrollToPosition(postion);
        }
    }
    public boolean IsFristItemShow(){
        if(_checktop == null){
            return true;
        }
        return _checktop._isScrollToTop;
    }
    public void addOnScrollListener(RecyclerView.OnScrollListener scrollListener){
        if(mCoreView!=null){
            mCoreView.addOnScrollListener(scrollListener);
        }
    }
    public void removeOnScrollListener(RecyclerView.OnScrollListener scrollListener){
        if(mCoreView!=null){
            mCoreView.removeOnScrollListener(scrollListener);
        }
    }
    public void setRefreshBackGround(@DrawableRes int rid, @ColorRes int cid){
        if(txtTipView!=null){
            txtTipView.setBackgroundResource(rid);
            txtTipView.setTextColor(getResources().getColor(cid));
        }
    }
    public void SetTipsTheme(@DrawableRes int rid, @ColorInt int color){
        if(txtTipView!=null){
            txtTipView.setBackgroundResource(rid);
            txtTipView.setTextColor(color);
        }
    }
    public void addOnItemTouchListener(RecyclerView.OnItemTouchListener listener){
        if(mCoreView!=null){
            mCoreView.addOnItemTouchListener(listener);
        }
    }
    public Iterator<BunItem> getDataBefroPosition(int pos, int count){
        if(mAdpter!=null){
            return mAdpter.getDataBefroPosition(pos,count);
        }
        return new ArrayList<BunItem>().iterator();
    }
    public Iterator<BunItem> getDataBefroPosition(int pos,int count,Predicate<BunItem> predicate){
        if(mAdpter!=null){
            return mAdpter.getDataBefroPosition(pos,count,predicate);
        }
        return new ArrayList<BunItem>().iterator();
    }
    public List<BunItem> getDataAfterPostion(int pos,int count){
        if(mAdpter!=null){
            return mAdpter.getDataAfterPostion(pos,count);
        }
        return new ArrayList<>();
    }
    public Iterator<BunItem> getTopData(int start, int count, Predicate<BunItem> predicate){
        if(mAdpter!=null){
            return mAdpter.getTopData(start,count,predicate);
        }
        return new ArrayList<BunItem>().iterator();
    }
    public BunItem getDataByPostion(int postion){
        if(mAdpter!=null){
            return mAdpter.getItemDataByPostion(postion);
        }
        return null;
    }
    public int getId(){
        return hashCode();
    }

        //=====================================================================
    private boolean checkAdpter(){

        if(mAdpter == null){
            mAdpter=new HeaderAndFooterWrapper(new BunRecycleViewAdpter(_factory,getId()));
            if(hasLoadmoreUI){
                BunFooterView _footerView=new BunFooterView(getContext());
                _footerView.setLoadMoreInterface(this,HttpLoader.HttpLoadPos.POSTION_BOTTOM_LOADMORE);
                _footerView.setShowLoadEnd(showLoadEnd);
                mAdpter.addFootView(_footerView);
            }
            mCoreView.setAdapter(mAdpter);
        }
        return (mAdpter!=null);
    }
    private void LoadNewData(HttpLoader.HttpLoadPos loadPos,boolean Manually){
        if(mcallback!=null){
            mcallback.onLoadData(loadPos,Manually,this);
        }
    }

    //=============================================
    @Override
    public boolean CanLoad(HttpLoader.HttpLoadPos postion) {
        if(isLoading){
            return false;
        }
        return true;
    }
    @Override
    public void onLoading(HttpLoader.HttpLoadPos postion) {
        isLoading=true;
        if(postion != HttpLoader.HttpLoadPos.POSTION_UP_REFRESH){
            if(mAdpter!=null){
                mAdpter.UpdataLoadingState(postion, IBunLoadMore.BunLoadState.LOADING);
            }
        }
        if(uiHandler !=null){
            Message msg=uiHandler.obtainMessage(MSG_WHAT_LOADING);
            msg.arg2=postion.mIndex;
            uiHandler.sendMessage(msg);
        }
    }
    @Override
    public void OnSucess(ListResult items, HttpLoader.HttpLoadPos postion) {
        if(postion == HttpLoader.HttpLoadPos.POSTION_UP_REFRESH){
            isLastPage = false;
        }
        _sendLoadSucessResult(items,postion.mIndex);
    }
    @Override
    public void onFailed(int code, String msg, HttpLoader.HttpLoadPos postion) {
        _sendLoadFailedResult(code,msg,postion.mIndex);
    }
    @Override
    public void OnLastPage() {
        isLastPage=true;
        if(uiHandler !=null){
            uiHandler.sendEmptyMessage(MSG_WHAT_LASTPAGE);
        }
    }
    //===============================================
    //=============================================================
    private void _sendLoadSucessResult(Object data, int loadpos){
        if(uiHandler == null){
            return;
        }
        Message msg=uiHandler.obtainMessage(MSG_LOADLIST_SUCESS);
        msg.arg1=loadpos;
        msg.arg2=0;
        msg.obj=data;
        uiHandler.sendMessage(msg);
    }
    private void _sendLoadFailedResult(int code,String strmsg,int loadpos){
        if(uiHandler == null){
            return;
        }
        Message msg=uiHandler.obtainMessage(MSG_LOADLIST_FAILED);
        msg.arg1=loadpos;
        msg.arg2=code;
        msg.obj=strmsg;
        uiHandler.sendMessage(msg);
    }

    public void AddItemToTop(ListResult<BunItem> result, List<BunItem> items){
        if(!checkAdpter()){
            return;
        }
        mAdpter.AddItemToTop(result,items);
    }
    public void AddItemToTop(List<BunItem> items){
        ListResult<BunItem> result = new ListResult<>(items);
        AddItemToTop(result,items);
    }
    public void InsertItem(int insertpos, List<BunItem> items){
        if(insertpos<0){
            insertpos=0;
        }
        if(!checkAdpter()){
            return;
        }
        mAdpter.InsertItemToTop(insertpos,items);
    }
    public boolean InsertItemToTop(int topoffset,BunItem item) {
        if(topoffset<0 || item == null){
            return false;
        }
        if(!checkAdpter()){
            return false;
        }
        return mAdpter.InsertItemToTop(topoffset,item);
    }

    public void InsertItemsBottomOfViewPort(List<BunItem> items ,HttpLoader.ItemInsertMode insertMode){
       RecyclerView.LayoutManager manager= mCoreView.getLayoutManager();
       if(manager instanceof  LinearLayoutManager){
           LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mCoreView.getLayoutManager();
           if(linearLayoutManager!=null){
               int count=linearLayoutManager.getItemCount();
               int insertpos= linearLayoutManager.findLastVisibleItemPosition() + 1;
               if(insertpos>=0 && insertpos<count){
                   ListResult<BunItem> result = new ListResult<>(insertpos,insertMode);
                   result.data=items;
                   AddItemToTop(result,items);
               }
           }
       }

    }

    public void InsertItem(BunItem item,int index){
        List<BunItem> resultitem=new ArrayList<>();
        resultitem.add(item);
        ListResult<BunItem> result = new ListResult<>(index,HttpLoader.ItemInsertMode.INSERT_MODE);
        result.data=resultitem;
        AddItemToTop(result,resultitem);

    }
    public void ClearData(){
        isLastPage = false;
        if(mAdpter!=null){
            mAdpter.ClearData();
            mAdpter.UpdataLoadingState(HttpLoader.HttpLoadPos.POSTION_BOTTOM_LOADMORE, IBunLoadMore.BunLoadState.INIT);
        }
        if(_checktop!=null){
            _checktop.Reset();
        }
    }
    public boolean isEmpty(){
        if(mAdpter==null){
            return true;
        }
        return (mAdpter.getCount()<=0);
    }

    public void AddItemToBottom(List<BunItem> items){
        if(items==null && items.size()<1){
            return;
        }
        if(!checkAdpter()){
            return;
        }
        mAdpter.AddItemToBottom(0,items);
    }
    private void CommentResultUI(HttpLoader.HttpLoadPos loadpos){
        _endLoading();
        if (mcallback != null) {
            mcallback.onLoadEnd();
        }
        if(!checkAdpter()){
            return;
        }

        if(loadpos == HttpLoader.HttpLoadPos.POSTION_UP_REFRESH){
            if(mRefreshView!=null){
                mRefreshView.setRefreshing(false);
            }
        }else{
            mAdpter.UpdataLoadingState(loadpos, IBunLoadMore.BunLoadState.END);
        }
    }

    private void handleSucessResultUI(Message msg){
        HttpLoader.HttpLoadPos loadpos= HttpLoader.HttpLoadPos.fromInt(msg.arg1);
        CommentResultUI(loadpos);
        HideErrorView(true);
        ListResult msgobj=(ListResult) msg.obj;
        List<BunItem> items=msgobj.data;
        int size=items.size();
        if(loadpos == HttpLoader.HttpLoadPos.POSTION_BOTTOM_LOADMORE){
            AddItemToBottom(items);
        }else{
            AddItemToTop(msgobj,items);
            if (msgobj.Addmodel == HttpLoader.ItemInsertMode.REPLACE_MOEE) {
                mCoreView.scrollToPosition(0);
            }
        }
        if(loadpos == HttpLoader.HttpLoadPos.POSTION_UP_REFRESH){
            showTip(size);
        }
        isLoading=false;

    }
    private void handleFailedResultUI(Message msg){
        if(msg == null || msg.what!=MSG_LOADLIST_FAILED){
            return;
        }
        HttpLoader.HttpLoadPos loadpos= HttpLoader.HttpLoadPos.fromInt(msg.arg1);
        CommentResultUI(loadpos);
        int code=msg.arg2;
        String strmsg=(String) msg.obj;
        if(!Utils.isConnected(getContext())){
            code=LoadErrorBean.SYSERROR_NONET;
            strmsg="no network connected";
        }
        _showErrorString(code,strmsg,loadpos);
        isLoading=false;
    }

    private void handleDataLoading(Message msg){
       HttpLoader.HttpLoadPos pos= HttpLoader.HttpLoadPos.fromInt(msg.arg2);
        _startloading(pos);
    }
    private void handleLastPage(){
        _endLoading();
        if(IsErrorShow()){
            return;
        }
        if(mAdpter!=null){
            mAdpter.UpdataLoadingState(HttpLoader.HttpLoadPos.POSTION_BOTTOM_LOADMORE, IBunLoadMore.BunLoadState.NOMORE);
        }
    }
    //==================================================
    public void  _startloading(HttpLoader.HttpLoadPos pos){
        if( (mAdpter ==null) || ((mAdpter!=null) && mAdpter.getCount()<=0)){
            if(loadingview!=null){
                loadingview.setVisibility(View.VISIBLE);
            }
            HideErrorView(false);
        }else if(pos == HttpLoader.HttpLoadPos.POSTION_UP_REFRESH){
            if(mRefreshView!=null && showuprefresh){
                mRefreshView.setRefreshing(true);
            }
        }
    }
    public void _endLoading(){
        if(loadingview!=null){
            loadingview.setVisibility(View.GONE);
        }
    }

    public void _showErrorString(int code,String msg,HttpLoader.HttpLoadPos loadpos){
        if(mAdpter.getCount()<=0){
            ShowErrorView(code,msg,loadpos);
        }else if(loadpos !=HttpLoader.HttpLoadPos.POSTION_UP_REFRESH){
            mAdpter.UpdataLoadingState(loadpos, IBunLoadMore.BunLoadState.FAILED);
        }
    }

    private void showTip(int total) {
        if(!showRefreshTips){
            return;
        }
        if(total <1){
            return;
        }else if(total<10){
            total=10;
        }
        String formatstr=getContext().getResources().getString(R.string.refresh_tips_format);
        final String tipvalue=String.format(formatstr,total);
        txtTipView.setText(tipvalue);
        AnimalHelper.AlphaAnimaltion(txtTipView, true, new Runnable() {
            @Override
            public void run() {
                uiHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        AnimalHelper.AlphaAnimaltion(txtTipView, false, null);
                    }
                },1000);
            }
        });

    }

    @Override
    public void LoadMoreDataFromBottom(HttpLoader.HttpLoadPos loaspos) {

        if(!InnerLoadMare(loaspos)){
            /*if(mAdpter!=null){
                mAdpter.UpdataLoadingState(loaspos, IBunLoadMore.BunLoadState.FAILED);
            }*/
            return;
        }
        LoadNewData(loaspos,true);
    }
    private boolean InnerLoadMare(HttpLoader.HttpLoadPos loaspos){
        if(isLastPage){
            return false;
        }
        if(!canBottomLoadmore && loaspos== HttpLoader.HttpLoadPos.POSTION_BOTTOM_LOADMORE){
            return false;
        }
        if(!CanLoad(loaspos)){
            return false;
        }
        if(mAdpter!=null && mAdpter.GetLoadMoreState()==IBunLoadMore.BunLoadState.FAILED){
            return false;
        }
        return true;

    }

    public RecyclerView getRecyclerView() {
        return mCoreView;
    }

    @Override
    public void OnRetryLoad(HttpLoader.HttpLoadPos loadPos, boolean Manually) {
        LoadNewData(loadPos,Manually);
    }
    //=========================do error show begin====================
    private void ShowErrorView(int code, String msg, HttpLoader.HttpLoadPos pos){
        View TargetView=findViewById(R.id.list_error_view);
        boolean isaddview=true;
        if(TargetView!=null){
            int errcode=(Integer)TargetView.getTag(R.id.tag_error_code);
            if(errcode == code){
                TargetView.setVisibility(VISIBLE);
                isaddview=false;
            }else{
                removeView(TargetView);
            }
        }
        if(isaddview){
            Context actcxt=getContext();
            if(actcxt instanceof Activity && ((Activity) actcxt).isFinishing()){
                return;
            }
            LoadErrorBean bean=new LoadErrorBean(code,msg,pos,this);
            if(mcallback!=null){
                TargetView=mcallback.getErrorView(this,bean);
            }
            if(TargetView == null){
                NoNetWorkView defaultview=NoNetWorkView.getView(getContext());
                if(defaultview!=null){
                    defaultview.SetLoadErrorBean(bean);
                    TargetView=defaultview;
                }
            }
            if(TargetView==null){
                return;
            }
            TargetView.setId(R.id.list_error_view);
            TargetView.setVisibility(VISIBLE);
            TargetView.setTag(R.id.tag_error_code,code);
            RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            addView(TargetView,params);
        }
    }
    private void HideErrorView(boolean remove){
        View view=findViewById(R.id.list_error_view);
        if(view!=null){
            view.setVisibility(GONE);
            if(remove){
                removeView(view);
            }
        }
    }
    private boolean IsErrorShow(){
        View view=findViewById(R.id.list_error_view);
        if(view!=null && view.getVisibility()==VISIBLE){
            return true;
        }
        return false;
    }
    //==========================do error show end====================
    public interface BunDataListener{
        void onLoadData(HttpLoader.HttpLoadPos loadpos, boolean Manually, ListUpdataListener<BunItem> listener);
        void onLoadEnd();
        View getErrorView(BunRecycleView view,LoadErrorBean bean);
    }

}
