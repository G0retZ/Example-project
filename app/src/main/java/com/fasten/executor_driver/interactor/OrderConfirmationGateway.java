package com.fasten.executor_driver.interactor;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.entity.Order;
import io.reactivex.Completable;

/**
 * Гейтвей подтверждения заказа.
 */
public interface OrderConfirmationGateway {

  /**
   * Передает решение исполнителя по принятию заказа.
   *
   * @param order заказа, к которому относится это решение.
   * @param accepted согласие исполнителя на прием заказа.
   * @return {@link Completable} результат - успех либо ошибка таймаута ожидания решения на сервере.
   */
  @NonNull
  Completable sendDecision(@NonNull Order order, boolean accepted);
}
