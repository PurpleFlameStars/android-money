package com.dzfd.gids.baselibs.UI.listview;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.GoLemon.supplier.BunItem;
import com.dzfd.gids.baselibs.R;
import com.dzfd.gids.baselibs.UI.pullrefresh.BunRefreshLayout;
import com.dzfd.gids.baselibs.UI.widgets.NoNetWorkView;
import com.dzfd.gids.baselibs.listener.ListResult;
import com.dzfd.gids.baselibs.listener.ListUpdataListener;
import com.dzfd.gids.baselibs.network.HttpLoader;
import com.dzfd.gids.baselibs.utils.BunToast;
import com.dzfd.gids.baselibs.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zheng on 2018/12/25.
 */

public class BunListView extends RelativeLayout implements AbsListView.OnScrollListener, ListUpdataListener<BunItem> {

    private ListView mCoreListview;
    private View mlistfooter;
    private TextView txtfootercontent;
    private ViewStub loadingview;
    private NoNetWorkView merrview;
    private BunListAdpter mAdpter;
    private BunlistViewListener mcallback;
    private BunRefreshLayout mRefreshView;

    private int firstIncomePos=-1;

    private int mLastTopIndex=0;
    private int mLastTopPixel=-1;
    private int mScrollDirection=0;
    protected static final int SCROLL_STOP=0;
    protected static final int SCROLL_UP=-1;
    protected static final int SCROLL_DOWN=1;
    private int lastScrollDirection=SCROLL_STOP;
    protected int mFirstVisibleItem = -1;
    private int lastscrollFirst=-1;
    protected int mVisibleItemCount;
    protected int mTotalItemCount=0;

    private boolean isLoading=false;
    private boolean isLastPage=false;

    private final int MSG_LOADVIDEOLIST=1,MSG_WHAT_LOADING=2,MSG_WHAT_LASTPAGE=3;

    private Handler uiHandler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case MSG_LOADVIDEOLIST: {
                    handleLoadingResultUI(msg);
                    break;
                }
                case MSG_WHAT_LOADING:{
                    handleDataLoading();
                    break;
                }
                case MSG_WHAT_LASTPAGE:{
                    handleLastPage();
                }

            }
            return false;
        }
    });

    public BunListView(Context context) {
        this(context,null);
    }

    public BunListView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public BunListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater layoutInflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View bunlistview=layoutInflater.inflate(R.layout.bun_viewlist,this);
        boolean showuprefresh=true;
        TypedArray types=context.obtainStyledAttributes(attrs,R.styleable.BunListView);
        if(types != null){
            showuprefresh=types.getBoolean(R.styleable.BunListView_canrefresh,true);
            types.recycle();
        }

        mRefreshView = findViewById(R.id.refreshlayout);
        if (mRefreshView != null) {
            mRefreshView.setTipColor(Utils.getColorRes(getResources(), R.color.transparent), Utils.getColorRes(getResources(), R.color.themeColorRed));
            mRefreshView.setOnRefreshListener(new BunRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    if (mcallback != null) {
                        mcallback.onLoadData(false);
                    }
                }
            });
            mRefreshView.setEnabled(showuprefresh);
        }

        mCoreListview=(ListView) findViewById(R.id.bunlistview);
        if(mCoreListview!=null){
            mCoreListview.setOnScrollListener(this);
        }

        initFooter(context,mCoreListview);
        initLoadingView();
        initErrview();
    }
    public boolean checkAdpter(){
        if(mAdpter == null){
            mAdpter=new BunListAdpter(getContext(),0);
            mCoreListview.setAdapter(mAdpter);
        }
        return (mAdpter!=null);
    }
    public  void setHolderFactory(HolderFactory factory){
       boolean bres= checkAdpter();
       if(bres){
           mAdpter.setHolderFactory(factory);
       }
    }

    public void showFreshLayout(){
        if(mRefreshView!=null){
            mRefreshView.setRefreshing(true);
        }
    }

    public void enableUpRefresh(boolean enable) {
        if (mRefreshView != null) {
            mRefreshView.setEnabled(enable);
        }
    }

    //==================================================
    public void addListHeaderView(View sub){
        if(mCoreListview !=null){
            mCoreListview.addHeaderView(sub);
        }
    }
    public void setListener(BunlistViewListener listener){
        mcallback=listener;
    }
  /*  public void setmIncomeLoader(IncomeLoader loader,int firstadPos){
        mIncomeLoader=loader;
        firstIncomePos=firstadPos;
    }*/

    public void initFooter(Context cxt, ListView bunlistview){
        if(cxt==null || bunlistview==null){
            return;
        }
        mlistfooter=((LayoutInflater) cxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.video_list_footer, null, false);
        mlistfooter.setVisibility(View.GONE);
        bunlistview.addFooterView(mlistfooter,null,false);
        txtfootercontent=(TextView) mlistfooter.findViewById(R.id.txt_footer_content);

    }
    public void initLoadingView(){
        loadingview=(ViewStub)findViewById(R.id.common_loading);
        if(loadingview !=null){
            loadingview.inflate();
            loadingview.setVisibility(View.GONE);
        }
    }
    public void initErrview(){
       /* merrview=(NoNetWorkView) findViewById(R.id.nonetview);
        if(merrview!=null){
            merrview.setRetryListener(new NoNetWorkView.INetWorkRetry() {
                @Override
                public void OnRetry() {
                    if(mcallback!=null){
                        mcallback.onRetry();
                    }

                }
            });
            merrview.setVisibility(View.GONE);
        }*/
    }
    public BunItem getFistItem(){
        if(mAdpter == null || mAdpter.getCount()<1){
            return null;
        }
       int count= mAdpter.getCount();
        for(int index=0;index<count;index++){
            BunItem item=mAdpter.getItem(index);
            return item;
        }
        return null;
    }

    //==================================================
    public void  _startloading(){
        if( (mAdpter ==null) || ((mAdpter!=null) && mAdpter.getCount()<=0)){
            if(loadingview!=null){
                loadingview.setVisibility(View.VISIBLE);
            }
            if (merrview != null) {
                merrview.setVisibility(View.GONE);
            }
        }else{
            showLoadingText("正在加载…");
        }
    }
    public void _endLoading(){
        _endLoading("加载完成~");
    }
    public void _endLoading(String str){
        if(loadingview!=null){
            loadingview.setVisibility(View.GONE);
        }
        showLoadingText(str);
    }
    public void showLoadingText(String txt){
        if(mlistfooter !=null){
            int visib= mlistfooter.getVisibility();
            if(visib != View.VISIBLE){
                mlistfooter.setVisibility(View.VISIBLE);
            }
        }
        if(txtfootercontent !=null){
            txtfootercontent.setText(txt);
        }
    }
    public void _showErrorString(String noneterr, String toasterr, boolean isbottom){
        if(mAdpter.getCount()<=0){
            if(mCoreListview !=null){
                mCoreListview.setVisibility(View.GONE);
            }
            merrview.setVisibility(View.VISIBLE);
        }else if(!isbottom){
            showTip(toasterr);
        } else{
            BunToast.showShort(getContext(),toasterr);
        }
    }
    private void _uiOnDataLoadFailed(String errorstr, boolean isbottom){
        _endLoading();
        if(!Utils.isConnected(getContext())){
            _showErrorString("网络错误","网络加载出错，请稍后重试",isbottom);
        }else if(!TextUtils.isEmpty(errorstr)){
            _showErrorString(errorstr,errorstr,isbottom);
        }
    }
    private void showTip(String tipText) {
        if (mRefreshView != null) {
            mRefreshView.setTipResult(tipText, false);
        }
    }
    //============================================================================
   /* public void checkScrollDriction(AbsListView view, int firstVisibleItem){
        View v = view.getChildAt(0);
        int top = (v == null) ? 0 : v.getTop();
        if(mLastTopPixel == -1){
            mLastTopPixel=top;
        }
        int scrollDirection = SCROLL_STOP;
        if (firstVisibleItem > mLastTopIndex) {
            scrollDirection = SCROLL_UP;
        } else if (firstVisibleItem < mLastTopIndex) {
            scrollDirection = SCROLL_DOWN;
        } else {
            if(top < mLastTopPixel) {
                scrollDirection = SCROLL_UP;
            } else if(top > mLastTopPixel) {
                scrollDirection = SCROLL_DOWN;
            }
        }
        mLastTopIndex = firstVisibleItem;
        mLastTopPixel = top;
        mScrollDirection=scrollDirection;
    }
    private void prepareToCheckAddAds(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount){
        if( (visibleItemCount == 0) || (totalItemCount == 0) || (mAdpter.getCount()==0)){
            return;
        }
        int videosnumperad=4;

        int addcount=(visibleItemCount>videosnumperad)?videosnumperad:visibleItemCount;
        int addpos=-1;
        if(lastScrollDirection == SCROLL_UP){
            addpos=firstVisibleItem+addcount;
        }else if(lastScrollDirection == SCROLL_DOWN){
            addpos=firstVisibleItem-1;
        }
        if(addpos<0 || addpos>=mAdpter.getCount()){
            return;
        }
        BunListItem bean = mAdpter.getItem(addpos);
        if(bean instanceof IncomeData){
            return;
        }

        if(lastScrollDirection == SCROLL_UP){
            int afterpos=addpos-videosnumperad;
            if(afterpos<1){
                afterpos=1;
            }
            int beforpos=addpos+1;
            if(beforpos>mAdpter.getCount()){
                beforpos=mAdpter.getCount();
            }
            for(int index=afterpos;index<beforpos;index++){
                if(index == addpos){
                    continue;
                }
                bean = mAdpter.getItem(index);
                if(bean instanceof IncomeData){
                    return;
                }
            }
        }else if(lastScrollDirection == SCROLL_DOWN){
            int beforpos=addpos+videosnumperad;
            if(beforpos>mAdpter.getCount()){
                beforpos=mAdpter.getCount();
            }
            int afterpos=addpos-1;
            if(afterpos<0){
                afterpos=0;
            }
            for(int index=afterpos;index<beforpos;index++){
                if(index == addpos){
                    continue;
                }
                bean = mAdpter.getItem(index);
                if(bean instanceof IncomeData){
                    return;
                }
            }
        }else if(lastScrollDirection == SCROLL_STOP){
            return;
        }
        AddAdItemToList(addpos);

    }
    private void AddAdItemToList(int addpos){
        if( (mAdpter == null) ||( mAdpter.getCount()<=0)|| (addpos <0)){
            return;
        }
        if(addpos>mAdpter.getCount()){
            return;
        }
        if(mIncomeLoader !=null){
            IncomeData info = mIncomeLoader.getAdItem(true);
            if(info !=null){
                mAdpter.insert(info,addpos);
            }
        }
    }
    private void insertAdItemToList(IncomeData info,int addpos){
        if(info == null){
            return;
        }
        if( (mAdpter == null) ||( mAdpter.getCount()<=0)|| (addpos <0)){
            return;
        }
        if(addpos>mAdpter.getCount()){
            return;
        }
        mAdpter.insert(info,addpos);
    }
    private void AddAditemToList() {
        if(mIncomeLoader == null){
            return;
        }
        final IncomeData info = mIncomeLoader.getAdItem(true);
        if (info != null) {
            insertAdItemToList(info,firstIncomePos);
        } else {
            Thread adthread = new Thread(new Runnable() {
                @Override
                public void run() {
                    int count = 0;
                    while (count < 5) {
                        count++;
                        IncomeData mListAdinfo = mIncomeLoader.getAdItem(false);
                        if (mListAdinfo != null) {
                            final IncomeData finalMListAdinfo = mListAdinfo;
                            uiHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    insertAdItemToList(finalMListAdinfo,firstIncomePos);
                                }
                            });
                            mIncomeLoader.loadAds(1);
                            break;
                        } else {
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }
            });
            adthread.start();
        }
    }*/
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE){
            /*if(mIncomeLoader !=null){
                if(lastscrollFirst != mFirstVisibleItem){
                    prepareToCheckAddAds(view,mFirstVisibleItem,mVisibleItemCount,mTotalItemCount);
                    lastscrollFirst=mFirstVisibleItem;
                }
            }*/
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (totalItemCount <= 1) {
            return;
        }
        if(isLastPage){
            return;
        }
        mFirstVisibleItem=firstVisibleItem;
        mVisibleItemCount = visibleItemCount;
        mTotalItemCount=totalItemCount;
        /*{
            checkScrollDriction(view,firstVisibleItem);
            if(mScrollDirection != SCROLL_STOP){
                lastScrollDirection=mScrollDirection;
            }
        }*/

        int bottomitem=firstVisibleItem+visibleItemCount;
        int offset=totalItemCount-bottomitem;
        if(offset==0){
            if(mcallback!=null){
                mcallback.onLoadData(true);
            }
        }
    }


    //=============================================================
    private void _sendVideoListResult(boolean result, Object data, int opt){
        if(uiHandler == null){
            return;
        }
        Message msg=uiHandler.obtainMessage(MSG_LOADVIDEOLIST);
        msg.arg1=result?1:0;
        msg.arg2=opt;
        msg.obj=data;
        uiHandler.sendMessage(msg);
    }
    public void AddItemToTop(List<BunItem> items){
        if(items==null && items.size()<1){
            return;
        }
        if(!checkAdpter()){
            return;
        }
        List<BunItem> resultitems=new ArrayList<>();
        if(items!=null){
            resultitems.addAll(items);
        }
        for(int index=0;index<mAdpter.getCount();index++){
            resultitems.add(mAdpter.getItem(index));
        }
        mAdpter.clear();
        mAdpter.addAll(resultitems);

    }
    public void AddItemToBottom(List<BunItem> items){
        if(items==null && items.size()<1){
            return;
        }
        if(!checkAdpter()){
            return;
        }
        mAdpter.addAll(items);
    }

    private void handleLoadingResultUI(Message msg){
        _endLoading();
        if(mRefreshView!=null){
            mRefreshView.setRefreshing(false);
        }
        if (mcallback != null) {
            mcallback.onLoadEnd();
        }
        if(!checkAdpter()){
            return;
        }
        boolean isfristscreen=(mAdpter.getCount()==0);
        if(msg == null){
            return;
        }
        boolean result=(msg.arg1==1);
        boolean bottomRefresh=(msg.arg2== HttpLoader.HttpLoadPos.POSTION_BOTTOM_LOADMORE.mIndex);
        if(result){
            if(mCoreListview!=null){
                mCoreListview.setVisibility(View.VISIBLE);
            }
            Object msgobj=msg.obj;
            List<BunItem> items = null;
            if(msgobj instanceof List<?>){
                items=(List<BunItem>)msgobj;
            }
            int size=items.size();
            if(bottomRefresh){
                mAdpter.addAll(items);
            }else{
                List<BunItem> resultitems=new ArrayList<>();
                if(items!=null){
                    resultitems.addAll(items);
                }
                for(int index=0;index<mAdpter.getCount();index++){
                    resultitems.add(mAdpter.getItem(index));
                }
                mAdpter.clear();
                mAdpter.addAll(resultitems);
            }
            if(!bottomRefresh){
                String tips="已为您更新"+ String.valueOf(size)+"条信息";
                showTip(tips);
            }
        }else{
            _uiOnDataLoadFailed("请检查网络，获取视列表数据失败",bottomRefresh);
        }
    }
    private void handleDataLoading(){
        _startloading();
    }
   private void handleLastPage(){
       _endLoading("已为您加载所有内容~~");

   }

    public boolean CanLoad(HttpLoader.HttpLoadPos postion) {
        if(isLoading){
            return false;
        }
        return true;
    }

    public void onLoading(HttpLoader.HttpLoadPos postion) {
        isLoading=true;
        if(uiHandler !=null){
            uiHandler.sendEmptyMessage(MSG_WHAT_LOADING);
        }
    }

    public void OnSucess(ListResult items, HttpLoader.HttpLoadPos postion) {
        isLoading=false;
        _sendVideoListResult(true,items.data,postion.mIndex);
    }

    public void onFailed(int code, String msg, HttpLoader.HttpLoadPos postion) {
        isLoading=false;
        _sendVideoListResult(false,msg,postion.mIndex);
    }
    public void OnLastPage() {
        isLastPage=true;
        if(uiHandler !=null){
            uiHandler.sendEmptyMessage(MSG_WHAT_LASTPAGE);
        }
    }
    //=========================================================
    public interface BunlistViewListener{
        void onRetry();
        void onLoadData(boolean bottom);
        void onLoadEnd();
    }


}
