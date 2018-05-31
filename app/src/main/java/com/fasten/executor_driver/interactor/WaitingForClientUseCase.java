package com.fasten.executor_driver.interactor;

import android.support.annotation.NonNull;
import io.reactivex.Completable;

/**
 * Юзкейс движения к клиенту. Слушает заказ из гейтвея, передает действия исполнителя.
 */
public interface WaitingForClientUseCase {

  /**
   * Сообщает серверу о начале исполнения заказа.
   *
   * @return {@link Completable} результат - успех либо ошибка на сервере.
   */
  @NonNull
  Completable startTheOrder();
}
