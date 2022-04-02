package com.gu.galarm.listener;

import org.json.JSONObject;

public interface OnHttpResponseListener {
	void onSuccess(int code, String data);

	void onFailure(String message);
}



