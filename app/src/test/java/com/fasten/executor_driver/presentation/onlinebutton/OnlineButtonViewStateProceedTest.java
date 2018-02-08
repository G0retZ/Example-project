package com.fasten.executor_driver.presentation.onlinebutton;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class OnlineButtonViewStateProceedTest {

  private OnlineButtonViewStateProceed viewState;

  @Mock
  private OnlineButtonViewActions phoneViewActions;

  @Before
  public void setUp() throws Exception {
    viewState = new OnlineButtonViewStateProceed();
  }

  @Test
  public void testActions() throws Exception {
    // Действие:
    viewState.apply(phoneViewActions);

    // Результат:
    verify(phoneViewActions).enableGoOnlineButton(true);
    verify(phoneViewActions).showGoOnlineError(null);
    verify(phoneViewActions).goChooseVehicle();
    verifyNoMoreInteractions(phoneViewActions);
  }
}