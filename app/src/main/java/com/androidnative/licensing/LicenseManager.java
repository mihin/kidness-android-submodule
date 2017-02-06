package com.androidnative.licensing;

import android.provider.Settings;

import com.androidnative.utils.NativeUtility;

import com.google.android.vending.licensing.AESObfuscator;
import com.google.android.vending.licensing.LicenseChecker;
import com.google.android.vending.licensing.LicenseCheckerCallback;
import com.google.android.vending.licensing.ServerManagedPolicy;

public class LicenseManager {
	public static final String LICENSE_MANAGER_LISTENER_NAME = "AN_LicenseManager";

	private static LicenseCheckerCallback mLicenseCheckerCallback;
    private static LicenseChecker mChecker;

    private static final byte[] SALT = new byte[] {
        -46, 65, 30, -121, -103, -57, 74, -64, 51, 88, -95,
        -45, 77, -115, -36, -113, -11, 32, -64, 89
        };

	public static void StartLicenseRequest(String base64PublicKey) {
		// Construct the LicenseCheckerCallback. The library calls this when done.
        mLicenseCheckerCallback = new AppLicenseCheckerCallback();

        // Construct the LicenseChecker with a Policy.
        mChecker = new LicenseChecker(
        		NativeUtility.GetApplicationContex(), new ServerManagedPolicy(NativeUtility.GetApplicationContex(),
        				new AESObfuscator(SALT, NativeUtility.GetLauncherActivity().getPackageName(),
        						Settings.Secure.getString(NativeUtility.GetApplicationContex().getContentResolver(), Settings.Secure.ANDROID_ID))),
        						base64PublicKey  // Your APP public licensing key.
            );

        mChecker.checkAccess(mLicenseCheckerCallback);
	}
}
