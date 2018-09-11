package com.cargopull.executor_driver.presentation.orderconfirmation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OrderConfirmationViewStateResultTest {

  @Mock
  private OrderConfirmationViewActions viewActions;

  @Test
  public void testActionsWithMessage() {
    // Действие:
    new OrderConfirmationViewStateResult("message").apply(viewActions);

    // Результат:
    verify(viewActions).showDriverOrderConfirmationPending(false);
    verify(viewActions).enableAcceptButton(false);
    verify(viewActions).enableDeclineButton(false);
    verify(viewActions).showBlockingMessage("message");
    verifyNoMoreInteractions(viewActions);
  }

  @Test
  public void testEquals() {
    OrderConfirmationViewStateResult viewState = new OrderConfirmationViewStateResult("message");
    assertEquals(viewState, viewState);
    assertEquals(viewState, new OrderConfirmationViewStateResult("message"));
    assertNotEquals(viewState, new OrderConfirmationViewStateResult(""));
    assertNotEquals(viewState, new OrderConfirmationViewStateIdle());
    assertNotEquals(viewState, null);
  }

  @Test
  public void testHashCode() {
    OrderConfirmationViewStateResult viewState = new OrderConfirmationViewStateResult("message");
    assertEquals(viewState.hashCode(), "message".hashCode());
  }
}