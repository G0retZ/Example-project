package com.cargopull.executor_driver.presentation.ordershistoryheader;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.entity.OrdersHistorySummary;
import com.cargopull.executor_driver.presentation.ViewState;
import java.text.DecimalFormat;

/**
 * Состояние вида истории заказов.
 */
final class OrdersHistoryHeaderViewStateLoaded implements
    ViewState<OrdersHistoryHeaderViewActions> {

  @NonNull
  private final OrdersHistorySummary ordersHistorySummary;

  OrdersHistoryHeaderViewStateLoaded(@NonNull OrdersHistorySummary ordersHistorySummary) {
    this.ordersHistorySummary = ordersHistorySummary;
  }

  @Override
  public void apply(@NonNull OrdersHistoryHeaderViewActions stateActions) {
    stateActions.setVisible(R.id.pendingIndicator, false);
    stateActions.setVisible(R.id.networkErrorText, false);
    stateActions.setVisible(R.id.retryButton, false);
    stateActions.setVisible(R.id.earnedTitle, true);
    stateActions.setVisible(R.id.earned, true);
    stateActions.setVisible(R.id.earnedCount, true);
    stateActions.setVisible(R.id.rejectedTitle, true);
    stateActions.setVisible(R.id.rejected, true);
    stateActions.setVisible(R.id.rejectedCount, true);
    stateActions.setVisible(R.id.missedTitle, true);
    stateActions.setVisible(R.id.missed, true);
    stateActions.setVisible(R.id.missedCount, true);
    boolean showCents = stateActions.isShowCents();
    int fractionDigits = showCents ? 2 : 0;
    DecimalFormat decimalFormat = new DecimalFormat(stateActions.getCurrencyFormat());
    decimalFormat.setMaximumFractionDigits(fractionDigits);
    decimalFormat.setMinimumFractionDigits(fractionDigits);
    float cost = ordersHistorySummary.getCompletedOrders() / 100f;
    cost = showCents ? cost : Math.round(cost);
    stateActions.setText(R.id.earned, decimalFormat.format(cost));
    stateActions.setFormattedText(R.id.earnedCount, R.string.orders_count,
        ordersHistorySummary.getCompletedOrdersCount());
    cost = ordersHistorySummary.getRejectedOrders() / 100f;
    cost = showCents ? cost : Math.round(cost);
    stateActions.setText(R.id.rejected, decimalFormat.format(cost));
    stateActions.setFormattedText(R.id.rejectedCount, R.string.orders_count,
        ordersHistorySummary.getRejectedOrdersCount());
    cost = ordersHistorySummary.getMissedOrders() / 100f;
    cost = showCents ? cost : Math.round(cost);
    stateActions.setText(R.id.missed, decimalFormat.format(cost));
    stateActions.setFormattedText(R.id.missedCount, R.string.orders_count,
        ordersHistorySummary.getMissedOrdersCount());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    OrdersHistoryHeaderViewStateLoaded that = (OrdersHistoryHeaderViewStateLoaded) o;

    return ordersHistorySummary.equals(that.ordersHistorySummary);
  }

  @Override
  public int hashCode() {
    return ordersHistorySummary.hashCode();
  }
}
