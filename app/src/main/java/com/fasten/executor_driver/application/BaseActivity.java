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
import com.fasten.executor_driver.presentation.geolocation.GeoLocationNavigate;
import com.fasten.executor_driver.presentation.geolocation.GeoLocationViewModel;
import com.fasten.executor_driver.presentation.splahscreen.SplashScreenNavigate;
import com.fasten.executor_driver.presentation.splahscreen.SplashScreenViewModel;
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
  private SplashScreenViewModel splashScreenViewModel;
  @Nullable
  private GeoLocationViewModel geoLocationViewModel;

  @Inject
  public void setSplashScreenViewModel(@NonNull SplashScreenViewModel splashScreenViewModel) {
    this.splashScreenViewModel = splashScreenViewModel;
  }

  @Inject
  public void setGeoLocationViewModel(@NonNull GeoLocationViewModel geoLocationViewModel) {
    this.geoLocationViewModel = geoLocationViewModel;
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getDiComponent().inject(this);
    if (splashScreenViewModel == null || geoLocationViewModel == null) {
      throw new RuntimeException("Shit! WTF?!");
    }
    splashScreenViewModel.getNavigationLiveData().observeForever(destination -> {
      if (destination != null) {
        navigate(destination);
      }
    });
    geoLocationViewModel.getNavigationLiveData().observeForever(destination -> {
      if (destination != null) {
        navigate(destination);
      }
    });
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
                .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        );
        break;
      case SplashScreenNavigate.NO_NETWORK:
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
      case SplashScreenNavigate.AUTHORIZE:
        startActivity(
            new Intent(this, LoginActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK)
        );
        break;
      case SplashScreenNavigate.MAP_SHIFT_CLOSED:
        startActivity(
            new Intent(this, MapActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK)
        );
        break;
      case SplashScreenNavigate.MAP_SHIFT_OPENED:
        startActivity(
            new Intent(this, MapActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK)
        );
        break;
      case SplashScreenNavigate.MAP_ONLINE:
        startActivity(
            new Intent(this, OnlineActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK)
        );
        break;
    }
  }
}
