package com.cargopull.executor_driver.presentation.movingtoclient;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.presentation.ViewState;

/**
 * Состояние ожидания при подтверждении или отказе от заказа.
 */
final class MovingToClientViewStatePending implements ViewState<MovingToClientViewActions> {

  @Override
  public void apply(@NonNull MovingToClientViewActions stateActions) {
    stateActions.showMovingToClientPending(true);
    stateActions.enableMovingToClientCallButton(true);
  }
}
