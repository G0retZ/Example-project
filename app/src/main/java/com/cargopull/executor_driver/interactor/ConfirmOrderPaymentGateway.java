package com.cargopull.executor_driver.interactor;

import android.support.annotation.NonNull;
import io.reactivex.Completable;

/**
 * Гейтвей подтверждения оплаты заказа.
 */
interface ConfirmOrderPaymentGateway {

  /**
   * Передает запрос подтверждения оплаты заказа.
   *
   * @return {@link Completable} результат - успех либо ошибка на сервере.
   */
  @NonNull
  Completable confirmOrderPayment();
}
