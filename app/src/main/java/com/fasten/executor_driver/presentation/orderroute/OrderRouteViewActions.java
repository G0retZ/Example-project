package com.fasten.executor_driver.presentation.orderroute;

import android.support.annotation.NonNull;

/**
 * Действия для смены состояния вида окна списка ТС исполнителя.
 */
interface OrderRouteViewActions {

  /**
   * Показать индикатор процесса.
   *
   * @param pending - показать или нет?
   */
  void showOrderRoutePending(boolean pending);

  /**
   * Задать список точек маршрута.
   *
   * @param routePointItems - список ТС
   */
  void setRoutePointItems(@NonNull RoutePointItems routePointItems);

  /**
   * Показать сообщение об ошибке.
   *
   * @param show - показать или нет?
   */
  void showOrderRouteErrorMessage(boolean show);
}
