package com.ford.caseiterator;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Set;
import java.util.Vector;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Profile;
import android.util.Log;
//import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import com.ford.caseiterator.configparser.ConfigParser;
import com.ford.caseiterator.configparser.JsonConfigParser;
import com.ford.caseiterator.constants.ApiNames;
import com.ford.caseiterator.constants.ConfigConstants;
import com.ford.caseiterator.datastructure.ActionHandler;
import com.ford.caseiterator.datastructure.ApiWrapper;
import com.ford.caseiterator.datastructure.ApiWrapperList;
import com.ford.caseiterator.file.FileImplementer;
import com.ford.caseiterator.utils.Debug;
import com.ford.syncV4.exception.SyncException;
import com.ford.syncV4.exception.SyncExceptionCause;
import com.ford.syncV4.proxy.RPCMessage;
import com.ford.syncV4.proxy.RPCRequest;
import com.ford.syncV4.proxy.SyncProxyALM;
import com.ford.syncV4.proxy.TTSChunkFactory;
import com.ford.syncV4.proxy.interfaces.IProxyListenerALM;
import com.ford.syncV4.proxy.rpc.*;
import com.ford.syncV4.proxy.rpc.enums.AppHMIType;
import com.ford.syncV4.proxy.rpc.enums.AppInterfaceUnregisteredReason;
import com.ford.syncV4.proxy.rpc.enums.AudioType;
import com.ford.syncV4.proxy.rpc.enums.BitsPerSample;
import com.ford.syncV4.proxy.rpc.enums.ButtonEventMode;
import com.ford.syncV4.proxy.rpc.enums.ButtonName;
import com.ford.syncV4.proxy.rpc.enums.ButtonPressMode;
import com.ford.syncV4.proxy.rpc.enums.DriverDistractionState;
import com.ford.syncV4.proxy.rpc.enums.HMILevel;
import com.ford.syncV4.proxy.rpc.enums.ImageType;
import com.ford.syncV4.proxy.rpc.enums.InteractionMode;
import com.ford.syncV4.proxy.rpc.enums.Language;
import com.ford.syncV4.proxy.rpc.enums.Result;
import com.ford.syncV4.proxy.rpc.enums.SamplingRate;
import com.ford.syncV4.proxy.rpc.enums.SoftButtonType;
import com.ford.syncV4.proxy.rpc.enums.SpeechCapabilities;
import com.ford.syncV4.proxy.rpc.enums.SystemAction;
import com.ford.syncV4.proxy.rpc.enums.TextAlignment;
import com.ford.syncV4.transport.BTTransportConfig;
import com.ford.syncV4.transport.BaseTransportConfig;
import com.ford.syncV4.transport.TCPTransportConfig;
import com.ford.syncV4.util.DebugTool;

public class FordService extends Service implements IProxyListenerALM {

	private static FordService instance = null;
	private SyncProxyALM mSyncProxy = null;
	private final String TAG = "FordService";
	private ApiWrapperList mAPIWrapperList = null;
	private ConfigParser mConfigParser;
	private int correlationID = 1;
	private HMILevel hmilevel = null;

	private BroadcastReceiver mBR = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if (action
					.equalsIgnoreCase(CaseIteratorActivity.ACTION_CONFIG_PATH)) {
				String config_path = intent
						.getStringExtra(CaseIteratorActivity.CONFIG_PATH);
				if(mAPIWrapperList!=null){
					mAPIWrapperList.clear();
				}
				buildApiListFromConfig(config_path);
				initRPCAfterConfigParser();
			}
		}
	};
	private CaseIteratorActivity mActivity = null;

	public static FordService getInstance() {
		return instance;
	}

	public SyncProxyALM getProxy() {
		return mSyncProxy;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "onCreate");
		instance = this;
		IntentFilter intentfilter = new IntentFilter();
		intentfilter.addAction(CaseIteratorActivity.ACTION_CONFIG_PATH);
		registerReceiver(mBR, intentfilter);
		

	}

	@Override
	public int onStartCommand(Intent intent, int flag, int startId) {
		Log.d(TAG, "onStartCommand");
		String configpath = intent.getStringExtra("config_path");
		// mActivity = CaseIteratorActivity.getInstance();

		mConfigParser = createConfigParser();
		if (configpath != null) {
			buildApiListFromConfig(configpath);
			startProxy();
		} else {
			Toast.makeText(this, "No Config File found", Toast.LENGTH_LONG)
					.show();
		}
		return 0;
	}

	public ConfigParser createConfigParser() {
		return new JsonConfigParser();
	}

	public void buildApiListFromConfig(String filepath) {
//		if (mAPIWrapperList != null) {
//			mAPIWrapperList.clear();
//		}
		FileImplementer fi = new FileImplementer(filepath);
		String configs = fi.getContent();
		Log.d(TAG, "configs is " + configs);
		if (configs.length() < 1)
			return;
		int result = mConfigParser.parseConfig(configs);
		if (result < 0) {
			Toast.makeText(this, "Config Parse Error", Toast.LENGTH_LONG)
					.show();
			mAPIWrapperList = new ApiWrapperList();
			return;
		}
		mAPIWrapperList = mConfigParser.getApiWrapperList();
		Debug.DebugLog("ApiWrapperList length is "
				+ mAPIWrapperList.getApiCount());

	}

	public RPCRequest buildApiFromApiWrapper(ApiWrapper apiwrap) {
		String apiname = apiwrap.getName();
		RPCRequest rpc = null;
		Hashtable<String, Object> parameters = apiwrap.getParameters();
		rpc = new RPCRequest(apiname);
		Set<String> keys = parameters.keySet();
		for (String key : keys) {
			rpc.setParameters(key, parameters.get(key));
		}

		return rpc;
	}

	public static String _FUNC_() {
		StackTraceElement traceElement = ((new Exception()).getStackTrace())[1];
		return traceElement.getMethodName() + "\n";
	}

	public void startProxy() {
		try {
			Log.d(TAG, "onStartCommand to connect with SYNC using SyncProxyALM");

			mSyncProxy = new SyncProxyALM(this, "ApisDemo", true,
					Language.ZH_CN, Language.ZH_CN, "1234566799080");

		} catch (SyncException e) {
			// TODO Auto-generated catch block
			Log.d("Kyle", e.getMessage());
			if (mSyncProxy == null)
				stopSelf();
			e.printStackTrace();
		}
	}

	public void onDestroy() {
		instance = null;
		try {
			if (mSyncProxy != null)
				mSyncProxy.dispose();
			mSyncProxy = null;
		} catch (SyncException e) {
			// TODO Auto-generated catch block
			Log.e(TAG, "dispose mSyncProxy failed");
			e.printStackTrace();
		}
		unregisterReceiver(mBR);
		super.onDestroy();
	}

	public void initRPCAfterConfigParser() {
		if (hmilevel != HMILevel.HMI_FULL) {
			Toast.makeText(this, "HMI is not FULL", Toast.LENGTH_LONG).show();
			return;
		}
		for (int i = 0; i < mAPIWrapperList.getApiCount(); i++) {
			RPCRequest rpc = buildApiFromApiWrapper(mAPIWrapperList.getApi(i));
			rpc.setCorrelationID(correlationID++);
			try {
				mSyncProxy.sendRPCRequest(rpc);
			} catch (SyncException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onOnHMIStatus(OnHMIStatus notification) {
		// TODO Auto-generated method stub
		// mActivity.addLogToPanel(this._FUNC_());
		hmilevel = notification.getHmiLevel();
		switch (hmilevel) {
		case HMI_BACKGROUND:
			Log.d("Kyle", "HMI_BACKGOUND");
			// buildCommands();
			break;
		case HMI_FULL:
			Log.d("Kyle", "HMI_FULL");
			if (notification.getFirstRun()) {
				initRPCAfterConfigParser();
			}
			break;
		case HMI_NONE:
			Log.d("Kyle", "HMI_NONE");
		case HMI_LIMITED:
			Log.d("Kyle", "HMI_LIMITED");
		default:
			break;
		}
	}

	@Override
	public void onProxyClosed(String info, Exception e) {
		// TODO Auto-generated method stub
		Log.d("Kyle", "onProxyClosed");
		if (mSyncProxy != null) {
			try {
				mSyncProxy.resetProxy();
			} catch (SyncException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		// stopSelf();
	}

	public static String FormatStackTrace(Throwable throwable) {
		if (throwable == null)
			return "";
		String rtn = throwable.getStackTrace().toString();
		try {
			Writer writer = new StringWriter();
			PrintWriter printWriter = new PrintWriter(writer);
			throwable.printStackTrace(printWriter);
			printWriter.flush();
			writer.flush();
			rtn = writer.toString();
			printWriter.close();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception ex) {
		}
		return rtn + "\n";
	}

	@Override
	public void onAddCommandResponse(AddCommandResponse response) {
		// TODO Auto-generated method stub
		Log.d("Kyle", "Add command done for " + response.getCorrelationID()
				+ " result is " + response.getResultCode());
	}

	@Override
	public void onAddSubMenuResponse(AddSubMenuResponse response) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCreateInteractionChoiceSetResponse(
			CreateInteractionChoiceSetResponse response) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAlertResponse(AlertResponse response) {
		// TODO Auto-generated method stub
		Debug.DebugLog("onShowResponse " + response.getInfo() + " "
				+ response.getResultCode());
	}

	@Override
	public void onDeleteCommandResponse(DeleteCommandResponse response) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDeleteInteractionChoiceSetResponse(
			DeleteInteractionChoiceSetResponse response) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDeleteSubMenuResponse(DeleteSubMenuResponse response) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGenericResponse(GenericResponse response) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onOnCommand(OnCommand notification) {
		// TODO Auto-generated method stub
		// ms.onVoiceCommand(notification, correlationID++);
		int id = notification.getCmdID();
		ActionHandler apiwrap = mAPIWrapperList.getHandlerByTypeAndID(
				ConfigConstants.ACTION_TRIGGER_COMMAND, id);
		if (apiwrap != null) {
			RPCRequest rr = buildApiFromApiWrapper(apiwrap.getHandler());
			rr.setCorrelationID(correlationID++);
			try {
				mSyncProxy.sendRPCRequest(rr);
			} catch (SyncException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onPerformInteractionResponse(PerformInteractionResponse response) {
		// TODO Auto-generated method stub

		Log.d("Kyle", "response info is " + response.getInfo() + " code is "
				+ response.getResultCode());
		if (response.getSuccess()) {
			int choiceid = response.getChoiceID();
			ActionHandler apiwrap = mAPIWrapperList.getHandlerByTypeAndID(
					ConfigConstants.ACTION_TRIGGER_CHOICE, choiceid);
			if (apiwrap != null) {
				RPCRequest rr = buildApiFromApiWrapper(apiwrap.getHandler());
				rr.setCorrelationID(correlationID++);
				try {
					mSyncProxy.sendRPCRequest(rr);
				} catch (SyncException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		// ms.onPerformInteractionResponse(response);
	}

	@Override
	public void onResetGlobalPropertiesResponse(
			ResetGlobalPropertiesResponse response) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSetGlobalPropertiesResponse(
			SetGlobalPropertiesResponse response) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSetMediaClockTimerResponse(SetMediaClockTimerResponse response) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onShowResponse(ShowResponse response) {
		// TODO Auto-generated method stub
		Debug.DebugLog("onShowResponse " + response.getInfo() + " "
				+ response.getResultCode());
	}

	@Override
	public void onSpeakResponse(SpeakResponse response) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onOnButtonEvent(OnButtonEvent notification) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onOnButtonPress(OnButtonPress notification) {
		// TODO Auto-generated method stub
		Debug.DebugLog("onButtonPress ButonName: "
				+ notification.getButtonName());
		ButtonName name = notification.getButtonName();
		if (name.equals(ButtonName.CUSTOM_BUTTON)) {
			int id = notification.getCustomButtonName();
			ActionHandler apiwrap = mAPIWrapperList.getHandlerByTypeAndID(
					ConfigConstants.ACTION_TRIGGER_BUTTON, id);
			if (apiwrap != null) {
				RPCRequest rr = buildApiFromApiWrapper(apiwrap.getHandler());
				rr.setCorrelationID(correlationID++);
				try {
					mSyncProxy.sendRPCRequest(rr);
				} catch (SyncException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else {
			Log.e("Kyle","name is "+name.name());
			ActionHandler apiwrap1 = mAPIWrapperList.getHandlerByTypeAndName(ConfigConstants.ACTION_TRIGGER_BUTTON, name.name());
			if (apiwrap1 != null) {
				RPCRequest rr1 = buildApiFromApiWrapper(apiwrap1.getHandler());
				rr1.setCorrelationID(correlationID++);
				try {
					mSyncProxy.sendRPCRequest(rr1);
				} catch (SyncException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				Log.e("Kyle","found no api action");
			}
		}
	}

	@Override
	public void onSubscribeButtonResponse(SubscribeButtonResponse response) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUnsubscribeButtonResponse(UnsubscribeButtonResponse response) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onOnPermissionsChange(OnPermissionsChange notification) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onOnDriverDistraction(OnDriverDistraction arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSubscribeVehicleDataResponse(
			SubscribeVehicleDataResponse response) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUnsubscribeVehicleDataResponse(
			UnsubscribeVehicleDataResponse response) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGetVehicleDataResponse(GetVehicleDataResponse response) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onReadDIDResponse(ReadDIDResponse response) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGetDTCsResponse(GetDTCsResponse response) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onOnVehicleData(OnVehicleData notification) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPerformAudioPassThruResponse(
			PerformAudioPassThruResponse response) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onPerformAudioPassThru response: " + response.getInfo());

	}

	@Override
	public void onEndAudioPassThruResponse(EndAudioPassThruResponse response) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onOnAudioPassThru(OnAudioPassThru notification) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPutFileResponse(PutFileResponse response) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDeleteFileResponse(DeleteFileResponse response) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onListFilesResponse(ListFilesResponse response) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSetAppIconResponse(SetAppIconResponse response) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onScrollableMessageResponse(ScrollableMessageResponse response) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onChangeRegistrationResponse(ChangeRegistrationResponse response) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSetDisplayLayoutResponse(SetDisplayLayoutResponse response) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onOnLanguageChange(OnLanguageChange notification) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSliderResponse(SliderResponse response) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDialNumberResponse(DialNumberResponse arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onError(String arg0, Exception arg1) {
		// TODO Auto-generated method stub

	}

}