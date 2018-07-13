package com.cargopull.executor_driver.presentation.orderconfirmation;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OrderConfirmationViewStatePendingTest {

  @Mock
  private OrderConfirmationViewActions viewActions;

  @Test
  public void testActions() {
    // Действие:
    new OrderConfirmationViewStatePending().apply(viewActions);

    // Результат:
    verify(viewActions).showDriverOrderConfirmationPending(true);
    verify(viewActions).enableAcceptButton(false);
    verify(viewActions).enableDeclineButton(false);
    verifyNoMoreInteractions(viewActions);
  }
}