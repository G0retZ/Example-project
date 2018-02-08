package com.fasten.executor_driver.presentation.map;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MapViewStateTest {

  private MapViewState viewState;

  @Mock
  private MapViewActions mapViewActions;

  @Before
  public void setUp() throws Exception {
    viewState = new MapViewState("geoData");
  }

  @Test
  public void testActions() throws Exception {
    // Действие:
    viewState.apply(mapViewActions);

    // Результат:
    verify(mapViewActions).updateHeatMap("geoData");
    verifyNoMoreInteractions(mapViewActions);
  }

  @Test
  public void testEquals() throws Exception {
    assertEquals(viewState, new MapViewState("geoData"));
    assertNotEquals(viewState, new MapViewState(null));
    assertNotEquals(viewState, new MapViewState("geoDat"));
  }
}