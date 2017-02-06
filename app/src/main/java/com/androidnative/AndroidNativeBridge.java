package com.androidnative;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.unity3d.player.UnityPlayerActivity;

@SuppressLint("NewApi")
public class AndroidNativeBridge extends UnityPlayerActivity {


	// --------------------------------------
	// INITIALIZE
	// --------------------------------------

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d("AndroidNative", "AndroidNativeBridge::onCreate");
		super.onCreate(savedInstanceState);
	}


	// --------------------------------------
	// Override
	// --------------------------------------

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d("AndroidNative", "AndroidNativeBridge::onActivityResult");
		super.onActivityResult(requestCode, resultCode, data);

	}

	@Override
	public void onNewIntent(Intent intent) {
		Log.d("AndroidNative", "AndroidNativeBridge::onNewIntent");
		super.onNewIntent(intent);
		
	}

}
