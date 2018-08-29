package com.cargopull.executor_driver.presentation.order;

import android.support.annotation.NonNull;

/**
 * Действия для смены состояния вида окна заказа.
 */
public interface OrderViewActions {

  /**
   * Показать индикатор процесса.
   *
   * @param pending - показать или нет?
   */
  void showOrderPending(boolean pending);

  /**
   * показать точку погрузки на карте по урл.
   *
   * @param url - адрес картинки с точкой погрузки на карте
   */
  void showLoadPoint(@NonNull String url);

  /**
   * Показать индикатор таймаута до встречи с клиентом.
   *
   * @param timeout - время оставшееся до таймаута в секундах
   */
  void showTimeout(int timeout);

  /**
   * Показать индикатор таймаута приниятия решения по заказу.
   *
   * @param progress - сколько процентов осталось до окончания
   * @param timeout - время оставшееся до таймаут
   */
  void showTimeout(int progress, long timeout);

  /**
   * Показать растояние до первой точки маршрута.
   *
   * @param distance - расстояние
   */
  void showFirstPointDistance(String distance);

  /**
   * Показать время до первой точки маршрута.
   *
   * @param etaTime - время до первой точки в секундах
   */
  void showFirstPointEta(int etaTime);

  /**
   * Показать адрес следующей точки маршрута.
   *
   * @param coordinates - координаты
   * @param address - адрес
   */
  void showNextPointAddress(@NonNull String coordinates, @NonNull String address);

  /**
   * Показать комментарий к адресу следующей точки маршрута.
   *
   * @param comment - координаты
   */
  void showNextPointComment(@NonNull String comment);

  /**
   * Показать адрес последней точки маршрута.
   *
   * @param address - адрес
   */
  void showLastPointAddress(@NonNull String address);

  /**
   * Показать количество точек маршрута.
   *
   * @param count - количество
   */
  void showRoutePointsCount(int count);

  /**
   * Показать услугу заказа.
   *
   * @param serviceName - имя услуги
   */
  void showServiceName(@NonNull String serviceName);

  /**
   * Показать рассчет стоимости.
   *
   * @param priceText - оценка стоимости заказа
   */
  void showEstimatedPrice(@NonNull String priceText);

  /**
   * Показать ценовые условия заказа.
   *
   * @param routeDistance - длинна маршрута
   * @param time - время заказа в секундах
   * @param cost - общая стоимость пакета в копейках
   */
  void showOrderConditions(@NonNull String routeDistance, int time, long cost);

  /**
   * Показать время занятости по заказу.
   *
   * @param occupationTime - время занятости
   */
  @SuppressWarnings({"unused", "EmptyMethod"})
  void showOrderOccupationTime(@NonNull String occupationTime);

  /**
   * Показать дату занятости по заказу.
   *
   * @param occupationDate - дата занятости
   */
  @SuppressWarnings({"unused", "EmptyMethod"})
  void showOrderOccupationDate(@NonNull String occupationDate);

  /**
   * Показать опции, необходимые для принятия заказа.
   *
   * @param options - список опций, требуемых для выполнения заказа
   */
  void showOrderOptionsRequirements(@NonNull String options);

  /**
   * Показать комментарий к заказу.
   *
   * @param comment - адрес
   */
  void showComment(@NonNull String comment);

  /**
   * показать сообщение о том, что заказ истек.
   *
   * @param show - показать или нет?
   */
  void showOrderExpired(boolean show);
}
