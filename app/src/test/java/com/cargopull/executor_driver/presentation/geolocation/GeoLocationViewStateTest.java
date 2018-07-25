package com.cargopull.executor_driver.presentation.geolocation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.cargopull.executor_driver.entity.GeoLocation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class GeoLocationViewStateTest {

  private GeoLocationViewState viewState;

  @Mock
  private GeoLocationViewActions viewActions;

  @Before
  public void setUp() {
    viewState = new GeoLocationViewState(new GeoLocation(1, 2, 3));
  }

  @Test
  public void testActions() {
    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).updateLocation(new GeoLocation(1, 2, 3));
    verifyNoMoreInteractions(viewActions);
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