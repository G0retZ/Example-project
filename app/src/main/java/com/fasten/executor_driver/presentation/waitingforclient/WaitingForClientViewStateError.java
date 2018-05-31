package com.fasten.executor_driver.presentation.waitingforclient;

import android.support.annotation.NonNull;
import com.fasten.executor_driver.presentation.ViewState;

/**
 * Состояние ошибки сети вида ожидания клиента.
 */
final class WaitingForClientViewStateError implements ViewState<WaitingForClientViewActions> {

  @Override
  public void apply(@NonNull WaitingForClientViewActions stateActions) {
    stateActions.showWaitingForClientPending(false);
    stateActions.showNetworkErrorMessage(true);
  }
}
