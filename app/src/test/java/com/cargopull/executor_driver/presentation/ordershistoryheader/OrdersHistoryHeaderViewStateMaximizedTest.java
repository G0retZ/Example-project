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
public class OrdersHistoryHeaderViewStateMaximizedTest {

  private ViewState<OrdersHistoryHeaderViewActions> viewState;
  @Mock
  private OrdersHistorySummary ordersHistorySummary;
  @Mock
  private OrdersHistoryHeaderViewActions viewActions;
  @Mock
  private Runnable runnable;

  @Before
  public void setUp() {
    when(ordersHistorySummary.getCompletedOrders()).thenReturn(61_382_92L);
    when(ordersHistorySummary.getRejectedOrders()).thenReturn(12_990_39L);
    when(ordersHistorySummary.getCancelledOrders()).thenReturn(3_830_20L);
    when(ordersHistorySummary.getMissedOrders()).thenReturn(5_747_28L);
    when(viewActions.getCurrencyFormat()).thenReturn("##,###,### Ps");
    viewState = new OrdersHistoryHeaderViewStateMaximized(ordersHistorySummary, runnable);
  }

  @Test
  public void testActionsWithoutCents() {
    // Дано:
    when(viewActions.isShowCents()).thenReturn(false);
    DecimalFormat decimalFormat = new DecimalFormat("##,###,### Ps");
    decimalFormat.setMaximumFractionDigits(0);
    decimalFormat.setMinimumFractionDigits(0);

    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).getCurrencyFormat();
    verify(viewActions).isShowCents();
    verify(viewActions).setVisible(R.id.pendingIndicator, false);
    verify(viewActions).setVisible(R.id.networkErrorText, false);
    verify(viewActions).setVisible(R.id.retryButton, false);
    verify(viewActions).setVisible(R.id.summaryTitle, true);
    verify(viewActions).setVisible(R.id.summaryProfit, true);
    verify(viewActions).setVisible(R.id.slash, true);
    verify(viewActions).setVisible(R.id.summaryLoss, true);
    verify(viewActions).setVisible(R.id.expandMore, true);
    verify(viewActions).setVisible(R.id.completedTitle, true);
    verify(viewActions).setVisible(R.id.completed, true);
    verify(viewActions).setVisible(R.id.commissionTitle, true);
    verify(viewActions).setVisible(R.id.commission, true);
    verify(viewActions).setVisible(R.id.rejectedTitle, true);
    verify(viewActions).setVisible(R.id.rejected, true);
    verify(viewActions).setVisible(R.id.cancelledTitle, true);
    verify(viewActions).setVisible(R.id.cancelled, true);
    verify(viewActions).setVisible(R.id.missedTitle, true);
    verify(viewActions).setVisible(R.id.missed, true);
    verify(viewActions).setText(R.id.summaryProfit, decimalFormat.format(61383));
    verify(viewActions).setText(R.id.completed, decimalFormat.format(61383));
    verify(viewActions).setText(R.id.summaryLoss, decimalFormat.format(22568));
    verify(viewActions).setText(R.id.commission, decimalFormat.format(0));
    verify(viewActions).setText(R.id.rejected, decimalFormat.format(12990));
    verify(viewActions).setText(R.id.cancelled, decimalFormat.format(3830));
    verify(viewActions).setText(R.id.missed, decimalFormat.format(5747));
    verify(viewActions).setClickAction(R.id.expandMore, runnable);
    verifyNoMoreInteractions(viewActions);
  }

  @Test
  public void testActionsWithCents() {
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
    verify(viewActions).setVisible(R.id.summaryTitle, true);
    verify(viewActions).setVisible(R.id.summaryProfit, true);
    verify(viewActions).setVisible(R.id.slash, true);
    verify(viewActions).setVisible(R.id.summaryLoss, true);
    verify(viewActions).setVisible(R.id.expandMore, true);
    verify(viewActions).setVisible(R.id.completedTitle, true);
    verify(viewActions).setVisible(R.id.completed, true);
    verify(viewActions).setVisible(R.id.commissionTitle, true);
    verify(viewActions).setVisible(R.id.commission, true);
    verify(viewActions).setVisible(R.id.rejectedTitle, true);
    verify(viewActions).setVisible(R.id.rejected, true);
    verify(viewActions).setVisible(R.id.cancelledTitle, true);
    verify(viewActions).setVisible(R.id.cancelled, true);
    verify(viewActions).setVisible(R.id.missedTitle, true);
    verify(viewActions).setVisible(R.id.missed, true);
    verify(viewActions).setText(R.id.summaryProfit, decimalFormat.format(61382.92));
    verify(viewActions).setText(R.id.completed, decimalFormat.format(61382.92));
    verify(viewActions).setText(R.id.summaryLoss, decimalFormat.format(22567.87));
    verify(viewActions).setText(R.id.commission, decimalFormat.format(0));
    verify(viewActions).setText(R.id.rejected, decimalFormat.format(12990.39));
    verify(viewActions).setText(R.id.cancelled, decimalFormat.format(3830.20));
    verify(viewActions).setText(R.id.missed, decimalFormat.format(5747.28));
    verify(viewActions).setClickAction(R.id.expandMore, runnable);
    verifyNoMoreInteractions(viewActions);
  }
}