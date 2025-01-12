package com.example.notificationwebhookapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.widget.BaseAdapter;


import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AppAdapter extends BaseAdapter {
    private final Context context;
    private final List<AppData> appDataList;
    private final Set<String> selectedApps;

    public AppAdapter(Context context, List<AppData> appDataList, Set<String> selectedApps) {
        this.context = context;
        this.appDataList = appDataList;
        this.selectedApps = new HashSet<>(selectedApps);
    }

    @Override
    public int getCount() {
        return appDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return appDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_app, parent, false);
        }

        TextView appNameTextView = convertView.findViewById(R.id.appName);
        CheckBox appCheckBox = convertView.findViewById(R.id.appCheckBox);

        AppData appData = appDataList.get(position);
        appNameTextView.setText(appData.getName());
        appCheckBox.setChecked(selectedApps.contains(appData.getPackageName()));

        appCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedApps.add(appData.getPackageName());
            } else {
                selectedApps.remove(appData.getPackageName());
            }
        });

        return convertView;
    }

    public Set<String> getSelectedAppPackages() {
        return selectedApps;
    }
}
