package com.fasten.executor_driver.presentation.order;

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
   * Показать координаты точки погрузки.
   *
   * @param coordinates - координаты
   */
  void showLoadPointCoordinates(@NonNull String coordinates);

  /**
   * Показать индикатор таймаута по заданным параметрам.
   *
   * @param timeout - время оставшееся до таймаута в секундах
   */
  void showTimeout(int timeout);

  /**
   * Показать растояние до точки погрузки.
   *
   * @param distance - расстояние
   */
  void showDistance(String distance);

  /**
   * Показать адрес точки погрузки.
   *
   * @param address - адрес
   */
  void showLoadPointAddress(@NonNull String address);

  /**
   * Показать рассчет стоимости.
   *
   * @param priceText - оценка стоимости заказа
   */
  void showEstimatedPrice(@NonNull String priceText);

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
