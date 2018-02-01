package com.fasten.executor_driver.application;

import android.app.Application;
import android.support.annotation.NonNull;

import com.fasten.executor_driver.di.AppComponent;
import com.fasten.executor_driver.di.AppModule;
import com.fasten.executor_driver.di.DaggerAppComponent;

/**
 * Application.
 */

@SuppressWarnings("WeakerAccess")
public class MainApplication extends Application {

  private AppComponent mAppComponent;

  @Override
  public void onCreate() {
    super.onCreate();
    mAppComponent = DaggerAppComponent.builder().appModule(new AppModule(this)).build();
  }

  @NonNull
  public AppComponent getAppComponent() {
    return mAppComponent;
  }
}
