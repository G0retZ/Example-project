package com.cargopull.executor_driver.presentation.calltooperator;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.presentation.ViewState;

/**
 * Состояние вида процесса звонка оператору.
 */
final class CallToOperatorViewStateCalling implements ViewState<CallToOperatorViewActions> {

  @Override
  public void apply(@NonNull CallToOperatorViewActions stateActions) {
    stateActions.showCallingToOperator(true);
  }
}
