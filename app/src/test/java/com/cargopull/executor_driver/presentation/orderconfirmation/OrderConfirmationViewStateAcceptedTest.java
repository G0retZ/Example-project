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
public class OrderConfirmationViewStateAcceptedTest {

  @Mock
  private OrderConfirmationViewActions viewActions;

  @Test
  public void testActionsWithMessage() {
    // Действие:
    InOrder inOrder = Mockito.inOrder(viewActions);
    new OrderConfirmationViewStateAccepted("message").apply(viewActions);

    // Результат:
    inOrder.verify(viewActions).showDriverOrderConfirmationPending(true);
    inOrder.verify(viewActions).enableAcceptButton(false);
    inOrder.verify(viewActions).enableDeclineButton(false);
    inOrder.verify(viewActions).showDeclinedMessage(null);
    inOrder.verify(viewActions).showFailedMessage(null);
    inOrder.verify(viewActions).showAcceptedMessage("message");
    verifyNoMoreInteractions(viewActions);
  }

  @Test
  public void testEquals() {
    OrderConfirmationViewStateAccepted viewState = new OrderConfirmationViewStateAccepted(
        "message");
    assertEquals(viewState, viewState);
    assertEquals(viewState, new OrderConfirmationViewStateAccepted("message"));
    assertNotEquals(viewState, new OrderConfirmationViewStateAccepted(""));
    assertNotEquals(viewState, "");
    assertNotEquals(viewState, null);
  }

  @Test
  public void testHashCode() {
    OrderConfirmationViewStateAccepted viewState = new OrderConfirmationViewStateAccepted(
        "message");
    assertEquals(viewState.hashCode(), "message".hashCode());
  }
}