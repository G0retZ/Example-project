package com.fasten.executor_driver.presentation.waitingforclient;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Состояние ошибки сети вида ожидания клиента.
 */
final class WaitingForClientViewStateNetworkError extends
    WaitingForClientViewState {

  WaitingForClientViewStateNetworkError(@Nullable OrderItem orderItem) {
    super(orderItem);
  }

  @Override
  public void apply(@NonNull WaitingForClientViewActions stateActions) {
    super.apply(stateActions);
    stateActions.showWaitingForClientPending(false);
    stateActions.showOrderAvailabilityError(false);
    stateActions.showNetworkErrorMessage(true);
  }
}
