package com.cargopull.executor_driver.interactor;

/**
 * Юзкейс принятия решения по заказу.
 */
public interface OrderDecisionUseCase {

  /**
   * Сообщает, что заказ более не актуален, чтобы все подписчики обновили свое состояние. Нужно для
   * случаев, когда вместо сообщения от сервера обрабатывается результат принятия или отказа.
   */
  void setOrderOfferDecisionMade();
}
