package com.cargopull.executor_driver.interactor;

import android.support.annotation.NonNull;
import io.reactivex.Completable;

/**
 * Юзкейс подтверждения оплаты заказа.
 */
interface ConfirmOrderPaymentUseCase {

  /**
   * Подтвердить оплату заказа.
   *
   * @return {@link Completable} результат - успех либо ошибка на сервере.
   */
  @NonNull
  Completable confirmPayment();
}
