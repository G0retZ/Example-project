package com.cargopull.executor_driver.presentation.movingtoclient;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.presentation.ViewState;

/**
 * Состояние бездействия вида заказа.
 */
final class MovingToClientViewStateCalling implements ViewState<MovingToClientViewActions> {

  @Override
  public void apply(@NonNull MovingToClientViewActions stateActions) {
    stateActions.showMovingToClientPending(false);
    stateActions.enableMovingToClientCallButton(false);
  }
}
