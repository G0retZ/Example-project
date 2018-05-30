package com.fasten.executor_driver.presentation.order;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;

import com.fasten.executor_driver.presentation.ViewState;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OrderViewStateNetworkErrorTest {

  private OrderViewStateNetworkError viewState;

  @Mock
  private OrderViewActions orderViewActions;

  @Mock
  private ViewState<OrderViewActions> parentViewState;
  @Mock
  private ViewState<OrderViewActions> parentViewState1;

  @Before
  public void setUp() {
    viewState = new OrderViewStateNetworkError(parentViewState);
  }

  @Test
  public void testActions() {
    // Действие:
    viewState.apply(orderViewActions);

    // Результат:
    verify(orderViewActions).showOrderPending(false);
    verify(orderViewActions).showOrderAvailabilityError(false);
    verify(orderViewActions).showNetworkErrorMessage(true);
    verifyNoMoreInteractions(orderViewActions);
    verify(parentViewState, only()).apply(orderViewActions);
  }

  @Test
  public void testNoActions() {
    // Дано:
    viewState = new OrderViewStateNetworkError(null);

    // Действие:
    viewState.apply(orderViewActions);

    // Результат:
    verify(orderViewActions).showOrderPending(false);
    verify(orderViewActions).showOrderAvailabilityError(false);
    verify(orderViewActions).showNetworkErrorMessage(true);
    verifyNoMoreInteractions(orderViewActions);
    verifyZeroInteractions(parentViewState);
  }

  @Test
  public void testEquals() {
    viewState = new OrderViewStateNetworkError(parentViewState);
    assertEquals(viewState, new OrderViewStateNetworkError(parentViewState));
    assertNotEquals(viewState, new OrderViewStateNetworkError(parentViewState1));
    assertNotEquals(viewState, new OrderViewStateNetworkError(null));
  }
}