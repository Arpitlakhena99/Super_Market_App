package com.example.supermarket.singleton;

public class AppConfigSingleton {
    private static AppConfigSingleton instance;

    private String appName = "Super Market App";

    private AppConfigSingleton() {
    }

    public static synchronized AppConfigSingleton getInstance() {
        if (instance == null) {
            instance = new AppConfigSingleton();
        }
        return instance;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }
}

