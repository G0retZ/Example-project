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
public class OrderViewStateExpiredTest {

  private OrderViewStateExpired viewState;

  @Mock
  private OrderViewActions viewActions;

  @Mock
  private ViewState<OrderViewActions> parentViewState;
  @Mock
  private ViewState<OrderViewActions> parentViewState1;

  @Test
  public void testActions() {
    // Дано:
    viewState = new OrderViewStateExpired(parentViewState, "message");

    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).showOrderExpiredMessage("message");
    verifyNoMoreInteractions(viewActions);
    verify(parentViewState, only()).apply(viewActions);
  }

  @Test
  public void testNoActions() {
    // Дано:
    viewState = new OrderViewStateExpired(null, "mess");

    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).showOrderExpiredMessage("mess");
    verifyNoMoreInteractions(viewActions);
    verifyZeroInteractions(parentViewState);
  }

  @Test
  public void testEquals() {
    viewState = new OrderViewStateExpired(parentViewState, "message");
    assertEquals(viewState, new OrderViewStateExpired(parentViewState, "message"));
    assertNotEquals(viewState, new OrderViewStateExpired(parentViewState, "mess"));
    assertNotEquals(viewState, new OrderViewStateExpired(parentViewState1, "message"));
    assertNotEquals(viewState, new OrderViewStateExpired(null, "message"));
  }
}