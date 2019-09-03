package com.cargopull.executor_driver.presentation.orderconfirmation;

import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OrderConfirmationViewStatePendingTest {

  @Mock
  private OrderConfirmationViewActions viewActions;

  @Test
  public void testActions() {
    // Дано:
    InOrder inOrder = Mockito.inOrder(viewActions);

    // Действие:
    new OrderConfirmationViewStatePending().apply(viewActions);

    // Результат:
    inOrder.verify(viewActions).showDriverOrderConfirmationPending(true);
    inOrder.verify(viewActions).enableAcceptButton(false);
    inOrder.verify(viewActions).enableDeclineButton(false);
    inOrder.verify(viewActions).showAcceptedMessage(null);
    inOrder.verify(viewActions).showDeclinedMessage(null);
    inOrder.verify(viewActions).showFailedMessage(null);
    inOrder.verify(viewActions).showTimeout(-1);
    verifyNoMoreInteractions(viewActions);
  }
}