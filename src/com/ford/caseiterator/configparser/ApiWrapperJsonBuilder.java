package com.ford.caseiterator.configparser;

import java.util.Hashtable;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ford.caseiterator.constants.ApiNames;
import com.ford.caseiterator.constants.ApiParameterLists;
import com.ford.caseiterator.constants.ConfigConstants;
import com.ford.caseiterator.datastructure.ApiWrapper;
import com.ford.caseiterator.datastructure.ActionHandler;
import com.ford.caseiterator.utils.Debug;
import com.ford.syncV4.proxy.RPCRequest;
import com.ford.syncV4.proxy.rpc.SoftButton;

public class ApiWrapperJsonBuilder {

	public static ApiWrapper buildApiFromJson(JSONObject json)
			throws JSONException {

		String apiname = json.getString(ConfigConstants.API_NAME);
		Debug.DebugLog("builder buildApiFromJson: api " + apiname);
		ApiWrapper apiwrapper = new ApiWrapper(apiname);
		Hashtable<String, Object> parameters = new Hashtable<String, Object>();

		JSONObject jsonpara = json.getJSONObject(ConfigConstants.API_PARAMETER);
		
		RPCRequest rr = new RPCRequest(apiname);
		rr.deserializeJSON(jsonpara);
		
		
//		if (apiname.equalsIgnoreCase(ApiNames.Show)) {
//			Debug.DebugLog("show is found");
//			for (String str : ApiParameterLists.SHOW_PARALIST) {
//				if (jsonpara.has(str)) {
//					if (str.equalsIgnoreCase(ApiNames.softButtons)) {
//						JSONArray softbuttonjson = jsonpara.getJSONArray(ApiNames.softButtons);
//						parameters.put(str,buildSoftButtonsFromJson(softbuttonjson));
//					} else {
//						String value = jsonpara.getString(str);
//						if (value != null) {
//							parameters.put(str, value);
//							
//						}
//					}
//				}
//			}
//		} else if (apiname.equalsIgnoreCase(ApiNames.Alert)) {
//			Debug.DebugLog("Alert found");
//		} else {
//			Debug.DebugLog("cannot foudn apis");
//			return null;
//		}
		apiwrapper.setParameters(rr.deserializeJSONObject(jsonpara));
//		if (json.has(ConfigConstants.API_RESPONSE_HANDLER)) {
//			JSONObject jsonresp = json
//					.getJSONObject(ConfigConstants.API_RESPONSE_HANDLER);
//			if (jsonresp.has(ConfigConstants.API_HANDLER_TYPE)) {
//				String type = jsonresp
//						.getString(ConfigConstants.API_HANDLER_TYPE);
//				ActionHandler reh = new ActionHandler(
//						ActionHandler.RESPONSE_TYPE_RPC);
//				reh.setHandler(buildApiFromJson(jsonresp
//						.getJSONObject(ConfigConstants.API_HANDLER_RPC)));
//				apiwrapper.setResponseHandler(reh);
//			}
//		}
		return apiwrapper;
	}
	
	public static Vector<SoftButton> buildSoftButtonsFromJson(JSONArray json) throws JSONException{
		Vector<SoftButton> softbuttons = new Vector<SoftButton>();
		int length = json.length();
		for (int i = 0; i < length; i++){
			JSONObject softbuttonjson = json.getJSONObject(i);
			SoftButton softbutton = new SoftButton();
			softbutton.deserializeJSON(softbuttonjson);
			softbuttons.add(softbutton);
		}
		return softbuttons;
	}

}
