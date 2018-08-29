package com.cargopull.executor_driver.presentation.order;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;

import com.cargopull.executor_driver.presentation.ViewState;
import org.junit.Before;
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

  @Before
  public void setUp() {
    viewState = new OrderViewStateExpired(parentViewState);
  }

  @Test
  public void testActions() {
    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).showOrderExpired(true);
    verifyNoMoreInteractions(viewActions);
    verify(parentViewState, only()).apply(viewActions);
  }

  @Test
  public void testNoActions() {
    // Дано:
    viewState = new OrderViewStateExpired(null);

    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).showOrderExpired(true);
    verifyNoMoreInteractions(viewActions);
    verifyZeroInteractions(parentViewState);
  }

  @Test
  public void testEquals() {
    viewState = new OrderViewStateExpired(parentViewState);
    assertEquals(viewState, new OrderViewStateExpired(parentViewState));
    assertNotEquals(viewState, new OrderViewStateExpired(parentViewState1));
    assertNotEquals(viewState, new OrderViewStateExpired(null));
  }
}