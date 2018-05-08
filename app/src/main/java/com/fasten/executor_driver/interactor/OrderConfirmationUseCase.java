package com.fasten.executor_driver.interactor;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.entity.Offer;
import io.reactivex.Completable;
import io.reactivex.Flowable;

/**
 * Юзкейс подтверждения заказа. Слушает заказы из гейтвея.
 */
public interface OrderConfirmationUseCase {

  /**
   * Запрашивает сбор данных о местоположении.
   *
   * @return {@link Flowable<Offer>} результат запроса.
   */
  Flowable<Offer> getOffers();

  /**
   * Передает отказ исполнителя от заказа.
   *
   * @return {@link Completable} результат - успех либо ошибка таймаута ожидания решения на сервере.
   */
  @NonNull
  Completable cancelOrder();
}
