package com.cargopull.executor_driver.presentation.waitingforclient;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.presentation.ViewState;

/**
 * Состояние бездействия вида ожидания клиента.
 */
final class WaitingForClientViewStateIdle implements ViewState<WaitingForClientViewActions> {

  @Override
  public void apply(@NonNull WaitingForClientViewActions stateActions) {
    stateActions.showWaitingForClientPending(false);
  }
}
