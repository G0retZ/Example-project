package com.cargopull.executor_driver.presentation.order;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;

import com.cargopull.executor_driver.presentation.ViewState;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OrderViewStateCancelledTest {

  private OrderViewStateCancelled viewState;

  @Mock
  private OrderViewActions viewActions;

  @Mock
  private ViewState<OrderViewActions> parentViewState;
  @Mock
  private ViewState<OrderViewActions> parentViewState1;

  @Test
  public void testActions() {
    // Дано:
    viewState = new OrderViewStateCancelled(parentViewState);

    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).showOrderCancelledMessage(true);
    verifyNoMoreInteractions(viewActions);
    verify(parentViewState, only()).apply(viewActions);
  }

  @Test
  public void testNoActions() {
    // Дано:
    viewState = new OrderViewStateCancelled(null);

    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).showOrderCancelledMessage(true);
    verifyNoMoreInteractions(viewActions);
    verifyZeroInteractions(parentViewState);
  }

  @Test
  public void testEquals() {
    viewState = new OrderViewStateCancelled(parentViewState);
    assertEquals(viewState, new OrderViewStateCancelled(parentViewState));
    assertNotEquals(viewState, new OrderViewStateCancelled(parentViewState1));
    assertNotEquals(viewState, new OrderViewStateCancelled(null));
  }
}