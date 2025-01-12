package com.example.notificationwebhookapp;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.net.Uri;
import android.content.ComponentName;
import android.content.pm.PackageInfo;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import java.util.Arrays;

import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;
import android.provider.Settings;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ListView appList;
    private List<AppInfo> installedApps;
    private AppListAdapter adapter;

    private static final String PREFS_NAME = "NotificationWebhookPrefs";
    private static final String SELECTED_APPS_KEY = "SelectedApps";
    private static final String CHANNEL_ID = "my_channel_id";
    private static final int NOTIFICATION_ID = 1;
    private static final String WEBHOOK_URL_KEY = "webhookUrl";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appList = (ListView) findViewById(R.id.appList);
        installedApps = getInstalledApps();
        adapter = new AppListAdapter(this, installedApps);
        appList.setAdapter(adapter);

        Button settingsButton = findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        Button saveAppsButton = findViewById(R.id.saveAppsButton);
        saveAppsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSelectedApps();
            }
        });

        Button enableNotificationsButton = findViewById(R.id.enableNotificationsButton);
        enableNotificationsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkNotificationListenerEnabled();
            }
        });

        loadSavedAppSelections();
        checkNotificationListenerEnabled();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent); // Set the new intent
        handleIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        handleIntent(getIntent());
    }

    private void handleIntent(Intent intent) {
        if (intent != null && "com.example.SEND_WEBHOOK".equals(intent.getAction())) {
            Log.d(TAG, "Intent received to send webhook");
            String title = intent.getStringExtra("title");
            String text = intent.getStringExtra("text");
            String packageName = intent.getStringExtra("package");
            Log.d(TAG, "Webhook details - Title: " + title + ", Text: " + text + ", Package: " + packageName);

            // Modify the JSON structure to remove the title, text, and package labels
            String jsonPayload = text;
            sendWebhookMessage(jsonPayload);
        }
    }

    private List<AppInfo> getInstalledApps() {
        List<AppInfo> apps = new ArrayList<>();
        PackageManager pm = getPackageManager();

        // Get a list of all installed applications
        List<ApplicationInfo> installedApps = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo appInfo : installedApps) {
            try {
                // Skip system apps that haven't been updated
                if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0 &&
                        (appInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 0) {
                    continue;
                }

                String appName = appInfo.loadLabel(pm).toString();
                Drawable icon = appInfo.loadIcon(pm);
                String packageName = appInfo.packageName;
                apps.add(new AppInfo(appName, packageName, icon));

            } catch (Exception e) {
                Log.e(TAG, "Error loading app info", e);
            }
        }

        // Sort apps alphabetically by name
        apps.sort((app1, app2) -> app1.name.compareToIgnoreCase(app2.name));

        return apps;
    }

    private void saveSelectedApps() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Set<String> selectedAppsSet = new HashSet<>();

        for (AppInfo appInfo : installedApps) {
            if (appInfo.isSelected) {
                selectedAppsSet.add(appInfo.packageName);
            }
        }

        editor.putStringSet(SELECTED_APPS_KEY, selectedAppsSet);
        editor.apply();
        Log.d(TAG, "Selected apps saved: " + selectedAppsSet);
    }

    private void loadSavedAppSelections() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        Set<String> selectedAppsSet = prefs.getStringSet(SELECTED_APPS_KEY, new HashSet<>());

        // Handle migration from String to Set<String>
        if (selectedAppsSet.isEmpty()) {
            String selectedAppsString = prefs.getString(SELECTED_APPS_KEY, "");
            if (!selectedAppsString.isEmpty()) {
                selectedAppsSet = new HashSet<>(Arrays.asList(selectedAppsString.split(",")));
                // Save the migrated data back to SharedPreferences
                SharedPreferences.Editor editor = prefs.edit();
                editor.putStringSet(SELECTED_APPS_KEY, selectedAppsSet);
                editor.apply();
            }
        }

        for (String packageName : selectedAppsSet) {
            for (AppInfo appInfo : installedApps) {
                if (appInfo.packageName.equals(packageName)) {
                    appInfo.isSelected = true;
                    break;
                }
            }
        }
        adapter.notifyDataSetChanged();
        Log.d(TAG, "Loaded saved app selections: " + selectedAppsSet);
    }

    private void checkNotificationListenerEnabled() {
        if (!isNotificationServiceEnabled()) {
            Log.d(TAG, "Notification listener is not enabled. Prompting user to enable it.");
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            Log.d(TAG, "Notification listener is already enabled.");
        }
    }

    private boolean isNotificationServiceEnabled() {
        String packageName = getPackageName();
        String flat = Settings.Secure.getString(getContentResolver(), "enabled_notification_listeners");
        if (flat != null && !flat.isEmpty()) {
            String[] names = flat.split(":");
            for (String name : names) {
                ComponentName componentName = ComponentName.unflattenFromString(name);
                if (componentName != null && packageName.equals(componentName.getPackageName())) {
                    return true;
                }
            }
        }
        return false;
    }

    private void createAndShowNotification() {
        createNotificationChannel(); // Create the notification channel if needed

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Notification Title")
                .setContentText("Notification Content")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
        Log.d(TAG, "Notification created and shown");
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name); // Replace with your channel name
            String description = getString(R.string.channel_description); // Replace with your channel description
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            Log.d(TAG, "Notification channel created");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) { // Check if it's the notification permission request
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, create and show notification
                createAndShowNotification();
            } else {
                // Permission denied, handle accordingly (e.g., show a message to the user)
                Log.d(TAG, "Notification permission denied");
            }
        }
    }

    public class AppListAdapter extends BaseAdapter {
        private Context context;
        private List<AppInfo> appInfoList;

        public AppListAdapter(Context context, List<AppInfo> appInfoList) {
            this.context = context;
            this.appInfoList = appInfoList;
        }

        @Override
        public int getCount() {
            return appInfoList.size();
        }

        @Override
        public AppInfo getItem(int position) {
            return appInfoList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.app_list_item, parent, false);
            }

            ImageView appIcon = convertView.findViewById(R.id.appIcon);
            TextView appName = convertView.findViewById(R.id.appName);
            CheckBox appCheckbox = convertView.findViewById(R.id.appCheckbox);

            AppInfo appInfo = getItem(position);
            appIcon.setImageDrawable(appInfo.icon);
            appName.setText(appInfo.name);
            appCheckbox.setChecked(appInfo.isSelected);

            appCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    appInfo.isSelected = isChecked;
                    Log.d(TAG, "App selection changed: " + appInfo.name + " - " + isChecked);
                }
            });

            return convertView;
        }
    }

    public static class AppInfo {
        public String name;
        public String packageName;
        public android.graphics.drawable.Drawable icon;
        public boolean isSelected;

        public AppInfo(String name, String packageName, android.graphics.drawable.Drawable icon) {
            this.name = name;
            this.packageName = packageName;
            this.icon = icon;
            this.isSelected = false;
        }
    }

    // New section to send the webhook message
    private void sendWebhookMessage(String jsonPayload) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String webhookUrl = prefs.getString(WEBHOOK_URL_KEY, "");

        if (webhookUrl.isEmpty()) {
            Log.e(TAG, "Webhook URL is not set in preferences.");
            return;
        }

        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.get("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(jsonPayload, JSON);
        Request request = new Request.Builder()
                .url(webhookUrl)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Failed to send webhook message", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Webhook message sent successfully");
                } else {
                    Log.e(TAG, "Failed to send webhook message: " + response.message());
                }
            }
        });
    }
}
