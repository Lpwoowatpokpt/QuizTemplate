package com.lpwoowatpokpt.quiztemplate.Common;

import android.app.Application;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.arch.lifecycle.ProcessLifecycleOwner;
import android.util.Log;

import com.google.firebase.database.FirebaseDatabase;

public class AppController extends Application implements LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onEnterForeground() {
        Log.d("AppController", "Foreground");
        isAppInBackground(false);
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onEnterBackground() {
        Log.d("AppController", "Background");
        isAppInBackground(true);
    }





    public interface ValueChangeListener {
        void onChanged(Boolean value);
    }
    private ValueChangeListener visibilityChangeListener;
    public void setOnVisibilityChangeListener(ValueChangeListener listener) {
        this.visibilityChangeListener = listener;
    }
    private void isAppInBackground(Boolean isBackground) {
        if (null != visibilityChangeListener) {
            visibilityChangeListener.onChanged(isBackground);
        }
    }
    private static AppController mInstance;
    public static AppController getInstance() {
        return mInstance;
    }



    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        mInstance = this;

        // addObserver
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    }

}
