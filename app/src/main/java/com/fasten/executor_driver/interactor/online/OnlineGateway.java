package com.fasten.executor_driver.interactor.online;

import android.support.annotation.NonNull;
import io.reactivex.Completable;

/**
 * Гейтвей выхода на линию
 */
public interface OnlineGateway {

  /**
   * Запрашивает у сервера выход на линию. Сервер должен вернуть либо 200 ОК, либо ошибку с причиной
   * отказа.
   *
   * @return {@link Completable} результат запроса выхода на линию.
   */
  @NonNull
  Completable goOnline();
}
