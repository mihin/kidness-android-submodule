package com.androidnative;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.androidnative.features.AppInfoLoader;
import com.androidnative.features.CameraAPI;
import com.androidnative.features.common.AddressBookManager;
import com.androidnative.features.common.AndroidNativeUtility;
import com.androidnative.features.social.twitter.ANTwitter;
import com.androidnative.utils.Base64;
import com.androidnative.utils.Base64DecoderException;
import com.androidnative.utils.NativeUtility;

public class AN_Bridge {
	
	

	/**
	 * Tag used on log messages.
	 */
	public static final String TAG = "AndroidNative";
	

	/**
	 * Splitters.
	 */
	public static final String UNITY_SPLITTER = "|";
	public static final String UNITY_EOF = "endofline";
	
	
	
	
	private static FileOutputStream fos;
	
	
	
	public static void Test() {
		Log.d("AndroidNative", "Test called");
	}
	
	
	public static void loadAndroidId() {
		AndroidNativeUtility.GetInstance().GetAndroidId();
	}
	
	public static void SaveToGalalry(String ImageData, String name) {
		try {
			try {
				CameraAPI.GetInstance().SaveToGalalry(ImageData, name);
			} catch (Exception ex) {
				ex.printStackTrace();
				Log.d("AndroidNative", "Error: " + ex.getMessage());
			}
		} catch (NoClassDefFoundError ex) {
			Log.d("AndroidNative", "NoClassDefFoundError SaveToGalalry: " + ex.getMessage());
		}
		
	}
	
	
	public static void InitCameraAPI(String folderName, String maxSize, String mode, int format) {
		try {
			try {
				CameraAPI.GetInstance().Init(folderName, Integer.parseInt(maxSize) , Integer.parseInt(mode), format);
			} catch (Exception ex) {
				ex.printStackTrace();
				Log.d("AndroidNative", "Error: " + ex.getMessage());
			}
		} catch (NoClassDefFoundError ex) {
			Log.d("AndroidNative", "NoClassDefFoundError InitCameraAPI: " + ex.getMessage());
		}	
	}


	public static void GetImageFromGallery() {
		try {
			try {
				CameraAPI.GetInstance().GetImageFromGallery();
			} catch (Exception ex) {
				ex.printStackTrace();
				Log.d("AndroidNative", "Error: " + ex.getMessage());
			}
		} catch (NoClassDefFoundError ex) {
			Log.d("AndroidNative", "NoClassDefFoundError GetImageFromGallery: " + ex.getMessage());
		}
		
	}
	
	public static void GetImageFromCamera(String bSaveToGallery) {
		try {
			try {
				CameraAPI.GetInstance().GetImageFromCamera(Boolean.valueOf(bSaveToGallery));
			} catch (Exception ex) {
				ex.printStackTrace();
				Log.d("AndroidNative", "Error: " + ex.getMessage());
			}
		} catch (NoClassDefFoundError ex) {
			Log.d("AndroidNative", "NoClassDefFoundError GetImageFromCamera: " + ex.getMessage());
		}	
	}
	
	
	// --------------------------------------
	// Twitter
	// --------------------------------------

	public static void TwitterInit(String consumer_key, String consumer_secret) {
		try {
			ANTwitter.GetInstance().Init(consumer_key, consumer_secret);
		} catch (NoClassDefFoundError ex) {
			Log.d("AndroidNative", "NoClassDefFoundError TwitterInit: " + ex.getMessage());
		}
		
	}

	public static void AuthificateUser() {
		try {
			ANTwitter.GetInstance().AuthificateUser();			
		} catch (NoClassDefFoundError ex) {
			Log.d("AndroidNative", "NoClassDefFoundError AuthificateUser: " + ex.getMessage());
		}
		
	}

	public static void LoadUserData() {
		try {
			ANTwitter.GetInstance().LoadUserData();
		} catch (NoClassDefFoundError ex) {
			Log.d("AndroidNative", "NoClassDefFoundError LoadUserData: " + ex.getMessage());
		}
		
	}

	public static void TwitterPost(String status) {
		try {
			ANTwitter.GetInstance().Twitt(status);
		} catch (NoClassDefFoundError ex) {
			Log.d("AndroidNative", "NoClassDefFoundError ANTwitter.GetInstance().Twitt(status);: " + ex.getMessage());
		}
		
	}

	public static void TwitterPostWithImage(String status, String data) throws IOException, Base64DecoderException {
		
		try {
			Log.d("AndroidNative", "TwitterPostWithImage: ");
			byte[] byteArray = Base64.decode(data);

			File tempFile;
			tempFile = new File(NativeUtility.GetLauncherActivity().getCacheDir(), "twitter_post_image");
			fos = new FileOutputStream(tempFile);
			fos.write(byteArray);

			ANTwitter.GetInstance().Twitt(status, tempFile);
		} catch (NoClassDefFoundError ex) {
			Log.d("AndroidNative", "NoClassDefFoundError TwitterPostWithImage: " + ex.getMessage());
		}
		

	}

	public static void LogoutFromTwitter() {
		try {
			ANTwitter.GetInstance().logoutFromTwitter();
		} catch (NoClassDefFoundError ex) {
			Log.d("AndroidNative", "NoClassDefFoundError LogoutFromTwitter: " + ex.getMessage());
		}
	}
	
	

	// --------------------------------------
	// Utils
	// --------------------------------------
	
	public static void isPackageInstalled(String packagename) {
		try {
			AndroidNativeUtility.GetInstance().isPackageInstalled(packagename);
		} catch (Exception ex) {
			ex.printStackTrace();
			Log.d("AndroidNative", "Error: " + ex.getMessage());
		}
	}
	
	public static void runPackage(String packagename) {
		try {
			AndroidNativeUtility.GetInstance().runPackage(packagename);
		} catch (Exception ex) {
			ex.printStackTrace();
			Log.d("AndroidNative", "Error: " + ex.getMessage());
		}
	}
	
	// --------------------------------------
	// OTHER FEATURES
	// --------------------------------------

	public static void loadAddressBook() {
		AddressBookManager.GetInstance().load();
	}

	public static void LoadPackageInfo() {
		AppInfoLoader.LoadPackageInfo();
	}
	
	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	@SuppressLint("NewApi")
	public static void StartLockTask() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			NativeUtility.GetLauncherActivity().startLockTask();
				
			Log.d("AndroidNative", "Lock Task Started for " + NativeUtility.GetApplicationContex().getPackageName());
		} else {
			Log.d("AndroidNative", "This API is NOT supported for current SDK version: " + Build.VERSION.SDK_INT);
		}
	}
	
	@SuppressLint("NewApi")
	public static void StopLockTask() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			NativeUtility.GetLauncherActivity().stopLockTask();
			
			Log.d("AndroidNative", "Lock Task Stoped for " + NativeUtility.GetApplicationContex().getPackageName());
		} else {
			Log.d("AndroidNative", "This API is NOT supported for current SDK version: " + Build.VERSION.SDK_INT);
		}
	}
	
	public static void OpenAppInStore(String appPackageName) {
		Uri uri = Uri.parse("market://details?id=" + appPackageName);
		Intent intent = new Intent (Intent.ACTION_VIEW, uri);
		NativeUtility.GetLauncherActivity().startActivity(intent);
	}
	
}
