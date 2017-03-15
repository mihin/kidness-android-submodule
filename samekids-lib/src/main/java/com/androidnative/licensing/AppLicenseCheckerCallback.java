package com.androidnative.licensing;

import com.google.android.vending.licensing.LicenseCheckerCallback;
import com.google.android.vending.licensing.Policy;

import com.unity3d.player.UnityPlayer;

public class AppLicenseCheckerCallback implements LicenseCheckerCallback {
	public void allow(int reason) {
    	switch (reason) {
    	case Policy.LICENSED: {
    		// User is licensed to use this app.
    		UnityPlayer.UnitySendMessage(LicenseManager.LICENSE_MANAGER_LISTENER_NAME, "OnLicenseRequestRes", "RESULT_LICENSED");
    	} break;
    	case Policy.RETRY: {
    		UnityPlayer.UnitySendMessage(LicenseManager.LICENSE_MANAGER_LISTENER_NAME, "OnLicenseRequestRes", "RESULT_RETRY");
    	} break;
    	default: {
    		UnityPlayer.UnitySendMessage(LicenseManager.LICENSE_MANAGER_LISTENER_NAME, "OnLicenseRequestRes", "ERROR_UNKNOWN");
    	} break;
    	}
    }

    public void dontAllow(int reason) {
    	switch (reason) {
    	case Policy.NOT_LICENSED: {
    		// User is NOT licensed to use this app.
            // Your response should always inform the user that the application
            // is not licensed, but your behavior at that point can vary. You might
            // provide the user a limited access version of your app or you can
            // take them to Google Play to purchase the app.
    		UnityPlayer.UnitySendMessage(LicenseManager.LICENSE_MANAGER_LISTENER_NAME, "OnLicenseRequestRes", "RESULT_NOT_LICENSED");
    	} break;
    	case Policy.RETRY: {
    		// If the reason received from the policy is RETRY, it was probably
            // due to a loss of connection with the service, so we should give the
            // user a chance to retry. So show a dialog to retry.            
            UnityPlayer.UnitySendMessage(LicenseManager.LICENSE_MANAGER_LISTENER_NAME, "OnLicenseRequestRes", "RESULT_RETRY");
    	} break;
    	default: {
        	UnityPlayer.UnitySendMessage(LicenseManager.LICENSE_MANAGER_LISTENER_NAME, "OnLicenseRequestRes", "ERROR_UNKNOWN");
    	} break;
    	}
    }

	@Override
	public void applicationError(int reason) {
		switch(reason) {
		case LicenseCheckerCallback.ERROR_CHECK_IN_PROGRESS: {
			UnityPlayer.UnitySendMessage(LicenseManager.LICENSE_MANAGER_LISTENER_NAME, "OnLicenseRequestRes", "ERROR_CHECK_IN_PROGRESS");
		} break;
		case LicenseCheckerCallback.ERROR_INVALID_PACKAGE_NAME: {
			UnityPlayer.UnitySendMessage(LicenseManager.LICENSE_MANAGER_LISTENER_NAME, "OnLicenseRequestRes", "ERROR_INVALID_PACKAGE_NAME");
		} break;
		case LicenseCheckerCallback.ERROR_INVALID_PUBLIC_KEY: {
			UnityPlayer.UnitySendMessage(LicenseManager.LICENSE_MANAGER_LISTENER_NAME, "OnLicenseRequestRes", "ERROR_INVALID_PUBLIC_KEY");
		} break;
		case LicenseCheckerCallback.ERROR_MISSING_PERMISSION: {
			UnityPlayer.UnitySendMessage(LicenseManager.LICENSE_MANAGER_LISTENER_NAME, "OnLicenseRequestRes", "ERROR_MISSING_PERMISSION");
		} break;
		case LicenseCheckerCallback.ERROR_NON_MATCHING_UID: {
			UnityPlayer.UnitySendMessage(LicenseManager.LICENSE_MANAGER_LISTENER_NAME, "OnLicenseRequestRes", "ERROR_NON_MATCHING_UID");
		} break;
		case LicenseCheckerCallback.ERROR_NOT_MARKET_MANAGED: {
			UnityPlayer.UnitySendMessage(LicenseManager.LICENSE_MANAGER_LISTENER_NAME, "OnLicenseRequestRes", "ERROR_NOT_MARKET_MANAGED");
		} break;
		default: {
			UnityPlayer.UnitySendMessage(LicenseManager.LICENSE_MANAGER_LISTENER_NAME, "OnLicenseRequestRes", "ERROR_UNKNOWN");
		} break;
		}
	}
}
