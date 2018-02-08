package com.fasten.executor_driver.interactor.online;

import android.support.annotation.NonNull;
import io.reactivex.Completable;

/**
 * Юзкейс выхода на линию
 */
public interface OnlineUseCase {

  /**
   * Запрашивает выход на линию.
   *
   * @return {@link Completable} результат запроса.
   */
  @NonNull
  Completable goOnline();
}
