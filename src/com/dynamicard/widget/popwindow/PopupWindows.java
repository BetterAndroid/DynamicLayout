package com.dynamicard.widget.popwindow;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnTouchListener;

import android.widget.PopupWindow;
import android.content.Context;

/**
 * 自定义 popup window.
 * @author wy
 */
public class PopupWindows {
	protected Context mContext;
	protected PopupWindow mWindow;
	protected View mRootView;
	protected Drawable mBackground = null;
	protected WindowManager mWindowManager;

	/**
	 * 构造方法
	 * @param Context context
	 */
	public PopupWindows(Context context) {
		mContext = context;
		mWindow = new PopupWindow(context);
		mWindow.setTouchInterceptor(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
					mWindow.dismiss();
					return true;
				}
				return false;
			}
		});
		mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
	}

	/**
	 * On dismiss
	 */
	protected void onDismiss() {
	}

	/**
	 * On show
	 */
	protected void onShow() {
	}

	/**
	 * On pre show
	 */
	protected void preShow() {
		if (mRootView == null)
			throw new IllegalStateException("setContentView was not called with a view to display.");
		onShow();
		if (mBackground == null)
			mWindow.setBackgroundDrawable(new BitmapDrawable());
		else
			mWindow.setBackgroundDrawable(mBackground);
		mWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
		mWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
		mWindow.setTouchable(true);
		mWindow.setFocusable(true);
		mWindow.setOutsideTouchable(true);
		mWindow.setContentView(mRootView);
	}

	/**
	 * 设置背景 drawable.
	 * @param background 背景drawable
	 */
	public void setBackgroundDrawable(Drawable background) {
		mBackground = background;
	}

	/**
	 * 设置内容视图。
	 * @param root 根view
	 */
	public void setContentView(View root) {
		mRootView = root;
		mWindow.setContentView(root);
	}

	/**
	 * 设置内容视图。
	 * @param layoutResID 资源id
	 */
	public void setContentView(int layoutResID) {
		LayoutInflater inflator = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		setContentView(inflator.inflate(layoutResID, null));
	}

	/**
	 * 在窗口中设置监听Dismiss
	 */
	public void setOnDismissListener(PopupWindow.OnDismissListener listener) {
		mWindow.setOnDismissListener(listener);
	}

	/**
	 * 关闭该弹出窗口。
	 */
	public void dismiss() {
		mWindow.dismiss();
	}
}