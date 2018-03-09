package com.fasten.executor_driver.application;

import android.app.Application;
import android.content.Intent;
import android.support.annotation.NonNull;
import com.fasten.executor_driver.di.AppComponent;
import com.fasten.executor_driver.di.AppComponentImpl;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * Application.
 */

@SuppressWarnings("WeakerAccess")
public class MainApplication extends Application {

  private AppComponent mAppComponent;

  @Override
  public void onCreate() {
    super.onCreate();
    Subject<String> logoutEventSubject = PublishSubject.create();
    logoutEventSubject
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(message -> {
          Intent intent = new Intent(this, LoginActivity.class);
          intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
          startActivity(intent);
        });

    mAppComponent = new AppComponentImpl(this, logoutEventSubject);
  }

  @NonNull
  public AppComponent getAppComponent() {
    return mAppComponent;
  }
}
