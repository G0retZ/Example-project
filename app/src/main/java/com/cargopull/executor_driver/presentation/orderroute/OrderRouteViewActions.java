package com.cargopull.executor_driver.presentation.orderroute;

import android.support.annotation.NonNull;
import java.util.List;

/**
 * Действия для смены состояния вида окна списка ТС исполнителя.
 */
public interface OrderRouteViewActions {

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
  void setRoutePointItems(@NonNull List<RoutePointItem> routePointItems);
}
