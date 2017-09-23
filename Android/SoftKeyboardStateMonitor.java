package com.jebysun.keybordchangemonitor.monitor;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import android.view.ViewTreeObserver;

/**
 * 软键盘打开/关闭状态变化监听器
 *
 * 参考：
 * http://blog.csdn.net/qq_24531461/article/details/60151395
 *
 * Created by JebySun on 2017/9/24.
 */

public class SoftKeyboardStateMonitor {

    // 软键盘最小高度
    private final int MIN_KEYBOARD_HEIGHT_PX = 200;
    private View mDecorView;

    private ViewTreeObserver.OnGlobalLayoutListener mOnGlobalLayoutListener;

    public SoftKeyboardStateMonitor(Activity activity) {
        mDecorView = activity.getWindow().getDecorView();
    }

    private ViewTreeObserver.OnGlobalLayoutListener buildOnGlobalLayoutListener(final SoftKeyboardStateChangeListener listener) {
        return new ViewTreeObserver.OnGlobalLayoutListener() {
            private final Rect windowVisibleDisplayFrame = new Rect();
            private int lastVisibleDecorViewHeight;

            @Override
            public void onGlobalLayout() {
                //获取decorview的可见范围
                mDecorView.getWindowVisibleDisplayFrame(windowVisibleDisplayFrame);
                final int visibleDecorViewHeight = windowVisibleDisplayFrame.height();

                // 通过decorview高度变化判断是否显示来软键盘
                if (lastVisibleDecorViewHeight != 0) {
                    if (lastVisibleDecorViewHeight > visibleDecorViewHeight + MIN_KEYBOARD_HEIGHT_PX) {
                        // 计算当前软键盘高度(这个高度包含全屏时navigation bar的高度)
                        int currentKeyboardHeight = mDecorView.getHeight() - windowVisibleDisplayFrame.bottom;
                        // 通知软键盘打开了
                        listener.onKeyboardShown(currentKeyboardHeight);
                    } else if (lastVisibleDecorViewHeight + MIN_KEYBOARD_HEIGHT_PX < visibleDecorViewHeight) {
                        // 通知软键盘关闭了
                        listener.onKeyboardHidden();
                    }
                }
                //保存decorview高度
                lastVisibleDecorViewHeight = visibleDecorViewHeight;
            }
        };
    }


    public void registerSoftKeyboardStateChangeListener(SoftKeyboardStateChangeListener listener) {
        mOnGlobalLayoutListener = buildOnGlobalLayoutListener(listener);
        mDecorView.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
    }

    @SuppressWarnings("deprecation")
    public void unregisterSoftKeyboardStateChangeListener() {
        if (mDecorView == null || mOnGlobalLayoutListener == null) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mDecorView.getViewTreeObserver().removeOnGlobalLayoutListener(mOnGlobalLayoutListener);
        } else {
            mDecorView.getViewTreeObserver().removeGlobalOnLayoutListener(mOnGlobalLayoutListener);
        }

        mOnGlobalLayoutListener = null;
        mDecorView = null;
    }




    public interface SoftKeyboardStateChangeListener {
        void onKeyboardShown(int keyboardHeight);
        void onKeyboardHidden();
    }

}
