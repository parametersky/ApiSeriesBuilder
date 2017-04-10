package com.ford.caseiterator.constants;

import java.util.List;
import java.util.Set;

public class ApiParameterLists {
	public static final String[] SHOW_PARALIST = { ApiNames.mainField1,
			ApiNames.mainField2, ApiNames.mainField3, ApiNames.mainField4,
			ApiNames.mediaTrack, ApiNames.mediaClock, ApiNames.statusBar,
			ApiNames.alignment, ApiNames.graphic, ApiNames.softButtons,
			ApiNames.customPresets };
	public static final String[] ALERT_PARALIST = { ApiNames.alertText1,
			ApiNames.alertText2, ApiNames.alertText3, ApiNames.ttsChunks,
			ApiNames.duration, ApiNames.softButtons, ApiNames.playTone };

	public static final String[] SoftButtonList = { ApiNames.softButtonID,
			ApiNames.type, ApiNames.text, ApiNames.image,
			ApiNames.isHighlighted, ApiNames.systemAction };

	/*
	 * I have selected several apis to define the parameter type below: Apis
	 * are: Alert, Show,
	 * AddCommand,DeleteCommand,AddSubMenu,DeleteSubMenu,CreateInteractionChoiceSet
	 * ,DeleteInteractionChoiceSet,Speak
	 * ResetGlobalProperties,SetGlobalProperties, SoftButtons
	 */
	
	public static final String[] IntergerParameters = { ApiNames.cmdID,
			ApiNames.menuID, ApiNames.position, ApiNames.duration,
			ApiNames.interactionChoiceSetID, ApiNames.softButtonID,
			ApiNames.systemAction };
	public static final String[] StringParameters = { ApiNames.menuName,
			ApiNames.alertText1, ApiNames.alertText2, ApiNames.alertText3,
			ApiNames.vrHelpTitle, ApiNames.mainField1, ApiNames.mainField2,
			ApiNames.mainField3, ApiNames.mainField4, ApiNames.statusBar,
			ApiNames.mediaClock, ApiNames.mediaTrack,// this is a softbuttontype
			ApiNames.type, ApiNames.text };
	public static final String[] MenuParamsParameters = { ApiNames.menuParams };
	public static final String[] StringArrayParameters = { ApiNames.vrCommands,
	/*
	 * this should be ttschunk array but in json it 's described as String "
	 */
	ApiNames.ttsChunks, ApiNames.helpPrompt, ApiNames.timeoutPrompt,

	ApiNames.customPresets };
	public static final String[] ImageParameters = { ApiNames.cmdIcon,
			ApiNames.graphic, ApiNames.image };
	public static final String[] BoolParameters = { ApiNames.playTone,
			ApiNames.isHighlighted };
	public static final String[] SoftButtonsParameters = { ApiNames.softButtons };
	public static final String[] ChoiceSetParameters = { ApiNames.choiceSet };
	public static final String[] GlobalPropertyParameters = { ApiNames.properties };
	public static final String[] VrHelpParameters = { ApiNames.vrHelp };
	public static final String[] TextAlignmentParameters = { ApiNames.alignment };

}
