package com.fasten.executor_driver.interactor;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.entity.RoutePoint;
import io.reactivex.Completable;

/**
 * Гейтвей пометки точки как открытой/закрытой.
 */
public interface OrderRouteGateway {

  /**
   * Запрашивает сервер пометить точку.
   *
   * @param routePoint - точка маршрута.
   * @param check - снять или установить метку.
   * @return {@link Completable} результат - успех либо ошибка на сервере.
   */
  @NonNull
  Completable checkRoutePoint(@NonNull RoutePoint routePoint, boolean check);
}
