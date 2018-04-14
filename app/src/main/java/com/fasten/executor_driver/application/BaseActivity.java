package com.fasten.executor_driver.application;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import com.fasten.executor_driver.R;
import com.fasten.executor_driver.di.AppComponent;
import com.fasten.executor_driver.presentation.executorstate.ExecutorStateNavigate;
import com.fasten.executor_driver.presentation.executorstate.ExecutorStateViewModel;
import com.fasten.executor_driver.presentation.geolocation.GeoLocationNavigate;
import com.fasten.executor_driver.presentation.geolocation.GeoLocationViewModel;
import java.util.LinkedList;
import javax.inject.Inject;

/**
 * Базовая {@link Activity} с поддержкой:
 * <ul>
 * <li>Перехвата нажатия назад</li>
 * <li>Lifecycle components</li>
 * <li>переходов при смене состояния</li>
 * </ul>
 */

@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {

  @NonNull
  private final LinkedList<OnBackPressedInterceptor> onBackPressedInterceptors = new LinkedList<>();
  @Nullable
  private ExecutorStateViewModel executorStateViewModel;
  @Nullable
  private GeoLocationViewModel geoLocationViewModel;

  @Inject
  public void setExecutorStateViewModel(@NonNull ExecutorStateViewModel executorStateViewModel) {
    this.executorStateViewModel = executorStateViewModel;
  }

  @Inject
  public void setGeoLocationViewModel(@NonNull GeoLocationViewModel geoLocationViewModel) {
    this.geoLocationViewModel = geoLocationViewModel;
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getDiComponent().inject(this);
    if (executorStateViewModel == null || geoLocationViewModel == null) {
      throw new RuntimeException("Shit! WTF?!");
    }
    executorStateViewModel.getNavigationLiveData().observe(this, destination -> {
      if (destination != null) {
        navigate(destination);
      }
    });
    geoLocationViewModel.getNavigationLiveData().observe(this, destination -> {
      if (destination != null) {
        navigate(destination);
      }
    });
    initGeoLocations();
    initExecutorStates();
  }

  /**
   * Добавляет {@link OnBackPressedInterceptor} в реестр перехватчиков.
   *
   * @param interceptor перехватчик нажатия кнопки "назад"
   */
  public void registerOnBackPressedInterceptor(@NonNull OnBackPressedInterceptor interceptor) {
    onBackPressedInterceptors.add(interceptor);
  }

  /**
   * Удаляет {@link OnBackPressedInterceptor} из реестра перехватчиков.
   *
   * @param interceptor перехватчик нажатия кнопки "назад"
   */
  public void unregisterOnBackPressedInterceptor(@NonNull OnBackPressedInterceptor interceptor) {
    onBackPressedInterceptors.remove(interceptor);
  }

  /**
   * Пробегает по реестру перехватчиков пока кто либо не обработает нажатие "назад".
   * Если никто не обработал то вызывает воплощение метода в родителе (super).
   */
  @Override
  public void onBackPressed() {
    for (OnBackPressedInterceptor onBackPressedInterceptor : onBackPressedInterceptors) {
      if (onBackPressedInterceptor.onBackPressed()) {
        return;
      }
    }
    super.onBackPressed();
  }

  /**
   * Возвращает {@link AppComponent} для внедрения зависимостей.
   *
   * @return DI компонент
   */
  @NonNull
  public AppComponent getDiComponent() {
    return ((MainApplication) getApplication()).getAppComponent();
  }

  /**
   * Метод перехода куда либо.
   * Позволяет отвязать {@link android.app.Fragment} от конкретных {@link Activity}.
   *
   * @param destination пункт назначения
   */
  public void navigate(@NonNull String destination) {
    switch (destination) {
      case GeoLocationNavigate.RESOLVE_GEO_PROBLEM:
        startActivity(
            new Intent(this, GeolocationResolutionActivity.class)
        );
        finish();
        break;
      case ExecutorStateNavigate.NO_NETWORK:
        new Builder(this)
            .setTitle(R.string.error)
            .setMessage("Без сети не работаем!")
            .setCancelable(false)
            .setPositiveButton(
                getString(android.R.string.ok),
                (a, b) -> android.os.Process.killProcess(android.os.Process.myPid())
            )
            .create()
            .show();
        break;
      case ExecutorStateNavigate.AUTHORIZE:
        startActivity(new Intent(this, LoginActivity.class));
        finish();
        break;
      case ExecutorStateNavigate.MAP_SHIFT_CLOSED:
        startActivity(new Intent(this, MapActivity.class));
        finish();
        break;
      case ExecutorStateNavigate.MAP_SHIFT_OPENED:
        startActivity(new Intent(this, OnlineActivity.class));
        finish();
        break;
      case ExecutorStateNavigate.MAP_ONLINE:
        startActivity(new Intent(this, OnlineActivity.class));
        finish();
        break;
      case ExecutorStateNavigate.OFFER_CONFIRMATION:
        new Builder(this)
            .setTitle(R.string.error)
            .setMessage("Подтверждаем заказ!")
            .setCancelable(false)
            .setPositiveButton(
                getString(android.R.string.ok),
                (a, b) -> android.os.Process.killProcess(android.os.Process.myPid())
            )
            .create()
            .show();
        break;
      case ExecutorStateNavigate.APPROACHING_LOAD_POINT:
        new Builder(this)
            .setTitle(R.string.error)
            .setMessage("На пути к клиенту!")
            .setCancelable(false)
            .setPositiveButton(
                getString(android.R.string.ok),
                (a, b) -> android.os.Process.killProcess(android.os.Process.myPid())
            )
            .create()
            .show();
        break;
    }
  }

  void initExecutorStates() {
    if (executorStateViewModel != null) {
      executorStateViewModel.initializeExecutorState();
    }
  }

  void initGeoLocations() {
    if (geoLocationViewModel != null) {
      geoLocationViewModel.updateGeoLocations();
    }
  }
}
