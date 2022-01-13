package com.cargopull.executor_driver.presentation.order;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.cargopull.executor_driver.presentation.ViewState;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OrderViewStatePendingTest {

  private OrderViewStatePending viewState;

  @Mock
  private OrderViewActions viewActions;

  @Mock
  private ViewState<OrderViewActions> parentViewState;
  @Mock
  private ViewState<OrderViewActions> parentViewState1;

  @Before
  public void setUp() {
    viewState = new OrderViewStatePending(parentViewState);
  }

  @Test
  public void testActions() {
    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).blockWithPending("OrderViewState");
    verifyNoMoreInteractions(viewActions);
    verify(parentViewState, only()).apply(viewActions);
  }

  @Test
  public void testNoActions() {
    // Дано:
    viewState = new OrderViewStatePending(null);

    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).blockWithPending("OrderViewState");
    verifyNoMoreInteractions(viewActions);
    verifyNoInteractions(parentViewState);
  }

  @Test
  public void testEquals() {
    viewState = new OrderViewStatePending(parentViewState);
    assertEquals(viewState, new OrderViewStatePending(parentViewState));
    assertNotEquals(viewState, new OrderViewStatePending(parentViewState1));
    assertNotEquals(viewState, new OrderViewStatePending(null));
  }
}