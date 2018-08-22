package com.cargopull.executor_driver.presentation.cancelorderreasons;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.cargopull.executor_driver.presentation.ViewState;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CancelOrderReasonsViewStatePendingTest {

  private CancelOrderReasonsViewStatePending viewState;

  @Mock
  private CancelOrderReasonsViewActions viewActions;
  @Mock
  private ViewState<CancelOrderReasonsViewActions> parentViewState;
  @Mock
  private ViewState<CancelOrderReasonsViewActions> parentViewState1;

  @Test
  public void testActions() {
    // Дано:
    viewState = new CancelOrderReasonsViewStatePending(parentViewState);

    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).showCancelOrderReasonsPending(true);
    verify(viewActions).showCancelOrderReasons(true);
    verify(parentViewState, only()).apply(viewActions);
    verifyNoMoreInteractions(viewActions);
  }

  @Test
  public void testActionsWithNull() {
    // Дано:
    viewState = new CancelOrderReasonsViewStatePending(null);

    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).showCancelOrderReasonsPending(true);
    verify(viewActions).showCancelOrderReasons(false);
    verifyNoMoreInteractions(viewActions);
  }

  @Test
  public void testEquals() {
    viewState = new CancelOrderReasonsViewStatePending(parentViewState);
    assertEquals(viewState, new CancelOrderReasonsViewStatePending(parentViewState));
    assertNotEquals(viewState, new CancelOrderReasonsViewStatePending(parentViewState1));
    assertNotEquals(viewState, new CancelOrderReasonsViewStatePending(null));
  }
}