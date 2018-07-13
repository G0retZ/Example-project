package com.cargopull.executor_driver.presentation.nextroutepoint;

import com.cargopull.executor_driver.presentation.ViewModel;

/**
 * ViewModel окна следующей точки маршрута заказа.
 */
public interface NextRoutePointViewModel extends ViewModel<NextRoutePointViewActions> {

  /**
   * Закрывает точку маршрута.
   */
  void closeRoutePoint();

  /**
   * Завершает выполнение заказа.
   */
  void completeTheOrder();
}
