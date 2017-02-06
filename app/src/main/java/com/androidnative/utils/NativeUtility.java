package com.androidnative.utils;

import android.app.Activity;
import android.content.Context;

import com.unity3d.player.UnityPlayer;

public class NativeUtility {
	public static Activity GetLauncherActivity() {
		return UnityPlayer.currentActivity;
	}
	
	
	public static Context GetApplicationContex() {
		return GetLauncherActivity().getApplicationContext();
	}
	

}
