package com.cargopull.executor_driver.presentation.clientorderconfirmationtime;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.presentation.ViewState;

/**
 * Состояние вида остановки таймера ожидания подтверждения клиентом.
 */
class ClientOrderConfirmationTimeViewStateNotCounting implements
    ViewState<ClientOrderConfirmationTimeViewActions> {

  @Override
  public void apply(@NonNull ClientOrderConfirmationTimeViewActions stateActions) {
    stateActions.setWaitingForClientTimeText(R.string.client_confirmation_problem);
    stateActions.showWaitingForClientTimer(false);
  }
}
