package com.george.board.appAuth;


import android.support.v7.app.AppCompatDelegate;

/**
 * Application object; ensures that the support library is correctly configured for use of
 * vector drawables.
 */
public class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
}