package com.cargopull.executor_driver.presentation.waitingforclient;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.presentation.ViewState;

/**
 * Состояние ожидания при звонке клиенту или начале выполнения заказа.
 */
final class WaitingForClientViewStatePending implements ViewState<WaitingForClientViewActions> {

  @Override
  public void apply(@NonNull WaitingForClientViewActions stateActions) {
    stateActions.showWaitingForClientPending(true);
  }
}
