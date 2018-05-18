package com.fasten.executor_driver.presentation.waitingforclient;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Состояние ошибки доступности зака вида ожидания клиента.
 */
final class WaitingForClientViewStateUnavailableError extends
    WaitingForClientViewState {

  WaitingForClientViewStateUnavailableError(@Nullable OrderItem orderItem) {
    super(orderItem);
  }

  @Override
  public void apply(@NonNull WaitingForClientViewActions stateActions) {
    super.apply(stateActions);
    stateActions.showWaitingForClientPending(false);
    stateActions.showOrderAvailabilityError(true);
    stateActions.showNetworkErrorMessage(false);
  }
}
