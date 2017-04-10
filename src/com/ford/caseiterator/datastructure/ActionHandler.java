package com.ford.caseiterator.datastructure;

public class ActionHandler {
	private String mTriggerType = "";
	private int mIdentifier = -1;
	private String mButtonName = "";
	private ApiWrapper mHandler = null;

	public ActionHandler() {

	}

	public ActionHandler(int type) {
		// mType = type;

	}
	public void setType(String type){
		mTriggerType = type;
	}
	public String getType(){
		return mTriggerType;
	}
	public int getID(){
		return mIdentifier;
	}
	public void setID(int id){
		mIdentifier = id;
	}
	public String getButtonName(){
		return mButtonName;
	}
	public void setButtonName(String buttonname){
		mButtonName = buttonname;
	}
	public void setHandler(ApiWrapper handler) {
		mHandler = handler;
	}
	public ApiWrapper getHandler(){
		return mHandler;
	}
}
