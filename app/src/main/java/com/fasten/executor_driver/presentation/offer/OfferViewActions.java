package com.fasten.executor_driver.presentation.offer;

import android.support.annotation.NonNull;

/**
 * Действия для смены состояния вида окна заказа.
 */
public interface OfferViewActions {

  /**
   * Показать индикатор процесса.
   *
   * @param pending - показать или нет?
   */
  void showOfferPending(boolean pending);

  /**
   * показать точку погрузки на карте по урл.
   *
   * @param url - адрес картинки с точкой погрузки на карте
   */
  void showLoadPoint(@NonNull String url);

  /**
   * Показать растояние до точки погрузки.
   *
   * @param distance - расстояние
   */
  void showDistance(long distance);

  /**
   * Показать индикатор таймаута по заданным параметрам.
   *
   * @param progress - сколько процентов осталось до окончания
   * @param timeout - время оставшееся до таймаут
   */
  void showTimeout(long progress, long timeout);

  /**
   * Показать адрес точки погрузки.
   *
   * @param address - адрес
   */
  void showLoadPointAddress(String address);

  /**
   * Показать количество грузчиков.
   *
   * @param count - количество
   */
  void showPortersCount(int count);

  /**
   * Показать количество пассажиров.
   *
   * @param count - количество
   */
  void showPassengersCount(int count);

  /**
   * Показать комментарий к заказу.
   *
   * @param comment - адрес
   */
  void showOfferComment(String comment);

  /**
   * Показать ошибку доступности заказов.
   *
   * @param show - показать или нет?
   */
  void showOfferAvailabilityError(boolean show);

  /**
   * Показать сообщение об ошибке сети.
   *
   * @param show - показать или нет?
   */
  void showOfferNetworkErrorMessage(boolean show);

  /**
   * Задействовать кнопку отказа.
   *
   * @param enable - задействовать или нет?
   */
  void enableDeclineButton(boolean enable);

  /**
   * Задействовать кнопку приема.
   *
   * @param enable - задействовать или нет?
   */
  void enableAcceptButton(boolean enable);
}
