package com.cargopull.executor_driver.view;

import android.content.Context;
import android.content.pm.PackageManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import io.reactivex.Completable;
import io.reactivex.subjects.CompletableSubject;

/**
 * DRY класс проверки разрешений.
 */
public class PermissionChecker {

  private final int uuid;
  @NonNull
  private final CompletableSubject completableSubject = CompletableSubject.create();

  /**
   * Конструктор.
   *
   * @param uuid - ИД запроса разрешений
   */
  public PermissionChecker(int uuid) {
    this.uuid = uuid;
  }

  @Override
  public String toString() {
    return "PermissionChecker{" +
        "uuid=" + uuid +
        ", completableSubject=" + completableSubject +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    PermissionChecker that = (PermissionChecker) o;

    return uuid == that.uuid && completableSubject.equals(that.completableSubject);
  }

  @Override
  public int hashCode() {
    int result = uuid;
    result = 31 * result + completableSubject.hashCode();
    return result;
  }

  /**
   * Запрос проверки разрешений.
   *
   * @param fragment - {@link Fragment}, для которого идет запрос
   * @param context - {@link Context} запроса
   * @param permissions - список разрешений
   * @return {@link Completable} результат запроса
   */
  public Completable check(@Nullable Fragment fragment, @Nullable Context context,
      @NonNull String... permissions) {
    if (context == null || fragment == null) {
      return Completable.error(new SecurityException("Access denied."));
    }
    boolean allowed = true;
    for (String permission : permissions) {
      allowed = allowed & ContextCompat.checkSelfPermission(context, permission)
          == PackageManager.PERMISSION_GRANTED;
    }
    if (allowed) {
      return Completable.complete();
    } else {
      // Небходимо ли показывать разъяснение?
      if (fragment.shouldShowRequestPermissionRationale(permissions[0])) {
        // Заяснить пользователю необходимость *асинхронно* -- не блокировать
        // этот поток в ожидании ответа пользователя! После того как пользователь
        // увидел пояснения, Попробуй снова запросить разрешение.
        fragment.requestPermissions(permissions, uuid);
      } else {
        // Заяснять не нужно, мы можем запросить разрешения.
        fragment.requestPermissions(permissions, uuid);
      }
      return completableSubject;
    }
  }

  /**
   * Публикуем результат запроса.
   *
   * @param requestCode ИД запроса разрешений, если не соответствует {@link #uuid}, то выходим
   * @param permissions список разрешений
   * @param grantResults результаты для соответствующих разрешений, которые или {@link
   * android.content.pm.PackageManager#PERMISSION_GRANTED} или {@link
   * android.content.pm.PackageManager#PERMISSION_DENIED}.
   */
  public void onResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    if (requestCode != uuid || permissions.length == 0 || grantResults.length == 0) {
      return;
    }
    boolean allowed = true;
    for (int result : grantResults) {
      allowed = allowed & result == PackageManager.PERMISSION_GRANTED;
    }
    if (allowed) {
      completableSubject.onComplete();
    } else {
      completableSubject.onError(new SecurityException("Access denied."));
    }
  }
}
