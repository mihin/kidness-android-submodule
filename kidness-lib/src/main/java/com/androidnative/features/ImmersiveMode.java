package com.androidnative.features;

import com.androidnative.utils.NativeUtility;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

@SuppressLint("NewApi")
public class ImmersiveMode {
	
	public static boolean IsImmersiveModeEnabled = false;
	
	private static int SYSTEM_UI_HIDE_DELAY = 2000;
	private static boolean isSystemUiShown = true;
	private static Handler handler;
	
	private static Runnable checkSystemUiRunnable;
	  
	  
	
	public static void enableImmersiveMode() {
		
		if(IsImmersiveModeEnabled) {
			return;
		}
		
		checkSystemUiRunnable  = new Runnable() {
		    @Override
		    public void run() {
		      checkHideSystemUI();
		    }
		  };
		  
		handler =  new Handler();

		if (IsValidOSVersion()) {

			IsImmersiveModeEnabled = true;
			onWindowFocusChanged(true);
			UiChangeListener();

			try {
				 final ViewTreeObserver.OnWindowFocusChangeListener mWindowFocusListener =
				            new ViewTreeObserver.OnWindowFocusChangeListener() {
				                @Override
				                public void onWindowFocusChanged(boolean hasFocus) {
				                	ImmersiveMode.onWindowFocusChanged(hasFocus);
				                }
				            };
				 
				            ViewTreeObserver vto =  NativeUtility.GetLauncherActivity().getWindow().getDecorView().getRootView().getViewTreeObserver(); 
				            vto.addOnWindowFocusChangeListener(mWindowFocusListener);
			} catch(Exception ex) {
				  Log.d("AndroidNative", "Adding  OnWindowFocusChangeListener observer has failed: " + ex.getMessage());
			}

		}
		
	}

	
	public static void onWindowFocusChanged(boolean hasFocus) {

	    if(hasFocus && IsImmersiveModeEnabled) {
	    	ApplyImmersive();
	    }
		
	   
	    
		
	}
	

	  
	 private static void checkHideSystemUI() {
		// Log.d("AndroidNative", "checkHideSystemUI: " );
		 // Check if system UI is shown and hide it by post a delayed handler
	    if (isSystemUiShown) {
	    	ApplyImmersive();
	    	handler.postDelayed(checkSystemUiRunnable, SYSTEM_UI_HIDE_DELAY);
	    }
	 }
	  
	
	public static void UiChangeListener() {
		 Log.d("AndroidNative", "UiChangeListener: " );
		 
		 
        final View decorView = NativeUtility.GetLauncherActivity().getWindow().getDecorView();
            decorView.setOnSystemUiVisibilityChangeListener (new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
            	
            	 
            	
            	 if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                     handler.postDelayed(checkSystemUiRunnable, SYSTEM_UI_HIDE_DELAY);
                     isSystemUiShown = true;
                     ApplyImmersive();
                   } else {
                     isSystemUiShown = false;
                   }
            	 
            	 Log.d("AndroidNative", "UiChangeListener isSystemUiShown: " + isSystemUiShown );
            }
        });
    }
	
	private static void ApplyImmersive() {
	
		if (IsValidOSVersion()) {
			
			NativeUtility.GetLauncherActivity().getWindow().getDecorView().setSystemUiVisibility(
	                  View.SYSTEM_UI_FLAG_LAYOUT_STABLE
	                  | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
	                  | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
	                  | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
	                  | View.SYSTEM_UI_FLAG_FULLSCREEN
	                  | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
			
		}
	
	}
	
	
	public static boolean IsValidOSVersion() {
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		if (currentapiVersion >= android.os.Build.VERSION_CODES.KITKAT) {
			return true;
		} else {
			return false;
		}
	}
}
