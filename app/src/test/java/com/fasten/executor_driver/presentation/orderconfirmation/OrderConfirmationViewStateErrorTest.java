package com.fasten.executor_driver.presentation.orderconfirmation;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OrderConfirmationViewStateErrorTest {

  @Mock
  private OrderConfirmationViewActions orderConfirmationViewActions;

  @Test
  public void testActions() {
    // Действие:
    new OrderConfirmationViewStateError().apply(orderConfirmationViewActions);

    // Результат:
    verify(orderConfirmationViewActions).showDriverOrderConfirmationPending(false);
    verify(orderConfirmationViewActions).enableAcceptButton(false);
    verify(orderConfirmationViewActions).enableDeclineButton(false);
    verify(orderConfirmationViewActions).showOrderAvailabilityError(false);
    verify(orderConfirmationViewActions).showNetworkErrorMessage(true);
    verifyNoMoreInteractions(orderConfirmationViewActions);
  }
}