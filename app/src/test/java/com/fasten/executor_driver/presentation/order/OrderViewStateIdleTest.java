package com.fasten.executor_driver.presentation.order;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OrderViewStateIdleTest {

  private OrderViewStateIdle viewState;

  @Mock
  private OrderViewActions orderViewActions;

  @Mock
  private OrderItem orderItem;
  @Mock
  private OrderItem orderItem2;

  @Before
  public void setUp() {
    when(orderItem.getAddress()).thenReturn("address");
    viewState = new OrderViewStateIdle(orderItem);
  }

  @Test
  public void testActions() {
    // Дано:
    when(orderItem.getLoadPointMapUrl()).thenReturn("url");
    when(orderItem.getCoordinatesString()).thenReturn("1.3,2.4");
    when(orderItem.getDistance()).thenReturn("123L");
    when(orderItem.getAddress()).thenReturn("address");
    when(orderItem.getOrderComment()).thenReturn("comm");
    when(orderItem.getSecondsToMeetClient()).thenReturn(654321);
    when(orderItem.getEstimatedPrice()).thenReturn("1000");
    when(orderItem.getOrderOptionsRequired()).thenReturn("1,2,3");
    when(orderItem.getProgressLeft()).thenReturn(new long[]{3, 5});

    // Действие:
    viewState.apply(orderViewActions);

    // Результат:
    verify(orderViewActions).showLoadPoint("url");
    verify(orderViewActions).showLoadPointCoordinates("1.3,2.4");
    verify(orderViewActions).showDistance("123L");
    verify(orderViewActions).showLoadPointAddress("address");
    verify(orderViewActions).showComment("comm");
    verify(orderViewActions).showTimeout(654321);
    verify(orderViewActions).showTimeout(3, 5);
    verify(orderViewActions).showEstimatedPrice("1000");
    verify(orderViewActions).showOrderOptionsRequirements("1,2,3");
    verify(orderViewActions).showOrderPending(false);
    verify(orderViewActions).showOrderAvailabilityError(false);
    verify(orderViewActions).showNetworkErrorMessage(false);
    verifyNoMoreInteractions(orderViewActions);
  }

  @Test
  public void testEquals() {
    assertEquals(viewState, new OrderViewStateIdle(orderItem));
    assertNotEquals(viewState, new OrderViewStateIdle(orderItem2));
  }
}