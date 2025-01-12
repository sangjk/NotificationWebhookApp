package com.example.notificationwebhookapp;

import android.app.Notification;
import android.content.Intent;
import android.content.SharedPreferences;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;

public class NotificationListener extends NotificationListenerService {

    private static final String TAG = "NotificationListener";
    private static final String PREFS_NAME = "NotificationWebhookPrefs";
    private static final String SELECTED_APPS_KEY = "SelectedApps";

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Log.d(TAG, "Notification posted: " + sbn.getPackageName());

        String packageName = sbn.getPackageName();

        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        Set<String> selectedApps = sharedPreferences.getStringSet(SELECTED_APPS_KEY, new HashSet<>());

        Log.d(TAG, "Selected apps: " + selectedApps);

        if (selectedApps.contains(packageName)) {
            Log.d(TAG, "Notification is from a selected app: " + packageName);

            Notification notification = sbn.getNotification();
            if (notification != null && notification.extras != null) {
                String title = notification.extras.getString(Notification.EXTRA_TITLE);
                String text = notification.extras.getString(Notification.EXTRA_TEXT);

                // Log the notification details
                Log.d(TAG, "Notification details - Title: " + title + ", Text: " + text);

                // Send webhook message
                Intent intent = new Intent(this, MainActivity.class);
                intent.setAction("com.example.SEND_WEBHOOK");
                intent.putExtra("title", title);
                intent.putExtra("text", text);
                intent.putExtra("package", packageName);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                Log.d(TAG, "Intent sent to MainActivity to forward webhook message");
            } else {
                Log.e(TAG, "Notification or extras are null for package: " + packageName);
            }
        } else {
            Log.d(TAG, "Notification is not from a selected app: " + packageName);
        }
    }
}
