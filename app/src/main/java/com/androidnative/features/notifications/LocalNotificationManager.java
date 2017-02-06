package com.androidnative.features.notifications;

import com.androidnative.gcm.ANCloudMessageService;
import com.androidnative.utils.NativeUtility;

import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class LocalNotificationManager {
	
	// --------------------------------------
	// Local Notifications
	// --------------------------------------
	
	public static void requestCurrentAppLaunchNotificationId() {
		LocalNotificationsController.GetInstance()
				.requestCurrentAppLaunchNotificationId();
	}

	public static void ScheduleLocalNotification(String title, String message,
			String seconds, String id, String icon, String sound, String vibration,
			String showIfAppIsForeground, String largeIcon) {
		int sec = Integer.parseInt(seconds);
		int nId = Integer.parseInt(id);
		boolean vibro = Boolean.parseBoolean(vibration);
		boolean showIfAppForeground = Boolean.parseBoolean(showIfAppIsForeground);
		LocalNotificationsController.GetInstance().scheduleNotification(title,
				message, sec, nId, icon, sound, vibro, showIfAppForeground, largeIcon);
	}

	public static void canselLocalNotification(String id) {
		int nId = Integer.parseInt(id);
		LocalNotificationsController.GetInstance().canselNotification(nId);
	}
	
	public static void HideAllNotifications() {
		NotificationManager manager = (NotificationManager) NativeUtility.GetApplicationContex().getSystemService(Context.NOTIFICATION_SERVICE);
		manager.cancelAll();
	}
	
	// --------------------------------------
	// Toast Notification
	// --------------------------------------

	public static void ShowToastNotification(String text, String duration) {
		Context context = NativeUtility.GetApplicationContex();

		Toast toast = Toast.makeText(context, text, Integer.parseInt(duration));
		toast.show();
	}

	// --------------------------------------
	// Google Cloud Message
	// --------------------------------------
	
	public static void InitPushNotifications(String icon, String sound, String vibration, String showWhenAppForeground, String replaceOldWithNew) {
		try {
			ANCloudMessageService.GetInstance().InitNotificationParams(icon, sound, Boolean.parseBoolean(vibration), Boolean.parseBoolean(showWhenAppForeground), Boolean.parseBoolean(replaceOldWithNew));
		} catch (NoClassDefFoundError ex) {
			Log.d("AndroidNative", "NoClassDefFoundError InitPushNotifications: " + ex.getMessage());
		}
	}
	
	public static void InitParsePushNotifications(String appId, String dotNetKey) {
		try {
			ANCloudMessageService.GetInstance().initParsePushNotifications(appId, dotNetKey);
		} catch (NoClassDefFoundError ex) {
			Log.d("AndroidNative", "NoClassDefFoundError GCMRgisterDevice: "
					+ ex.getMessage());
		}
	}

	public static void GCMRgisterDevice(String senderId) {
		try {
			ANCloudMessageService.GetInstance().registerDevice(senderId);
		} catch (NoClassDefFoundError ex) {
			Log.d("AndroidNative", "NoClassDefFoundError GCMRgisterDevice: "
					+ ex.getMessage());
		}

	}

	public static void GCMLoadLastMessage() {
		try {
			ANCloudMessageService.GetInstance().LoadLastMessage();
		} catch (NoClassDefFoundError ex) {
			Log.d("AndroidNative", "NoClassDefFoundError GCMLoadLastMessage: "
					+ ex.getMessage());
		}

	}
	
	public static void GCMRemoveLastMessageInfo() {
		try {
			ANCloudMessageService.GetInstance().RemoveLastMessageInfo();
		} catch (NoClassDefFoundError ex) {
			Log.d("AndroidNative", "NoClassDefFoundError GCMLoadLastMessage: "
					+ ex.getMessage());
		}

	}
	
	
	
}
