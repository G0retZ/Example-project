package com.cargopull.executor_driver.presentation.ordershistoryheader;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.entity.OrdersHistorySummary;
import com.cargopull.executor_driver.presentation.ViewState;
import java.text.DecimalFormat;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OrdersHistoryHeaderViewStateLoadedTest {

  private ViewState<OrdersHistoryHeaderViewActions> viewState;
  @Mock
  private OrdersHistorySummary ordersHistorySummary;
  @Mock
  private OrdersHistoryHeaderViewActions viewActions;

  @Before
  public void setUp() {
    when(ordersHistorySummary.getCompletedOrders()).thenReturn(61_382_92L);
    when(ordersHistorySummary.getRejectedOrders()).thenReturn(12_990_39L);
    when(ordersHistorySummary.getMissedOrders()).thenReturn(5_747_28L);
    when(ordersHistorySummary.getCompletedOrdersCount()).thenReturn(3);
    when(ordersHistorySummary.getRejectedOrdersCount()).thenReturn(2);
    when(ordersHistorySummary.getMissedOrdersCount()).thenReturn(1);
    when(viewActions.getCurrencyFormat()).thenReturn("##,###,### Ps");
    viewState = new OrdersHistoryHeaderViewStateLoaded(ordersHistorySummary);
  }

  @Test
  public void testActions() {
    // Дано:
    when(viewActions.isShowCents()).thenReturn(true);
    DecimalFormat decimalFormat = new DecimalFormat("##,###,### Ps");
    decimalFormat.setMaximumFractionDigits(2);
    decimalFormat.setMinimumFractionDigits(2);

    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).getCurrencyFormat();
    verify(viewActions).isShowCents();
    verify(viewActions).setVisible(R.id.pendingIndicator, false);
    verify(viewActions).setVisible(R.id.networkErrorText, false);
    verify(viewActions).setVisible(R.id.retryButton, false);
    verify(viewActions).setVisible(R.id.earnedTitle, true);
    verify(viewActions).setVisible(R.id.earned, true);
    verify(viewActions).setVisible(R.id.earnedCount, true);
    verify(viewActions).setVisible(R.id.rejectedTitle, true);
    verify(viewActions).setVisible(R.id.rejected, true);
    verify(viewActions).setVisible(R.id.rejectedCount, true);
    verify(viewActions).setVisible(R.id.missedTitle, true);
    verify(viewActions).setVisible(R.id.missed, true);
    verify(viewActions).setVisible(R.id.missedCount, true);
    verify(viewActions).setText(R.id.earned, decimalFormat.format(61382.92));
    verify(viewActions).setFormattedText(R.id.earnedCount, R.string.orders_count, 3);
    verify(viewActions).setText(R.id.rejected, decimalFormat.format(12990.39));
    verify(viewActions).setFormattedText(R.id.rejectedCount, R.string.orders_count, 2);
    verify(viewActions).setText(R.id.missed, decimalFormat.format(5747.28));
    verify(viewActions).setFormattedText(R.id.missedCount, R.string.orders_count, 1);
    verifyNoMoreInteractions(viewActions);
  }
}