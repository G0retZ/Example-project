package com.cargopull.executor_driver.application;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.di.AppComponent;
import com.cargopull.executor_driver.presentation.CommonNavigate;
import com.cargopull.executor_driver.presentation.announcement.AnnouncementStateViewActions;
import com.cargopull.executor_driver.presentation.announcement.AnnouncementViewModel;
import com.cargopull.executor_driver.presentation.executorstate.ExecutorStateViewActions;
import com.cargopull.executor_driver.presentation.executorstate.ExecutorStateViewModel;
import com.cargopull.executor_driver.presentation.serverconnection.ServerConnectionNavigate;
import com.cargopull.executor_driver.presentation.serverconnection.ServerConnectionViewModel;
import com.cargopull.executor_driver.presentation.servertime.ServerTimeViewModel;
import com.cargopull.executor_driver.presentation.updatemessage.UpdateMessageViewActions;
import com.cargopull.executor_driver.presentation.updatemessage.UpdateMessageViewModel;
import com.cargopull.executor_driver.view.PendingDialogFragment;
import com.cargopull.executor_driver.view.ServerConnectionFragment;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import javax.inject.Inject;

/**
 * Базовая {@link Activity} с поддержкой:
 * <ul>
 * <li>Перехвата нажатия назад</li>
 * <li>Lifecycle components</li>
 * <li>переходов при смене состояния</li>
 * <li>Показа объявлений</li>
 * <li>Показа сообщений об ошибках данных</li>
 * <li>Показа сообщений о новой версии</li>
 * </ul>
 */

@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity implements ExecutorStateViewActions,
    AnnouncementStateViewActions, UpdateMessageViewActions {

  @NonNull
  private final PendingDialogFragment pendingDialogFragment = new PendingDialogFragment();
  @NonNull
  private final LinkedList<OnBackPressedInterceptor> onBackPressedInterceptors = new LinkedList<>();
  private final Set<String> blockers = new HashSet<>();
  @Nullable
  private ExecutorStateViewModel executorStateViewModel;
  @Nullable
  private UpdateMessageViewModel updateMessageViewModel;
  @Nullable
  private AnnouncementViewModel announcementViewModel;
  @Nullable
  private ServerConnectionViewModel serverConnectionViewModel;
  @Nullable
  private ServerTimeViewModel serverTimeViewModel;
  @Nullable
  private Dialog onlineDialog;
  @Nullable
  private Dialog announcementDialog;
  @Nullable
  private Dialog updateDialog;
  @Nullable
  private Dialog errorDialog;
  private boolean resumed;

  @Inject
  public void setExecutorStateViewModel(@NonNull ExecutorStateViewModel executorStateViewModel) {
    this.executorStateViewModel = executorStateViewModel;
  }

  @Inject
  public void setUpdateMessageViewModel(@NonNull UpdateMessageViewModel updateMessageViewModel) {
    this.updateMessageViewModel = updateMessageViewModel;
  }

  @Inject
  public void setAnnouncementViewModel(@NonNull AnnouncementViewModel announcementViewModel) {
    this.announcementViewModel = announcementViewModel;
  }

  @Inject
  public void setServerConnectionViewModel(
      @NonNull ServerConnectionViewModel serverConnectionViewModel) {
    this.serverConnectionViewModel = serverConnectionViewModel;
  }

  @Inject
  public void setServerTimeViewModel(@Nullable ServerTimeViewModel serverTimeViewModel) {
    this.serverTimeViewModel = serverTimeViewModel;
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getDiComponent().inject(this);
    if (executorStateViewModel == null) {
      throw new IllegalStateException("Граф зависимостей поломан!");
    }
    if (updateMessageViewModel == null) {
      throw new IllegalStateException("Граф зависимостей поломан!");
    }
    if (announcementViewModel == null) {
      throw new IllegalStateException("Граф зависимостей поломан!");
    }
    if (serverConnectionViewModel == null) {
      throw new IllegalStateException("Граф зависимостей поломан!");
    }
    if (serverTimeViewModel == null) {
      throw new IllegalStateException("Граф зависимостей поломан!");
    }
    executorStateViewModel.getViewStateLiveData().observe(this, viewState -> {
      if (viewState != null) {
        viewState.apply(this);
      }
    });
    updateMessageViewModel.getViewStateLiveData().observe(this, viewState -> {
      if (viewState != null) {
        viewState.apply(this);
      }
    });
    announcementViewModel.getViewStateLiveData().observe(this, viewState -> {
      if (viewState != null) {
        viewState.apply(this);
      }
    });
    serverConnectionViewModel.getNavigationLiveData().observe(this, destination -> {
      if (destination != null) {
        navigate(destination);
      }
    });
    serverTimeViewModel.getNavigationLiveData().observe(this, destination -> {
      if (destination != null) {
        navigate(destination);
      }
    });
  }

  @Override
  public void onResume() {
    super.onResume();
    resumed = true;
    if (onlineDialog != null) {
      onlineDialog.show();
    }
    if (announcementDialog != null) {
      announcementDialog.show();
    }
    if (updateDialog != null) {
      updateDialog.show();
    }
    if (errorDialog != null) {
      errorDialog.show();
    }
  }

  @Override
  public void onPause() {
    super.onPause();
    resumed = false;
    if (onlineDialog != null && onlineDialog.isShowing()) {
      onlineDialog.dismiss();
    }
    if (announcementDialog != null && announcementDialog.isShowing()) {
      announcementDialog.dismiss();
    }
    if (updateDialog != null && updateDialog.isShowing()) {
      updateDialog.dismiss();
    }
    if (errorDialog != null && errorDialog.isShowing()) {
      errorDialog.dismiss();
    }
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
      case CommonNavigate.NO_CONNECTION:
        Fragment fragment = getSupportFragmentManager()
            .findFragmentById(R.id.fragment_server_connection);
        if (fragment != null && fragment instanceof ServerConnectionFragment) {
          ((ServerConnectionFragment) fragment).blink();
        }
        break;
      case CommonNavigate.SERVER_DATA_ERROR:
        ((MainApplication) getApplication()).navigate(destination);
        errorDialog = new Builder(this)
            .setTitle(R.string.error)
            .setMessage(R.string.server_data_format_error)
            .setCancelable(false)
            .setPositiveButton(getString(android.R.string.ok), (a, b) -> exitAndKill())
            .create();
        if (resumed) {
          errorDialog.show();
        }
        break;
      case ServerConnectionNavigate.VERSION_DEPRECATED:
        ((MainApplication) getApplication()).navigate(destination);
        errorDialog = new Builder(this)
            .setTitle(R.string.error)
            .setMessage(R.string.version_deprecated)
            .setCancelable(false)
            .setPositiveButton(getString(R.string.update), (a, b) -> {
              try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(
                    "market://details?id=com.cargopull.executor_driver"
                )));
              } catch (android.content.ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(
                    "https://play.google.com/store/apps/details?id=com.cargopull.executor_driver"
                )));
              }
            })
            .setNegativeButton(getString(R.string.exit_app), (a, b) -> exitAndKill())
            .create();
        if (resumed) {
          errorDialog.show();
        }
        break;
      case CommonNavigate.EXIT:
        ((MainApplication) getApplication()).navigate(destination);
        blockWithPending(true, "exit");
        exitAndKill();
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

  @Override
  public void showAnnouncementMessage(@NonNull String message) {
    announcementDialog = new Builder(this)
        .setTitle(R.string.information)
        .setMessage(message)
        .setCancelable(false)
        .setPositiveButton(getString(android.R.string.ok),
            ((dialog, which) -> {
              announcementDialog = null;
              if (announcementViewModel != null) {
                announcementViewModel.announcementConsumed();
              }
            }))
        .create();
    if (resumed) {
      announcementDialog.show();
    }
  }

  @Override
  public void showOnlineMessage(@NonNull String message) {
    onlineDialog = new Builder(this)
        .setTitle(R.string.information)
        .setMessage(message)
        .setCancelable(false)
        .setPositiveButton(getString(android.R.string.ok),
            ((dialog, which) -> {
              onlineDialog = null;
              if (executorStateViewModel != null) {
                executorStateViewModel.messageConsumed();
              }
            }))
        .create();
    if (resumed) {
      onlineDialog.show();
    }
  }

  @Override
  public void showUpdateMessage(@NonNull String message) {
    updateDialog = new Builder(this)
        .setMessage(message)
        .setCancelable(false)
        .setPositiveButton(getString(R.string.update), (dialog, which) -> {
          updateDialog = null;
          if (updateMessageViewModel != null) {
            updateMessageViewModel.messageConsumed();
          }
          try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(
                "market://details?id=com.cargopull.executor_driver"
            )));
          } catch (android.content.ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(
                "https://play.google.com/store/apps/details?id=com.cargopull.executor_driver"
            )));
          }
        })
        .setNegativeButton(getString(R.string.not_now), (dialog, which) -> {
          updateDialog = null;
          if (updateMessageViewModel != null) {
            updateMessageViewModel.messageConsumed();
          }
        })
        .create();
    if (resumed) {
      updateDialog.show();
    }
  }

  private void exitAndKill() {
    if (Build.VERSION.SDK_INT >= 21) {
      finishAndRemoveTask();
    } else {
      finishAffinity();
    }
    new Handler().postDelayed(
        () -> android.os.Process.killProcess(android.os.Process.myPid()), 1000
    );
  }
}
