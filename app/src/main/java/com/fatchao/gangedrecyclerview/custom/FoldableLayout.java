package com.fatchao.gangedrecyclerview.custom;

import android.content.Context;
import android.graphics.Point;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Created by fuyuanyuan on 2018/3/8.
 */

public class FoldableLayout extends RelativeLayout {

  private final static String TAG = "FoldableLayout";

  private boolean isShowShadow = true;
  //手势处理类
  private GestureDetectorCompat gestureDetector;
  //视图拖拽移动帮助类
  private ViewDragHelper dragHelper;
  //滑动监听器
  private DragListener dragListener;
  //水平拖拽的距离
  private int range;
  //宽度
  private int width;
  //高度
  private int height;
  //fold 宽
  private int fd_width;
  //fold 高
  private int fd_height;
  //main视图距离在ViewGroup距离左边的距离
  private int mainLeft;
  private Context context;
  private ImageView iv_shadow;
  //左侧布局
  private View foldView;
  //右侧(主界面布局)
  private View unFoldView;

  private Point mAutoBackBottomPos = new Point();
  private Point mAutoBackTopPos = new Point();
  private Point mAutoBackScreenBottomPos = new Point();

  //页面状态 默认为关闭
  private LaoutState status = LaoutState.LAOUT_UNFOLD;

  private boolean isManual = true;

  private enum LaoutState {
    LAOUT_FOLD, LAOUT_UNFOLD
  }

  public FoldableLayout(Context context) {
    this(context, null);
  }


  public FoldableLayout(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
    this.context = context;
  }

  public FoldableLayout(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    gestureDetector = new GestureDetectorCompat(context, new YScrollDetector());
    dragHelper = ViewDragHelper.create(this, dragHelperCallback);
    dragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_ALL);
  }

  class YScrollDetector extends SimpleOnGestureListener {

  }

  /**
   * 布局加载完成回调
   * 做一些初始化的操作
   */
  @Override
  protected void onFinishInflate() {
    super.onFinishInflate();
    unFoldView = getChildAt(0);
    Log.d(TAG, "onFinishInflate: unFoldView = " + getChildAt(0));
    foldView = getChildAt(1);

    unFoldView.setClickable(true);

//    foldView.setClickable(true);



  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);

    width = unFoldView.getMeasuredWidth();
    height = unFoldView.getMeasuredHeight();

    fd_height = foldView.getMeasuredHeight();
    fd_width = foldView.getMeasuredWidth();

    //可以水平拖拽滑动的距离 一共为屏幕宽度的60%
    range = (int) (width * 0.6f);
  }





  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//    measureChild(vg);
  }

  /**
   * 调用进行left和main 视图进行位置布局
   * @param changed
   * @param l
   * @param t
   * @param r
   * @param b
   */
  @Override
  protected void onLayout(boolean changed, int l, int t, int r, int b) {
    super.onLayout(changed, l, t, r, b);

    Log.d(TAG, "onLayout: aaaaaa");

    foldView.layout(foldView.getLeft(), foldView.getTop() + foldView.getHeight(), foldView.getRight(), foldView.getBottom() + foldView.getHeight());
    int screenH = getHeight();


    mAutoBackTopPos.x = unFoldView.getLeft();
    mAutoBackTopPos.y = screenH - unFoldView.getHeight();

    mAutoBackBottomPos.x = mAutoBackTopPos.x;
    mAutoBackBottomPos.y = screenH - foldView.getHeight();

    mAutoBackScreenBottomPos.x = mAutoBackTopPos.x;
    mAutoBackScreenBottomPos.y = screenH;
  }

  /**
   * 拦截触摸事件
   * @param ev
   * @return
   */
  @Override
  public boolean onInterceptTouchEvent(MotionEvent ev) {
    boolean isCanDragge = false;
    switch (ev.getAction()) {
      case MotionEvent.ACTION_DOWN: {
        isManual = true;
        View toCapture = dragHelper.getCapturedView();
        isCanDragge = (toCapture != null
            && unFoldView == toCapture);
        break;
      }

      case MotionEvent.ACTION_MOVE: {
        unFoldView.setFocusableInTouchMode(true);
        unFoldView.requestFocus();
      }
    }
    if (isCanDragge) {
      Log.d(TAG, "onInterceptTouchEvent: onInterceptTouchEvent = " + super.onInterceptTouchEvent(ev));
      return super.onInterceptTouchEvent(ev);
    } else {
      Log.d(TAG, "onInterceptTouchEvent: shouldInterceptTouchEvent = " + dragHelper.shouldInterceptTouchEvent(ev));
      return dragHelper.shouldInterceptTouchEvent(ev);
    }
  }

  /**
   * 将拦截的到事件给ViewDragHelper进行处理
   * @param e
   * @return
   */
  @Override
  public boolean onTouchEvent(MotionEvent e) {
    try {
      Log.d(TAG, "onInterceptTouchEvent: ");
      dragHelper.processTouchEvent(e);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return false;
  }

  /**
   * 实现子View的拖拽滑动，实现Callback当中相关的方法
   */
  private ViewDragHelper.Callback dragHelperCallback = new ViewDragHelper.Callback() {
    /**
     * 水平方向移动
     * @param child Child view being dragged
     * @param left Attempted motion along the X axis
     * @param dx Proposed change in position for left
     * @return
     */
    @Override
    public int clampViewPositionHorizontal(View child, int left, int dx) {

      final int leftBound = getPaddingLeft();
      final int rightBound = getWidth() - child.getWidth() - leftBound;
      final int newLeft = Math.min(Math.max(left, leftBound), rightBound);

      return newLeft;

    }

    @Override
    public void onViewCaptured(View capturedChild, int activePointerId) {
      super.onViewCaptured(capturedChild, activePointerId);

      if (capturedChild == foldView) {
        unFoldView.setFocusable(true);
      }
    }
//
//
//    @Override
//    public void onViewDragStateChanged(int state) {
//      super.onViewDragStateChanged(state);
//    }

    /**
     * 竖直方向移动
     * @param child Child view being dragged
     * @param top Attempted motion along the X axis
     * @param dy Proposed change in position for top
     * @return
     */
    @Override
    public int clampViewPositionVertical(View child, int top, int dy) {
      if (child == unFoldView) {
        int topBound = getHeight() - unFoldView.getHeight();
        int bottomBound = getHeight() - foldView.getHeight();

        Log.d(TAG, "clampViewPositionVertical: position = " + Math.min(bottomBound, Math.max(top, topBound)));
        return Math.min(bottomBound, Math.max(top, topBound));
      } else {
        int foldTopBound = getHeight() - foldView.getHeight() - 1;
        int foldBottomBound = getHeight() - foldView.getHeight();
        return Math.min(foldBottomBound, Math.max(top, foldTopBound));
      }
    }

    /**
     * 拦截所有的子View
     * @param child Child the user is attempting to capture
     * @param pointerId ID of the pointer attempting the capture
     * @return
     */
    @Override
    public boolean tryCaptureView(View child, int pointerId) {

      if (child == foldView) {
        dragHelper.setmCapturedView(unFoldView);
      }

      return child == unFoldView || child == foldView;
    }
    /**
     * 设置水平方向滑动的最远距离
     * @param child Child view to check  屏幕宽度
     * @return
     */
    @Override
    public int getViewHorizontalDragRange(View child) {
//      dragHelper.getCapturedView();
      Log.d(TAG, "getViewHorizontalDragRange: getMeasuredWidth() - child.getMeasuredWidth()");
      return getMeasuredWidth() - child.getMeasuredWidth();
    }

//    @Override
//    public void onViewCaptured(View capturedChild, int activePointerId) {
//      super.onViewCaptured(capturedChild, activePointerId);
//    }
//
//    @Override
//    public void onEdgeTouched(int edgeFlags, int pointerId) {
//      super.onEdgeTouched(edgeFlags, pointerId);
//      Toast.makeText(getContext(), "edgeTouched", Toast.LENGTH_SHORT).show();
//    }
//
//    @Override
//    public void onEdgeDragStarted(int edgeFlags, int pointerId) {
//      dragHelper.captureChildView(unFoldView, pointerId);
//    }

    
    /**
     * 设置垂直方向滑动的最远距离
     * @param child Child view to check  屏幕宽度
     * @return
     */
    @Override
    public int getViewVerticalDragRange(View child) {
      Log.d(TAG, "onInterceptTouchEvent: vertical range = " +  (getMeasuredHeight() - child.getMeasuredHeight()));
      return getMeasuredHeight() - child.getMeasuredHeight();
    }

    /**
     * 当拖拽的子View，手势释放的时候回调的方法， 然后根据左滑或者右滑的距离进行判断打开或者关闭
     * @param releasedChild
     * @param xvel             x方向移动的速度：正值：向右移动  负值：向左移动
     * @param yvel             y方向移动的速度：正值：向下移动  负值：向上移动
     */
    @Override
    public void onViewReleased(View releasedChild, float xvel, float yvel) {
      super.onViewReleased(releasedChild, xvel, yvel);
      int screenH = getHeight();
      int vg_fold_H = foldView.getHeight();
      int limit = 50;
      if (releasedChild == unFoldView) {
        if (releasedChild.getY() < screenH - vg_fold_H - limit ) {
          openUnfoldView();
        } else if (releasedChild.getY() >= screenH - vg_fold_H - limit
            && releasedChild.getY() < screenH - vg_fold_H) {
          closeUnfoldView();
        }
        isManual = false;
      }
    }

    /**
     * 子View被拖拽 移动的时候回调的方法
     * @param changedView View whose position changed
     * @param left New X coordinate of the left edge of the view
     * @param top New Y coordinate of the top edge of the view
     * @param dx Change in X position from the last call
     * @param dy Change in Y position from the last call
     */
    @Override
    public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
      super.onViewPositionChanged(changedView, left, top, dx, dy);

      int foldDy = (int)(-dy * 0.7);

      if (changedView == unFoldView && isManual) {
        foldView.layout(foldView.getLeft() + dx, foldView.getTop() + foldDy, foldView.getRight() + dx,  foldView.getBottom() + foldDy);
      }
      
      
    }
  };

  private void closeUnfoldView() {

    if (dragHelper.smoothSlideViewTo(unFoldView, mAutoBackBottomPos.x, mAutoBackBottomPos.y)) {
      ViewCompat.postInvalidateOnAnimation(this);
    }

    foldView.layout(foldView.getLeft(), getHeight() - foldView.getHeight(), foldView.getRight(), getHeight());

  }

  private void openUnfoldView() {
//    dragHelper.settleCapturedViewAt(mAutoBackTopPos.x, mAutoBackTopPos.y);
    if (dragHelper.smoothSlideViewTo(unFoldView, mAutoBackTopPos.x, mAutoBackTopPos.y)) {
      ViewCompat.postInvalidateOnAnimation(this);
    }

    foldView.layout(foldView.getLeft(), getHeight(), foldView.getRight(),  getHeight() + foldView.getHeight());

  }


  @Override
  public void invalidate() {
    super.invalidate();
  }

  @Override
  public void computeScroll() {
      // 滚动过程的计算，也是交给ViewDragHelper，按以下这么写就好了
      if (dragHelper.continueSettling(true)) {
        invalidate();
      }
  }



  /**
   * 滑动相关回调接口
   */
  public interface DragListener {
    //界面打开
    public void onOpen();
    //界面关闭
    public void onClose();
    //界面滑动过程中
    public void onDrag(float percent);
  }
  public void setDragListener(DragListener dragListener) {
    this.dragListener = dragListener;
  }


}
