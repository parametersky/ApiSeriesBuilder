package com.ford.caseiterator.configparser;

public class ConfigParserFactory {
	
	private static ConfigParserFactory mFactory = null;
	private ConfigParserFactory(){
		
	}
	public static ConfigParserFactory getConfigParserFactory(){
		if(mFactory == null){
			mFactory = new ConfigParserFactory();
		}
		return mFactory;
	}
//	public static ConfigParser createConfigParser(){
//		return new ConfigParser();
//	}
}
