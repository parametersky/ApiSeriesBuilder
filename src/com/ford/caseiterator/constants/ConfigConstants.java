package com.ford.caseiterator.constants;

public class ConfigConstants {
	
//	{
//	    "apis": [
//	        {
//	            "name": "show",
//	            "parameters": {
//	                "mainField1": "this is a demo",
//	                "mainField2": "DEMO",
//	                "softButtons": [
//	                    {
//	                        "type": "TEXT",
//	                        "text": "Alert",
//	                        "isHighlighted": false,
//	                        "softButtonID": 101,
//	                        "systemAction": 1
//	                    }
//	                ]
//	            }
//	        }
//	    ]
//	,
//	"actions":
//		[
//		 {"trigger":"command",
//			 "identifier":1012,
//			 "handler":{
//			 "name":"alert",
//			 "parameters": {
//	            "softButtons": [
//	                            {
//	                                "softButtonID": 1022,
//	                                "image": {
//	                                    "value": "0xD0",
//	                                    "imageType": "DYNAMIC"
//	                                },
//	                                "text": "text",
//	                                "type": "BOTH",
//	                                "isHighlighted": false,
//	                                "systemAction": "DEFAULT_ACTION"
//	                            }
//	                        ],
//	                        "duration": 192,
//	                        "alertText1": "alert text1",
//	                        "playTone": false,
//	                        "alertText2": "alert text2",
//	                        "alertText3": "alert Text3",
//	                        "ttsChunks": [
//	                            {
//	                                "type": "TEXT",
//	                                "text": "this is a alert demo"
//	                            },
//	                            {
//	                                "type": "TEXT",
//	                                "text": "this is a alert demo2"
//	                            }
//	                        ]
//	                    },
//			 
//		 }
//		 
//		 }
//		 ]
//	}
//	
	public static final String API_HEAD = "apis";
	public static final String API_NAME = "name";
	public static final String API_PARAMETER = "parameters";
	public static final String API_RESPONSE_HANDLER = "response_handler";
	public static final String API_HANDLER_TYPE = "type";
	public static final String API_HANDLER_RPC="rpc";
	
	public static enum ACTION_TRIGGER_TYPE{
		BUTTON("button"),
		COMMAND("command"),
		CHOICE("choice");
		
		String mType;
		
		private ACTION_TRIGGER_TYPE(String type){
			mType = type;
		}
		public String toString(){
			return mType;
		}
	};
	
	public static final String ACTION_HEAD = "actions";
	public static final String ACTION_TRIGGER = "trigger";
	public static final String ACTION_TRIGGER_ID = "identifier";
	public static final String ACTION_TRIGGER_BTNNAME = "buttonname";
	public static final String ACTION_TRIGGER_COMMAND = "command";
	public static final String ACTION_TRIGGER_BUTTON = "button";
	public static final String ACTION_TRIGGER_CHOICE = "choice";
	public static final String ACTION_HANDLER = "handler";

	
}
