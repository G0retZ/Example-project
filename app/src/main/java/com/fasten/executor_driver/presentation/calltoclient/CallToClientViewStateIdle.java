package com.fasten.executor_driver.presentation.calltoclient;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.presentation.ViewState;

/**
 * Состояние бездействия вида заказа.
 */
final class CallToClientViewStateIdle implements ViewState<CallToClientViewActions> {

  @Override
  public void apply(@NonNull CallToClientViewActions stateActions) {
    stateActions.showCallToClientPending(false);
    stateActions.showNetworkErrorMessage(false);
  }
}
