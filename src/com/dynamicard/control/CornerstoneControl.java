package com.dynamicard.control;


import java.util.ArrayList;
import java.util.List;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dynamicard.afinal.FinalHttp;
import com.dynamicard.afinal.http.AjaxCallBack;
import com.dynamicard.afinal.http.AjaxParams;
import com.dynamicard.model.CornerstoneInfoMode;

import android.content.Context;  
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

public class CornerstoneControl {

	private Handler handler = null;
	public CornerstoneControl(Context context, Handler handler) {
		this.handler = handler;
	}
	public void onSuccess() {
//		System.out.println("statusCode:" + statusCode + "////content:" + content);
		FinalHttp finalHttp = new FinalHttp();
		AjaxParams params = new AjaxParams();
		finalHttp.post(url(), params, new AjaxCallBack<String>() {
			@Override
			public void onSuccess(String content) {
				// TODO Auto-generated method stub
				super.onSuccess(content);
				try {
					Message msg = Message.obtain();
					msg.obj = parseNewsJSON(content);
					msg.what = 0;
					handler.sendMessage(msg);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				// TODO Auto-generated method stub
				super.onFailure(t, errorNo, strMsg);
			}
		});
	}
	
	public void onFailure() {
		
	}
	
	public String url() {
		// TODO Auto-generated method stub
		return "http://www.duitang.com/album/1733789/masn/p/2/24/";
	}
	
	public List<CornerstoneInfoMode> parseNewsJSON(String json) throws JSONException {
		List<CornerstoneInfoMode> duitangs = new ArrayList<CornerstoneInfoMode>();
		if (!TextUtils.isEmpty(json)) {
			JSONObject newsObject = new JSONObject(json);
			JSONObject jsonObject = newsObject.getJSONObject("data");
			JSONArray blogsJson = jsonObject.getJSONArray("blogs");
			for (int i = 0; i < blogsJson.length(); i++) {
				JSONObject newsInfoLeftObject = blogsJson.getJSONObject(i);
				CornerstoneInfoMode newsInfo = new CornerstoneInfoMode();
				newsInfo.setAlbid(newsInfoLeftObject.isNull("albid") ? "" : newsInfoLeftObject.getString("albid"));
				newsInfo.setIsrc(newsInfoLeftObject.isNull("isrc") ? "" : newsInfoLeftObject.getString("isrc"));
				newsInfo.setMsg(newsInfoLeftObject.isNull("msg") ? "" : newsInfoLeftObject.getString("msg"));
				duitangs.add(newsInfo);
			}
		}
		return duitangs;
	}
	
}
