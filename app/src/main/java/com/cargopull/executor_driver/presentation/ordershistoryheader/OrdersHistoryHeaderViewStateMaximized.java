package com.cargopull.executor_driver.presentation.ordershistoryheader;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.entity.OrdersHistorySummary;
import com.cargopull.executor_driver.presentation.ViewState;
import java.text.DecimalFormat;

/**
 * Состояние вида истории заказов.
 */
final class OrdersHistoryHeaderViewStateMaximized implements ViewState<OrdersHistoryHeaderViewActions> {

  @NonNull
  private final OrdersHistorySummary ordersHistorySummary;
  @NonNull
  private final Runnable minimizeAction;

  OrdersHistoryHeaderViewStateMaximized(@NonNull OrdersHistorySummary ordersHistorySummary,
      @NonNull Runnable minimizeAction) {
    this.ordersHistorySummary = ordersHistorySummary;
    this.minimizeAction = minimizeAction;
  }

  @Override
  public void apply(@NonNull OrdersHistoryHeaderViewActions stateActions) {
    stateActions.setVisible(R.id.pendingIndicator, false);
    stateActions.setVisible(R.id.networkErrorText, false);
    stateActions.setVisible(R.id.retryButton, false);
    stateActions.setVisible(R.id.summaryProfit, true);
    stateActions.setVisible(R.id.summaryLoss, true);
    stateActions.setVisible(R.id.completed, true);
    stateActions.setVisible(R.id.commission, true);
    stateActions.setVisible(R.id.rejected, true);
    stateActions.setVisible(R.id.cancelled, true);
    stateActions.setVisible(R.id.missed, true);
    boolean showCents = stateActions.isShowCents();
    int fractionDigits = showCents ? 2 : 0;
    DecimalFormat decimalFormat = new DecimalFormat(stateActions.getCurrencyFormat());
    decimalFormat.setMaximumFractionDigits(fractionDigits);
    decimalFormat.setMinimumFractionDigits(fractionDigits);
    float cost = ordersHistorySummary.getCompletedOrders() / 100f;
    cost = showCents ? cost : Math.round(cost);
    stateActions.setText(R.id.summaryProfit, decimalFormat.format(cost));
    stateActions.setText(R.id.completed, decimalFormat.format(cost));
    cost = (ordersHistorySummary.getRejectedOrders() + ordersHistorySummary.getCancelledOrders()
        + ordersHistorySummary.getMissedOrders()) / 100f;
    cost = showCents ? cost : Math.round(cost);
    stateActions.setText(R.id.summaryLoss, decimalFormat.format(cost));
    stateActions.setText(R.id.commission, decimalFormat.format(0));
    cost = ordersHistorySummary.getRejectedOrders() / 100f;
    cost = showCents ? cost : Math.round(cost);
    stateActions.setText(R.id.rejected, decimalFormat.format(cost));
    cost = ordersHistorySummary.getCancelledOrders() / 100f;
    cost = showCents ? cost : Math.round(cost);
    stateActions.setText(R.id.cancelled, decimalFormat.format(cost));
    cost = ordersHistorySummary.getMissedOrders() / 100f;
    cost = showCents ? cost : Math.round(cost);
    stateActions.setText(R.id.missed, decimalFormat.format(cost));
    stateActions.setClickAction(R.id.expandMore, minimizeAction);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    OrdersHistoryHeaderViewStateMaximized that = (OrdersHistoryHeaderViewStateMaximized) o;

    return ordersHistorySummary.equals(that.ordersHistorySummary);
  }

  @Override
  public int hashCode() {
    return ordersHistorySummary.hashCode();
  }
}
