package com.ford.caseiterator.configparser;

import com.ford.caseiterator.datastructure.ApiWrapperList;

public abstract class ConfigParser {
	
	public static int CONFIG_TYPE_JSON = 101;
	public ConfigParser(){
		
	}
	public abstract ApiWrapperList getApiWrapperList();
	public abstract int parseConfig(String content);
}
