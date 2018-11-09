package com.cargopull.executor_driver.presentation.geolocationstate;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.cargopull.executor_driver.presentation.ViewActions;
import com.cargopull.executor_driver.presentation.ViewState;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class GeoLocationStateReadyViewStateTest {

  @Mock
  private ViewActions viewActions;

  @Test
  public void testActions() {
    // Дано:
    ViewState<ViewActions> viewState = new GeoLocationStateReadyViewState();

    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).setVisible(-1, false);
    verifyNoMoreInteractions(viewActions);
  }
}