package com.fasten.executor_driver.presentation.waitingforclient;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.presentation.ViewState;

/**
 * Состояние бездействия вида ожидания клиента.
 */
final class WaitingForClientViewStateIdle implements ViewState<WaitingForClientViewActions> {

  @Override
  public void apply(@NonNull WaitingForClientViewActions stateActions) {
    stateActions.showWaitingForClientPending(false);
    stateActions.showNetworkErrorMessage(false);
  }
}
