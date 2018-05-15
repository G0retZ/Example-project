package com.fasten.executor_driver.presentation.movingtoclient;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Состояние бездействия вида заказа.
 */
final class MovingToClientViewStateIdle extends
    MovingToClientViewState {

  MovingToClientViewStateIdle(@Nullable RouteItem routeItem) {
    super(routeItem);
  }

  @Override
  public void apply(@NonNull MovingToClientViewActions stateActions) {
    super.apply(stateActions);
    stateActions.showMovingToClientPending(false);
    stateActions.showOrderAvailabilityError(false);
    stateActions.showNetworkErrorMessage(false);
  }
}
