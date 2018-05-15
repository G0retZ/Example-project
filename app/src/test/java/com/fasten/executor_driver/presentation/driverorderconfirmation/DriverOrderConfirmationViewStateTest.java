package com.fasten.executor_driver.presentation.driverorderconfirmation;

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
public class DriverOrderConfirmationViewStateTest {

  private DriverOrderConfirmationViewState viewState;

  @Mock
  private DriverOrderConfirmationViewActions driverOrderConfirmationViewActions;

  @Mock
  private OrderItem orderItem;
  @Mock
  private OrderItem orderItem2;

  @Before
  public void setUp() {
    when(orderItem.getAddress()).thenReturn("address");
    viewState = new DriverOrderConfirmationViewState(orderItem);
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
    verifyNoMoreInteractions(driverOrderConfirmationViewActions);
  }

  @Test
  public void testNoActions() {
    // Дано:
    viewState = new DriverOrderConfirmationViewState(null);

    // Действие:
    viewState.apply(driverOrderConfirmationViewActions);

    // Результат:
    verifyZeroInteractions(driverOrderConfirmationViewActions);
  }

  @Test
  public void testEquals() {
    assertEquals(viewState, new DriverOrderConfirmationViewState(orderItem));
    assertNotEquals(viewState, new DriverOrderConfirmationViewState(orderItem2));
    assertNotEquals(viewState, new DriverOrderConfirmationViewState(null));
  }
}