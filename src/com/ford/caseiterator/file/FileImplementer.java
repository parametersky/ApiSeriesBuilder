package com.ford.caseiterator.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class FileImplementer {
	private String 			mFilePath;
	private OutputStream	mOS;
	private InputStream		mIS;
	
	public FileImplementer(String path){
		mFilePath = path;
		try {
			mOS = new FileOutputStream(mFilePath, true);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public String getPath(){
		return mFilePath;
	}
	
	public void writeString(String str){
		String newstr = str+'\n';
		try {
			mOS.write(newstr.getBytes());
			mOS.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.print("IOException found");
			e.printStackTrace();
		}
	}
	
	public void writeStringArray(ArrayList<String> strlist){
		for (String str:strlist){
			writeString(str);
		}
	}
	public String getContent(){
		byte[] content = new byte[1024];
		StringBuilder strbuilder = new StringBuilder();
		try {
			mIS = new FileInputStream(mFilePath);
			int count = 0;
			while((count = mIS.read(content)) > 0){
				strbuilder.append(new String(content,"UTF-8"));
			}
			mIS.close();
			return strbuilder.toString();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e){
			e.printStackTrace();
		}
		return "";
		
	}
	public void close(){
		try {
			mOS.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void cleanContent(){
		try {
			mOS.close();
			mOS = null;
			
			new File(mFilePath).delete();
			mOS = new FileOutputStream(mFilePath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
