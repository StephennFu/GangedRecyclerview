package com.fatchao.gangedrecyclerview.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by fuyuanyuan on 2018/3/12.
 */

public class DimensionUtils {

  /**
   * 获得屏幕宽度
   *
   * @param context
   * @return
   */
  public static int getScreenWidth(Context context)
  {
    WindowManager wm = (WindowManager) context
        .getSystemService(Context.WINDOW_SERVICE);
    DisplayMetrics outMetrics = new DisplayMetrics();
    wm.getDefaultDisplay().getMetrics(outMetrics);
    return outMetrics.widthPixels;
  }

  /**
   * 获得屏幕高度
   *
   * @param context
   * @return
   */
  public static int getScreenHeight(Context context)
  {
    WindowManager wm = (WindowManager) context
        .getSystemService(Context.WINDOW_SERVICE);
    DisplayMetrics outMetrics = new DisplayMetrics();
    wm.getDefaultDisplay().getMetrics(outMetrics);
    return outMetrics.heightPixels;
  }

  /**
   * 获得状态栏的高度
   *
   * @param context
   * @return
   */
  public static int getStatusHeight(Context context)
  {

    int statusHeight = -1;
    try
    {
      Class<?> clazz = Class.forName("com.android.internal.R$dimen.xml");
      Object object = clazz.newInstance();
      int height = Integer.parseInt(clazz.getField("status_bar_height")
          .get(object).toString());
      statusHeight = context.getResources().getDimensionPixelSize(height);
    } catch (Exception e)
    {
      e.printStackTrace();
    }
    return statusHeight;
  }

  /**
   * 获取当前屏幕截图，包含状态栏
   *
   * @param activity
   * @return
   */
  public static Bitmap snapShotWithStatusBar(Activity activity)
  {
    View view = activity.getWindow().getDecorView();
    view.setDrawingCacheEnabled(true);
    view.buildDrawingCache();
    Bitmap bmp = view.getDrawingCache();
    int width = getScreenWidth(activity);
    int height = getScreenHeight(activity);
    Bitmap bp = null;
    bp = Bitmap.createBitmap(bmp, 0, 0, width, height);
    view.destroyDrawingCache();
    return bp;

  }

  /**
   * 获取当前屏幕截图，不包含状态栏
   *
   * @param activity
   * @return
   */
  public static Bitmap snapShotWithoutStatusBar(Activity activity)
  {
    View view = activity.getWindow().getDecorView();
    view.setDrawingCacheEnabled(true);
    view.buildDrawingCache();
    Bitmap bmp = view.getDrawingCache();
    Rect frame = new Rect();
    activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
    int statusBarHeight = frame.top;

    int width = getScreenWidth(activity);
    int height = getScreenHeight(activity);
    Bitmap bp = null;
    bp = Bitmap.createBitmap(bmp, 0, statusBarHeight, width, height
        - statusBarHeight);
    view.destroyDrawingCache();
    return bp;

  }


  public static int getRealHeight(Context context) {
    WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    Display display = wm.getDefaultDisplay();
    int screenHeight = 0;

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
      DisplayMetrics dm = new DisplayMetrics();
      display.getRealMetrics(dm);
      screenHeight = dm.heightPixels;

      //或者也可以使用getRealSize方法
//            Point size = new Point();
//            display.getRealSize(size);
//            screenHeight = size.y;
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
      try {
        screenHeight = (Integer) Display.class.getMethod("getRawHeight").invoke(display);
      } catch (Exception e) {
        DisplayMetrics dm = new DisplayMetrics();
        display.getMetrics(dm);
        screenHeight = dm.heightPixels;
      }
    }
    return screenHeight;
  }


  public static int getNavigationBarHeight(Context context) {
    int navigationBarHeight = -1;
    Resources resources = context.getResources();
    int resourceId = resources.getIdentifier("navigation_bar_height","dimen", "android");
    if (resourceId > 0) {
      navigationBarHeight = resources.getDimensionPixelSize(resourceId);
    }
    return navigationBarHeight;
  }


}
