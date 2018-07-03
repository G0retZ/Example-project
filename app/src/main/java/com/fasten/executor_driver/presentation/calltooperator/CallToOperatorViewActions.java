package com.fasten.executor_driver.presentation.calltooperator;

/**
 * Действия для смены состояния вида окна связи с оператором.
 */
public interface CallToOperatorViewActions {

  /**
   * Показать статус звонка.
   *
   * @param calling - звоним или нет?
   */
  void showCallingToOperator(boolean calling);
}
