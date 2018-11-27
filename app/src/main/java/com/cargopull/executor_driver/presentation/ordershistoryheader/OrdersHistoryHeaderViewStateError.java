package com.cargopull.executor_driver.presentation.ordershistoryheader;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.presentation.ViewState;

/**
 * Состояние вида ошибки получения истории заказов.
 */
final class OrdersHistoryHeaderViewStateError implements ViewState<OrdersHistoryHeaderViewActions> {

  @Override
  public void apply(@NonNull OrdersHistoryHeaderViewActions stateActions) {
    stateActions.setVisible(R.id.pendingIndicator, false);
    stateActions.setVisible(R.id.networkErrorText, true);
    stateActions.setVisible(R.id.retryButton, true);
    stateActions.setVisible(R.id.summaryProfit, false);
    stateActions.setVisible(R.id.summaryLoss, false);
    stateActions.setVisible(R.id.completed, false);
    stateActions.setVisible(R.id.commission, false);
    stateActions.setVisible(R.id.rejected, false);
    stateActions.setVisible(R.id.cancelled, false);
    stateActions.setVisible(R.id.missed, false);
    stateActions.setClickAction(R.id.expandMore, null);
  }
}
