package com.fasten.executor_driver.presentation.waitingforclient;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Состояние бездействия вида ожидания клиента.
 */
final class WaitingForClientViewStateIdle extends
    WaitingForClientViewState {

  WaitingForClientViewStateIdle(@Nullable OrderItem orderItem) {
    super(orderItem);
  }

  @Override
  public void apply(@NonNull WaitingForClientViewActions stateActions) {
    super.apply(stateActions);
    stateActions.showWaitingForClientPending(false);
    stateActions.showOrderAvailabilityError(false);
    stateActions.showNetworkErrorMessage(false);
  }
}
