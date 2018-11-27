package com.cargopull.executor_driver.presentation.ordershistoryheader;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.presentation.ViewState;

/**
 * Состояние вида ожидания истории заказов.
 */
final class OrdersHistoryHeaderViewStatePending implements ViewState<OrdersHistoryHeaderViewActions> {

  @Override
  public void apply(@NonNull OrdersHistoryHeaderViewActions stateActions) {
    stateActions.setVisible(R.id.pendingIndicator, true);
    stateActions.setVisible(R.id.networkErrorText, false);
    stateActions.setVisible(R.id.retryButton, false);
    stateActions.setVisible(R.id.summaryTitle, true);
    stateActions.setVisible(R.id.summaryProfit, false);
    stateActions.setVisible(R.id.slash, true);
    stateActions.setVisible(R.id.summaryLoss, false);
    stateActions.setVisible(R.id.expandMore, true);
    stateActions.setVisible(R.id.completedTitle, false);
    stateActions.setVisible(R.id.completed, false);
    stateActions.setVisible(R.id.commissionTitle, false);
    stateActions.setVisible(R.id.commission, false);
    stateActions.setVisible(R.id.rejectedTitle, false);
    stateActions.setVisible(R.id.rejected, false);
    stateActions.setVisible(R.id.cancelledTitle, false);
    stateActions.setVisible(R.id.cancelled, false);
    stateActions.setVisible(R.id.missedTitle, false);
    stateActions.setVisible(R.id.missed, false);
    stateActions.setClickAction(R.id.expandMore, null);
  }
}
