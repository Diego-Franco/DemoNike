package com.defch.demonike;

import android.app.Application;
import android.content.res.Configuration;
import com.pubnub.api.*;


/**
 * Created by DiegoFranco on 3/9/15.
 */
public class MainApplication extends Application {

    private static MainApplication instance;
    private Pubnub pubnub;

    public static MainApplication getInstance() {
        return instance;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        pubnub = new Pubnub(getResources().getString(R.string.publish_key), getResources().getString(R.string.subscribe_key));
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public Pubnub getPubnub() {
        return pubnub;
    }
}
