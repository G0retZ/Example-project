package com.cargopull.executor_driver.interactor;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.entity.Order;
import io.reactivex.Single;

/**
 * Гейтвей подтверждения заказа.
 */
public interface OrderConfirmationGateway {

  /**
   * Передает решение исполнителя по принятию заказа.
   *
   * @param order заказа, к которому относится это решение.
   * @param accepted согласие исполнителя на прием заказа.
   * @return {@link Single<String>} результат - успех либо ошибка с текстом.
   */
  @NonNull
  Single<String> sendDecision(@NonNull Order order, boolean accepted);
}
