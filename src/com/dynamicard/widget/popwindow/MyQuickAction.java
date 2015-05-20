package com.dynamicard.widget.popwindow;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import com.dynamicard.ui.MyActivity;
import com.dynamicard.ui.R;

/**
 * MyQuickAction对话框，显示操作列表图标和文字像在Gallery3D应用程序。目前支持纵向和横向布局。
 * @author wy
 */
public class MyQuickAction extends PopupWindows implements OnDismissListener {
	private View mRootView;
	private LayoutInflater mInflater;
	private ViewGroup mTrack;
	private OnDismissListener mDismissListener;

	private List<ActionItem> actionItems = new ArrayList<ActionItem>();

	private boolean mDidAction;

	private int mInsertPos;
	private int mAnimStyle;
	private int rootWidth = 0;

	public static final int ANIM_GROW_FROM_LEFT = 1;
	public static final int ANIM_GROW_FROM_RIGHT = 2;
	public static final int ANIM_GROW_FROM_CENTER = 3;
	public static final int ANIM_REFLECT = 4;
	public static final int ANIM_AUTO = 5;

	/**
	 * @param context Context
	 */
	public MyQuickAction(Context context) {
		super(context);
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		setRootViewId(R.layout.popup_window);
		mAnimStyle = ANIM_GROW_FROM_CENTER;
	}

	/**
	 * 获取行动项目索引
	 * @param index 项目的索引（从回调位置）
	 * @return Action Item at the position
	 */
	public ActionItem getActionItem(int index) {
		return actionItems.get(index);
	}

	/**
	 * 设置根视图。
	 * @param id 布局资源IDs
	 */
	public void setRootViewId(int id) {
		mRootView = (ViewGroup) mInflater.inflate(id, null);
		mTrack = (ViewGroup) mRootView.findViewById(R.id.tracks);
		mRootView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		setContentView(mRootView);
	}

	/**
	 * 设置动画风格
	 * 
	 * @param mAnimStyle 动画风格，默认设置为ANIM_AUTO
	 */
	public void setAnimStyle(int mAnimStyle) {
		this.mAnimStyle = mAnimStyle;
	}

	/**
	 * 添加行动项目
	 * @param action {@link ActionItem}
	 */
	public void addActionItem(final Context context, List<ActionItem> actionItems) {
		this.actionItems = actionItems;
		View container = mInflater.inflate(R.layout.popup_action_grid, null);
		Button action_btn = (Button) container.findViewById(R.id.popup_action_btn);
		action_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dismiss();
			}
		});
		GridView action_gridview = (GridView) container.findViewById(R.id.popup_action_gridview);
		MyGridAdapter adapter = new MyGridAdapter(mContext, actionItems);
		action_gridview.setAdapter(adapter);
		action_gridview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				System.out.println("position="+position);
			}
		});
		container.setFocusable(true);
		container.setClickable(true);
		mTrack.addView(container, mInsertPos);
		mInsertPos++;
	}
	
	/**
	 * 获取通知栏的高度
	 * @return 高度
	 */
	 public int getStatusBarHeight() {
        return Resources.getSystem().getDimensionPixelSize(Resources.getSystem().getIdentifier("status_bar_height", "dimen", "android"));
    }
	
	/**
	 * 显示quickaction弹出。弹出自动定位，在顶部或anchor视图底部。
	 */
	@SuppressWarnings("deprecation")
	public void show(View anchor) {
		preShow();
		int xPos, yPos;
		mDidAction = false;
		int[] location = new int[2];
		anchor.getLocationOnScreen(location);
		Rect anchorRect = new Rect(location[0], location[1] + getStatusBarHeight()-10, location[0] + anchor.getWidth(), location[1] + anchor.getHeight() + getStatusBarHeight()-10);
		mRootView.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		int rootHeight = mRootView.getMeasuredHeight();
		if (rootWidth == 0) {
			rootWidth = mRootView.getMeasuredWidth();
		}
		int screenWidth = mWindowManager.getDefaultDisplay().getWidth();
		int screenHeight = mWindowManager.getDefaultDisplay().getHeight();
		// 自动获得的弹出式X坐标（左上）
		if ((anchorRect.left + rootWidth) > screenWidth) {
			xPos = anchorRect.left - (rootWidth - anchor.getWidth());
			xPos = (xPos < 0) ? 0 : xPos;
		} else {
			if (anchor.getWidth() > rootWidth) {
				xPos = anchorRect.centerX() - (rootWidth / 2);
			} else {
				xPos = anchorRect.left;
			}
		}

		int dyTop = anchorRect.top;
		int dyBottom = screenHeight - anchorRect.bottom;

		boolean onTop = (dyTop > dyBottom) ? true : false;
		if (onTop) {
			if (rootHeight > dyTop) {
				yPos = 15;
				LayoutParams l = mTrack.getLayoutParams();
				l.height = dyTop - anchor.getHeight();
			} else {
				yPos = anchorRect.top - rootHeight;
			}
		} else {
			yPos = anchorRect.bottom;

			if (rootHeight > dyBottom) {
				LayoutParams l = mTrack.getLayoutParams();
				l.height = dyBottom;
			}
		}

		setAnimationStyle(screenWidth, anchorRect.centerX(), onTop);
		if(!mWindow.isShowing()) {
			mWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, xPos, yPos);
		}
	}

	/**
	 * 设置动画风格
	 * @param screenWidth  屏幕宽度
	 * @param requestedX 从左侧边缘的距离
	 * @param onTop 标志以指示应显示在弹出。设置为TRUE,如果显示的anchor view点，反之亦然顶部
	 */
	private void setAnimationStyle(int screenWidth, int requestedX, boolean onTop) {
		int arrowPos = 0;
		switch (mAnimStyle) {
		case ANIM_GROW_FROM_LEFT:
			mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Left
					: R.style.Animations_PopDownMenu_Left);
			break;

		case ANIM_GROW_FROM_RIGHT:
			mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Right
					: R.style.Animations_PopDownMenu_Right);
			break;

		case ANIM_GROW_FROM_CENTER:
			mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Center
					: R.style.Animations_PopDownMenu_Center);
			break;

		case ANIM_REFLECT:
			mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Reflect
					: R.style.Animations_PopDownMenu_Reflect);
			break;

		case ANIM_AUTO:
			if (arrowPos <= screenWidth / 4) {
				mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Left
						: R.style.Animations_PopDownMenu_Left);
			} else if (arrowPos > screenWidth / 4
					&& arrowPos < 3 * (screenWidth / 4)) {
				mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Center
						: R.style.Animations_PopDownMenu_Center);
			} else {
				mWindow.setAnimationStyle((onTop) ? R.style.Animations_PopUpMenu_Right
						: R.style.Animations_PopDownMenu_Right);
			}

			break;
		}
	}

	/**
	 * 设置监听窗口消失. This listener will only be fired if
	 * the quicakction dialog is dismissed by clicking outside the dialog or clicking on sticky item.
	 */
	public void setOnDismissListener(MyQuickAction.OnDismissListener listener) {
		setOnDismissListener(this);
		mDismissListener = listener;
	}

	@Override
	public void onDismiss() {
		if (!mDidAction && mDismissListener != null) {
			mDismissListener.onDismiss();
		}
	}

	/**
	 * Listener for window dismiss 监听窗口消失
	 */
	public interface OnDismissListener {
		public abstract void onDismiss();
	}
	
	/**
	 * PopupWindow的itemAdapter 
	 * @author wy
	 */
	public class MyGridAdapter extends BaseAdapter {

		private Context mContext;
		private List<ActionItem> actionItems;
		public MyGridAdapter(Context mContext, List<ActionItem> actionItems) {
			super();
			this.mContext = mContext;
			this.actionItems = actionItems;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return actionItems!=null? actionItems.size() : 0;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return actionItems.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			LayoutInflater inflater = LayoutInflater.from(mContext);
			ViewHolder holder;
			if(convertView==null) {
				convertView = inflater.inflate(R.layout.popup_action_item, null);
				holder = new ViewHolder();
				holder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
				holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			String item = actionItems.get(position).getTitle();
			holder.tv_title.setText(item);
			convertView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
//					System.out.println("view="+v+"------position=" + position);
//					Toast.makeText(mContext, "view="+v+"------position=" + position, Toast.LENGTH_SHORT).show();
					Intent intent = new Intent(mContext, MyActivity.class);
					intent.putExtra("title", actionItems.get(position).getTitle());
					mContext.startActivity(intent);
					mWindow.dismiss();
				}
			});
			return convertView;
		}

		class ViewHolder {
			ImageView iv_icon;
			TextView tv_title;
		}
		
	}
	
	
}