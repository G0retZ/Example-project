package com.cargopull.executor_driver.presentation.orderconfirmation;

import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OrderConfirmationViewStateExpiredTest {

  @Mock
  private OrderConfirmationViewActions viewActions;

  @Test
  public void testActions() {
    // Действие:
    InOrder inOrder = Mockito.inOrder(viewActions);
    new OrderConfirmationViewStateExpired().apply(viewActions);

    // Результат:
    inOrder.verify(viewActions).showDriverOrderConfirmationPending(false);
    inOrder.verify(viewActions).enableAcceptButton(false);
    inOrder.verify(viewActions).enableDeclineButton(false);
    inOrder.verify(viewActions).showAcceptedMessage(null);
    inOrder.verify(viewActions).showDeclinedMessage(null);
    inOrder.verify(viewActions).showFailedMessage(null);
    verifyNoMoreInteractions(viewActions);
  }
}