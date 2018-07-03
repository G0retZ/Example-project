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
   * Показать растояние до точки погрузки.
   *
   * @param distance - расстояние
   */
  void showDistance(String distance);

  /**
   * Показать адрес точки погрузки.
   *
   * @param coordinates - координаты
   * @param address - адрес
   */
  void showLoadPointAddress(@NonNull String coordinates, @NonNull String address);

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
}
