package com.fasten.executor_driver.application;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import com.fasten.executor_driver.di.AppComponent;
import com.fasten.executor_driver.di.AppComponentImpl;
import com.fasten.executor_driver.entity.GeoLocation;
import com.fasten.executor_driver.interactor.DataReceiver;
import com.fasten.executor_driver.interactor.GeoLocationUseCase;
import com.fasten.executor_driver.interactor.UnAuthUseCase;
import com.fasten.executor_driver.presentation.persistence.PersistenceViewActions;
import com.fasten.executor_driver.presentation.persistence.PersistenceViewModel;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

/**
 * Application.
 */

public class MainApplication extends Application implements PersistenceViewActions {

  private AppComponent mAppComponent;

  private UnAuthUseCase unAuthUseCase;
  private PersistenceViewModel persistenceViewModel;
  private GeoLocationUseCase geoLocationUseCase;
  private DataReceiver<GeoLocation> geoLocationDataReceiver;

  private ActivityLifecycleCallbacks problemsActivityLifecycleCallbacks = new ActivityLifecycleCallbacks() {
    private boolean onScreen;

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
    }

    @Override
    public void onActivityResumed(Activity activity) {
      onScreen = true;
    }

    @Override
    public void onActivityPaused(Activity activity) {
      onScreen = false;
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
      if (onScreen && activity instanceof GeolocationResolutionActivity) {
        reloadGeoLocations();
      }
    }
  };

  @Inject
  public void setUnAuthUseCase(@NonNull UnAuthUseCase unAuthUseCase) {
    this.unAuthUseCase = unAuthUseCase;
  }

  @Inject
  public void setPersistenceViewModel(
      PersistenceViewModel persistenceViewModel) {
    this.persistenceViewModel = persistenceViewModel;
  }

  @Inject
  public void setGeoLocationUseCase(GeoLocationUseCase geoLocationUseCase) {
    this.geoLocationUseCase = geoLocationUseCase;
  }

  @Inject
  public void setGeoLocationDataReceiver(DataReceiver<GeoLocation> geoLocationDataReceiver) {
    this.geoLocationDataReceiver = geoLocationDataReceiver;
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
    listenForGeoLocations();
    reloadGeoLocations();
    registerActivityLifecycleCallbacks(problemsActivityLifecycleCallbacks);
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

  private void reloadGeoLocations() {
    geoLocationUseCase.reload().subscribeOn(Schedulers.single())
        .observeOn(AndroidSchedulers.mainThread()).subscribe();
  }

  private void listenForGeoLocations() {
    geoLocationDataReceiver.get()
        .subscribeOn(Schedulers.single())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(geoLocation -> {
        }, throwable -> {
          startActivity(new Intent(this, GeolocationResolutionActivity.class));
          listenForGeoLocations();
        }, this::listenForGeoLocations);
  }
}
