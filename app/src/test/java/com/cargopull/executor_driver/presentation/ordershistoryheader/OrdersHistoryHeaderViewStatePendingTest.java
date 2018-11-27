package com.cargopull.executor_driver.presentation.ordershistoryheader;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.cargopull.executor_driver.R;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OrdersHistoryHeaderViewStatePendingTest {

  @Mock
  private OrdersHistoryHeaderViewActions viewActions;

  @Test
  public void testActions() {
    // Действие:
    new OrdersHistoryHeaderViewStatePending().apply(viewActions);

    // Результат:
    verify(viewActions).setVisible(R.id.pendingIndicator, true);
    verify(viewActions).setVisible(R.id.networkErrorText, false);
    verify(viewActions).setVisible(R.id.retryButton, false);
    verify(viewActions).setVisible(R.id.summaryTitle, true);
    verify(viewActions).setVisible(R.id.summaryProfit, false);
    verify(viewActions).setVisible(R.id.slash, true);
    verify(viewActions).setVisible(R.id.summaryLoss, false);
    verify(viewActions).setVisible(R.id.expandMore, true);
    verify(viewActions).setVisible(R.id.completedTitle, false);
    verify(viewActions).setVisible(R.id.completed, false);
    verify(viewActions).setVisible(R.id.commissionTitle, false);
    verify(viewActions).setVisible(R.id.commission, false);
    verify(viewActions).setVisible(R.id.rejectedTitle, false);
    verify(viewActions).setVisible(R.id.rejected, false);
    verify(viewActions).setVisible(R.id.cancelledTitle, false);
    verify(viewActions).setVisible(R.id.cancelled, false);
    verify(viewActions).setVisible(R.id.missedTitle, false);
    verify(viewActions).setVisible(R.id.missed, false);
    verify(viewActions).setClickAction(R.id.expandMore, null);
    verifyNoMoreInteractions(viewActions);
  }
}