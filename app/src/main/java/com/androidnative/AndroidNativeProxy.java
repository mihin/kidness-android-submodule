package com.androidnative;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.androidnative.features.CameraAPI;
import com.androidnative.features.social.twitter.ANTwitter;
import com.kbeanie.imagechooser.api.ChooserType;
import com.unity3d.player.UnityPlayer;

public class AndroidNativeProxy extends Activity {
	private static final String BRIDGED_INTENT_KEY = "BRIDGED_INTENT";
	private static final String BRIDGED_REQUEST_CODE_KEY = "BRIDGED_REQUEST_CODE_KEY";
	private static final String TASK_ID = "TASK_ID";
	private static final String TWITTER_URL_KEY = "TWITTER_URL_KEY";
	
	
	
	private static final int START_ACTIVITY_FOR_RESULT_TASK = 0;
	private static final int CHOOSE_IMAGE_TASK = 1;  
	private static final int TWITTER_AUTH_TASK = 2; 
	

	@Override
    protected void onStart() {			
		int taskID = getIntent().getIntExtra(TASK_ID, -1);
		Log.d("AndroidNative", "AndroidNativeProxy::onStart " + taskID);
		switch(taskID) {
			case START_ACTIVITY_FOR_RESULT_TASK: 
				StartActivity();
				break;
			case CHOOSE_IMAGE_TASK:
				GetImageFromGallery();
				break;
			case TWITTER_AUTH_TASK:
				Log.d("AndroidNative", "AndroidNativeProxy::TWITTER_AUTH_TASK");				
				Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(getIntent().getStringExtra(TWITTER_URL_KEY)));				
				startActivityForResult(i, TWITTER_AUTH_TASK);
				break;
			default:
				finish();
				break;
		}

        super.onStart();
    }
	
	
	public static void StartTwitterProxyActivity(String url) {
		Intent i = new Intent(UnityPlayer.currentActivity, AndroidNativeProxy.class);		
		i.putExtra(TASK_ID, TWITTER_AUTH_TASK);
		i.putExtra(TWITTER_URL_KEY, url);
		
		UnityPlayer.currentActivity.startActivity(i);
	}
	
	private void StartActivity() {
		int requestCode = getIntent().getIntExtra(BRIDGED_REQUEST_CODE_KEY, 0);
		Intent bridgedIntent = (Intent) getIntent().getParcelableExtra(BRIDGED_INTENT_KEY);
        
		startActivityForResult(bridgedIntent, requestCode);
	}
	
	 @Override
	 protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		Log.d("AndroidNative", "AndroidNativeProxy::onActivityResult " + requestCode + " " + resultCode);
		
		switch (requestCode) {
			case CHOOSE_IMAGE_TASK:
			case ChooserType.REQUEST_PICK_PICTURE:
			case CameraAPI.RESULT_IMAGE_CAPTURE:
				try {
					 CameraAPI.GetInstance().onActivityResult(requestCode, resultCode, data);
				} catch (Exception ex) {
					ex.printStackTrace();
					Log.d("AndroidNative", "GooglePlaySupportActivity::onActivityResult Error: " + ex.getMessage());
				}
				break;
			case TWITTER_AUTH_TASK:
				//Just nothing to do here. finish() method will do all the best © alexray
				break;
		}		
		
	 	super.onActivityResult(requestCode, resultCode, data);
	 	
	 	finish(); 
	 }
	 
	 
	@Override
	public void onNewIntent(Intent intent) {

		Log.d("AndroidNative", "AndroidNativeProxy::onNewIntent");
		
		try {
			try {
				ANTwitter.GetInstance().SetIntent(intent);
			} catch (Throwable ex) {
				Log.d("AndroidNative", "onNewIntent has failed");
			}
		} catch(NoClassDefFoundError e) {
			Log.d("AndroidNative", "AndroidNativeProxy::onNewIntent ANTwitter not found");
		}
		
		super.onNewIntent(intent);
	}

	
	
	public static void startProxyForResult(Intent intent, int requestCode) {
		Intent i = new Intent(UnityPlayer.currentActivity, AndroidNativeProxy.class);
		
		i.putExtra(TASK_ID, START_ACTIVITY_FOR_RESULT_TASK);
		i.putExtra(BRIDGED_REQUEST_CODE_KEY, requestCode);
		i.putExtra(BRIDGED_INTENT_KEY, intent);
		

		UnityPlayer.currentActivity.startActivity(i);
	}
	
	
	public static void startImageChooserProxy() {
		Intent i = new Intent(UnityPlayer.currentActivity, AndroidNativeProxy.class);
		i.putExtra(TASK_ID, CHOOSE_IMAGE_TASK);
		
		UnityPlayer.currentActivity.startActivity(i);
	}
	
	
	
	// --------------------------------------
	// Camera And Gallery
	// --------------------------------------
	
	public void GetImageFromGallery() {
		CameraAPI.GetInstance().StartImageChooser(this);
	}
	

}
