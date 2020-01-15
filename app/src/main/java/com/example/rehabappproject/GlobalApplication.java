package com.example.rehabappproject;

import android.app.Application;
import android.content.Context;

import com.google.firebase.auth.FirebaseUser;

/**
 * This code is part of the "Android Get Application Context From Anywhere" tutorial by Jerry Zhao
     * Created by Jerry on 3/21/2018.
     */

    public class GlobalApplication extends Application {

        private static Context appContext;
        private static FirebaseUser user;
        private static StatusBarHandler statusBarHandler;

        @Override
        public void onCreate() {
            super.onCreate();
            appContext = getApplicationContext();

        }

        public static Context getAppContext() {
            return appContext;
        }
}
