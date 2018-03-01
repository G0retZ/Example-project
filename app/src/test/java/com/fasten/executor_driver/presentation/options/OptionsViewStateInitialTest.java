package com.fasten.executor_driver.presentation.options;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.ArrayList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

// TODO: написать недостающие тесты.
@RunWith(MockitoJUnitRunner.class)
public class OptionsViewStateInitialTest {

  private OptionsViewStateInitial viewState;

  @Mock
  private OptionsViewActions codeViewActions;

  @Before
  public void setUp() throws Exception {
    viewState = new OptionsViewStateInitial();
  }

  @Test
  public void testActions() throws Exception {
    // Действие:
    viewState.apply(codeViewActions);

    // Результат:
    verify(codeViewActions).enableReadyButton(false);
    verify(codeViewActions).showVehicleOptionsList(true);
    verify(codeViewActions).showVehicleOptionsPending(false);
    verify(codeViewActions).showVehicleOptionsListErrorMessage(false);
    verify(codeViewActions).setVehicleOptionsListItems(
        new OptionsListItems(new ArrayList<>(), new ArrayList<>())
    );
    verifyNoMoreInteractions(codeViewActions);
  }
}