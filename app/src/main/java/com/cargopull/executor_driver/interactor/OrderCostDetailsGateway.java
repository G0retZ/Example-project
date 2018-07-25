package com.cargopull.executor_driver.interactor;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.entity.OrderCostDetails;
import io.reactivex.Flowable;

/**
 * Гейтвей получения детального расчета заказа.
 */
public interface OrderCostDetailsGateway {

  /**
   * Ожидает детальные расчеты заказа у сокета.
   *
   * @return {@link Flowable<OrderCostDetails>} заказы для исполнителя.
   */
  @NonNull
  Flowable<OrderCostDetails> getOrderCostDetails();
}
