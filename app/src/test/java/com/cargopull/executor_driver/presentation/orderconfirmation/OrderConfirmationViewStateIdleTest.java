package com.cargopull.executor_driver.presentation.orderconfirmation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OrderConfirmationViewStateIdleTest {

  private OrderConfirmationViewStateIdle orderConfirmationViewStateIdle;
  @Mock
  private OrderConfirmationViewActions viewActions;
  @Mock
  private OrderConfirmationTimeoutItem orderConfirmationTimeoutItem;
  @Mock
  private OrderConfirmationTimeoutItem orderConfirmationTimeoutItem2;

  @Before
  public void setUp() {
    orderConfirmationViewStateIdle = new OrderConfirmationViewStateIdle(
        orderConfirmationTimeoutItem);
  }

  @Test
  public void testActions() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewActions);
    when(orderConfirmationTimeoutItem.getProgressLeft()).thenReturn(73);
    when(orderConfirmationTimeoutItem.getTimeout()).thenReturn(13_000L);

    // Действие:
    orderConfirmationViewStateIdle.apply(viewActions);

    // Результат:
    inOrder.verify(viewActions).showDriverOrderConfirmationPending(false);
    inOrder.verify(viewActions).enableAcceptButton(true);
    inOrder.verify(viewActions).enableDeclineButton(true);
    inOrder.verify(viewActions).showAcceptedMessage(null);
    inOrder.verify(viewActions).showDeclinedMessage(null);
    inOrder.verify(viewActions).showFailedMessage(null);
    inOrder.verify(viewActions).showTimeout(73, 13_000);
    verifyNoMoreInteractions(viewActions);
  }

  @Test
  public void testEquals() {
    assertEquals(orderConfirmationViewStateIdle, orderConfirmationViewStateIdle);
    assertEquals(orderConfirmationViewStateIdle,
        new OrderConfirmationViewStateIdle(orderConfirmationTimeoutItem));
    assertNotEquals(orderConfirmationViewStateIdle,
        new OrderConfirmationViewStateIdle(orderConfirmationTimeoutItem2));
    assertNotEquals(orderConfirmationViewStateIdle, "");
    assertNotEquals(orderConfirmationViewStateIdle, null);
  }
}