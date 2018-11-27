package com.cargopull.executor_driver.presentation.ordershistoryheader;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.entity.OrdersHistorySummary;
import com.cargopull.executor_driver.presentation.ViewState;
import java.text.DecimalFormat;

/**
 * Состояние вида истории заказов.
 */
final class OrdersHistoryHeaderViewStateMinimized implements ViewState<OrdersHistoryHeaderViewActions> {

  @NonNull
  private final OrdersHistorySummary ordersHistorySummary;
  @NonNull
  private final Runnable maximizeAction;

  OrdersHistoryHeaderViewStateMinimized(@NonNull OrdersHistorySummary ordersHistorySummary,
      @NonNull Runnable maximizeAction) {
    this.ordersHistorySummary = ordersHistorySummary;
    this.maximizeAction = maximizeAction;
  }

  @Override
  public void apply(@NonNull OrdersHistoryHeaderViewActions stateActions) {
    stateActions.setVisible(R.id.pendingIndicator, false);
    stateActions.setVisible(R.id.networkErrorText, false);
    stateActions.setVisible(R.id.retryButton, false);
    stateActions.setVisible(R.id.summaryProfit, true);
    stateActions.setVisible(R.id.summaryLoss, true);
    stateActions.setVisible(R.id.completed, false);
    stateActions.setVisible(R.id.commission, false);
    stateActions.setVisible(R.id.rejected, false);
    stateActions.setVisible(R.id.cancelled, false);
    stateActions.setVisible(R.id.missed, false);
    boolean showCents = stateActions.isShowCents();
    int fractionDigits = showCents ? 2 : 0;
    DecimalFormat decimalFormat = new DecimalFormat(stateActions.getCurrencyFormat());
    decimalFormat.setMaximumFractionDigits(fractionDigits);
    decimalFormat.setMinimumFractionDigits(fractionDigits);
    float cost = ordersHistorySummary.getCompletedOrders() / 100f;
    cost = showCents ? cost : Math.round(cost);
    stateActions.setText(R.id.summaryProfit, decimalFormat.format(cost));
    cost = (ordersHistorySummary.getRejectedOrders() + ordersHistorySummary.getCancelledOrders()
        + ordersHistorySummary.getMissedOrders()) / 100f;
    cost = showCents ? cost : Math.round(cost);
    stateActions.setText(R.id.summaryLoss, decimalFormat.format(cost));
    stateActions.setClickAction(R.id.expandMore, maximizeAction);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    OrdersHistoryHeaderViewStateMinimized that = (OrdersHistoryHeaderViewStateMinimized) o;

    return ordersHistorySummary.equals(that.ordersHistorySummary);
  }

  @Override
  public int hashCode() {
    return ordersHistorySummary.hashCode();
  }
}
