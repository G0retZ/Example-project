package com.fasten.executor_driver.presentation.calltooperator;

/**
 * Действия для смены состояния вида окна связи с оператором.
 */
public interface CallToOperatorViewActions {

  /**
   * Показать индикатор процесса.
   *
   * @param pending - показать или нет?
   */
  @SuppressWarnings("unused")
  void showCallToOperatorPending(boolean pending);

  /**
   * Показать сообщение об ошибке сети.
   *
   * @param show - показать или нет?
   */
  @SuppressWarnings("unused")
  void showNetworkErrorMessage(boolean show);
}
