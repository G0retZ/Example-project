package com.fasten.executor_driver.presentation.orderroute;

import com.fasten.executor_driver.presentation.ViewModel;

/**
 * ViewModel окна списка точек маршрута заказа.
 */
public interface OrderRouteViewModel extends ViewModel<OrderRouteViewActions> {

  /**
   * Передает точку, выбранную исполнителем в качестве следующей.
   *
   * @param routePointItem - точка маршрута
   */
  void selectNextRoutePoint(RoutePointItem routePointItem);
}
