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
public class OrderConfirmationViewStateFailedTest {

  @Mock
  private OrderConfirmationViewActions viewActions;

  @Test
  public void testActionsWithMessage() {
    // Действие:
    InOrder inOrder = Mockito.inOrder(viewActions);
    new OrderConfirmationViewStateFailed("message").apply(viewActions);

    // Результат:
    inOrder.verify(viewActions).showDriverOrderConfirmationPending(false);
    inOrder.verify(viewActions).enableAcceptButton(false);
    inOrder.verify(viewActions).enableDeclineButton(false);
    inOrder.verify(viewActions).showAcceptedMessage(null);
    inOrder.verify(viewActions).showDeclinedMessage(null);
    inOrder.verify(viewActions).showFailedMessage("message");
    verifyNoMoreInteractions(viewActions);
  }

  @Test
  public void testEquals() {
    OrderConfirmationViewStateFailed viewState = new OrderConfirmationViewStateFailed("message");
    assertEquals(viewState, viewState);
    assertEquals(viewState, new OrderConfirmationViewStateFailed("message"));
    assertNotEquals(viewState, new OrderConfirmationViewStateFailed(""));
    assertNotEquals(viewState, "");
    assertNotEquals(viewState, null);
  }

  @Test
  public void testHashCode() {
    OrderConfirmationViewStateFailed viewState = new OrderConfirmationViewStateFailed("message");
    assertEquals(viewState.hashCode(), "message".hashCode());
  }
}