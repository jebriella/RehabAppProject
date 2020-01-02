package com.example.rehabappproject;

import android.app.Application;
import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;

/**
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
