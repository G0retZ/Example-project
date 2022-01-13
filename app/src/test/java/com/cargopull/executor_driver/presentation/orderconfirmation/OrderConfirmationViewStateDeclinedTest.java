package com.cargopull.executor_driver.presentation.orderconfirmation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OrderConfirmationViewStateDeclinedTest {

  @Mock
  private OrderConfirmationViewActions viewActions;

  @Test
  public void testActionsWithMessage() {
    // Action:
    InOrder inOrder = Mockito.inOrder(viewActions);
    new OrderConfirmationViewStateDeclined("message").apply(viewActions);

      // Effect:
    inOrder.verify(viewActions).showDriverOrderConfirmationPending(true);
    inOrder.verify(viewActions).enableAcceptButton(false);
    inOrder.verify(viewActions).enableDeclineButton(false);
    inOrder.verify(viewActions).showAcceptedMessage(null);
    inOrder.verify(viewActions).showFailedMessage(null);
    inOrder.verify(viewActions).showDeclinedMessage("message");
    verifyNoMoreInteractions(viewActions);
  }

  @Test
  public void testEquals() {
    OrderConfirmationViewStateDeclined viewState = new OrderConfirmationViewStateDeclined(
        "message");
    assertEquals(viewState, viewState);
    assertEquals(viewState, new OrderConfirmationViewStateDeclined("message"));
    assertNotEquals(viewState, new OrderConfirmationViewStateDeclined(""));
    assertNotEquals(viewState, "");
    assertNotEquals(viewState, null);
  }

  @Test
  public void testHashCode() {
    OrderConfirmationViewStateDeclined viewState = new OrderConfirmationViewStateDeclined(
        "message");
    assertEquals(viewState.hashCode(), "message".hashCode());
  }
}