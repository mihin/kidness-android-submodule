package com.androidnative.features;

import com.androidnative.AN_Bridge;
import com.androidnative.utils.NativeUtility;
import com.unity3d.player.UnityPlayer;

import android.app.UiModeManager;
import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;

public class AN_TV {
	
	private static String TV_SERVICE_LISTNER_NAME = "TVAppController";

	
	public static void AN_CheckForATVDevice() {
		

		UiModeManager uiModeManager = (UiModeManager) NativeUtility.GetLauncherActivity().getSystemService(Context.UI_MODE_SERVICE);
		if (uiModeManager.getCurrentModeType() == Configuration.UI_MODE_TYPE_TELEVISION) {
		    Log.d(AN_Bridge.TAG, "Running on a TV Device");
		    UnityPlayer.UnitySendMessage(TV_SERVICE_LISTNER_NAME, "OnDeviceStateResponce", "1");
		} else {
		    Log.d(AN_Bridge.TAG, "Running on a non-TV Device");
		    UnityPlayer.UnitySendMessage(TV_SERVICE_LISTNER_NAME, "OnDeviceStateResponce", "0");
		}
		

	}
}
