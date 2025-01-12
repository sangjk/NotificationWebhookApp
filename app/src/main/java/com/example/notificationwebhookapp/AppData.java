package com.example.notificationwebhookapp;

public class AppData {
    private final String name;
    private final String packageName;

    public AppData(String name, String packageName) {
        this.name = name;
        this.packageName = packageName;
    }

    public String getName() {
        return name;
    }

    public String getPackageName() {
        return packageName;
    }
}
