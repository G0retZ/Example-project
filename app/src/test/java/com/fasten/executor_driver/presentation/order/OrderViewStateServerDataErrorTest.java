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
public class OrderViewStateServerDataErrorTest {

  private OrderViewStateServerDataError viewState;

  @Mock
  private OrderViewActions orderViewActions;

  @Mock
  private ViewState<OrderViewActions> parentViewState;
  @Mock
  private ViewState<OrderViewActions> parentViewState1;

  @Before
  public void setUp() {
    viewState = new OrderViewStateServerDataError(parentViewState);
  }

  @Test
  public void testActions() {
    // Действие:
    viewState.apply(orderViewActions);

    // Результат:
    verify(orderViewActions).showOrderPending(false);
    verify(orderViewActions).showOrderServerDataError();
    verifyNoMoreInteractions(orderViewActions);
    verify(parentViewState, only()).apply(orderViewActions);
  }

  @Test
  public void testNoActions() {
    // Дано:
    viewState = new OrderViewStateServerDataError(null);

    // Действие:
    viewState.apply(orderViewActions);

    // Результат:
    verify(orderViewActions).showOrderPending(false);
    verify(orderViewActions).showOrderServerDataError();
    verifyNoMoreInteractions(orderViewActions);
    verifyZeroInteractions(parentViewState);
  }

  @Test
  public void testEquals() {
    viewState = new OrderViewStateServerDataError(parentViewState);
    assertEquals(viewState, new OrderViewStateServerDataError(parentViewState));
    assertNotEquals(viewState, new OrderViewStateServerDataError(parentViewState1));
    assertNotEquals(viewState, new OrderViewStateServerDataError(null));
  }
}