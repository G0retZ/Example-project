package com.fasten.executor_driver.interactor;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.entity.Offer;
import io.reactivex.Completable;
import io.reactivex.Flowable;

/**
 * Юзкейс предложений заказов. Слушает предложения из гейтвея.
 */
public interface OfferUseCase {

  /**
   * Запрашивает данные о предложениях заказов.
   *
   * @return {@link Flowable<Offer>} результат запроса.
   */
  Flowable<Offer> getOffers();

  /**
   * Передает решение исполнителя по принятию заказа.
   *
   * @param confirmed согласие исполнителя на прием заказа.
   * @return {@link Completable} результат - успех либо ошибка таймаута ожидания решения на сервере.
   */
  @NonNull
  Completable sendDecision(boolean confirmed);
}
