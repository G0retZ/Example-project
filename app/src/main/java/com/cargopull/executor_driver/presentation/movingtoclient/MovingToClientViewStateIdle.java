package com.cargopull.executor_driver.presentation.movingtoclient;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.presentation.ViewState;

/**
 * Состояние бездействия вида заказа.
 */
final class MovingToClientViewStateIdle implements ViewState<MovingToClientViewActions> {

  @Override
  public void apply(@NonNull MovingToClientViewActions stateActions) {
    stateActions.showMovingToClientPending(false);
    stateActions.enableMovingToClientCallButton(true);
  }
}
