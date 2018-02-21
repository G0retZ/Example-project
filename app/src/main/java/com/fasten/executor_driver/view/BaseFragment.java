package com.fasten.executor_driver.view;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import com.fasten.executor_driver.application.BaseActivity;
import com.fasten.executor_driver.application.OnBackPressedInterceptor;
import com.fasten.executor_driver.di.AppComponent;

/**
 * {@link Fragment} с поддержкой:
 * <ul>
 * <li>onBackPressed()</li>
 * <li>Отображения ошибки сети</li>
 * </ul>
 */

public class BaseFragment extends Fragment implements OnBackPressedInterceptor {

  @Nullable
  private BaseActivity baseActivity;

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    try {
      baseActivity = (BaseActivity) context;
    } catch (ClassCastException e) {
      Log.w(this.getClass().getName(), context.getClass().getName() +
          " не наследует BaseActivity. OnBackPressed никогда не будет вызван.");
    }
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (baseActivity != null) {
      onDependencyInject(baseActivity.getDiComponent());
    }
  }

  @Override
  public void onDetach() {
    super.onDetach();
    baseActivity = null;
  }

  /**
   * Колбэк для внедрения зависимостей. Вызывается сразу после завершения {@link #onCreate(Bundle)}
   * здесь.
   *
   * @param appComponent - компонент, который может произвести внедрение
   */
  protected void onDependencyInject(AppComponent appComponent) {
  }

  @Override
  public void onResume() {
    super.onResume();
    if (baseActivity != null) {
      baseActivity.registerOnBackPressedInterceptor(this);
    }
  }

  @Override
  public void onPause() {
    super.onPause();
    if (baseActivity != null) {
      baseActivity.unregisterOnBackPressedInterceptor(this);
    }
  }

  @Override
  public boolean onBackPressed() {
    return false;
  }

  @SuppressWarnings("unused")
  public void showNoNetworkError() {
    if (getView() != null) {
      Snackbar.make(getView(), "", Snackbar.LENGTH_SHORT).show();
    }
  }

  /**
   * Метод перехода куда либо.
   * Позволяет отвязать {@link android.app.Fragment} от конкретных {@link Activity}
   *
   * @param destination пункт назначения.
   */
  protected void navigate(@NonNull String destination) {
    if (baseActivity != null) {
      baseActivity.navigate(destination);
    }
  }
}
