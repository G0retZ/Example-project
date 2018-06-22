package com.fasten.executor_driver.presentation.geolocation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;

import com.fasten.executor_driver.presentation.ViewState;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class GeoLocationViewStateServerDataErrorTest {


  private ViewState<GeoLocationViewActions> viewState;

  @Mock
  private GeoLocationViewActions viewActions;
  @Mock
  private ViewState<GeoLocationViewActions> parentViewState;
  @Mock
  private ViewState<GeoLocationViewActions> parentViewState1;

  @Test
  public void testActions() {
    // Дано:
    viewState = new GeoLocationViewStateServerDataError(parentViewState);

    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions, only()).showGeoLocationServerDataError();
    verify(parentViewState, only()).apply(viewActions);
  }

  @Test
  public void testActionsWithNull() {
    // Дано:
    viewState = new GeoLocationViewStateServerDataError(null);

    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions, only()).showGeoLocationServerDataError();
  }

  @Test
  public void testEquals() {
    viewState = new GeoLocationViewStateServerDataError(parentViewState);
    assertEquals(viewState, new GeoLocationViewStateServerDataError(parentViewState));
    assertNotEquals(viewState, new GeoLocationViewStateServerDataError(parentViewState1));
    assertNotEquals(viewState, new GeoLocationViewStateServerDataError(null));
  }
}