package com.cargopull.executor_driver.presentation.orderconfirmation;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OrderConfirmationViewStateIdleTest {

  @Mock
  private OrderConfirmationViewActions viewActions;

  @Test
  public void testActions() {
    // Действие:
    new OrderConfirmationViewStateIdle().apply(viewActions);

    // Результат:
    verify(viewActions).showDriverOrderConfirmationPending(false);
    verify(viewActions).enableAcceptButton(true);
    verify(viewActions).enableDeclineButton(true);
    verifyNoMoreInteractions(viewActions);
  }
}