package com.fasten.executor_driver.application;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Application;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import com.fasten.executor_driver.R;
import com.fasten.executor_driver.di.AppComponent;
import com.fasten.executor_driver.di.AppComponentImpl;
import com.fasten.executor_driver.entity.ExecutorState;
import com.fasten.executor_driver.entity.GeoLocation;
import com.fasten.executor_driver.interactor.DataReceiver;
import com.fasten.executor_driver.interactor.GeoLocationUseCase;
import com.fasten.executor_driver.interactor.UnAuthUseCase;
import com.fasten.executor_driver.presentation.persistence.PersistenceViewActions;
import com.fasten.executor_driver.presentation.persistence.PersistenceViewModel;
import com.fasten.executor_driver.presentation.splahscreen.SplashScreenViewActions;
import com.fasten.executor_driver.presentation.splahscreen.SplashScreenViewModel;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import javax.inject.Inject;

/**
 * Application.
 */

public class MainApplication extends Application implements PersistenceViewActions,
    SplashScreenViewActions {

  private boolean showError = false;
  private AppComponent mAppComponent;
  private UnAuthUseCase unAuthUseCase;
  private PersistenceViewModel persistenceViewModel;
  private SplashScreenViewModel splashScreenViewModel;
  private GeoLocationUseCase geoLocationUseCase;
  private Activity currentActivity;
  private final ActivityLifecycleCallbacks problemsActivityLifecycleCallbacks = new ActivityLifecycleCallbacks() {

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
    }

    @Override
    public void onActivityResumed(Activity activity) {
      currentActivity = activity;
      showNetworkError(currentActivity);
    }

    @Override
    public void onActivityPaused(Activity activity) {
      currentActivity = null;
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
      if (currentActivity != null && activity instanceof GeolocationResolutionActivity) {
        reloadGeoLocations();
      }
    }
  };
  private DataReceiver<GeoLocation> geoLocationDataReceiver;
  private DataReceiver<ExecutorState> executorStateDataReceiver;

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
  public void setSplashScreenViewModel(SplashScreenViewModel splashScreenViewModel) {
    this.splashScreenViewModel = splashScreenViewModel;
  }

  @Inject
  public void setGeoLocationUseCase(GeoLocationUseCase geoLocationUseCase) {
    this.geoLocationUseCase = geoLocationUseCase;
  }

  @Inject
  public void setGeoLocationDataReceiver(DataReceiver<GeoLocation> geoLocationDataReceiver) {
    this.geoLocationDataReceiver = geoLocationDataReceiver;
  }

  public void setExecutorStateDataReceiver(DataReceiver<ExecutorState> executorStateDataReceiver) {
    this.executorStateDataReceiver = executorStateDataReceiver;
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
    splashScreenViewModel.getViewStateLiveData().observeForever(viewState -> {
      if (viewState != null) {
        viewState.apply(this);
      }
    });
    registerActivityLifecycleCallbacks(problemsActivityLifecycleCallbacks);
    listenForGeoLocations();
    reloadGeoLocations();
    listenForExecutorState();
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
        .doAfterTerminate(this::listenForUnAuth)
        .subscribe(
            () -> {
              geoLocationUseCase.stop()
                  .subscribeOn(Schedulers.single())
                  .observeOn(AndroidSchedulers.mainThread())
                  .subscribe(
                      () -> {
                      }, throwable -> {
                      }
                  );
              stopService();
              Intent intent = new Intent(this, LoginActivity.class);
              intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
              startActivity(intent);
            }, throwable -> {
            }
        );
  }

  private void reloadGeoLocations() {
    geoLocationUseCase.reload()
        .subscribeOn(Schedulers.single())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            () -> {
            }, throwable -> {
            }
        );
  }

  private void listenForGeoLocations() {
    geoLocationDataReceiver.get()
        .subscribeOn(Schedulers.single())
        .observeOn(AndroidSchedulers.mainThread())
        .doAfterTerminate(this::listenForGeoLocations)
        .subscribe(geoLocation -> {
        }, throwable -> startActivity(new Intent(this, GeolocationResolutionActivity.class)));
  }

  private void listenForExecutorState() {
    executorStateDataReceiver.get()
        .subscribeOn(Schedulers.single())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(executorState -> {
          Intent intent = null;
          switch (executorState) {
            case SHIFT_CLOSED:
              intent = new Intent(this, MapActivity.class);
              break;
            case SHIFT_OPENED:
              intent = new Intent(this, MapActivity.class);
              break;
            case ONLINE:
              intent = new Intent(this, OnlineActivity.class);
              break;
          }
          intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
          startActivity(intent);
        }, throwable -> {
        });
  }

  @Override
  public void showPending(boolean pending) {
    System.out.println(pending);
  }

  @Override
  public void showNetworkErrorMessage(boolean show) {
    showError = show;
    showNetworkError(currentActivity);
  }

  private void showNetworkError(Activity activity) {
    if (activity != null && showError) {
      new Builder(activity)
          .setTitle(R.string.error)
          .setMessage("Без сети не работаем!")
          .setCancelable(false)
          .setPositiveButton(
              getString(android.R.string.ok),
              (a, b) -> android.os.Process.killProcess(android.os.Process.myPid())
          )
          .create()
          .show();
    }
  }
}
