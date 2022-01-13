package com.cargopull.executor_driver.presentation.order;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

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
  @Mock
  private Runnable action;
  @Mock
  private Runnable action1;

  @Test
  public void testActionsWithParent() {
    // Given:
    viewState = new OrderViewStateExpired(parentViewState, "message", action);

      // Action:
    viewState.apply(viewActions);

      // Effect:
    verify(viewActions).showPersistentDialog("message", action);
    verifyNoMoreInteractions(viewActions);
    verify(parentViewState, only()).apply(viewActions);
  }

  @Test
  public void testNoActionsWithoutParent() {
      // Given:
    viewState = new OrderViewStateExpired(null, "mess", action);

      // Action:
    viewState.apply(viewActions);

      // Effect:
    verify(viewActions).showPersistentDialog("mess", action);
    verifyNoMoreInteractions(viewActions);
    verifyNoInteractions(parentViewState);
  }

  @Test
  public void testEquals() {
    viewState = new OrderViewStateExpired(parentViewState, "message", action);
    assertEquals(viewState, new OrderViewStateExpired(parentViewState, "message", action));
    assertEquals(viewState, new OrderViewStateExpired(parentViewState, "message", action1));
    assertNotEquals(viewState, new OrderViewStateExpired(null, "message", action));
    assertNotEquals(viewState, new OrderViewStateExpired(parentViewState1, "message", action));
    assertNotEquals(viewState, new OrderViewStateExpired(parentViewState, "mess", action));
  }
}