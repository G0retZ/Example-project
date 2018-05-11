package com.fasten.executor_driver.presentation.orderconfirmation;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OrderConfirmationViewStateNetworkErrorTest {

  private OrderConfirmationViewStateNetworkError viewState;

  @Mock
  private OrderConfirmationViewActions orderConfirmationViewActions;

  @Mock
  private OrderConfirmationItem orderConfirmationItem;

  @Before
  public void setUp() {
    when(orderConfirmationItem.getAddress()).thenReturn("address");
    viewState = new OrderConfirmationViewStateNetworkError(orderConfirmationItem);
  }

  @Test
  public void testActions() {
    // Дано:
    when(orderConfirmationItem.getAddress()).thenReturn("address");
    when(orderConfirmationItem.getDistance()).thenReturn("123L");
    when(orderConfirmationItem.getLoadPointMapUrl()).thenReturn("url");
    when(orderConfirmationItem.getOfferComment()).thenReturn("comm");
    when(orderConfirmationItem.getEstimatedPrice()).thenReturn("1000");
    when(orderConfirmationItem.getOrderOptionsRequired()).thenReturn("1,2,3");

    // Действие:
    viewState.apply(orderConfirmationViewActions);

    // Результат:
    verify(orderConfirmationViewActions).showLoadPoint("url");
    verify(orderConfirmationViewActions).showDistance("123L");
    verify(orderConfirmationViewActions).showLoadPointAddress("address");
    verify(orderConfirmationViewActions).showEstimatedPrice("1000");
    verify(orderConfirmationViewActions).showOrderOptionsRequirements("1,2,3");
    verify(orderConfirmationViewActions).showOrderConfirmationComment("comm");
    verify(orderConfirmationViewActions).showOrderConfirmationPending(false);
    verify(orderConfirmationViewActions).enableDeclineButton(false);
    verify(orderConfirmationViewActions).showOrderConfirmationAvailabilityError(false);
    verify(orderConfirmationViewActions).showOrderConfirmationNetworkErrorMessage(true);
    verifyNoMoreInteractions(orderConfirmationViewActions);
  }

  @Test
  public void testNoActions() {
    // Дано:
    viewState = new OrderConfirmationViewStateNetworkError(null);

    // Действие:
    viewState.apply(orderConfirmationViewActions);

    // Результат:
    verify(orderConfirmationViewActions).showOrderConfirmationPending(false);
    verify(orderConfirmationViewActions).enableDeclineButton(false);
    verify(orderConfirmationViewActions).showOrderConfirmationAvailabilityError(false);
    verify(orderConfirmationViewActions).showOrderConfirmationNetworkErrorMessage(true);
    verifyNoMoreInteractions(orderConfirmationViewActions);
  }
}