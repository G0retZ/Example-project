package com.fasten.executor_driver.presentation.calltooperator;

import com.fasten.executor_driver.presentation.ViewModel;

/**
 * ViewModel окна связи с оператором.
 */
public interface CallToOperatorViewModel extends ViewModel<CallToOperatorViewActions> {

  /**
   * Запрашивает связь с оператором.
   */
  void callToOperator();
}
