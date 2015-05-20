package com.dynamicard.widget.popwindow;

import android.graphics.drawable.Drawable;
import android.graphics.Bitmap;

/**
 * Action item, bean显示为带有图标和文本菜单。.
 * @author wy
 */
public class ActionItem {
	private Drawable icon;
	private Bitmap thumb;
	private String title;
	private int actionId = -1;
    private boolean selected;
    private boolean sticky;
	
    public ActionItem(int actionId, String title, Drawable icon) {
        this.title = title;
        this.icon = icon;
        this.actionId = actionId;
    }
    public ActionItem() {
        this(-1, null, null);
    }
    
    public ActionItem(int actionId, String title) {
        this(actionId, title, null);
    }
    
    public ActionItem(Drawable icon) {
        this(-1, null, icon);
    }
    
    public ActionItem(int actionId, Drawable icon) {
        this(actionId, null, icon);
    }

    public void setTitle(String title) {
		this.title = title;
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public void setIcon(Drawable icon) {
		this.icon = icon;
	}
	
	public Drawable getIcon() {
		return this.icon;
	}
	
    public void setActionId(int actionId) {
        this.actionId = actionId;
    }
    
    public int getActionId() {
        return actionId;
    }
    
    /**
     * Set sticky status of button
     * @param sticky  true for sticky, pop up sends event but does not disappear
     */
    public void setSticky(boolean sticky) {
        this.sticky = sticky;
    }
    
    /**
     * @return  true if button is sticky, menu stays visible after press
     */
    public boolean isSticky() {
        return sticky;
    }
    
	/**
	 * Set selected flag;
	 * 
	 * @param selected Flag to indicate the item is selected
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	/**
	 * Check if item is selected
	 * @return true or false
	 */
	public boolean isSelected() {
		return this.selected;
	}

	/**
	 * Set thumb
	 * @param thumb Thumb image
	 */
	public void setThumb(Bitmap thumb) {
		this.thumb = thumb;
	}
	
	/**
	 * Get thumb image
	 * @return Thumb image
	 */
	public Bitmap getThumb() {
		return this.thumb;
	}
}