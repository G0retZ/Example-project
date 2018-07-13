package com.cargopull.executor_driver.presentation.clientorderconfirmationtime;

import android.support.annotation.NonNull;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.presentation.ViewState;

/**
 * Состояние вида работы таймера ожидания подтверждения клиентом.
 */
class ClientOrderConfirmationTimeViewStateCounting implements
    ViewState<ClientOrderConfirmationTimeViewActions> {

  private final long orderTimeElapsed;

  ClientOrderConfirmationTimeViewStateCounting(long orderTimeElapsed) {
    this.orderTimeElapsed = orderTimeElapsed;
  }

  @Override
  public void apply(@NonNull ClientOrderConfirmationTimeViewActions stateActions) {
    stateActions.setWaitingForClientTime(orderTimeElapsed);
    stateActions.setWaitingForClientTimeText(R.string.client_confirmation);
    stateActions.showWaitingForClientTimer(true);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    ClientOrderConfirmationTimeViewStateCounting that = (ClientOrderConfirmationTimeViewStateCounting) o;

    return orderTimeElapsed == that.orderTimeElapsed;
  }

  @Override
  public int hashCode() {
    return (int) (orderTimeElapsed ^ (orderTimeElapsed >>> 32));
  }
}
