package com.cargopull.executor_driver.presentation.balance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.cargopull.executor_driver.entity.ExecutorBalance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class BalanceViewStateTest {

  private BalanceViewState viewState;

  @Mock
  private BalanceViewActions viewActions;
  @Mock
  private ExecutorBalance executorBalance;
  @Mock
  private ExecutorBalance executorBalance1;

  @Before
  public void setUp() {
    when(executorBalance.getMainAccount()).thenReturn(1L);
    when(executorBalance.getBonusAccount()).thenReturn(2L);
    viewState = new BalanceViewState(executorBalance);
  }

  @Test
  public void testActions() {
    // Дано:

    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).showMainAccountAmount(1);
    verify(viewActions).showBonusAccountAmount(2);
    verify(viewActions).showBalancePending(false);
    verifyNoMoreInteractions(viewActions);
  }

  @Test
  public void testEquals() {
    assertEquals(viewState, new BalanceViewState(executorBalance));
    assertNotEquals(viewState, new BalanceViewState(executorBalance1));
  }
}