package com.cargopull.executor_driver.presentation.order;

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
  private OrderViewActions viewActions;

  @Mock
  private OrderItem orderItem;
  @Mock
  private OrderItem orderItem2;

  @Before
  public void setUp() {
    viewState = new OrderViewStateIdle(orderItem);
  }

  @Test
  public void testActions() {
    // Дано:
    when(orderItem.getLoadPointMapUrl()).thenReturn("url");
    when(orderItem.getCoordinatesString()).thenReturn("1.3,2.4");
    when(orderItem.getDistance()).thenReturn("123L");
    when(orderItem.getEtaSeconds()).thenReturn(321);
    when(orderItem.getNextAddress()).thenReturn("address");
    when(orderItem.getNextAddressComment()).thenReturn("a comment");
    when(orderItem.getRoutePointsCount()).thenReturn(7);
    when(orderItem.getLastAddress()).thenReturn("add");
    when(orderItem.getRouteLength()).thenReturn("la-la-la");
    when(orderItem.getEstimatedTimeSeconds()).thenReturn(7929);
    when(orderItem.getEstimatedPrice()).thenReturn(6812L);
    when(orderItem.getOccupationTime()).thenReturn("12:34");
    when(orderItem.getOccupationDate()).thenReturn("34-12");
    when(orderItem.getServiceName()).thenReturn("service");
    when(orderItem.getSecondsToMeetClient()).thenReturn(654321);
    when(orderItem.getOrderComment()).thenReturn("comm");
    when(orderItem.getEstimatedPriceText()).thenReturn("1000");
    when(orderItem.getOrderOptionsRequired()).thenReturn("1,2,3");
    when(orderItem.getProgressLeft()).thenReturn(new long[]{3, 5});

    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).showLoadPoint("url");
    verify(viewActions).showFirstPointDistance("123L");
    verify(viewActions).showFirstPointEta(321);
    verify(viewActions).showNextPointAddress("1.3,2.4", "address");
    verify(viewActions).showNextPointComment("a comment");
    verify(viewActions).showRoutePointsCount(7);
    verify(viewActions).showLastPointAddress("add");
    verify(viewActions).showOrderConditions("la-la-la", 7929, 6812);
    verify(viewActions).showOrderOccupationTime("12:34");
    verify(viewActions).showOrderOccupationDate("34-12");
    verify(viewActions).showServiceName("service");
    verify(viewActions).showTimeout(654321);
    verify(viewActions).showComment("comm");
    verify(viewActions).showTimeout(3, 5);
    verify(viewActions).showEstimatedPrice("1000");
    verify(viewActions).showOrderOptionsRequirements("1,2,3");
    verify(viewActions).showOrderPending(false);
    verify(viewActions).showOrderExpiredMessage(null);
    verifyNoMoreInteractions(viewActions);
  }

  @Test
  public void testEquals() {
    assertEquals(viewState, new OrderViewStateIdle(orderItem));
    assertNotEquals(viewState, new OrderViewStateIdle(orderItem2));
  }
}