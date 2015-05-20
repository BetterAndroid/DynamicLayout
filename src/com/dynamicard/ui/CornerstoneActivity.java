package com.dynamicard.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.dynamicard.afinal.FinalBitmap;
import com.dynamicard.control.CornerstoneControl;
import com.dynamicard.model.CornerstoneInfoMode;
import com.dynamicard.widget.popwindow.ActionItem;
import com.dynamicard.widget.popwindow.MyQuickAction;

/**
 * 动态规划算法
 * @author wy
 */
public class CornerstoneActivity extends Activity {
	private static final int COLUMNCOUNT = 2;//列数
	private int columnWidth = 250;// 每个item的宽度
	private int itemHeight = 0;
	private int rowCountPerScreen = 2;
	private int cols = 2;// 当前总列数
	private ArrayList<Integer> colYs = new ArrayList<Integer>();
//	private ArrayList<View> currentViews = new ArrayList<View>();
	private LayoutInflater mInflater;
	private RelativeLayout rootView;
	private FinalBitmap fb;
	private List<CornerstoneInfoMode> infos = new ArrayList<CornerstoneInfoMode>();
	private List<Point> lostPoint = new ArrayList<Point>();// 用于记录空白块的位置
//	private int currentPage = 1;
	private LazyScrollView rootScroll;
	private RelativeLayout loading_rl;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_cornerstone);
		init();
		CornerstoneControl cornerstoneControl = new CornerstoneControl(this, mHandler);
		cornerstoneControl.onSuccess();
	}
	@SuppressWarnings("deprecation")
	private void init() {
		rootView = (RelativeLayout) this.findViewById(R.id.rootView);
		rootView.setPersistentDrawingCache(ViewGroup.PERSISTENT_ANIMATION_CACHE);
		rootScroll = (LazyScrollView) this.findViewById(R.id.rootScroll);
//		rootScroll.setOnScrollListener(this);
		rootScroll.getView();
		mInflater = getLayoutInflater();
		Display display = getWindowManager().getDefaultDisplay();
		
		int width = display.getWidth();
		int height = display.getHeight();
		Configuration cf = this.getResources().getConfiguration();

		if (cf.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			rowCountPerScreen = 3;
		} else {
			rowCountPerScreen = 6;
		}
		columnWidth = width / COLUMNCOUNT;
		itemHeight = height / rowCountPerScreen;
		fb = FinalBitmap.create(this);
		for (int i = 0; i < 2; i++) {
			colYs.add(0);
		}
		loading_rl = (RelativeLayout) this.findViewById(R.id.loading_rl);
		loading_rl.setVisibility(View.VISIBLE);
	}
	
	
	private synchronized void addView(View view, final String uri, final int position) {
		placeBrick(view);
		ImageView picView = (ImageView) view.findViewById(R.id.imageView);
		rootView.addView(view);
		picView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d("MainActivity", "position:" + position);
				if(position == 0) {
					showChildQuickActionBar(v);
				} else {
					Intent intent = new Intent(getApplicationContext(), MyActivity.class);
					startActivity(intent);
				}
			}
		});
		startAnim(view);
		fb.display(picView, uri);
	}

	/*************************showPopupWindow************************************/
	MyQuickAction quickAction = null;
	private void showChildQuickActionBar(View view) {
		quickAction = new MyQuickAction(this);
		quickAction.addActionItem(this, initData());
		quickAction.show(view);
	}
	
	
	private List<ActionItem> initData() {
		// TODO Auto-generated method stub
		List<ActionItem> actionItems = new ArrayList<ActionItem>();
		ActionItem actionItem = new ActionItem(0, getString(R.string.Art));
		actionItems.add(actionItem);
		actionItem = new ActionItem(1, getString(R.string.Science));
		actionItems.add(actionItem);
		actionItem = new ActionItem(2, getString(R.string.Mathematics));
		actionItems.add(actionItem);
		actionItem = new ActionItem(3, getString(R.string.English));
		actionItems.add(actionItem);
		actionItem = new ActionItem(4, getString(R.string.Chinese));
		actionItems.add(actionItem);
		actionItem = new ActionItem(5, getString(R.string.Physical));
		actionItems.add(actionItem);
		actionItem = new ActionItem(6, getString(R.string.Chemistry));
		actionItems.add(actionItem);
		actionItem = new ActionItem(7, getString(R.string.History));
		actionItems.add(actionItem);
		actionItem = new ActionItem(1, getString(R.string.Biological));
		actionItems.add(actionItem);
		return actionItems;
	}
	
	/*************************************************************/
	
//	private final class DisplayNextView implements Animation.AnimationListener {
//		private final int mPosition;
//
//		private DisplayNextView(int position) {
//			mPosition = position;
//		}
//
//		public void onAnimationStart(Animation animation) {
//		}
//
//		public void onAnimationEnd(Animation animation) {
//		}
//
//		public void onAnimationRepeat(Animation animation) {
//		}
//	}

	/**
	 * 原理：动态规划
	 * @param view
	 */
	/*****************************布局算法********************************/
	int minimumY = 0;
	private void placeBrick(View view) {
		LayoutParams brick = (LayoutParams) view.getLayoutParams();
		int groupCount, colSpan, rowSpan;
		List<Integer> groupY = new ArrayList<Integer>();
		List<Integer> groupColY = new ArrayList<Integer>();
		colSpan = (int) Math.ceil(brick.width / this.columnWidth);// 计算跨几列
		colSpan = Math.min(colSpan, this.cols);// 取最小的列数
		rowSpan = (int) Math.ceil(brick.height / this.itemHeight);//行数
		Log.d("VideoShowActivity", "colSpan:" + colSpan);
		if (colSpan == 1) {// 说明没有跨列
			groupY = this.colYs;
			// 如果存在白块则从添加到白块中
			if (lostPoint.size() > 0 && rowSpan == 1) {
				Point point = lostPoint.get(0);
				int pTop = point.y;
				int pLeft = this.columnWidth * point.x;// 放置的left
				android.widget.RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
						brick.width, brick.height);
				params.leftMargin = pLeft;
				params.topMargin = pTop;
				view.setLayoutParams(params);
				lostPoint.remove(0);
				return;
			}
		} else {// 说明有跨列
			groupCount = this.cols + 1 - colSpan;// 添加item的时候列可以填充的列index
			for (int j = 0; j < groupCount; j++) {
				groupColY = this.colYs.subList(j, j + colSpan);
				groupY.add(j, Collections.max(groupColY));// 选择几个可添加的位置
			}
		}

		minimumY = Collections.min(groupY);// 取出几个可选位置中最小的添加
		int shortCol = 0;
		int len = groupY.size();
		for (int i = 0; i < len; i++) {
			if (groupY.get(i) == minimumY) {
				shortCol = i;// 获取到最小y值对应的列值
				break;
			}
		}
		
		int pTop = minimumY;// 这是放置的Top
		int pLeft = this.columnWidth * shortCol;// 放置的left
		android.widget.RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				brick.width, brick.height);
		params.leftMargin = pLeft;
		params.topMargin = pTop;
		view.setLayoutParams(params);
		if (colSpan != 1) {
			for (int i = 0; i < this.cols; i++) {
				if (minimumY > this.colYs.get(i)) {// 出现空行
					int y = minimumY - this.colYs.get(i);
					for (int j = 0; j < y / itemHeight; j++) {
						lostPoint.add(new Point(i, this.colYs.get(i)
								+ itemHeight * j));
					}
				}
			}
		}
		int setHeight = minimumY + brick.height, setSpan = this.cols + 1 - len;
		for (int i = 0; i < setSpan; i++) {
			this.colYs.set(shortCol + i, setHeight);
		}
	}

	@SuppressLint("HandlerLeak")
	@SuppressWarnings("unchecked")
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				loading_rl.setVisibility(View.GONE);
				postContent((List<CornerstoneInfoMode>)msg.obj);
				break;
			default:
				break;
			}
		};
	};
	
	/**
	 * 动态布局界面
	 * @param result
	 */
	private void postContent(List<CornerstoneInfoMode> result) {
		// TODO Auto-generated method stub
		// 动态计算ListView
		if (result != null) {
			Random r = new Random();
			for (int i = 0; i < result.size(); i++) {
				View v = mInflater.inflate(R.layout.activity_cornerstone_item, null);
				int nextInt = r.nextInt(50);
				if(i == 0) {
					nextInt = 45;
				}
				if(i == 1) {
					nextInt = 35;
				}
				if(i == 2) {
					nextInt = 20;
				} 
				if(i > 2) {
					nextInt = 20;
				}
				if(i == 4) {
					nextInt = 45;
				}
				if(i == 7) {
					nextInt = 26;
				}
				if(i == 10) {
					nextInt = 35;
				}
				// 模拟分为三种情况
				if (nextInt > 40) {
					// 跨两列两行
					android.widget.RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(columnWidth * 2, itemHeight * 2);
					v.setLayoutParams(params);
				} else if (nextInt > 30) {
					// 跨一列两行
					android.widget.RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(columnWidth, itemHeight * 2);
					v.setLayoutParams(params);
				} else if (nextInt > 25) {
					// 跨两列一行//填充的是宽度
					android.widget.RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(columnWidth * 2, itemHeight);
					v.setLayoutParams(params);
				} else {//俩行是一列
					android.widget.RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(columnWidth, itemHeight);
					v.setLayoutParams(params);
				}
				addView(v, result.get(i).getIsrc(), infos.size() + i);
			}
			infos.addAll(result);
		}
	}
	
	private void startAnim(View v) {
		final float centerX = columnWidth / 2.0f;
		final float centerY = itemHeight / 2.0f;
		Rotate3dAnimation rotation;
		rotation = new Rotate3dAnimation(10, 0, centerX, centerY);
		rotation.setDuration(1000);
		rotation.setFillAfter(true);
		rotation.setInterpolator(new DecelerateInterpolator());
		v.startAnimation(rotation);
	}

//	private int currentPage = 0;
//	@Override
//	public void onBottom() {
//		String url = "http://www.duitang.com/blogs/tags/hot/?page=" + currentPage + "&tags=%E5%8A%A8%E6%BC%AB%2C%E6%89%8B%E5%8A%9E%2C%E5%8A%A8%E7%94%BB%2C%E6%B5%B7%E8%B4%BC%E7%8E%8B%2C%E6%BC%AB%E7%94%BB&_type=";
//		currentPage++;
//	}
//
//	@Override
//	public void onAutoScroll(int l, int t, int oldl, int oldt) {
//	}
}
