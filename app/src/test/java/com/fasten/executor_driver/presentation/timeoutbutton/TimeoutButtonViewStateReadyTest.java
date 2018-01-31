package com.fasten.executor_driver.presentation.timeoutbutton;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(MockitoJUnitRunner.class)
public class TimeoutButtonViewStateReadyTest {

  private TimeoutButtonViewStateReady viewState;

  @Mock
  private TimeoutButtonViewActions timeoutButtonViewActions;

  @Before
  public void setUp() throws Exception {
    viewState = new TimeoutButtonViewStateReady();
  }

  @Test
  public void testActions() throws Exception {
    // Действие:
    viewState.apply(timeoutButtonViewActions);

    // Результат:
    verify(timeoutButtonViewActions).showTimer(null);
    verify(timeoutButtonViewActions).setResponsive(true);
    verifyNoMoreInteractions(timeoutButtonViewActions);
  }
}
