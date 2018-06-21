package com.fasten.executor_driver.presentation.orderconfirmation;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OrderConfirmationViewStateIdleTest {

  @Mock
  private OrderConfirmationViewActions orderConfirmationViewActions;

  @Test
  public void testActions() {
    // Действие:
    new OrderConfirmationViewStateIdle().apply(orderConfirmationViewActions);

    // Результат:
    verify(orderConfirmationViewActions).showDriverOrderConfirmationPending(false);
    verify(orderConfirmationViewActions).enableAcceptButton(true);
    verify(orderConfirmationViewActions).enableDeclineButton(true);
    verifyNoMoreInteractions(orderConfirmationViewActions);
  }
}