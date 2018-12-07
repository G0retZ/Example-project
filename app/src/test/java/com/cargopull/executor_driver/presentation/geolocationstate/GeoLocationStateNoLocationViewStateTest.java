package com.cargopull.executor_driver.presentation.geolocationstate;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.presentation.ImageTextViewActions;
import com.cargopull.executor_driver.presentation.ViewState;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class GeoLocationStateNoLocationViewStateTest {

  @Mock
  private ImageTextViewActions viewActions;

  @Test
  public void testActions() {
    // Дано:
    ViewState<ImageTextViewActions> viewState = new GeoLocationStateNoLocationViewState();

    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).setVisible(-1, true);
    verify(viewActions).setImage(R.id.geoIcon, R.drawable.ic_geolocation_icon);
    verify(viewActions).setText(R.id.geoTitleText, R.string.turn_on_geolocation);
    verify(viewActions).setText(R.id.explainText, R.string.geolocation_required);
    verifyNoMoreInteractions(viewActions);
  }
}