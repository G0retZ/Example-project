package com.fasten.executor_driver.presentation.geolocation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.fasten.executor_driver.entity.GeoLocation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class GeoLocationViewStateTest {

  private GeoLocationViewState viewState;

  @Mock
  private GeoLocationViewActions mapViewActions;

  @Before
  public void setUp() {
    viewState = new GeoLocationViewState(new GeoLocation(1, 2, 3));
  }

  @Test
  public void testActions() {
    // Действие:
    viewState.apply(mapViewActions);

    // Результат:
    verify(mapViewActions).updateLocation(new GeoLocation(1, 2, 3));
    verifyNoMoreInteractions(mapViewActions);
  }

  @Test
  public void testEquals() {
    assertEquals(viewState, new GeoLocationViewState(
        new GeoLocation(1, 2, 3))
    );
    assertNotEquals(viewState, new GeoLocationViewState(
        new GeoLocation(1, 2, 5))
    );
  }
}