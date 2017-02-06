package com.androidnative.features.notifications;

import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import com.androidnative.utils.NativeUtility;
import com.unity3d.player.UnityPlayer;



public class LocalNotificationsController {
	
	public static final String TITILE_KEY  	= "TITILE_KEY";
	public static final String MESSAGE_KEY 	= "MESSAGE_KEY";
	public static final String ID_KEY 	 	= "ID_KEY";
	public static final String ICON_NAME 	= "ICON_NAME";
	public static final String SOUND_NAME 	= "SOUND_NAME";
	public static final String VIBRATION 	= "VIBRATION";
	public static final String SOUND_SILENT = "SOUND_SILENT";
	public static final String SHOW_IF_APP_FOREGROUND = "SHOW_IF_APP_FOREGROUND";
	public static final String LARGE_ICON = "LARGE_ICON";
	
	private static LocalNotificationsController _inctance = null;
	
	public static LocalNotificationsController GetInstance() {
		if (_inctance == null) {
			_inctance = new LocalNotificationsController();
		}

		return _inctance;
	}
	
	@SuppressLint("NewApi") 
	public void requestCurrentAppLaunchNotificationId() {
		int id = -1;
		if(NativeUtility.GetLauncherActivity().getIntent().getExtras() != null) {
			if(NativeUtility.GetLauncherActivity().getIntent().hasExtra(LocalNotificationsController.ID_KEY)) {
				id = NativeUtility.GetLauncherActivity().getIntent().getExtras().getInt(LocalNotificationsController.ID_KEY);
			}
		}
		
		UnityPlayer.UnitySendMessage("AndroidNotificationManager", "OnNotificationIdLoadedEvent",  Integer.toString(id));
	}
	
	@SuppressLint("NewApi")
	public void scheduleNotification(String title, String message, int seconds, int id, String icon, String sound, boolean vibration,
			boolean showIfAppForeground, String largeIcon) {
		Intent resultIntent = new Intent(NativeUtility.GetApplicationContex(), LocalNotificationReceiver.class);
		resultIntent.putExtra(TITILE_KEY, title);
		resultIntent.putExtra(MESSAGE_KEY, message);
		resultIntent.putExtra(ID_KEY, id);
		resultIntent.putExtra(ICON_NAME, icon);
		resultIntent.putExtra(SOUND_NAME, sound);
		resultIntent.putExtra(VIBRATION, vibration);
		resultIntent.putExtra(SHOW_IF_APP_FOREGROUND, showIfAppForeground);
		resultIntent.putExtra(LARGE_ICON, largeIcon);
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.SECOND, seconds);
		
		AlarmManager am = (AlarmManager) NativeUtility.GetLauncherActivity().getSystemService(Activity.ALARM_SERVICE);
		// In reality, you would want to have a static variable for the request code instead of 192837
		PendingIntent sender = PendingIntent.getBroadcast(NativeUtility.GetApplicationContex(), id, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		// start the activity when the user clicks the notification text
		am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), sender);
		
		Log.d("AndroidNative", "LocalNotificationsController scheduleNotification with id: " + id);
	}
	
	
	@SuppressLint("NewApi")
	public void canselNotification(int id) {	
		Intent resultIntent = new Intent(NativeUtility.GetApplicationContex(), LocalNotificationReceiver.class);
		
		AlarmManager am = (AlarmManager) NativeUtility.GetLauncherActivity().getSystemService(Activity.ALARM_SERVICE);
		// In reality, you would want to have a static variable for the request code instead of 192837
		PendingIntent sender = PendingIntent.getBroadcast(NativeUtility.GetApplicationContex(), id, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		// start the activity when the user clicks the notification text
		am.cancel(sender);
		
		Log.d("AndroidNative", "LocalNotificationsController canselNotification with id:" + id);
	}
	
}
