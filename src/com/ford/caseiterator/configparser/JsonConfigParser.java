package com.ford.caseiterator.configparser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ford.caseiterator.constants.ConfigConstants;
import com.ford.caseiterator.datastructure.ActionHandler;
import com.ford.caseiterator.datastructure.ApiWrapper;
import com.ford.caseiterator.datastructure.ApiWrapperList;
import com.ford.caseiterator.utils.Debug;

public class JsonConfigParser extends ConfigParser {

	private ApiWrapperList mApilist = null;
	private JSONObject mJsonObj = null;

	public JsonConfigParser() {
		mApilist = new ApiWrapperList();
	}

	@Override
	public int parseConfig(String content) {
		// TODO Auto-generated method stub
		try {
			Debug.DebugLog("parseCOnfig begin:");
			mJsonObj = new JSONObject(content);
			JSONArray apilist = mJsonObj.getJSONArray(ConfigConstants.API_HEAD);
			int len = apilist.length();
			Debug.DebugLog("apilist's length is " + len);
			for (int i = 0; i < len; i++) {
				JSONObject jsonapi = apilist.getJSONObject(i);

				ApiWrapper api = ApiWrapperJsonBuilder
						.buildApiFromJson(jsonapi);

				if (api != null) {
					mApilist.addApi(api);
				}

			}
			if (mJsonObj.has(ConfigConstants.ACTION_HEAD)) {
				JSONArray actionlist = mJsonObj
						.getJSONArray(ConfigConstants.ACTION_HEAD);
				int alen = actionlist.length();
				for (int j = 0; j < alen; j++) {
					JSONObject jsonaction = actionlist.getJSONObject(j);
					ActionHandler ah = new ActionHandler();
					ah.setType(jsonaction
							.getString(ConfigConstants.ACTION_TRIGGER));
					if(jsonaction.has(ConfigConstants.ACTION_TRIGGER_ID)){
						ah.setID(jsonaction.getInt(ConfigConstants.ACTION_TRIGGER_ID));
					}
					if(jsonaction.has(ConfigConstants.ACTION_TRIGGER_BTNNAME)){
						ah.setButtonName(jsonaction.getString(ConfigConstants.ACTION_TRIGGER_BTNNAME));
					}
					if(jsonaction.has(ConfigConstants.ACTION_HANDLER)){
						ApiWrapper handler = ApiWrapperJsonBuilder.buildApiFromJson(jsonaction.getJSONObject(ConfigConstants.ACTION_HANDLER));
						ah.setHandler(handler);
						mApilist.addAction(ah);
					}
					
				}
			}
			return 0;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
	}

	public ApiWrapperList getApiWrapperList() {
		return mApilist;
	}

}
