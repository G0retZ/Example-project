package com.fasten.executor_driver.presentation.calltoclient;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.presentation.ViewState;

/**
 * Состояние ожидания при подтверждении или отказе от заказа.
 */
final class CallToClientViewStatePending implements ViewState<CallToClientViewActions> {

  @Override
  public void apply(@NonNull CallToClientViewActions stateActions) {
    stateActions.showCallToClientPending(true);
    stateActions.showCallingToClient(false);
  }
}
