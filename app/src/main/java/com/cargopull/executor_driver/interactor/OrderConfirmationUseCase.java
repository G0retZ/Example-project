package com.cargopull.executor_driver.interactor;

import android.support.annotation.NonNull;
import io.reactivex.Completable;

/**
 * Юзкейс принятия заказов. Принимает или отвергает последний полученный заказ.
 */
public interface OrderConfirmationUseCase {

  /**
   * Передает решение исполнителя по принятию заказа.
   *
   * @param confirmed согласие исполнителя на прием заказа.
   * @return {@link Completable} результат - успех либо ошибка таймаута ожидания решения на сервере.
   */
  @NonNull
  Completable sendDecision(boolean confirmed);
}
