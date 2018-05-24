package com.fasten.executor_driver.presentation.nextroutepoint;

import com.fasten.executor_driver.presentation.ViewModel;

/**
 * ViewModel окна следующей точки маршрута заказа.
 */
interface NextRoutePointViewModel extends ViewModel<NextRoutePointViewActions> {

  /**
   * Закрывает точку маршрута.
   */
  void closeRoutePoint();

}
