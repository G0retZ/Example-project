package com.fasten.executor_driver.presentation.waitingforclient;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Состояние ожидания при звонке клиенту или начале выполнения заказа.
 */
final class WaitingForClientViewStatePending extends
    WaitingForClientViewState {

  WaitingForClientViewStatePending(@Nullable OrderItem orderItem) {
    super(orderItem);
  }

  @Override
  public void apply(@NonNull WaitingForClientViewActions stateActions) {
    super.apply(stateActions);
    stateActions.showWaitingForClientPending(true);
    stateActions.showOrderAvailabilityError(false);
    stateActions.showNetworkErrorMessage(false);
  }
}
