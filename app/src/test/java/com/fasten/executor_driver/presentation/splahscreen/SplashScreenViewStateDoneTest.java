package com.fasten.executor_driver.presentation.splahscreen;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SplashScreenViewStateDoneTest {

  private SplashScreenViewStateDone viewState;

  @Mock
  private SplashScreenViewActions codeViewActions;

  @Before
  public void setUp() throws Exception {
    viewState = new SplashScreenViewStateDone();
  }

  @Test
  public void testActions() throws Exception {
    // Действие:
    viewState.apply(codeViewActions);

    // Результат:
    verify(codeViewActions).showPending(false);
    verify(codeViewActions).showNetworkErrorMessage(false);
    verifyNoMoreInteractions(codeViewActions);
  }
}