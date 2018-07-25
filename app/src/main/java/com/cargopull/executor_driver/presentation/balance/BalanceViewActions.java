package com.cargopull.executor_driver.presentation.balance;

/**
 * Действия для смены состояния вида окна баланса.
 */
public interface BalanceViewActions {

  /**
   * Показать сумму на основном счете.
   *
   * @param amount - сумма
   */
  void showMainAccountAmount(long amount);

  /**
   * Показать сумму на бонусном счете.
   *
   * @param amount - сумма
   */
  void showBonusAccountAmount(long amount);

  /**
   * Показать индикатор процесса.
   *
   * @param pending - показать или нет?
   */
  void showBalancePending(boolean pending);
}
