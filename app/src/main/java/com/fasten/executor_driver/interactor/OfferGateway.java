package com.fasten.executor_driver.interactor;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.entity.Offer;
import io.reactivex.Completable;
import io.reactivex.Flowable;

/**
 * Гейтвей работы с заказами.
 */
interface OfferGateway {

  /**
   * Ожидает заказы для исполнителя у сокета.
   *
   * @return {@link Flowable<Offer>} заказы для исполнителя.
   */
  @NonNull
  Flowable<Offer> getOffers();

  /**
   * Передает решение исполнителя по принятию заказа.
   *
   * @param offer заказа, к которому относится это решение.
   * @param confirmed согласие исполнителя на прием заказа.
   * @return {@link Completable} результат - успех либо ошибка таймаута ожидания решения на сервере.
   */
  @NonNull
  Completable sendDecision(@NonNull Offer offer, boolean confirmed);
}
