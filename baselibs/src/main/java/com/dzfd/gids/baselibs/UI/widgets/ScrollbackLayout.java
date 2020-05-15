package com.dzfd.gids.baselibs.UI.widgets;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.Scroller;

import com.dzfd.gids.baselibs.R;
import com.dzfd.gids.baselibs.utils.Utils;

import java.util.LinkedList;
import java.util.List;


public class ScrollbackLayout extends FrameLayout {
	private static final String TAG = ScrollbackLayout.class.getSimpleName();
	private View mContentView;
	private int mTouchSlop;
	private int downX;
	private int downY;
	private int tempX;
	private Scroller mScroller;
	private int viewWidth;
	private boolean isSilding;
	private boolean isFinish;
	private Drawable mShadowDrawable;
	private Activity mActivity;
	private List<ViewPager> mViewPagers = new LinkedList<>();
	private List<View> mExcludeViews = new LinkedList<>();
	private List<RecyclerView> mRecyclerViews = new LinkedList<>();
	private OnScrollbackListener mListener;
	private boolean mEnable = true;

	public ScrollbackLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ScrollbackLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
			mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
		}
		mScroller = new Scroller(context);

		mShadowDrawable = Utils.getDrawable(getResources(), R.drawable.scrollback_shadow_left);
	}

	public void addExcludeView(View v){
		mExcludeViews.add(v);
	}

	public void removeExcludeView(View v){
		mExcludeViews.remove(v);
	}

	public void setScrollbackEnable(boolean enable){
		mEnable = enable;
	}

	public void setOnScrollbackListener(OnScrollbackListener listener){
		mListener = listener;
	}


	public void attachToActivity(Activity activity) {
		mActivity = activity;
		TypedArray a = activity.getTheme().obtainStyledAttributes(
				new int[] { android.R.attr.windowBackground });
		a.recycle();

		ViewGroup decor = (ViewGroup) activity.getWindow().getDecorView();
		ViewGroup decorChild = (ViewGroup) decor.getChildAt(0);
		decorChild.setBackgroundColor(getResources().getColor(R.color.transparent));
		decor.removeView(decorChild);
		addView(decorChild);
		setContentView(decorChild);
		decor.addView(this);
	}

	private void setContentView(View decorChild) {
		mContentView = (View) decorChild.getParent();
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if(mEnable){
			ViewPager mViewPager = getTouchViewPager(mViewPagers, ev);
			RecyclerView mRecyclerView = getTouchRecyclerView(mRecyclerViews, ev);
			View excludeView = getTouchExcludeView(mExcludeViews, ev);

			if(excludeView != null || mViewPager != null && mViewPager.getCurrentItem() != 0 ||
					mRecyclerView != null && hasScrollToLeft(mRecyclerView)){
				return super.onInterceptTouchEvent(ev);
			}

			switch (ev.getAction()) {
				case MotionEvent.ACTION_DOWN:
					downX = tempX = (int) ev.getRawX();
					downY = (int) ev.getRawY();
					break;
				case MotionEvent.ACTION_MOVE:
					int moveX = (int) ev.getRawX();
					if (moveX - downX > mTouchSlop && Math.abs((int) ev.getRawY() - downY) < moveX - downX) {
						downX = tempX = (int) ev.getRawX();
						downY = (int) ev.getRawY();
						return true;
					}
					break;
			}
		}
		return super.onInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(@NonNull MotionEvent event) {
		if(mEnable){
			switch (event.getAction()) {
				case MotionEvent.ACTION_MOVE:
					int moveX = (int) event.getRawX();
					int deltaX = tempX - moveX;
					tempX = moveX;
					//x方向移动大于y方向时即判定正在侧滑返回
					if (moveX - downX > mTouchSlop && Math.abs((int) event.getRawY() - downY) < moveX - downX) {
						if (!isSilding && mListener != null) {
							mListener.onStartScroll();
						}
						isSilding = true;
					}

					if (moveX - downX >= 0 && isSilding) {
						mContentView.scrollBy(deltaX, 0);
					}
					break;
				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_CANCEL:
					isSilding = false;
					if (mContentView.getScrollX() <= -viewWidth / 5) {
						isFinish = true;
						scrollRight();
					} else {
						scrollOrigin();
						isFinish = false;
					}
					break;
			}

			return true;
		}

		return super.onTouchEvent(event);

	}

	/**
	 * 获取ScrollBackLayout里面的ViewPager的集合
	 */
	private void getAlLViewPager(List<ViewPager> viewPagers, ViewGroup parent){
		int childCount = parent.getChildCount();
		for(int i=0; i<childCount; i++){
			View child = parent.getChildAt(i);
			if(child instanceof ViewPager){
				viewPagers.add((ViewPager)child);
			}else if(child instanceof ViewGroup){
				getAlLViewPager(viewPagers, (ViewGroup) child);
			}
		}
	}

	/**
	 * 获取获取ScrollBackLayout里面的RecyclerView的集合
	 */
	private void getAllRecyclerView(List<RecyclerView> recyclerViews, ViewGroup parent){
		int childCount = parent.getChildCount();
		for(int i=0; i<childCount; i++){
			View child = parent.getChildAt(i);
			if(child instanceof RecyclerView){
				RecyclerView recyclerView = (RecyclerView)child;
				RecyclerView.LayoutManager manager=recyclerView.getLayoutManager();
				if(manager!=null && manager instanceof LinearLayoutManager){
					int oritation=((LinearLayoutManager) manager).getOrientation();
					if(oritation == LinearLayoutManager.HORIZONTAL){
						recyclerViews.add((RecyclerView)child);
					}
				}
			}else if(child instanceof ViewGroup){
				getAllRecyclerView(recyclerViews, (ViewGroup) child);
			}
		}
	}

	/**
	 * 返回我们touch的ViewPager
	 */
	private View getTouchExcludeView(List<View> views, MotionEvent ev) {
		if (views == null || views.size() == 0) {
			return null;
		}
		Rect mRect = new Rect();
		for (View v : views) {
			int relativeX = 0;
			int relativeY = 0;
			v.getHitRect(mRect);
			View parent = (View) v.getParent();
			while (parent != null && parent != this) {
				relativeX += parent.getLeft();
				relativeY += parent.getTop();
				parent = (View) parent.getParent();
			}
			if (mRect.contains((int) ev.getX() - relativeX, (int) ev.getY() - relativeY)) {
				return v;
			}
		}
		return null;
	}


	/**
	 * 返回我们touch的ViewPager
	 */
    private ViewPager getTouchViewPager(List<ViewPager> viewPagers, MotionEvent ev) {
        if (viewPagers == null || viewPagers.size() == 0) {
            return null;
        }
        Rect mRect = new Rect();
        for (ViewPager v : viewPagers) {
            int relativeX = 0;
            int relativeY = 0;
            v.getHitRect(mRect);
            View parent = (View) v.getParent();
            while (parent != null && parent != this) {
                relativeX += parent.getLeft();
                relativeY += parent.getTop();
                parent = (View) parent.getParent();
            }
            if (mRect.contains((int) ev.getX() - relativeX, (int) ev.getY() - relativeY)) {
                return v;
            }
        }
        return null;
    }

	/**
	 * 返回我们touch的ViewPager
	 */
	private RecyclerView getTouchRecyclerView(List<RecyclerView> recyclerViews, MotionEvent ev){
		if(recyclerViews == null || recyclerViews.size() == 0){
			return null;
		}
		Rect mRect = new Rect();
		for(RecyclerView v : recyclerViews){
			int relativeX = 0;
			int relativeY = 0;
			v.getHitRect(mRect);
			View parent = (View)v.getParent();
			while(parent != null && parent != this){
				if(parent instanceof ScrollView){
					ScrollView scrollView = (ScrollView)parent;
					relativeY -= scrollView.getScrollY();
				}else{
					relativeY += parent.getTop();
				}
				relativeX += parent.getLeft();
				parent = (View)parent.getParent();
			}
			if(mRect.contains((int)ev.getX() - relativeX, (int)ev.getY() - relativeY)){
				return v;
			}
		}
		return null;
	}


	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if (changed) {
			viewWidth = this.getWidth();
			getAlLViewPager(mViewPagers, this);
			getAllRecyclerView(mRecyclerViews, this);
		}
	}

	@Override
	protected void dispatchDraw(@NonNull Canvas canvas) {
		try{
			super.dispatchDraw(canvas);
		}catch (Exception e){
			String msg = "ScrollbackLayout dispatchDraw a:" + getActivityClassName() + " err:" + e.getMessage();
		}

		if (mShadowDrawable != null && mContentView != null) {

			int left = mContentView.getLeft()
					- mShadowDrawable.getIntrinsicWidth();
			int right = left + mShadowDrawable.getIntrinsicWidth();
			int top = mContentView.getTop();
			int bottom = mContentView.getBottom();

			mShadowDrawable.setBounds(left, top, right, bottom);
			mShadowDrawable.draw(canvas);
		}

	}

	private String getActivityClassName(){
		Context context = this.getContext();
		String name = "";
		if (context != null && context instanceof Activity){
			name = context.getClass().getName();
		}
		return name;
	}


	/**
	 * 滚动出界面
	 */
	private void scrollRight() {
		final int delta = (viewWidth + mContentView.getScrollX());
		// 调用startScroll方法来设置一些滚动的参数，我们在computeScroll()方法中调用scrollTo来滚动item
		mScroller.startScroll(mContentView.getScrollX(), 0, -delta + 1, 0,
				Math.abs(delta));
		postInvalidate();
	}

	/**
	 * 滚动到起始位置
	 */
	private void scrollOrigin() {
		int delta = mContentView.getScrollX();
		mScroller.startScroll(mContentView.getScrollX(), 0, -delta, 0,
				Math.abs(delta));
		postInvalidate();
	}

	@Override
	public void computeScroll() {
		// 调用startScroll的时候scroller.computeScrollOffset()返回true，
		if (mScroller.computeScrollOffset()) {
			mContentView.scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			postInvalidate();

			if (mScroller.isFinished()) {
				if(mListener != null){
					mListener.onScrollEnd(isFinish);
				}
				if (isFinish) {
					mActivity.finish();
				}
			}
		}
	}


	/**
	 * 判断RecyclerView是否滑动到最左边
	 */
	private boolean hasScrollToLeft(RecyclerView recyclerView){
		if(recyclerView == null || recyclerView.getLayoutParams() == null ||
				!(recyclerView.getLayoutManager() instanceof LinearLayoutManager) ){
			return false;

		}
		LinearLayoutManager lm= (LinearLayoutManager) recyclerView.getLayoutManager();
		int firstPostion = lm.findFirstVisibleItemPosition();
		if(firstPostion == 0){
			View firstView = lm.findViewByPosition(firstPostion);
			RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams)firstView.getLayoutParams();
			return firstView.getLeft() == layoutParams.leftMargin;
		}
		return false;
	}

	public interface OnScrollbackListener{
		void onScrollEnd(boolean isClose);
		void onStartScroll();
	}
}