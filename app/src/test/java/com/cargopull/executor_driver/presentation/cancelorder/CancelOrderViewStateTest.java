package com.cargopull.executor_driver.presentation.cancelorder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.cargopull.executor_driver.entity.CancelOrderReason;
import java.util.Collections;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CancelOrderViewStateTest {

  private CancelOrderViewState viewState;

  @Mock
  private CancelOrderViewActions viewActions;
  @Mock
  private CancelOrderReason routePointItems;
  @Mock
  private CancelOrderReason routePointItems1;

  @Test
  public void testActions() {
    // Дано:
    viewState = new CancelOrderViewState(Collections.singletonList(routePointItems));

    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).setCancelOrderReasons(Collections.singletonList(routePointItems));
    verify(viewActions).showCancelOrderReasons(true);
    verify(viewActions).showCancelOrderPending(false);
    verifyNoMoreInteractions(viewActions);
  }

  @Test
  public void testEquals() {
    viewState = new CancelOrderViewState(Collections.singletonList(routePointItems));
    assertEquals(viewState, new CancelOrderViewState(Collections.singletonList(routePointItems)));
    assertNotEquals(viewState,
        new CancelOrderViewState(Collections.singletonList(routePointItems1)));
  }
}