package com.cargopull.executor_driver.interactor;

import com.cargopull.executor_driver.entity.OrderCostDetails;
import io.reactivex.Flowable;

/**
 * Юзкейс детального расчета заказа. Слушает детальный расчет заказа из гейтвея.
 */
interface OrderCostDetailsUseCase {

  /**
   * Запрашивает детальный расчет выполняемого заказа.
   *
   * @return {@link Flowable<OrderCostDetails>} результат запроса.
   */
  Flowable<OrderCostDetails> getOrderCostDetails();
}
