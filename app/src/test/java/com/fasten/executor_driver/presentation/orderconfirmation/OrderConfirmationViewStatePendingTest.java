package com.fasten.executor_driver.presentation.orderconfirmation;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OrderConfirmationViewStatePendingTest {

  @Mock
  private OrderConfirmationViewActions orderConfirmationViewActions;

  @Test
  public void testActions() {
    // Действие:
    new OrderConfirmationViewStatePending().apply(orderConfirmationViewActions);

    // Результат:
    verify(orderConfirmationViewActions).showDriverOrderConfirmationPending(true);
    verify(orderConfirmationViewActions).enableAcceptButton(false);
    verify(orderConfirmationViewActions).enableDeclineButton(false);
    verify(orderConfirmationViewActions).showOrderAvailabilityError(false);
    verify(orderConfirmationViewActions).showNetworkErrorMessage(false);
    verifyNoMoreInteractions(orderConfirmationViewActions);
  }
}