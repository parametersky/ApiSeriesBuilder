package com.ford.caseiterator.datastructure;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.util.JsonWriter;
import android.util.Log;

import com.ford.caseiterator.constants.ConfigConstants;
import com.ford.caseiterator.file.FileImplementer;

public class ApiWrapperList {
	
	private List<ApiWrapper> mApiList = null;
	private List<ActionHandler> mActionList = null;
	
	public ApiWrapperList(){
		mApiList = new ArrayList<ApiWrapper>();
		mActionList = new ArrayList<ActionHandler>();
	}
	public void addApi(ApiWrapper apiwrap){
		mApiList.add(apiwrap);
	}
	public void addAction(ActionHandler apiwrap){
		mActionList.add(apiwrap);
	}
	public ActionHandler getAction(int index){
		if(index >= mApiList.size())return null;
		return mActionList.get(index);
	}
	
	public ActionHandler getHandlerByTypeAndName(String type,String Name){
		Log.e("Kyle","type is "+type+", name is "+Name);
		if(mActionList.isEmpty()) return null;
		Iterator<ActionHandler> iterator = mActionList.iterator();
		while(iterator.hasNext()){
			ActionHandler handler = iterator.next();
			if ( type.equalsIgnoreCase(handler.getType()) && Name.equalsIgnoreCase(handler.getButtonName())){
				return handler;
			}
		}
		return null;
	}
	
	public ActionHandler getHandlerByTypeAndID(String type,int id){
		if(mActionList.isEmpty()) return null;
		Iterator<ActionHandler> iterator = mActionList.iterator();
		while(iterator.hasNext()){
			ActionHandler handler = iterator.next();
			if ( type.equalsIgnoreCase(handler.getType()) && id == handler.getID()){
				return handler;
			}
		}
		return null;
	}
	public ApiWrapper getApi(int index){
		if(index >= mApiList.size())return null;
		return mApiList.get(index);
	}
	public int getApiCount(){
		return mApiList.size();
	}
	public void clear(){
		mApiList.clear();
		mActionList.clear();
	}
	
//	public void writeToFile(String filename){
//		final FileImplementer fi = new FileImplementer(filename);
//		Writer writer = new Writer() {
//			
//			@Override
//			public void write(char[] buf, int offset, int count) throws IOException {
//				// TODO Auto-generated method stub
//				fi.writeString(buf.toString());
//			}
//			
//			@Override
//			public void flush() throws IOException {
//				// TODO Auto-generated method stub
//				
//			}
//			
//			@Override
//			public void close() throws IOException {
//				// TODO Auto-generated method stub
//				fi.close();
//			}
//		};
//		JsonWriter jw = new JsonWriter(writer);
//		try {
//			jw.beginObject();
//			jw.name(ConfigConstants.API_HEAD);
//			jw.beginArray();
//			for(int index = 0; index < mApiList.size();index ++){
//				jw.beginObject();\
//				jw.name(mApiList.get(index).getName());
//				
//			}
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//	}
}
