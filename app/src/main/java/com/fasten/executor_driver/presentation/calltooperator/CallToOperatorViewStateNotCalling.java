package com.fasten.executor_driver.presentation.calltooperator;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.presentation.ViewState;

/**
 * Состояние вида отсутсвия процесса звонка оператору.
 */
final class CallToOperatorViewStateNotCalling implements ViewState<CallToOperatorViewActions> {

  @Override
  public void apply(@NonNull CallToOperatorViewActions stateActions) {
    stateActions.showCallingToOperator(false);
  }
}
