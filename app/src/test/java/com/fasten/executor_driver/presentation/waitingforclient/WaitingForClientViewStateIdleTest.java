package com.fasten.executor_driver.presentation.waitingforclient;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class WaitingForClientViewStateIdleTest {

  private WaitingForClientViewStateIdle viewState;

  @Mock
  private WaitingForClientViewActions driverOrderConfirmationViewActions;

  @Mock
  private OrderItem orderItem;

  @Before
  public void setUp() {
    viewState = new WaitingForClientViewStateIdle(orderItem);
  }

  @Test
  public void testActions() {
    // Дано:
    when(orderItem.getOrderComment()).thenReturn("comm");
    when(orderItem.getEstimatedPrice()).thenReturn("1000");
    when(orderItem.getOrderOptionsRequired()).thenReturn("1,2,3");

    // Действие:
    viewState.apply(driverOrderConfirmationViewActions);

    // Результат:
    verify(driverOrderConfirmationViewActions).showEstimatedPrice("1000");
    verify(driverOrderConfirmationViewActions).showOrderOptionsRequirements("1,2,3");
    verify(driverOrderConfirmationViewActions).showComment("comm");
    verify(driverOrderConfirmationViewActions).showWaitingForClientPending(false);
    verify(driverOrderConfirmationViewActions).showOrderAvailabilityError(false);
    verify(driverOrderConfirmationViewActions).showNetworkErrorMessage(false);
    verifyNoMoreInteractions(driverOrderConfirmationViewActions);
  }

  @Test
  public void testNoActions() {
    // Дано:
    viewState = new WaitingForClientViewStateIdle(null);

    // Действие:
    viewState.apply(driverOrderConfirmationViewActions);

    // Результат:
    verify(driverOrderConfirmationViewActions).showWaitingForClientPending(false);
    verify(driverOrderConfirmationViewActions).showOrderAvailabilityError(false);
    verify(driverOrderConfirmationViewActions).showNetworkErrorMessage(false);
    verifyNoMoreInteractions(driverOrderConfirmationViewActions);
  }
}