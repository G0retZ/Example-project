package com.cargopull.executor_driver.presentation.calltoclient;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.presentation.ViewState;

/**
 * Состояние вида процесса звонка клиенту.
 */
final class CallToClientViewStateCalling implements ViewState<CallToClientViewActions> {

  @Override
  public void apply(@NonNull CallToClientViewActions stateActions) {
    stateActions.showCallToClientPending(false);
    stateActions.showCallingToClient(true);
  }
}
