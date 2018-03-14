package com.fasten.executor_driver.application;

import android.app.Application;
import android.content.Intent;
import android.support.annotation.NonNull;
import com.fasten.executor_driver.di.AppComponent;
import com.fasten.executor_driver.di.AppComponentImpl;
import com.fasten.executor_driver.interactor.UnAuthUseCase;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Application.
 */

public class MainApplication extends Application {

  private AppComponent mAppComponent;

  private UnAuthUseCase unAuthUseCase;

  public void setUnAuthUseCase(@NonNull UnAuthUseCase unAuthUseCase) {
    this.unAuthUseCase = unAuthUseCase;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    mAppComponent = new AppComponentImpl(this.getApplicationContext());
    mAppComponent.inject(this);
    listenForUnAuth();
  }

  @NonNull
  public AppComponent getAppComponent() {
    return mAppComponent;
  }

  private void listenForUnAuth() {
    unAuthUseCase.getUnauthorized()
        .subscribeOn(Schedulers.single())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            () -> {
              Intent intent = new Intent(this, LoginActivity.class);
              intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
              startActivity(intent);
              listenForUnAuth();
            }, throwable -> {
            }
        );
  }
}
