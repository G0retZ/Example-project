package com.fasten.executor_driver.presentation.movingtoclient;

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
public class MovingToClientViewStateTest {

  private MovingToClientViewState viewState;

  @Mock
  private MovingToClientViewActions movingToClientViewActions;

  @Mock
  private RouteItem routeItem;
  @Mock
  private RouteItem routeItem2;

  @Before
  public void setUp() {
    when(routeItem.getAddress()).thenReturn("address");
    viewState = new MovingToClientViewState(routeItem);
  }

  @Test
  public void testActions() {
    // Дано:
    when(routeItem.getLoadPointMapUrl()).thenReturn("url");
    when(routeItem.getCoordinatesString()).thenReturn("coordinates");
    when(routeItem.getAddress()).thenReturn("address\ncomment");
    when(routeItem.getSecondsToMeetClient()).thenReturn(12345);

    // Действие:
    viewState.apply(movingToClientViewActions);

    // Результат:
    verify(movingToClientViewActions).showLoadPoint("url");
    verify(movingToClientViewActions).showLoadPointCoordinates("coordinates");
    verify(movingToClientViewActions).showLoadPointAddress("address\ncomment");
    verify(movingToClientViewActions).showTimeout(12345);
    verifyNoMoreInteractions(movingToClientViewActions);
  }

  @Test
  public void testNoActions() {
    // Дано:
    viewState = new MovingToClientViewState(null);

    // Действие:
    viewState.apply(movingToClientViewActions);

    // Результат:
    verifyZeroInteractions(movingToClientViewActions);
  }

  @Test
  public void testEquals() {
    assertEquals(viewState, new MovingToClientViewState(routeItem));
    assertNotEquals(viewState, new MovingToClientViewState(routeItem2));
    assertNotEquals(viewState, new MovingToClientViewState(null));
  }
}