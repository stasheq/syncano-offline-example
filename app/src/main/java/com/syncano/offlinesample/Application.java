package com.syncano.offlinesample;

import com.syncano.library.SyncanoBuilder;


public class Application extends android.app.Application {

    private static Application instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        new SyncanoBuilder().androidContext(getApplicationContext()).apiKey(BuildConfig.SYNCANO_API_KEY)
                .instanceName(BuildConfig.SYNCANO_INSTANCE_NAME).setAsGlobalInstance(true).build();
    }

    public static Application getInstance() {
        return instance;
    }
}
