package com.cargopull.executor_driver.presentation.calltoclient;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.presentation.ViewState;

/**
 * Состояние вида отсутсвия процесса звонка клиенту.
 */
final class CallToClientViewStateNotCalling implements ViewState<CallToClientViewActions> {

  @Override
  public void apply(@NonNull CallToClientViewActions stateActions) {
    stateActions.showCallToClientPending(false);
    stateActions.showCallingToClient(false);
  }
}
