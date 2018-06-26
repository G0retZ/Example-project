package com.fasten.executor_driver.interactor;

import android.support.annotation.NonNull;
import io.reactivex.Completable;

/**
 * Юзкейс поллинга цены сверх пакета.
 */
public interface CurrentCostPollingUseCase {

  /**
   * Запрашивает поллинг сервера, выдает окончание поллинга или ошибку.
   *
   * @return {@link Completable} результат запроса.
   */
  @NonNull
  Completable listenForPolling();
}
