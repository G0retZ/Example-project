package com.fasten.executor_driver.presentation.balance;

/**
 * Действия для смены состояния вида окна баланса.
 */
interface BalanceViewActions {

  /**
   * Показать сумму на основном счете.
   *
   * @param amount - сумма
   */
  void showMainAccountAmount(int amount);

  /**
   * Показать сумму на бонусном счете.
   *
   * @param amount - сумма
   */
  void showBonusAccountAmount(int amount);

  /**
   * Показать индикатор процесса.
   *
   * @param pending - показать или нет?
   */
  void showBalancePending(boolean pending);

  /**
   * Показать сообщение об ошибке.
   *
   * @param show - показать или нет?
   */
  void showBalanceErrorMessage(boolean show);
}
