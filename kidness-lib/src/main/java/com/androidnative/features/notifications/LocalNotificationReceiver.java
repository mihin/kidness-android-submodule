package com.androidnative.features.notifications;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


public class LocalNotificationReceiver extends BroadcastReceiver {

	
	@SuppressLint("NewApi")
	@Override
	public void onReceive(Context context, Intent intent) {
		
		Log.d("AndroidNative", "Notification recived" );
		
		Bundle extras 		= intent.getExtras();
		String title 		= extras.getString(LocalNotificationsController.TITILE_KEY);
    	String message 		= extras.getString(LocalNotificationsController.MESSAGE_KEY);
    	int notificationId	= extras.getInt(LocalNotificationsController.ID_KEY);
    	String iconName		= extras.getString(LocalNotificationsController.ICON_NAME);
    	String soundName	= extras.getString(LocalNotificationsController.SOUND_NAME);
    	boolean vibro		= extras.getBoolean(LocalNotificationsController.VIBRATION);
    	boolean showIfAppForeground = extras.getBoolean(LocalNotificationsController.SHOW_IF_APP_FOREGROUND);
    	String largeIconName = extras.getString(LocalNotificationsController.LARGE_ICON);
    	
    	if (!showIfAppForeground) {
    		//Check Current Foreground Activity Name
    		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    	    List<RunningTaskInfo> tasks = am.getRunningTasks(1);
    	    if (!tasks.isEmpty()) {
    	    	ComponentName topActivity = tasks.get(0).topActivity;
    	      	if (topActivity.getPackageName().equals(context.getPackageName())) {
    	      		return;
    	        }
    	    }
    	}
    	    	 
    	NotificationManager  mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    	
    	
    	
    
    	Intent appIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
    	//Intent appIntent = new Intent(context, launchIntent.getComponent().getClass());
    	
    
    	
    	
    	Log.d("AndroidNative", "LocalNotificationReceiver: " + appIntent.getComponent().getClass().getName());
    	
    	appIntent.putExtra(LocalNotificationsController.ID_KEY, notificationId);
        PendingIntent contentIntent = PendingIntent.getActivity(context, notificationId, appIntent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_CANCEL_CURRENT );
        
        int iconId = 0x7f020000;
        Resources res = context.getResources();
       	int id = res.getIdentifier(iconName, "drawable", context.getPackageName());
        iconId = id == 0 ? iconId : id;
        
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
	        .setSmallIcon(iconId)
	        .setContentTitle(title)
	        .setStyle(new NotificationCompat.BigTextStyle()
	        .bigText(message))
	        .setContentText(message);
        
        //Set Large Icon for Local Notification
        id = res.getIdentifier(largeIconName, "drawable", context.getPackageName());
        if (id != 0) {
        	Bitmap largeIcon = BitmapFactory.decodeResource(res, id);
        	mBuilder.setLargeIcon(largeIcon);
        }
        
        if (soundName.equals(LocalNotificationsController.SOUND_SILENT)) {
        	mBuilder.setDefaults(Notification.DEFAULT_LIGHTS);
        } else {
        	id = res.getIdentifier(soundName, "raw", context.getPackageName());
            if (id != 0) {
            	Uri uri = Uri.parse("android.resource://" + context.getPackageName() + "/" + id);
            	mBuilder.setDefaults(Notification.DEFAULT_LIGHTS);
                mBuilder.setSound(uri);
            } else {
            	mBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS);
            }
        }
        
        if (vibro) {
        	mBuilder.setVibrate(new long[] {250, 500, 250, 500});
        } else {
        	mBuilder.setVibrate(new long[]{});
        }

        mBuilder.setContentIntent(contentIntent).setAutoCancel(true);
        mNotificationManager.notify(notificationId, mBuilder.build());
        
	}
}
