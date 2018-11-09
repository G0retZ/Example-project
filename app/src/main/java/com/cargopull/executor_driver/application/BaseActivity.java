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
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.backend.settings.AppSettingsService;
import com.cargopull.executor_driver.di.AppComponent;
import com.cargopull.executor_driver.presentation.CommonNavigate;
import com.cargopull.executor_driver.presentation.announcement.AnnouncementStateViewActions;
import com.cargopull.executor_driver.presentation.announcement.AnnouncementViewModel;
import com.cargopull.executor_driver.presentation.executorstate.ExecutorStateViewActions;
import com.cargopull.executor_driver.presentation.executorstate.ExecutorStateViewModel;
import com.cargopull.executor_driver.presentation.geolocationstate.GeoLocationStateViewActions;
import com.cargopull.executor_driver.presentation.geolocationstate.GeoLocationStateViewModel;
import com.cargopull.executor_driver.presentation.serverconnection.ServerConnectionNavigate;
import com.cargopull.executor_driver.presentation.serverconnection.ServerConnectionViewModel;
import com.cargopull.executor_driver.presentation.servertime.ServerTimeViewModel;
import com.cargopull.executor_driver.presentation.updatemessage.UpdateMessageViewActions;
import com.cargopull.executor_driver.presentation.updatemessage.UpdateMessageViewModel;
import com.cargopull.executor_driver.view.GeoEngagementDialogFragment;
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
public class BaseActivity extends AppCompatActivity implements GeoLocationStateViewActions,
    ExecutorStateViewActions, AnnouncementStateViewActions, UpdateMessageViewActions {

  @NonNull
  private final PendingDialogFragment pendingDialogFragment = new PendingDialogFragment();
  @NonNull
  private final GeoEngagementDialogFragment geoEngagementDialogFragment = new GeoEngagementDialogFragment();
  @NonNull
  private final LinkedList<OnBackPressedInterceptor> onBackPressedInterceptors = new LinkedList<>();
  private final Set<String> blockers = new HashSet<>();
  private AppSettingsService appSettingsService;
  private GeoLocationStateViewModel geoLocationStateViewModel;
  private ExecutorStateViewModel executorStateViewModel;
  private UpdateMessageViewModel updateMessageViewModel;
  private AnnouncementViewModel announcementViewModel;
  private ServerConnectionViewModel serverConnectionViewModel;
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
  // FIXME: https://jira.capsrv.xyz/browse/RUCAP-2244
  private int nightMode = -1;

  @Inject
  public void setAppSettingsService(@NonNull AppSettingsService appSettingsService) {
    this.appSettingsService = appSettingsService;
  }

  @Inject
  public void setGeoLocationStateViewModel(
      @NonNull GeoLocationStateViewModel geoLocationStateViewModel) {
    this.geoLocationStateViewModel = geoLocationStateViewModel;
  }

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
  public void setServerTimeViewModel(@NonNull ServerTimeViewModel serverTimeViewModel) {
    this.serverTimeViewModel = serverTimeViewModel;
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    pendingDialogFragment.setCancelable(false);
    geoEngagementDialogFragment.setCancelable(false);
    onDependencyInject(getDiComponent());
    // FIXME: https://jira.capsrv.xyz/browse/RUCAP-2244
    if (appSettingsService != null) {
      nightMode = appSettingsService.getNumber("mode");
    }
    geoLocationStateViewModel.getViewStateLiveData().observe(this, viewState -> {
      if (viewState != null) {
        viewState.apply(this);
      }
    });
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

  /**
   * Колбэк для внедрения зависимостей.
   *
   * @param appComponent - компонент, который может произвести внедрение
   */
  void onDependencyInject(AppComponent appComponent) {
    appComponent.inject(this);
  }

  @Override
  public void onResume() {
    super.onResume();
    // FIXME: https://jira.capsrv.xyz/browse/RUCAP-2244
    if (appSettingsService != null && appSettingsService.getNumber("mode") != nightMode) {
      recreate();
    }
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
   * Пробегает по реестру перехватчиков пока кто либо не обработает нажатие "назад". Если никто не
   * обработал то вызывает воплощение метода в родителе (super).
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
   * Метод перехода куда либо. Позволяет отвязать {@link android.app.Fragment} от конкретных {@link
   * Activity}.
   *
   * @param destination пункт назначения
   */
  public void navigate(@NonNull String destination) {
    switch (destination) {
      case CommonNavigate.NO_CONNECTION:
        Fragment fragment = getSupportFragmentManager()
            .findFragmentById(R.id.fragment_server_connection);
        if (fragment instanceof ServerConnectionFragment) {
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
            .setNegativeButton(getString(R.string.exit), (a, b) -> exitAndKill())
            .create();
        if (resumed) {
          errorDialog.show();
        }
        break;
      case CommonNavigate.EXIT:
        ((MainApplication) getApplication()).navigate(destination);
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
      pendingDialogFragment.dismiss();
    } else {
      pendingDialogFragment.show(getSupportFragmentManager(), "pending");
    }
  }

  /**
   * Заблокировать ЮИ экраном процесса. блокирует экран диалого процесса. Ведет учет запросов
   * блкоировки.
   *
   * @param blockerId - Уникальный ИД блокирующего.
   */
  public void blockWithPending(@NonNull String blockerId) {
    blockers.add(blockerId);
    pendingDialogFragment.show(getSupportFragmentManager(), "pending");
  }

  /**
   * Разблокировать ЮИ экраном процесса. блокирует экран диалого процесса. Ведет учет запросов
   * разблокировки. Если число запросов блокировки меньше запросов разблокировки, то не отображаем.
   *
   * @param blockerId - Уникальный ИД блокирующего.
   */
  public void unblockWithPending(@NonNull String blockerId) {
    blockers.remove(blockerId);
    if (blockers.isEmpty()) {
      pendingDialogFragment.dismiss();
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
              announcementViewModel.announcementConsumed();
            }))
        .create();
    if (resumed) {
      announcementDialog.show();
    }
  }

  @Override
  public void showExecutorStatusMessage(@NonNull String message) {
    onlineDialog = new Builder(this)
        .setTitle(R.string.information)
        .setMessage(message)
        .setCancelable(false)
        .setPositiveButton(getString(android.R.string.ok),
            ((dialog, which) -> {
              onlineDialog = null;
              executorStateViewModel.messageConsumed();
            }))
        .create();
    if (resumed) {
      onlineDialog.show();
    }
  }

  @Override
  public void showExecutorStatusInfo(@NonNull String message) {

  }

  @Override
  public void showUpdateMessage(@NonNull String message) {
    updateDialog = new Builder(this)
        .setMessage(message)
        .setCancelable(false)
        .setPositiveButton(getString(R.string.update), (dialog, which) -> {
          updateDialog = null;
          updateMessageViewModel.messageConsumed();
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
          updateMessageViewModel.messageConsumed();
        })
        .create();
    if (resumed) {
      updateDialog.show();
    }
  }

  @Override
  public void setVisible(@IdRes int id, boolean visible) {
    if (visible && showGeolocationStateAllowed()) {
      geoEngagementDialogFragment.show(getSupportFragmentManager(), "geoEngagement");
    }
  }

  @Override
  public void setText(@IdRes int id, @StringRes int stringId) {

  }

  @Override
  public void setImage(@IdRes int id, @DrawableRes int drawableId) {

  }

  protected boolean showGeolocationStateAllowed() {
    return false;
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
