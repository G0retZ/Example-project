package com.fasten.executor_driver.presentation.nextroutepoint;

import android.support.annotation.NonNull;

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
   * показать следующую точку погрузки на карте по урл.
   *
   * @param url - адрес картинки с точкой погрузки на карте. Если пусто, значит адреса нету.
   */
  @SuppressWarnings({"EmptyMethod", "unused"})
  void showNextRoutePoint(@NonNull String url);

  /**
   * Показать координаты следующей точки маршрута.
   *
   * @param coordinates - координаты
   */
  void showNextRoutePointCoordinates(@NonNull String coordinates);

  /**
   * Показать адрес следующей точки маршрута.
   *
   * @param address - текущее время заказа в секундах. Если пусто, значит адреса нету.
   */
  void showNextRoutePointAddress(@NonNull String address);

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

  /**
   * Показать сообщение об ошибке сети.
   *
   * @param show - показать или нет?
   */
  void showNextRoutePointNetworkErrorMessage(boolean show);
}
