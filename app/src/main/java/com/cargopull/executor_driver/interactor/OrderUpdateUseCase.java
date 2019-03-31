package com.cargopull.executor_driver.interactor;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.entity.Order;

/**
 * Юзкейс обновления информации по заказу.
 */
public interface OrderUpdateUseCase {

  /**
   * Обновляет текущий заказ актуальными данными.
   */
  void updateOrderWith(@NonNull Order order);
}
