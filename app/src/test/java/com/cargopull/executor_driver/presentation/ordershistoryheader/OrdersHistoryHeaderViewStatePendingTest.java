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
    verify(viewActions).setVisible(R.id.earnedTitle, false);
    verify(viewActions).setVisible(R.id.earned, false);
    verify(viewActions).setVisible(R.id.earnedCount, false);
    verify(viewActions).setVisible(R.id.rejectedTitle, false);
    verify(viewActions).setVisible(R.id.rejected, false);
    verify(viewActions).setVisible(R.id.rejectedCount, false);
    verify(viewActions).setVisible(R.id.missedTitle, false);
    verify(viewActions).setVisible(R.id.missed, false);
    verify(viewActions).setVisible(R.id.missedCount, false);
    verifyNoMoreInteractions(viewActions);
  }
}