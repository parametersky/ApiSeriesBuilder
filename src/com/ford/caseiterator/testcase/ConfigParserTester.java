package com.ford.caseiterator.testcase;

import java.util.Hashtable;
import java.util.Set;


import com.ford.caseiterator.configparser.ConfigParser;
import com.ford.caseiterator.configparser.JsonConfigParser;
import com.ford.caseiterator.constants.ApiNames;
import com.ford.caseiterator.datastructure.ApiWrapper;
import com.ford.caseiterator.datastructure.ApiWrapperList;
import com.ford.caseiterator.file.FileImplementer;
import com.ford.syncV4.proxy.RPCRequest;
import com.ford.syncV4.proxy.rpc.Show;

import junit.framework.TestCase;

public class ConfigParserTester extends TestCase {
	private static ApiWrapperList awl = null;
	private static String testString;
	public static void main(){
//		testString = "{\"api\":[{\"name\": \"show\",\"parameters\": {\"mainField1\": \"this is a demo\",\"mainField2\": \"DEMO\"}}]}";
//		FileImplementer fil = new FileImplementer("config");
//		fil.cleanContent();
//		fil.writeString(testString);
//		fil.close();
		
		buildApiListFromConfig("config");
		RPCRequest show = buildApiFromApiWrapper(awl.getApi(0));
	}

	public static void buildApiListFromConfig(String filepath) {
		FileImplementer fi = new FileImplementer(filepath);
		String configs = fi.getContent();
//		assertEquals(testString, configs);
		System.out.print(configs);
		if (configs.length() < 1)
			return;
		ConfigParser jsonparser = new JsonConfigParser();
		jsonparser.parseConfig(configs);
		awl = jsonparser.getApiWrapperList();

	}

	public static RPCRequest buildApiFromApiWrapper(ApiWrapper apiwrap) {
		String apiname = apiwrap.getName();
		RPCRequest rpc = null;
		Hashtable<String, Object> parameters = apiwrap.getParameters();
		if (apiname.equalsIgnoreCase(ApiNames.Show)) {
			rpc = new Show();
			Set<String> keys = parameters.keySet();
			for (String key : keys) {
				rpc.setParameters(key, parameters.get(key));
			}
		}
		return rpc;
	}
	
	public void testMain(){
		main();
	}
}
