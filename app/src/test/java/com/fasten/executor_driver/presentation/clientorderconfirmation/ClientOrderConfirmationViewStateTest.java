package com.fasten.executor_driver.presentation.clientorderconfirmation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ClientOrderConfirmationViewStateTest {

  private ClientOrderConfirmationViewState viewState;

  @Mock
  private ClientOrderConfirmationViewActions clientOrderConfirmationViewActions;

  @Mock
  private OrderItem orderItem;
  @Mock
  private OrderItem orderItem2;

  @Before
  public void setUp() {
    when(orderItem.getAddress()).thenReturn("address");
    viewState = new ClientOrderConfirmationViewState(orderItem);
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
    verifyNoMoreInteractions(clientOrderConfirmationViewActions);
  }

  @Test
  public void testNoActions() {
    // Дано:
    viewState = new ClientOrderConfirmationViewState(null);

    // Действие:
    viewState.apply(clientOrderConfirmationViewActions);

    // Результат:
    verifyZeroInteractions(clientOrderConfirmationViewActions);
  }

  @Test
  public void testEquals() {
    assertEquals(viewState, new ClientOrderConfirmationViewState(orderItem));
    assertNotEquals(viewState, new ClientOrderConfirmationViewState(orderItem2));
    assertNotEquals(viewState, new ClientOrderConfirmationViewState(null));
  }
}