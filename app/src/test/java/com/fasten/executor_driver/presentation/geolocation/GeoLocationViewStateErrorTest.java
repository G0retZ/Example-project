package com.fasten.executor_driver.presentation.geolocation;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class GeoLocationViewStateErrorTest {

  private GeoLocationViewStateError viewState;

  @Mock
  private GeoLocationViewActions mapViewActions;

  @Before
  public void setUp() throws Exception {
    viewState = new GeoLocationViewStateError();
  }

  @Test
  public void testActions() throws Exception {
    // Действие:
    viewState.apply(mapViewActions);

    // Результат:
    verify(mapViewActions).showGeoLocationError(true);
    verifyNoMoreInteractions(mapViewActions);
  }
}