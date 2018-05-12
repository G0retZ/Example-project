package com.fasten.executor_driver.presentation.clientorderconfirmation;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ClientOrderConfirmationViewStateUnavailableErrorTest {

  private ClientOrderConfirmationViewStateUnavailableError viewState;

  @Mock
  private ClientOrderConfirmationViewActions clientOrderConfirmationViewActions;

  @Mock
  private OrderItem orderItem;

  @Before
  public void setUp() {
    when(orderItem.getAddress()).thenReturn("address");
    viewState = new ClientOrderConfirmationViewStateUnavailableError(orderItem);
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

    // Действие:
    viewState.apply(clientOrderConfirmationViewActions);

    // Результат:
    verify(clientOrderConfirmationViewActions).showLoadPoint("url");
    verify(clientOrderConfirmationViewActions).showDistance("123L");
    verify(clientOrderConfirmationViewActions).showLoadPointAddress("address");
    verify(clientOrderConfirmationViewActions).showEstimatedPrice("1000");
    verify(clientOrderConfirmationViewActions).showOptionsRequirements("1,2,3");
    verify(clientOrderConfirmationViewActions).showComment("comm");
    verify(clientOrderConfirmationViewActions).showClientOrderConfirmationPending(false);
    verify(clientOrderConfirmationViewActions).enableDeclineButton(false);
    verify(clientOrderConfirmationViewActions).showOrderAvailabilityError(true);
    verify(clientOrderConfirmationViewActions).showNetworkErrorMessage(false);
    verifyNoMoreInteractions(clientOrderConfirmationViewActions);
  }

  @Test
  public void testNoActions() {
    // Дано:
    viewState = new ClientOrderConfirmationViewStateUnavailableError(null);

    // Действие:
    viewState.apply(clientOrderConfirmationViewActions);

    // Результат:
    verify(clientOrderConfirmationViewActions).showClientOrderConfirmationPending(false);
    verify(clientOrderConfirmationViewActions).enableDeclineButton(false);
    verify(clientOrderConfirmationViewActions).showOrderAvailabilityError(true);
    verify(clientOrderConfirmationViewActions).showNetworkErrorMessage(false);
    verifyNoMoreInteractions(clientOrderConfirmationViewActions);
  }
}