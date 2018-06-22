package com.fasten.executor_driver.presentation.cancelorder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.fasten.executor_driver.presentation.ViewState;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CancelOrderViewStateServerDataErrorTest {

  private CancelOrderViewStateServerDataError viewState;

  @Mock
  private CancelOrderViewActions viewActions;
  @Mock
  private ViewState<CancelOrderViewActions> parentViewState;
  @Mock
  private ViewState<CancelOrderViewActions> parentViewState1;

  @Test
  public void testActions() {
    // Дано:
    viewState = new CancelOrderViewStateServerDataError(parentViewState);

    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).showCancelOrderServerDataError();
    verify(viewActions).showCancelOrderReasons(true);
    verify(parentViewState, only()).apply(viewActions);
    verifyNoMoreInteractions(viewActions);
  }

  @Test
  public void testActionsWithNull() {
    // Дано:
    viewState = new CancelOrderViewStateServerDataError(null);

    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).showCancelOrderServerDataError();
    verify(viewActions).showCancelOrderReasons(false);
    verifyNoMoreInteractions(viewActions);
  }

  @Test
  public void testEquals() {
    viewState = new CancelOrderViewStateServerDataError(parentViewState);
    assertEquals(viewState, new CancelOrderViewStateServerDataError(parentViewState));
    assertNotEquals(viewState, new CancelOrderViewStateServerDataError(parentViewState1));
    assertNotEquals(viewState, new CancelOrderViewStateServerDataError(null));
  }
}