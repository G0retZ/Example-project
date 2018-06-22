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
public class OrderViewStateUnavailableErrorTest {

  private OrderViewStateUnavailableError viewState;

  @Mock
  private OrderViewActions viewActions;

  @Mock
  private ViewState<OrderViewActions> parentViewState;
  @Mock
  private ViewState<OrderViewActions> parentViewState1;

  @Before
  public void setUp() {
    viewState = new OrderViewStateUnavailableError(parentViewState);
  }

  @Test
  public void testActions() {
    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).showOrderPending(false);
    verify(viewActions).showOrderAvailabilityError();
    verifyNoMoreInteractions(viewActions);
    verify(parentViewState, only()).apply(viewActions);
  }

  @Test
  public void testNoActions() {
    // Дано:
    viewState = new OrderViewStateUnavailableError(null);

    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).showOrderPending(false);
    verify(viewActions).showOrderAvailabilityError();
    verifyNoMoreInteractions(viewActions);
    verifyZeroInteractions(parentViewState);
  }

  @Test
  public void testEquals() {
    viewState = new OrderViewStateUnavailableError(parentViewState);
    assertEquals(viewState, new OrderViewStateUnavailableError(parentViewState));
    assertNotEquals(viewState, new OrderViewStateUnavailableError(parentViewState1));
    assertNotEquals(viewState, new OrderViewStateUnavailableError(null));
  }
}