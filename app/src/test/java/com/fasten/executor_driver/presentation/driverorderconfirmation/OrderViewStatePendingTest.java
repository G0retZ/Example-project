package com.fasten.executor_driver.presentation.driverorderconfirmation;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OrderViewStatePendingTest {

  private DriverOrderConfirmationViewStatePending viewState;

  @Mock
  private DriverOrderConfirmationViewActions driverOrderConfirmationViewActions;

  @Mock
  private OrderItem orderItem;

  @Before
  public void setUp() {
    when(orderItem.getAddress()).thenReturn("address");
    viewState = new DriverOrderConfirmationViewStatePending(orderItem);
  }

  @Test
  public void testActions() {
    // Дано:
    when(orderItem.getAddress()).thenReturn("address");
    when(orderItem.getDistance()).thenReturn("123L");
    when(orderItem.getLoadPointMapUrl()).thenReturn("url");
    when(orderItem.getOrderComment()).thenReturn("comm");
    when(orderItem.getEstimatedPrice()).thenReturn("1000");
    when(orderItem.getOrderOptionsRequired()).thenReturn("1,2,3");
    when(orderItem.getProgressLeft()).thenReturn(new long[]{123, 4532});

    // Действие:
    viewState.apply(driverOrderConfirmationViewActions);

    // Результат:
    verify(driverOrderConfirmationViewActions).showLoadPoint("url");
    verify(driverOrderConfirmationViewActions).showDistance("123L");
    verify(driverOrderConfirmationViewActions).showLoadPointAddress("address");
    verify(driverOrderConfirmationViewActions).showEstimatedPrice("1000");
    verify(driverOrderConfirmationViewActions).showOrderOptionsRequirements("1,2,3");
    verify(driverOrderConfirmationViewActions).showComment("comm");
    verify(driverOrderConfirmationViewActions).showTimeout(123, 4532);
    verify(driverOrderConfirmationViewActions).showDriverOrderConfirmationPending(true);
    verify(driverOrderConfirmationViewActions).enableAcceptButton(false);
    verify(driverOrderConfirmationViewActions).enableDeclineButton(false);
    verify(driverOrderConfirmationViewActions).showOrderAvailabilityError(false);
    verify(driverOrderConfirmationViewActions).showNetworkErrorMessage(false);
    verifyNoMoreInteractions(driverOrderConfirmationViewActions);
  }

  @Test
  public void testNoActions() {
    // Дано:
    viewState = new DriverOrderConfirmationViewStatePending(null);

    // Действие:
    viewState.apply(driverOrderConfirmationViewActions);

    // Результат:
    verify(driverOrderConfirmationViewActions).showDriverOrderConfirmationPending(true);
    verify(driverOrderConfirmationViewActions).enableAcceptButton(false);
    verify(driverOrderConfirmationViewActions).enableDeclineButton(false);
    verify(driverOrderConfirmationViewActions).showOrderAvailabilityError(false);
    verify(driverOrderConfirmationViewActions).showNetworkErrorMessage(false);
    verifyNoMoreInteractions(driverOrderConfirmationViewActions);
  }
}