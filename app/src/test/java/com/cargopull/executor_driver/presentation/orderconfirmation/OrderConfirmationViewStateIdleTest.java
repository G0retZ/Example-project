package com.cargopull.executor_driver.presentation.orderconfirmation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OrderConfirmationViewStateIdleTest {

  @Mock
  private OrderConfirmationViewActions viewActions;
  @Mock
  private OrderConfirmationTimeoutItem orderConfirmationTimeoutItem;
  @Mock
  private OrderConfirmationTimeoutItem orderConfirmationTimeoutItem2;

  @Test
  public void testActionsWithTrue() {
    // Given:
    OrderConfirmationViewStateIdle orderConfirmationViewStateIdle = new OrderConfirmationViewStateIdle(
        orderConfirmationTimeoutItem, true);
    InOrder inOrder = Mockito.inOrder(viewActions);
    when(orderConfirmationTimeoutItem.getTimeout()).thenReturn(13_000L);

      // Action:
    orderConfirmationViewStateIdle.apply(viewActions);

      // Effect:
    inOrder.verify(viewActions).showDriverOrderConfirmationPending(false);
    inOrder.verify(viewActions).enableAcceptButton(true);
    inOrder.verify(viewActions).enableDeclineButton(true);
    inOrder.verify(viewActions).showAcceptedMessage(null);
    inOrder.verify(viewActions).showDeclinedMessage(null);
    inOrder.verify(viewActions).showFailedMessage(null);
    inOrder.verify(viewActions).showTimeout(13_000);
    verifyNoMoreInteractions(viewActions);
  }

  @Test
  public void testActionsWithFalse() {
      // Given:
    OrderConfirmationViewStateIdle orderConfirmationViewStateIdle = new OrderConfirmationViewStateIdle(
        orderConfirmationTimeoutItem, false);
    InOrder inOrder = Mockito.inOrder(viewActions);
    when(orderConfirmationTimeoutItem.getTimeout()).thenReturn(13_000L);

      // Action:
    orderConfirmationViewStateIdle.apply(viewActions);

      // Effect:
    inOrder.verify(viewActions).showDriverOrderConfirmationPending(false);
    inOrder.verify(viewActions).enableAcceptButton(false);
    inOrder.verify(viewActions).enableDeclineButton(true);
    inOrder.verify(viewActions).showAcceptedMessage(null);
    inOrder.verify(viewActions).showDeclinedMessage(null);
    inOrder.verify(viewActions).showFailedMessage(null);
    inOrder.verify(viewActions).showTimeout(13_000);
    verifyNoMoreInteractions(viewActions);
  }

  @Test
  public void testEquals() {
    OrderConfirmationViewStateIdle orderConfirmationViewStateIdle = new OrderConfirmationViewStateIdle(
        orderConfirmationTimeoutItem, true);
    assertEquals(orderConfirmationViewStateIdle, orderConfirmationViewStateIdle);
    assertEquals(orderConfirmationViewStateIdle,
        new OrderConfirmationViewStateIdle(orderConfirmationTimeoutItem, true));
    assertNotEquals(orderConfirmationViewStateIdle,
        new OrderConfirmationViewStateIdle(orderConfirmationTimeoutItem2, true));
    assertNotEquals(orderConfirmationViewStateIdle,
        new OrderConfirmationViewStateIdle(orderConfirmationTimeoutItem, false));
    assertNotEquals(orderConfirmationViewStateIdle, "");
    assertNotEquals(orderConfirmationViewStateIdle, null);
  }
}