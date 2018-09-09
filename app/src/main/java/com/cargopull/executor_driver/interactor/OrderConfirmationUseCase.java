package com.cargopull.executor_driver.interactor;

import android.support.annotation.NonNull;
import io.reactivex.Flowable;
import io.reactivex.Single;

/**
 * Юзкейс принятия заказов. Принимает или отвергает последний полученный заказ.
 */
public interface OrderConfirmationUseCase {

  /**
   * Получает таймаут для решения исполнителя по принятию пришедших заказов, из расчета один таймаут
   * на один заказ.
   *
   * @return {@link Flowable<Long>} результат - успех либо ошибка с текстом.
   */
  @NonNull
  Flowable<Long> getOrderDecisionTimeout();

  /**
   * Передает решение исполнителя по принятию заказа.
   *
   * @param confirmed согласие исполнителя на прием заказа.
   * @return {@link Single<String>} результат - успех либо ошибка с текстом.
   */
  @NonNull
  Single<String> sendDecision(boolean confirmed);
}
