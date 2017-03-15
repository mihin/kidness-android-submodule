package com.androidnative.features.common;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.net.DhcpInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.provider.Settings.Secure;
import android.util.Log;

import com.androidnative.AN_Bridge;
import com.androidnative.utils.NativeUtility;
import com.unity3d.player.UnityPlayer;

public class AndroidNativeUtility {
	public static final String UTILITY_LISTENER = "AndroidNativeUtility";
	

	private static AndroidNativeUtility _instance = null;


	public static AndroidNativeUtility GetInstance() {
		if (_instance == null) {
			_instance = new AndroidNativeUtility();
		}

		return _instance;
	}
	

	@SuppressLint("NewApi")
	public void isPackageInstalled(String packagename) {
		
	    PackageManager pm = NativeUtility.GetLauncherActivity().getPackageManager();
	    try {
	        pm.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES);
	        UnityPlayer.UnitySendMessage(UTILITY_LISTENER, "OnPacakgeFound", packagename );
	    } catch (NameNotFoundException e) {
	    	UnityPlayer.UnitySendMessage(UTILITY_LISTENER, "OnPacakgeNotFound", packagename );
	    }
	}
	
	@SuppressLint("NewApi")
	public void runPackage(String packagename) {
		PackageManager pm = NativeUtility.GetLauncherActivity().getPackageManager();
		Intent intent = pm.getLaunchIntentForPackage(packagename);
		NativeUtility.GetLauncherActivity().startActivity(intent);
	}
	
	
	public void GetAndroidId() {
		String android_id = Secure.getString(NativeUtility.GetApplicationContex().getContentResolver(),Secure.ANDROID_ID);
		Log.d("AndroidNative", "android_id: " + android_id);
		
		UnityPlayer.UnitySendMessage(UTILITY_LISTENER, "OnAndroidIdLoadedEvent", android_id);
	}

	public void GetGoogleAid() {
		String android_id = Secure.getString(NativeUtility.GetApplicationContex().getContentResolver(),Secure.ANDROID_ID);
		Log.d("AndroidNative", "google_aid: " + android_id);

		UnityPlayer.UnitySendMessage(UTILITY_LISTENER, "OnGoogleAidLoadedEvent", android_id);
	}
	
	public static void GetInternalStoragePath() {
        String path = "";
        try {
               path = NativeUtility.GetLauncherActivity().getApplicationContext().getFilesDir()
                             .getAbsolutePath();
        } catch (Error error) {
               Log.i("GetLocalPath Error: ", error.getMessage());
        }
        
        UnityPlayer.UnitySendMessage(UTILITY_LISTENER, "OnInternalStoragePathLoaded", path);
  }
 
  public static void GetExternalStoragePath() {
        String path = "";
        try {
               String state = Environment.getExternalStorageState();

               // Ensure that the external storage is mounted.
               if ((Environment.MEDIA_MOUNTED.equals(state))
                             || (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state))) {

                      // Get external storage path.
                      path = Environment.getExternalStorageDirectory()
                                   .getAbsolutePath();

                      // If the path is invalid, use "default" external path (and hope
                      // it works!)
                      if (path.length() > 0) {
                             path += "/Android/data/"
                                          + NativeUtility.GetLauncherActivity().getApplicationContext()
                                                       .getPackageName() + "/files/";
                      }
               }
        } catch (Error error) {
        	   Log.i("GetLocalPath Error: ", error.getMessage());
        }
        
        UnityPlayer.UnitySendMessage(UTILITY_LISTENER, "OnExternalStoragePathLoaded", path);
  }
  
  
  
  public static void openSettingsPage(String action) {
	  Intent intent = new Intent(action);
	  
	  if(action.equals(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)) {
		  intent.setData(Uri.parse("package:" + NativeUtility.GetApplicationContex().getPackageName()));
	  } 
	  
	  NativeUtility.GetLauncherActivity().startActivity(intent);
  }
	
  
  public static void LoadLocaleInfo() {
	  
	StringBuilder result = null;
	
	result = new StringBuilder();
	result.append(Locale.getDefault().getCountry());
	result.append(AN_Bridge.UNITY_SPLITTER);
	result.append(Locale.getDefault().getDisplayCountry());
	result.append(AN_Bridge.UNITY_SPLITTER);
	
	result.append(Locale.getDefault().getLanguage());
	result.append(AN_Bridge.UNITY_SPLITTER);
	result.append(Locale.getDefault().getDisplayLanguage());
	

	 UnityPlayer.UnitySendMessage(UTILITY_LISTENER, "OnLocaleInfoLoaded", result.toString());
	  
  }
  
  public static void loadPackagesList() {
	  
	  Log.i("AndroidNative",  "AndroidNativeUtility::loadPackagesList");
	  final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
	  mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
	  final List<ResolveInfo> pkgAppsList = NativeUtility.GetLauncherActivity().getApplicationContext().getPackageManager().queryIntentActivities( mainIntent, 0);
	  
	  StringBuilder result = new StringBuilder();
	  
	  for(ResolveInfo info : pkgAppsList) {
		  result.append( info.activityInfo.packageName);
		  result.append(AN_Bridge.UNITY_SPLITTER);

	  }
	  result.append(AN_Bridge.UNITY_EOF);
	  

	  
	  UnityPlayer.UnitySendMessage(UTILITY_LISTENER, "OnPackagesListLoaded", result.toString());
  }
	
  
  @SuppressLint("NewApi") 
  public static void loadNetworkInfo() {
	  WifiManager wifi = (WifiManager) NativeUtility.GetLauncherActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
	  WifiInfo wifiInfo = wifi.getConnectionInfo();
	  DhcpInfo dhcp = wifi.getDhcpInfo();

	  StringBuilder result = new StringBuilder();
	  
	  String mask = "";
	  if (dhcp != null) { 
		  mask = intToIP(dhcp.netmask);
	  }
	  
	  
	  result.append(mask);
	  result.append(AN_Bridge.UNITY_SPLITTER);
	
	  String ip = intToIP(wifiInfo.getIpAddress());
	  result.append(ip);
	  result.append(AN_Bridge.UNITY_SPLITTER);
	  
	  result.append(wifiInfo.getMacAddress());
	  result.append(AN_Bridge.UNITY_SPLITTER);
	  
	  result.append(wifiInfo.getSSID());
	  result.append(AN_Bridge.UNITY_SPLITTER);
	  
	  
	  result.append(wifiInfo.getBSSID());
	  result.append(AN_Bridge.UNITY_SPLITTER);
	  
	  result.append(wifiInfo.getLinkSpeed());
	  result.append(AN_Bridge.UNITY_SPLITTER);
	  
	  result.append(wifiInfo.getNetworkId());
	  
	  UnityPlayer.UnitySendMessage(UTILITY_LISTENER, "OnNetworkInfoLoaded", result.toString());
  }
  
  @SuppressLint("DefaultLocale") 
  private static String intToIP(int ipAddress) {
	    String ret = String.format("%d.%d.%d.%d", (ipAddress & 0xff),
	            (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff),
	            (ipAddress >> 24 & 0xff));

	    return ret;
  }
  
	
	

}
