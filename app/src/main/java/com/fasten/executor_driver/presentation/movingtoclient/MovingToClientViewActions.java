package com.fasten.executor_driver.presentation.movingtoclient;

import android.support.annotation.NonNull;

/**
 * Действия для смены состояния вида окна заказа.
 */
public interface MovingToClientViewActions {

  /**
   * Показать индикатор процесса.
   *
   * @param pending - показать или нет?
   */
  void showMovingToClientPending(boolean pending);

  /**
   * показать точку погрузки на карте по урл.
   *
   * @param url - адрес картинки с точкой погрузки на карте
   */
  void showLoadPoint(@NonNull String url);

  /**
   * Показать координаты точки погрузки.
   *
   * @param coordinates - координаты
   */
  void showLoadPointCoordinates(String coordinates);

  /**
   * Показать индикатор таймаута по заданным параметрам.
   *
   * @param timeout - время оставшееся до таймаута в секундах
   */
  void showTimeout(int timeout);

  /**
   * Показать адрес точки погрузки.
   *
   * @param address - адрес
   */
  void showLoadPointAddress(@NonNull String address);

  /**
   * Показать ошибку доступности заказов.
   *
   * @param show - показать или нет?
   */
  void showOrderAvailabilityError(boolean show);

  /**
   * Показать сообщение об ошибке сети.
   *
   * @param show - показать или нет?
   */
  void showNetworkErrorMessage(boolean show);
}
