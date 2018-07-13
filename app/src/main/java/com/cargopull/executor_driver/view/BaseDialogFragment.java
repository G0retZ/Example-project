package com.cargopull.executor_driver.view;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import com.cargopull.executor_driver.application.BaseActivity;
import com.cargopull.executor_driver.application.OnBackPressedInterceptor;
import com.cargopull.executor_driver.di.AppComponent;

/**
 * {@link DialogFragment} с поддержкой:
 * <ul>
 * <li>onBackPressed()</li>
 * <li>Отображения ошибки сети</li>
 * </ul>
 */

public class BaseDialogFragment extends DialogFragment implements OnBackPressedInterceptor {

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
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
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
  void onDependencyInject(AppComponent appComponent) {
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
   * Позволяет отвязать {@link android.app.Fragment} от конкретных {@link Activity}.
   *
   * @param destination пункт назначения
   */
  void navigate(@NonNull String destination) {
    if (baseActivity != null) {
      baseActivity.navigate(destination);
    }
  }

  /**
   * Показать экраном процесса.
   *
   * @param show - показать или нет.
   */
  void showPending(boolean show) {
    if (baseActivity != null) {
      baseActivity.blockWithPending(show);
    }
  }
}
