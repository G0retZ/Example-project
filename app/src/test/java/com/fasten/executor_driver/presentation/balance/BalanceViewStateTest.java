package com.fasten.executor_driver.presentation.balance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.fasten.executor_driver.entity.ExecutorBalance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class BalanceViewStateTest {

  private BalanceViewState viewState;

  @Mock
  private BalanceViewActions orderRouteViewActions;
  @Mock
  private ExecutorBalance executorBalance;
  @Mock
  private ExecutorBalance executorBalance1;

  @Before
  public void setUp() {
    when(executorBalance.getMainAccount()).thenReturn(1);
    when(executorBalance.getBonusAccount()).thenReturn(2);
    viewState = new BalanceViewState(executorBalance);
  }

  @Test
  public void testActions() {
    // Дано:

    // Действие:
    viewState.apply(orderRouteViewActions);

    // Результат:
    verify(orderRouteViewActions).showMainAccountAmount(1);
    verify(orderRouteViewActions).showBonusAccountAmount(2);
    verify(orderRouteViewActions).showBalancePending(false);
    verify(orderRouteViewActions).showBalanceErrorMessage(false);
    verifyNoMoreInteractions(orderRouteViewActions);
  }

  @Test
  public void testEquals() {
    assertEquals(viewState, new BalanceViewState(executorBalance));
    assertNotEquals(viewState, new BalanceViewState(executorBalance1));
  }
}