package com.androidnative.features;

import android.annotation.SuppressLint;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

import com.androidnative.AN_Bridge;
import com.androidnative.utils.NativeUtility;
import com.unity3d.player.UnityPlayer;

@SuppressLint("NewApi")
public class AppInfoLoader {

	
	public static void LoadPackageInfo() {
		
		PackageInfo pInfo;
		try {
			
			pInfo = NativeUtility.GetLauncherActivity().getPackageManager().getPackageInfo(NativeUtility.GetLauncherActivity().getPackageName(), 0);
			StringBuilder result = new StringBuilder();
			result.append(pInfo.versionName);
			result.append(AN_Bridge.UNITY_SPLITTER);
			result.append(pInfo.versionCode);
			result.append(AN_Bridge.UNITY_SPLITTER);
			result.append(pInfo.packageName);
			result.append(AN_Bridge.UNITY_SPLITTER);
			result.append(pInfo.lastUpdateTime);
			result.append(AN_Bridge.UNITY_SPLITTER);
			result.append(pInfo.sharedUserId);
			result.append(AN_Bridge.UNITY_SPLITTER);
			result.append(pInfo.sharedUserLabel);
			
			

			Log.d("AndroidNative", "App data loaded");
			UnityPlayer.UnitySendMessage("AndroidAppInfoLoader", "OnPackageInfoLoaded", result.toString());
			
		} catch (NameNotFoundException e) {
			Log.d("AndroidNative", "LoadInfo Failed");
			e.printStackTrace();
		}
		

	}


}
