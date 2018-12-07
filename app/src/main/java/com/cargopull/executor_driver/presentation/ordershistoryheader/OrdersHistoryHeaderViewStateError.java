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
    stateActions.setVisible(R.id.earnedTitle, false);
    stateActions.setVisible(R.id.earned, false);
    stateActions.setVisible(R.id.earnedCount, false);
    stateActions.setVisible(R.id.rejectedTitle, false);
    stateActions.setVisible(R.id.rejected, false);
    stateActions.setVisible(R.id.rejectedCount, false);
    stateActions.setVisible(R.id.missedTitle, false);
    stateActions.setVisible(R.id.missed, false);
    stateActions.setVisible(R.id.missedCount, false);
  }
}
