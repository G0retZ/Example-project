package com.cargopull.executor_driver.interactor;

import androidx.annotation.NonNull;
import io.reactivex.Completable;

/**
 * Юзкейс подтверждения оплаты заказа.
 */
public interface ConfirmOrderPaymentUseCase {

  /**
   * Подтвердить оплату заказа.
   *
   * @return {@link Completable} результат - успех либо ошибка на сервере.
   */
  @NonNull
  Completable confirmPayment();
}
