package com.androidnative.popups;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;



import com.androidnative.utils.NativeUtility;
import com.unity3d.player.UnityPlayer;

@SuppressLint("InlinedApi") 
public class PopUpsManager {
	
	private static AlertDialog currentDialog;

	public static void ShowMessage(String title, String message, String ok) {
		
		Log.d("AndroidNative", "ShowMessage: " + title + message + ok);
		
		int theme = 0;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			theme = android.R.style.Theme_Material_Light_Dialog;
		} else {
			theme = android.R.style.Theme_Holo_Dialog;
		}
		
		 AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(NativeUtility.GetLauncherActivity(), theme));
		 builder.setTitle(title);
		 builder.setMessage(message);
		 builder.setPositiveButton(ok, dialogClickListener);
		 builder.setOnKeyListener(KeyListener);
		 builder.setCancelable(false);

		 currentDialog = builder.show();
		 
	}
	
	
	public static void ShowDialog(String title, String message, String yes, String no) {
		int theme = 0;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			theme = android.R.style.Theme_Material_Light_Dialog;
		} else {
			theme = android.R.style.Theme_Holo_Dialog;
		}
		
		AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(NativeUtility.GetLauncherActivity(), theme));
		 builder.setTitle(title);
		 builder.setMessage(message);
		 builder.setPositiveButton(yes, dialogClickListener);
		 builder.setNegativeButton(no, dialogClickListener);
		 builder.setOnKeyListener(KeyListener);
		 builder.setCancelable(false);
		
		 
		 currentDialog =  builder.show();
	}
	
	
	
	public static void ShowRateDialog(String title, String message, String yes, String laiter, String no) {
		
		int theme = 0;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			theme = android.R.style.Theme_Material_Light_Dialog;
		} else {
			theme = android.R.style.Theme_Holo_Dialog;
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(NativeUtility.GetLauncherActivity(), theme));
		 builder.setTitle(title);
		 builder.setMessage(message);
		 builder.setPositiveButton(yes, rateDialogListener);
		 builder.setNegativeButton(no, rateDialogListener);
		 builder.setNeutralButton(laiter, rateDialogListener);
		 builder.setOnKeyListener(KeyListener);
		 builder.setCancelable(false);
		 
		 
		 currentDialog = builder.show();

	}
	
	public static void HideCurrentPopup() {
		if(currentDialog != null) {
			currentDialog.hide();
		}
	}
	
	
	@SuppressLint("NewApi")
	public static void OpenAppRatingPage(String url) {
		 Uri uri = Uri.parse(url);	
		 Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		 NativeUtility.GetLauncherActivity().startActivity(intent);
	}
	
	
	private static ProgressDialog progress = null;

	public static void ShowPreloader(String title, String message) {
		Log.d("AndroidNative", "ShowPreloader: ");
		
		int theme = 0;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			theme = android.R.style.Theme_Material_Light_Dialog;
		} else {
			theme = android.R.style.Theme_Holo_Dialog;
		}

		progress = new ProgressDialog(new ContextThemeWrapper(NativeUtility.GetLauncherActivity(), theme));
		progress.setTitle(title);
		progress.setMessage(message);
		progress.show();
		progress.setCancelable(false);

	}

	public static void HidePreloader() {
		Log.d("AndroidNative", "HidePreloader: ");
		if (progress != null) {
			progress.hide();
		}
	}
	

	
	private static DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
	    @Override
	    public void onClick(DialogInterface dialog, int which) {
	    	
	        switch (which){
	        
	        case DialogInterface.BUTTON_POSITIVE:
	        	UnityPlayer.UnitySendMessage("AndroidPopUp", "onPopUpCallBack", "0");
	            //Yes button clicked
	            break;

	        case DialogInterface.BUTTON_NEGATIVE:
	        	UnityPlayer.UnitySendMessage("AndroidPopUp", "onPopUpCallBack", "1");
	            //No button clicked
	            break;
	        }
	        
	        dialog = null;
	    }
	};
	
	
	private static DialogInterface.OnKeyListener KeyListener = new OnKeyListener() {
		
		@Override
		public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				Log.d("AndroidNative", "AndroidPopUp");
				UnityPlayer.UnitySendMessage("AndroidPopUp", "onPopUpCallBack", "1");
				UnityPlayer.UnitySendMessage("AndroidRateUsPopUp", "onPopUpCallBack", "1");
				Log.d("AndroidNative", "AndroidRateUsPopUp");
				dialog.dismiss();
		    }
			return false;
		}
	};
	
	private static DialogInterface.OnClickListener rateDialogListener = new DialogInterface.OnClickListener() {
	   
		@Override
	    public void onClick(DialogInterface dialog, int which) {
	        switch (which){
	        case DialogInterface.BUTTON_POSITIVE:
	        	UnityPlayer.UnitySendMessage("AndroidRateUsPopUp", "onPopUpCallBack", "0"); 
	            //Yes button clicked
	            break;

	        case DialogInterface.BUTTON_NEGATIVE:
	        	UnityPlayer.UnitySendMessage("AndroidRateUsPopUp", "onPopUpCallBack", "2");
	            //No button clicked
	            break;
	            
	        case DialogInterface.BUTTON_NEUTRAL:
	        	UnityPlayer.UnitySendMessage("AndroidRateUsPopUp", "onPopUpCallBack", "1");
	            //neutral button clicked
	            break;
	            
	        }
	        

	    }
	};

}
