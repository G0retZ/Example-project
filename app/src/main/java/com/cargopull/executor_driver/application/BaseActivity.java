package com.cargopull.executor_driver.application;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.di.AppComponent;
import com.cargopull.executor_driver.presentation.CommonNavigate;
import com.cargopull.executor_driver.view.PendingDialogFragment;
import com.cargopull.executor_driver.view.ServerConnectionFragment;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

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
  private final PendingDialogFragment pendingDialogFragment = new PendingDialogFragment();
  @NonNull
  private final LinkedList<OnBackPressedInterceptor> onBackPressedInterceptors = new LinkedList<>();
  private final Set<String> blockers = new HashSet<>();

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
      case CommonNavigate.NO_CONNECTION:
        Fragment fragment = getSupportFragmentManager()
            .findFragmentById(R.id.fragment_server_connection);
        if (fragment != null && fragment instanceof ServerConnectionFragment) {
          ((ServerConnectionFragment) fragment).blink();
        }
        break;
      case CommonNavigate.SERVER_DATA_ERROR:
        ((MainApplication) getApplication()).navigate(destination);
        break;
      case CommonNavigate.EXIT:
        blockWithPending(true, "exit");
        ((MainApplication) getApplication()).navigate(destination);
        break;
    }
  }

  /**
   * Заблокировать ЮИ экраном процесса. блокирует экран диалого процесса. Ведет учет запросов
   * блкоировки/разблокировки. Если число запросов блокировки больше запросов разблокировки, то
   * отображаем, иначе - не отображаем.
   *
   * @param block - блокировать или нет.
   * @param blockerId - Уникальный ИД блокирующего.
   */
  public void blockWithPending(boolean block, @NonNull String blockerId) {
    if (block) {
      blockers.add(blockerId);
    } else {
      blockers.remove(blockerId);
    }
    if (blockers.isEmpty()) {
      if (pendingDialogFragment.isShowing()) {
        pendingDialogFragment.dismiss();
      }
    } else {
      if (!pendingDialogFragment.isShowing()) {
        pendingDialogFragment.setCancelable(false);
        pendingDialogFragment.show(getSupportFragmentManager(), "pending");
      }
    }
  }
}
