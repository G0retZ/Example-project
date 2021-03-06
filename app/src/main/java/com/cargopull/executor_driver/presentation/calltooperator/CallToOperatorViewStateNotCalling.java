package com.cargopull.executor_driver.presentation.calltooperator;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.presentation.ViewState;

/**
 * Состояние вида отсутсвия процесса звонка оператору.
 */
final class CallToOperatorViewStateNotCalling implements ViewState<CallToOperatorViewActions> {

  @Override
  public void apply(@NonNull CallToOperatorViewActions stateActions) {
    stateActions.showCallingToOperator(false);
  }
}
