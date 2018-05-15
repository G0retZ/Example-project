package com.fasten.executor_driver.presentation.movingtoclient;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Состояние ошибки доступности зака вида заказа.
 */
final class MovingToClientViewStateUnavailableError extends MovingToClientViewState {

  MovingToClientViewStateUnavailableError(@Nullable RouteItem routeItem) {
    super(routeItem);
  }

  @Override
  public void apply(@NonNull MovingToClientViewActions stateActions) {
    super.apply(stateActions);
    stateActions.showMovingToClientPending(false);
    stateActions.showOrderAvailabilityError(true);
    stateActions.showNetworkErrorMessage(false);
  }
}
