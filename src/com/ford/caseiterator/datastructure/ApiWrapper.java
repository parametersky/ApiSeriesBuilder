package com.ford.caseiterator.datastructure;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class ApiWrapper {
	private String mName = "";
	private Hashtable<String, Object> mParameters = null;
	private ActionHandler mHandler = null;

	public ApiWrapper(String name){
		mName = name;
		mParameters = null;
		mHandler = null;
	}
	public void setParameters(Hashtable<String,Object> hashtable){
		mParameters = hashtable;
	}
	public void setResponseHandler(ActionHandler handler){
		mHandler = handler;
	}
	public String getName(){
		return mName;
	}
	public Hashtable<String, Object> getParameters(){
		return mParameters;
	}
}
