package com.ford.caseiterator;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import org.json.JSONException;

import com.ford.caseiterator.configparser.ConfigParser;
import com.ford.caseiterator.configparser.JsonConfigParser;
import com.ford.caseiterator.datastructure.ActionHandler;
import com.ford.caseiterator.datastructure.ApiWrapperList;
import com.ford.caseiterator.file.FileImplementer;
import com.ford.caseiterator.utils.Debug;
import com.ford.syncV4.proxy.RPCRequest;
import com.ford.syncV4.proxy.TTSChunkFactory;
import com.ford.syncV4.proxy.constants.Names;
import com.ford.syncV4.proxy.rpc.AddCommand;
import com.ford.syncV4.proxy.rpc.Alert;
import com.ford.syncV4.proxy.rpc.Choice;
import com.ford.syncV4.proxy.rpc.CreateInteractionChoiceSet;
import com.ford.syncV4.proxy.rpc.Image;
import com.ford.syncV4.proxy.rpc.MenuParams;
import com.ford.syncV4.proxy.rpc.PerformInteraction;
import com.ford.syncV4.proxy.rpc.Show;
import com.ford.syncV4.proxy.rpc.SoftButton;
import com.ford.syncV4.proxy.rpc.SubscribeButton;
import com.ford.syncV4.proxy.rpc.TTSChunk;
import com.ford.syncV4.proxy.rpc.VrHelpItem;
import com.ford.syncV4.proxy.rpc.enums.ButtonName;
import com.ford.syncV4.proxy.rpc.enums.ImageType;
import com.ford.syncV4.proxy.rpc.enums.InteractionMode;
import com.ford.syncV4.proxy.rpc.enums.SoftButtonType;
import com.ford.syncV4.proxy.rpc.enums.SpeechCapabilities;
import com.ford.syncV4.proxy.rpc.enums.SystemAction;
import com.ford.syncV4.proxy.rpc.enums.TextAlignment;
import com.lamerman.FileDialog;
import com.lamerman.SelectionMode;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.util.JsonWriter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

public class CaseIteratorActivity extends Activity {

	private ApiWrapperList awl = null;
	public static final String JOIN_STRING = ",";
	private String configpath = "/mnt/sdcard/config";
	public static final String INTENTHELPER_KEY_OBJECT = "IntentObject";
	public static final String INTENTHELPER_KEY_OBJECTSLIST = "IntentObjectsList";
	public static final String INTENTHELPER_KEY_KEYBOARDPROPERTIES = "IntentKeyboardProperties";
	public static final String INTENTHELPER_KEY_KEYBOARDPROPERTIES_EMPTY = "IntentKeyboardPropertiesEmpty";
	public static final String INTENT_KEY_OBJECTS_MAXNUMBER = "MaxObjectsNumber";
	private static final int SHOW_MAXSOFTBUTTONS = 8;

	private Button mBtnAddConfig = null;
	private Button mBtnSelectConfig = null;
	private LayoutInflater mLayoutInflator = null;

	private List<RPCRequest> mRpcList = null;
	private List<ActionHandler> mActionList = null;

	ArrayAdapter<String> mRPCAdapter = null;
	AlertDialog.Builder mAddRpcBuilder = null;

	private ArrayAdapter<ImageType> imageTypeAdapter;
	private Vector<SoftButton> currentSoftButtons;
	private CheckBox chkIncludeSoftButtons;
	private static int autoIncSoftButtonId = 101;

	// Request id for SoftButtonsListActivity
	static final int REQUEST_LIST_SOFTBUTTONS = 43;
	// Request id for ChoiceListActivity
	static final int REQUEST_LIST_CHOICES = 45;
	
	static final int REQUEST_OPEN_CONFIG = 46;

	static final String ACTION_CONFIG_PATH = "com.ford.caseiterator.CONFIG_PATH";
	static final String CONFIG_PATH = "CONFIG_PATH";

	private void addToFunctionsAdapter(ArrayAdapter<String> adapter,
			String functionName) {
		adapter.add(functionName);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_case_iterator);
		buildRPCList();
		Log.d("KYLETAG", "Can you see this log message");
		mLayoutInflator = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		mBtnSelectConfig = (Button) findViewById(R.id.SelectConfig);
		mBtnSelectConfig.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(CaseIteratorActivity.this,
						FileDialog.class);
				intent.putExtra(FileDialog.START_PATH, "/sdcard");
				intent.putExtra(FileDialog.CAN_SELECT_DIR, false);
				intent.putExtra(FileDialog.SELECTION_MODE,
						SelectionMode.MODE_OPEN);
				startActivityForResult(intent, REQUEST_OPEN_CONFIG);
			}
		});
		mBtnAddConfig = (Button) findViewById(R.id.AddConfig);
		mBtnAddConfig.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//
				// AlertDialog.Builder(this)).setTitle("�Զ��岼��").setView(layout)
				// ���� .setPositiveButton("ȷ��", null)
				// ���� .setNegativeButton("ȡ��", null).show();
				View view = mLayoutInflator.inflate(R.layout.config_editor,
						null);
				Button btnAddRpc = (Button) view.findViewById(R.id.addRpc);
				btnAddRpc.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						mAddRpcBuilder.show();
					}
				});
				AlertDialog.Builder builder = new Builder(getContext());
				builder.setView(view);
				builder.setTitle(R.string.AddConfigDialogTitle);
				builder.setPositiveButton(R.string.Finish,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub

							}
						});
				builder.show();
			}

		});

		Intent intent = new Intent();
		intent.setClass(this, FordService.class);
		intent.putExtra("config_path", configpath);
		startService(intent);

		// buildNewConfig();
		// // buildApiListFromConfig(configpath);
		// Show show = new Show();
		// show.setMainField1("mainfield1");
		// show.setMainField2("mainField2");
		// show.setMainField3("mainfield3");
		// show.setMainField4("mainfield4");
		// show.setMediaTrack("mediatrack");
		// show.setStatusBar("statusbar");
		// Image grahpic = new Image();
		// grahpic.setImageType(ImageType.DYNAMIC);
		// grahpic.setValue("0xD0");
		// show.setGraphic(grahpic);
		// Vector<String> customPresets = new Vector<String>();
		// customPresets.add("custom preset 1");
		// customPresets.add("custom preset 2");
		// show.setCustomPresets(customPresets);
		//
		// show.setAlignment(TextAlignment.CENTERED);
		// show.setMediaClock("12:13");
		// show.setCorrelationID(101);
		//
		// SoftButton softbutton = new SoftButton();
		// softbutton.setImage(grahpic);
		// softbutton.setIsHighlighted(false);
		// softbutton.setText("text");
		// softbutton.setType(SoftButtonType.SBT_TEXT);
		// softbutton.setSoftButtonID(1022);
		// softbutton.setSystemAction(SystemAction.DEFAULT_ACTION);
		//
		// Vector<SoftButton> softButtons = new Vector<SoftButton>();
		// softButtons.add(softbutton);
		// show.setSoftButtons(softButtons);
		//
		// Alert alert = new Alert();
		// alert.setAlertText1("alert text1");
		// alert.setAlertText2("alert text2");
		// alert.setAlertText3("alert Text3");
		// alert.setDuration(192);
		// alert.setPlayTone(false);
		// alert.setSoftButtons(softButtons);
		// Vector<TTSChunk> ttsChunks = new Vector<TTSChunk>();
		// TTSChunk ttschunk = TTSChunkFactory.createChunk(
		// SpeechCapabilities.TEXT, "this is a alert demo");
		// TTSChunk ttschunk1 = TTSChunkFactory.createChunk(
		// SpeechCapabilities.TEXT, "this is a alert demo2");
		// ttsChunks.add(ttschunk);
		// ttsChunks.add(ttschunk1);
		// alert.setTtsChunks(ttsChunks);
		//
		// CreateInteractionChoiceSet rpc = new CreateInteractionChoiceSet();
		// Vector<Choice> choices = new Vector<Choice>();
		// Choice choice1 = new Choice();
		// choice1.setChoiceID(1023);
		// Image image = new Image();
		// image.setValue("0x47");
		// image.setImageType(ImageType.STATIC);
		// choice1.setImage(image);
		// choice1.setMenuName("This is a test");
		// choice1.setVrCommands(new Vector<String>(Arrays
		// .asList((new String[] { "this is a test" }))));
		// choices.add(choice1);
		//
		// Choice choice2 = new Choice();
		// choice2.setChoiceID(1024);
		// choice2.setImage(image);
		// choice2.setMenuName("Text Choice2");
		// choice2.setVrCommands(new Vector<String>(Arrays
		// .asList(new String[] { "Text Choice2" })));
		// choices.add(choice2);
		//
		// rpc.setChoiceSet(choices);
		// rpc.setCorrelationID(11111);
		// rpc.setInteractionChoiceSetID(1212);
		//
		//
		// PerformInteraction pi = new PerformInteraction();
		// pi.setCorrelationID(10111);
		// pi.setHelpPrompt(TTSChunkFactory.createSimpleTTSChunks("you can tell you want"));
		// pi.setInitialPrompt(TTSChunkFactory.createSimpleTTSChunks("Please select an album"));
		// pi.setInitialText("Please select an album");
		// pi.setInteractionChoiceSetIDList(new
		// Vector<Integer>(Arrays.asList(new Integer[]{1212})));
		// pi.setInteractionMode(InteractionMode.MANUAL_ONLY);
		// pi.setTimeout(20000);
		// pi.setTimeoutPrompt(TTSChunkFactory.createSimpleTTSChunks("Time is running out"));
		// VrHelpItem vrhelp = new VrHelpItem();
		// vrhelp.setPosition(1);
		// vrhelp.setImage(image);
		// vrhelp.setText("this is a test");
		// pi.setVrHelp(new Vector<VrHelpItem>(Arrays.asList(new
		// VrHelpItem[]{vrhelp})));
		//
		// AddCommand addcommand = new AddCommand();
		// addcommand.setCmdID(1011);
		// addcommand.setCmdIcon(image);
		// addcommand.setCorrelationID(10002);
		// MenuParams menuparams = new MenuParams();
		// menuparams.setMenuName("MenuName");
		// menuparams.setParentID(0);
		// menuparams.setPosition(1);
		// addcommand.setMenuParams(menuparams);
		// addcommand.setVrCommands(new Vector<String>(Arrays.asList(new
		// String[]{"MenuName"})));
		
		 FileImplementer file = new
		 FileImplementer("/mnt/sdcard/api_jsonfile");
		// try {
		// file.writeString(show.serializeJSON().toString() + "\n");
		// file.writeString(alert.serializeJSON().toString() + "\n");
		// file.writeString(rpc.serializeJSON().toString() + "\n");
		// file.writeString(pi.serializeJSON().toString()+"\n");
		// file.writeString(addcommand.serializeJSON().toString()+"\n");
		// } catch (JSONException e1) {
		// // TODO Auto-generated catch block
		// e1.printStackTrace();
		// }
		//
		// try {
		// Debug.DebugLog(show.serializeJSON().toString());
		// } catch (JSONException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		SubscribeButton sb = new SubscribeButton();
		sb.setButtonName(ButtonName.SEEKLEFT);
		sb.setCorrelationID(129);
		
		try {
			file.writeString(sb.serializeJSON().toString()+"\n");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	

	private Context getContext() {
		return this;
	}

	public void buildApiListFromConfig(String filepath) {
		FileImplementer fi = new FileImplementer(filepath);
		String configs = fi.getContent();
		Log.d("DEBUG", "configs is " + configs);
		if (configs.length() < 1)
			return;
		ConfigParser jsonparser = new JsonConfigParser();
		jsonparser.parseConfig(configs);
		awl = jsonparser.getApiWrapperList();
		Debug.DebugLog("ApiWrapperList length is " + awl.getApiCount());

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.case_iterator, menu);
		return true;
	}

	public void buildNewConfig() {

	}

	public void addRPC() {
	}

	public boolean getIsMedia() {
		return true;
	}

	public static int getNewSoftButtonId() {
		return autoIncSoftButtonId++;
	}

	public void buildShowDialog() {
		// View view = mLayoutInflator.inflate(R.layout.show, null);
		// AlertDialog.Builder builder = new Builder(getContext());
		// builder.setView(view);
		// builder.setTitle(R.string.AddConfigDialogTitle);
		// builder.setPositiveButton(R.string.Finish, new
		// DialogInterface.OnClickListener() {
		//
		// @Override
		// public void onClick(DialogInterface dialog, int which) {
		// // TODO Auto-generated method stub
		//
		// }
		// });
		// builder.show();
		//
		final Context mContext = getContext();
		LayoutInflater inflater = (LayoutInflater) mContext
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.show, null);

		final CheckBox mainField1Check = (CheckBox) layout
				.findViewById(R.id.show_mainField1Check);
		final EditText mainField1 = (EditText) layout
				.findViewById(R.id.show_mainField1);
		final CheckBox mainField2Check = (CheckBox) layout
				.findViewById(R.id.show_mainField2Check);
		final EditText mainField2 = (EditText) layout
				.findViewById(R.id.show_mainField2);
		final CheckBox mainField3Check = (CheckBox) layout
				.findViewById(R.id.show_mainField3Check);
		final EditText mainField3 = (EditText) layout
				.findViewById(R.id.show_mainField3);
		final CheckBox mainField4Check = (CheckBox) layout
				.findViewById(R.id.show_mainField4Check);
		final EditText mainField4 = (EditText) layout
				.findViewById(R.id.show_mainField4);
		final CheckBox textAlignmentCheck = (CheckBox) layout
				.findViewById(R.id.show_textAlignmentCheck);
		final Spinner textAlignmentSpinner = (Spinner) layout
				.findViewById(R.id.show_textAlignmentSpinner);
		final CheckBox statusBarCheck = (CheckBox) layout
				.findViewById(R.id.show_statusBarCheck);
		final EditText statusBar = (EditText) layout
				.findViewById(R.id.show_statusBar);
		final CheckBox mediaClockCheck = (CheckBox) layout
				.findViewById(R.id.show_mediaClockCheck);
		final EditText mediaClock = (EditText) layout
				.findViewById(R.id.show_mediaClock);
		final CheckBox mediaTrackCheck = (CheckBox) layout
				.findViewById(R.id.show_mediaTrackCheck);
		final EditText mediaTrack = (EditText) layout
				.findViewById(R.id.show_mediaTrack);
		chkIncludeSoftButtons = (CheckBox) layout
				.findViewById(R.id.show_chkIncludeSBs);
		final Button softButtons = (Button) layout
				.findViewById(R.id.show_btnSoftButtons);
		final CheckBox customPresetsCheck = (CheckBox) layout
				.findViewById(R.id.show_customPresetsCheck);
		final EditText customPresets = (EditText) layout
				.findViewById(R.id.show_customPresets);

		final ArrayAdapter<TextAlignment> textAlignmentAdapter = new ArrayAdapter<TextAlignment>(
				mContext, android.R.layout.simple_spinner_item,
				TextAlignment.values());
		textAlignmentAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		textAlignmentSpinner.setAdapter(textAlignmentAdapter);
		textAlignmentSpinner.setSelection(textAlignmentAdapter
				.getPosition(TextAlignment.CENTERED));

		final boolean isMedia = getIsMedia();

		if (!isMedia) {
			int visibility = View.GONE;
			mediaClock.setVisibility(visibility);
			mediaTrack.setVisibility(visibility);
			mediaTrackCheck.setVisibility(visibility);
			mediaClockCheck.setVisibility(visibility);
		}

		// graphicType.setAdapter(imageTypeAdapter);
		// graphicType.setSelection(imageTypeAdapter
		// .getPosition(ImageType.DYNAMIC));
		// secondaryGraphicType.setAdapter(imageTypeAdapter);
		// secondaryGraphicType.setSelection(imageTypeAdapter
		// .getPosition(ImageType.DYNAMIC));

		SoftButton sb1 = new SoftButton();
		sb1.setSoftButtonID(CaseIteratorActivity.getNewSoftButtonId());
		sb1.setText("KeepContext");
		sb1.setType(SoftButtonType.SBT_TEXT);
		sb1.setIsHighlighted(false);
		sb1.setSystemAction(SystemAction.KEEP_CONTEXT);
		SoftButton sb2 = new SoftButton();
		sb2.setSoftButtonID(CaseIteratorActivity.getNewSoftButtonId());
		sb2.setText("StealFocus");
		sb2.setType(SoftButtonType.SBT_TEXT);
		sb2.setIsHighlighted(false);
		sb2.setSystemAction(SystemAction.STEAL_FOCUS);
		SoftButton sb3 = new SoftButton();
		sb3.setSoftButtonID(CaseIteratorActivity.getNewSoftButtonId());
		sb3.setText("Default");
		sb3.setType(SoftButtonType.SBT_TEXT);
		sb3.setIsHighlighted(false);
		sb3.setSystemAction(SystemAction.DEFAULT_ACTION);
		ArrayList<SoftButton> button = new ArrayList<SoftButton>();
		button.add(sb1);
		button.add(sb2);
		button.add(sb3);
		IntentHelper.addObjectForKey(button, "ALL_SOFTBUTTONS");

		currentSoftButtons = new Vector<SoftButton>();
		currentSoftButtons.add(sb1);
		currentSoftButtons.add(sb2);
		currentSoftButtons.add(sb3);

		Button btnSoftButtons = (Button) layout
				.findViewById(R.id.show_btnSoftButtons);
		btnSoftButtons.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, SoftButtonList.class);
				startActivityForResult(intent, REQUEST_LIST_SOFTBUTTONS);
			}
		});

		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				Show msg = new Show();
				// msg.setCorrelationID(getCorrelationid());

				if (mainField1Check.isChecked()) {
					msg.setMainField1(mainField1.getText().toString());
				}
				if (mainField2Check.isChecked()) {
					msg.setMainField2(mainField2.getText().toString());
				}
				if (mainField3Check.isChecked()) {
					msg.setMainField3(mainField3.getText().toString());
				}
				if (mainField4Check.isChecked()) {
					msg.setMainField4(mainField4.getText().toString());
				}
				if (textAlignmentCheck.isChecked()) {
					msg.setAlignment(textAlignmentAdapter
							.getItem(textAlignmentSpinner
									.getSelectedItemPosition()));
				}
				if (statusBarCheck.isChecked()) {
					msg.setStatusBar(statusBar.getText().toString());
				}
				if (isMedia) {
					if (mediaClockCheck.isChecked()) {
						msg.setMediaClock(mediaClock.getText().toString());
					}
					if (mediaTrackCheck.isChecked()) {
						msg.setMediaTrack(mediaTrack.getText().toString());
					}
				}
				// if (graphicCheck.isChecked()) {
				// Image image = new Image();
				// image.setImageType((ImageType) graphicType
				// .getSelectedItem());
				// image.setValue(graphic.getText().toString());
				// msg.setGraphic(image);
				// }
				// uncomment this block when the proxy supports this feature
				/*
				 * if (secondaryGraphicCheck.isChecked()) { Image image = new
				 * Image(); image.setImageType((ImageType) secondaryGraphicType
				 * .getSelectedItem());
				 * image.setValue(secondaryGraphic.getText().toString());
				 * msg.setSecondaryGraphic(image); }
				 */
				if (chkIncludeSoftButtons.isChecked()
						&& (currentSoftButtons != null)
						&& (currentSoftButtons.size() > 0)) {
					msg.setSoftButtons(currentSoftButtons);
				}
				currentSoftButtons = null;
				chkIncludeSoftButtons = null;
				if (customPresetsCheck.isChecked()) {
					String[] customPresetsList = customPresets.getText()
							.toString().split(JOIN_STRING);
					msg.setCustomPresets(new Vector<String>(Arrays
							.asList(customPresetsList)));
				}
			}
		});
		builder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						currentSoftButtons = null;
						chkIncludeSoftButtons = null;
						dialog.cancel();
					}
				});
		builder.setView(layout);
		builder.show();
	}

	private void buildRPCList() {
		mRPCAdapter = new ArrayAdapter<String>(getContext(),
				android.R.layout.select_dialog_item);
		addToFunctionsAdapter(mRPCAdapter, Names.Alert);
		addToFunctionsAdapter(mRPCAdapter, Names.Speak);
		addToFunctionsAdapter(mRPCAdapter, Names.Show);
		addToFunctionsAdapter(mRPCAdapter, Names.SubscribeButton);
		addToFunctionsAdapter(mRPCAdapter, Names.AddCommand);
		addToFunctionsAdapter(mRPCAdapter, Names.DeleteCommand);
		addToFunctionsAdapter(mRPCAdapter, Names.AddSubMenu);
		addToFunctionsAdapter(mRPCAdapter, Names.DeleteSubMenu);
		addToFunctionsAdapter(mRPCAdapter, Names.SetGlobalProperties);
		addToFunctionsAdapter(mRPCAdapter, Names.ResetGlobalProperties);
		addToFunctionsAdapter(mRPCAdapter, Names.SetMediaClockTimer);
		addToFunctionsAdapter(mRPCAdapter, Names.CreateInteractionChoiceSet);
		addToFunctionsAdapter(mRPCAdapter, Names.DeleteInteractionChoiceSet);
		addToFunctionsAdapter(mRPCAdapter, Names.PerformInteraction);
		addToFunctionsAdapter(mRPCAdapter, Names.EncodedSyncPData);
		addToFunctionsAdapter(mRPCAdapter, Names.SyncPData);
		addToFunctionsAdapter(mRPCAdapter, Names.Slider);
		addToFunctionsAdapter(mRPCAdapter, Names.ScrollableMessage);
		addToFunctionsAdapter(mRPCAdapter, Names.ChangeRegistration);
		addToFunctionsAdapter(mRPCAdapter, Names.PutFile);
		addToFunctionsAdapter(mRPCAdapter, Names.DeleteFile);
		addToFunctionsAdapter(mRPCAdapter, Names.ListFiles);
		addToFunctionsAdapter(mRPCAdapter, Names.SetAppIcon);
		addToFunctionsAdapter(mRPCAdapter, Names.PerformAudioPassThru);
		addToFunctionsAdapter(mRPCAdapter, Names.EndAudioPassThru);
		addToFunctionsAdapter(mRPCAdapter, Names.SubscribeVehicleData);
		addToFunctionsAdapter(mRPCAdapter, Names.GetVehicleData);
		addToFunctionsAdapter(mRPCAdapter, Names.ReadDID);
		addToFunctionsAdapter(mRPCAdapter, Names.GetDTCs);
		addToFunctionsAdapter(mRPCAdapter, Names.ShowConstantTBT);
		addToFunctionsAdapter(mRPCAdapter, Names.UpdateTurnList);
		addToFunctionsAdapter(mRPCAdapter, Names.SetDisplayLayout);
		addToFunctionsAdapter(mRPCAdapter, Names.RegisterAppInterface);
		addToFunctionsAdapter(mRPCAdapter, Names.UnregisterAppInterface);
		mAddRpcBuilder = new AlertDialog.Builder(getContext());
		mAddRpcBuilder.setAdapter(mRPCAdapter, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				if (Names.Show.equalsIgnoreCase(mRPCAdapter.getItem(which))) {
					buildShowDialog();
				}
			}
		});
		// mAddRpcBuilder.show();

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_LIST_SOFTBUTTONS:
			if (resultCode == RESULT_OK) {
				currentSoftButtons = new Vector<SoftButton>();
				ArrayList<SoftButton> softbuttons = (ArrayList<SoftButton>) IntentHelper
						.getObjectForKey("SELECTED_SOFTBUTTON");
				if (softbuttons != null)
					currentSoftButtons.addAll(softbuttons);

				if (chkIncludeSoftButtons != null) {
					chkIncludeSoftButtons.setChecked(true);
				}
			}
			IntentHelper.removeObjectForKey("SELECTED_SOFTBUTTON");
			break;

		case REQUEST_LIST_CHOICES:
			if (resultCode == RESULT_OK) {
				Vector<Choice> choices = (Vector<Choice>) IntentHelper
						.getObjectForKey(INTENTHELPER_KEY_OBJECTSLIST);
				// sendCreateInteractionChoiceSet(choices);
			}
			IntentHelper.removeObjectForKey(INTENTHELPER_KEY_OBJECTSLIST);
			break;
		case REQUEST_OPEN_CONFIG:
			if(resultCode == RESULT_OK){
				String fileName = data.getStringExtra(FileDialog.RESULT_PATH);
				Log.e("Kyle","select file is "+fileName);
				Intent configintent = new Intent();
				configintent.setAction(ACTION_CONFIG_PATH);
				configintent.putExtra(CONFIG_PATH, fileName);
				this.sendBroadcast(configintent);
			}
		default:
			break;
		}
	}
}
