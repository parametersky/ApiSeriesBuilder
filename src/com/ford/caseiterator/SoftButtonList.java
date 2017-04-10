package com.ford.caseiterator;

import java.util.ArrayList;

import com.ford.syncV4.proxy.rpc.Image;
import com.ford.syncV4.proxy.rpc.SoftButton;
import com.ford.syncV4.proxy.rpc.enums.ImageType;
import com.ford.syncV4.proxy.rpc.enums.SoftButtonType;
import com.ford.syncV4.proxy.rpc.enums.SystemAction;
import com.ford.syncV4.util.DebugTool;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

public class SoftButtonList extends ListActivity {

	private static SoftButtonArrayAdapter mButtonList = null;
	private Button mAddButton = null;
	private Button mOKButton = null;
	LayoutInflater mInflater = null;

	private EditText editId;
	private EditText editText;
	private EditText editImage;
	private CheckBox checkBoxHighlighted;
	private Spinner spinnerType;
	private Spinner spinnerImageType;
	private CheckBox checkBoxUseSystemAction;
	private Spinner spinnerSystemAction;
	private View mSoftButtonView;
	private ArrayAdapter<SoftButtonType> typeAdapter;
	private ArrayAdapter<ImageType> imageTypeAdapter;
	private ArrayAdapter<SystemAction> systemActionAdapter;
	
	private AlertDialog.Builder builder = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.softbuttonlist);

		mButtonList = new SoftButtonArrayAdapter(this);
		ArrayList<SoftButton> softbuttons = ((ArrayList<SoftButton>) IntentHelper
				.getObjectForKey("ALL_SOFTBUTTONS"));
		if (softbuttons != null) {
			Log.d("KYLE","setButtons");
			mButtonList.setSoftButtonArray(softbuttons);
		}
		builder = new AlertDialog.Builder(
				SoftButtonList.this);
		setListAdapter(mButtonList);
		mInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

		mSoftButtonView = mInflater.inflate(R.layout.softbutton, null);
		builder.setView(mSoftButtonView);
		
		editId = (EditText) mSoftButtonView.findViewById(R.id.softbutton_id);
		editText = (EditText) mSoftButtonView.findViewById(R.id.softbutton_text);
		editImage = (EditText) mSoftButtonView.findViewById(R.id.softbutton_image);
		checkBoxHighlighted = (CheckBox) mSoftButtonView
				.findViewById(R.id.softbutton_isHighlighted);
		spinnerType = (Spinner) mSoftButtonView.findViewById(R.id.softbutton_type);
		spinnerImageType = (Spinner) mSoftButtonView
				.findViewById(R.id.softbutton_imageType);
		checkBoxUseSystemAction = (CheckBox) mSoftButtonView
				.findViewById(R.id.softbutton_useSystemAction);
		spinnerSystemAction = (Spinner) mSoftButtonView
				.findViewById(R.id.softbutton_systemAction);

		// setup adapters
		typeAdapter = new ArrayAdapter<SoftButtonType>(this,
				android.R.layout.simple_spinner_item, SoftButtonType.values());
		typeAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerType.setAdapter(typeAdapter);

		imageTypeAdapter = new ArrayAdapter<ImageType>(this,
				android.R.layout.simple_spinner_item, ImageType.values());
		imageTypeAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerImageType.setAdapter(imageTypeAdapter);
		spinnerImageType.setSelection(imageTypeAdapter
				.getPosition(ImageType.DYNAMIC));

		systemActionAdapter = new ArrayAdapter<SystemAction>(this,
				android.R.layout.simple_spinner_item, SystemAction.values());
		systemActionAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerSystemAction.setAdapter(systemActionAdapter);


		mOKButton = (Button) findViewById(R.id.OK);
		mOKButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				IntentHelper.addObjectForKey(mButtonList.getSoftButtonList(),
						"ALL_SOFTBUTTONS");
				IntentHelper.addObjectForKey(
						mButtonList.getSelectedSoftButton(),
						"SELECTED_SOFTBUTTON");
				SoftButtonList.this.setResult(RESULT_OK);
				SoftButtonList.this.finish();
			}
		});
		mAddButton = (Button) findViewById(R.id.addSoftButton);
		mAddButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub


				builder.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub

							}
						});
				builder.setPositiveButton("Finish",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub

								SoftButton result = new SoftButton();
								result.setType((SoftButtonType) spinnerType
										.getSelectedItem());
								switch (result.getType()) {
								case SBT_TEXT:
									setTextToSoftButton(result);
									break;
								case SBT_IMAGE:
									setImageToSoftButton(result);
									break;
								case SBT_BOTH:
									setTextToSoftButton(result);
									setImageToSoftButton(result);
									break;
								}
								result.setIsHighlighted(checkBoxHighlighted
										.isChecked());
								if (checkBoxUseSystemAction.isChecked()) {
									result.setSystemAction((SystemAction) spinnerSystemAction
											.getSelectedItem());
								}
								try {
									result.setSoftButtonID(Integer
											.parseInt(editId.getText()
													.toString()));
								} catch (NumberFormatException e) {
									result.setSoftButtonID(5555);
								}

								mButtonList.addSoftButton(result);
								SoftButtonList.this.onContentChanged();
								
							}
						});
				builder.show();
			}
		});
		getListView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
				// TODO Auto-generated method stub
				com.ford.caseiterator.utils.Debug.DebugLog("ListView onClick");
				Log.d("KYLE","ListActivity onListViewItemClick");
				SoftButton softButton = (SoftButton) mButtonList.getItem(arg2);
				editId.setText(String.valueOf(softButton.getSoftButtonID()));
				editText.setText(softButton.getText());
				spinnerType.setSelection(typeAdapter.getPosition(softButton
						.getType()));
				switch (softButton.getType()) {
				case SBT_TEXT:
					setTextFromSoftButton(softButton);
					break;
				case SBT_IMAGE:
					setImageFromSoftButton(softButton);
					break;
				case SBT_BOTH:
					setTextFromSoftButton(softButton);
					setImageFromSoftButton(softButton);
					break;
				}
				checkBoxHighlighted.setChecked(softButton.getIsHighlighted());
				SystemAction systemAction = softButton.getSystemAction();
				if (systemAction != null) {
					spinnerSystemAction.setSelection(systemActionAdapter
							.getPosition(systemAction));
				} else {
					checkBoxUseSystemAction.setChecked(false);
					spinnerSystemAction.setSelection(0);
				}

				

				builder.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub

							}
						});
				builder.setPositiveButton("Finish",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub

								SoftButton result = new SoftButton();
								result.setType((SoftButtonType) spinnerType
										.getSelectedItem());
								switch (result.getType()) {
								case SBT_TEXT:
									setTextToSoftButton(result);
									break;
								case SBT_IMAGE:
									setImageToSoftButton(result);
									break;
								case SBT_BOTH:
									setTextToSoftButton(result);
									setImageToSoftButton(result);
									break;
								}
								result.setIsHighlighted(checkBoxHighlighted
										.isChecked());
								if (checkBoxUseSystemAction.isChecked()) {
									result.setSystemAction((SystemAction) spinnerSystemAction
											.getSelectedItem());
								}
								try {
									result.setSoftButtonID(Integer
											.parseInt(editId.getText()
													.toString()));
								} catch (NumberFormatException e) {
									result.setSoftButtonID(5555);
								}

							}
						});
				builder.show();
			}
		});
	}

	public void onListItemClick(ListView l, View v, int position,long id){
		super.onListItemClick(l, v, position, id);
		// TODO Auto-generated method stub
		com.ford.caseiterator.utils.Debug.DebugLog("ListView onClick");
		Log.d("KYLE","ListActivity onListItemClick");
		SoftButton softButton = (SoftButton) mButtonList.getItem(position);
		editId.setText(String.valueOf(softButton.getSoftButtonID()));
		editText.setText(softButton.getText());
		spinnerType.setSelection(typeAdapter.getPosition(softButton
				.getType()));
		switch (softButton.getType()) {
		case SBT_TEXT:
			setTextFromSoftButton(softButton);
			break;
		case SBT_IMAGE:
			setImageFromSoftButton(softButton);
			break;
		case SBT_BOTH:
			setTextFromSoftButton(softButton);
			setImageFromSoftButton(softButton);
			break;
		}
		checkBoxHighlighted.setChecked(softButton.getIsHighlighted());
		SystemAction systemAction = softButton.getSystemAction();
		if (systemAction != null) {
			spinnerSystemAction.setSelection(systemActionAdapter
					.getPosition(systemAction));
		} else {
			checkBoxUseSystemAction.setChecked(false);
			spinnerSystemAction.setSelection(0);
		}

		

		builder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						// TODO Auto-generated method stub

					}
				});
		builder.setPositiveButton("Finish",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						// TODO Auto-generated method stub

						SoftButton result = new SoftButton();
						result.setType((SoftButtonType) spinnerType
								.getSelectedItem());
						switch (result.getType()) {
						case SBT_TEXT:
							setTextToSoftButton(result);
							break;
						case SBT_IMAGE:
							setImageToSoftButton(result);
							break;
						case SBT_BOTH:
							setTextToSoftButton(result);
							setImageToSoftButton(result);
							break;
						}
						result.setIsHighlighted(checkBoxHighlighted
								.isChecked());
						if (checkBoxUseSystemAction.isChecked()) {
							result.setSystemAction((SystemAction) spinnerSystemAction
									.getSelectedItem());
						}
						try {
							result.setSoftButtonID(Integer
									.parseInt(editId.getText()
											.toString()));
						} catch (NumberFormatException e) {
							result.setSoftButtonID(5555);
						}

					}
				});
		builder.show();
	}
	
	private void setImageFromSoftButton(SoftButton softButton) {
		editImage.setText(softButton.getImage().getValue());
		spinnerImageType.setSelection(imageTypeAdapter.getPosition(softButton
				.getImage().getImageType()));
	}

	private void setTextFromSoftButton(SoftButton softButton) {
		editText.setText(softButton.getText());
	}

	private void setTextToSoftButton(SoftButton softButton) {
		softButton.setText(editText.getText().toString());
	}

	private void setImageToSoftButton(SoftButton softButton) {
		Image image = new Image();
		image.setValue(editImage.getText().toString());
		image.setImageType((ImageType) spinnerImageType.getSelectedItem());
		softButton.setImage(image);
	}
}
