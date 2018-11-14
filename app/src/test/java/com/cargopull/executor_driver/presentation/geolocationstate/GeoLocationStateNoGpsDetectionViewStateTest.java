package com.cargopull.executor_driver.presentation.geolocationstate;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.presentation.ViewActions;
import com.cargopull.executor_driver.presentation.ViewState;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class GeoLocationStateNoGpsDetectionViewStateTest {

  @Mock
  private ViewActions viewActions;

  @Test
  public void testActions() {
    // Дано:
    ViewState<ViewActions> viewState = new GeoLocationStateNoGpsDetectionViewState();

    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).setVisible(-1, true);
    verify(viewActions).setImage(R.id.geoIcon, R.drawable.ic_gps_detection_icon);
    verify(viewActions).setText(R.id.titleText, R.string.turn_on_geo_detection);
    verify(viewActions).setText(R.id.explainText, R.string.gps_detection_required);
    verifyNoMoreInteractions(viewActions);
  }
}