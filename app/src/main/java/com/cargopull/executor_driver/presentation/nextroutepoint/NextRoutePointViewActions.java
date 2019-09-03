package com.cargopull.executor_driver.presentation.nextroutepoint;

import androidx.annotation.NonNull;

/**
 * Действия для смены состояния вида следующей точки маршрута заказа.
 */
public interface NextRoutePointViewActions {

  /**
   * Показать индикатор процесса.
   *
   * @param pending - показать или нет?
   */
  void showNextRoutePointPending(boolean pending);

  /**
   * Показать адрес следующей точки маршрута.
   *
   * @param coordinates - координаты
   * @param address - текущее время заказа в секундах. Если пусто, значит адреса нету.
   */
  void showNextRoutePointAddress(@NonNull String coordinates, @NonNull String address);

  /**
   * Показать комментарий к следующей точки маршрута.
   *
   * @param comment - текущее время заказа в секундах. Если пусто, значит комментария нету.
   */
  void showNextRoutePointComment(@NonNull String comment);

  /**
   * Показать кнопку закрытия точки.
   *
   * @param show - показать или нет?
   */
  void showCloseNextRoutePointAction(boolean show);

  /**
   * Показать кнопку завершения заказа.
   *
   * @param show - показать или нет?
   */
  void showCompleteOrderAction(boolean show);

  /**
   * Показать "по городу".
   *
   * @param show - показать или нет?
   */
  void showNoRouteRide(boolean show);
}
