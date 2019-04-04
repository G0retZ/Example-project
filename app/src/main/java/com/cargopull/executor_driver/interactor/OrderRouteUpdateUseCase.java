package com.cargopull.executor_driver.interactor;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.entity.RoutePoint;
import java.util.List;

/**
 * Юзкейс обновления маршрута заказа.
 */
public interface OrderRouteUpdateUseCase {

  /**
   * Обновляет маршрут текущего заказ актуальными данными.
   */
  void updateRouteWith(@NonNull List<RoutePoint> routePoints);
}
