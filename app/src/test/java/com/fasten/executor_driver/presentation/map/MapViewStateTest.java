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
  private MapViewActions viewActions;

  @Before
  public void setUp() {
    viewState = new MapViewState("geoData");
  }

  @Test
  public void testActions() {
    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).updateHeatMap("geoData");
    verifyNoMoreInteractions(viewActions);
  }

  @Test
  public void testEquals() {
    assertEquals(viewState, new MapViewState("geoData"));
    assertNotEquals(viewState, new MapViewState(null));
    assertNotEquals(viewState, new MapViewState("geoDat"));
  }
}