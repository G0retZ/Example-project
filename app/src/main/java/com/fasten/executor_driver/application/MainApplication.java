package com.fasten.executor_driver.application;

import android.app.Application;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import com.fasten.executor_driver.di.AppComponent;
import com.fasten.executor_driver.di.AppComponentImpl;
import com.fasten.executor_driver.interactor.UnAuthUseCase;
import com.fasten.executor_driver.presentation.persistence.PersistenceViewActions;
import com.fasten.executor_driver.presentation.persistence.PersistenceViewModel;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Application.
 */

public class MainApplication extends Application implements PersistenceViewActions {

  private AppComponent mAppComponent;

  private UnAuthUseCase unAuthUseCase;
  private PersistenceViewModel persistenceViewModel;

  public void setUnAuthUseCase(@NonNull UnAuthUseCase unAuthUseCase) {
    this.unAuthUseCase = unAuthUseCase;
  }

  public void setPersistenceViewModel(
      PersistenceViewModel persistenceViewModel) {
    this.persistenceViewModel = persistenceViewModel;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    mAppComponent = new AppComponentImpl(this.getApplicationContext());
    mAppComponent.inject(this);
    listenForUnAuth();
    persistenceViewModel.getViewStateLiveData().observeForever(viewState -> {
      if (viewState != null) {
        viewState.apply(this);
      }
    });
  }

  @NonNull
  public AppComponent getAppComponent() {
    return mAppComponent;
  }

  @Override
  public void startService(@StringRes int title, @StringRes int text) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      startForegroundService(new Intent(this, PersistenceService.class)
          .putExtra(Intent.EXTRA_TITLE, title)
          .putExtra(Intent.EXTRA_TEXT, text)
      );
    } else {
      startService(new Intent(this, PersistenceService.class)
          .putExtra(Intent.EXTRA_TITLE, title)
          .putExtra(Intent.EXTRA_TEXT, text)
      );
    }
  }

  @Override
  public void stopService() {
    stopService(new Intent(this, PersistenceService.class));
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
