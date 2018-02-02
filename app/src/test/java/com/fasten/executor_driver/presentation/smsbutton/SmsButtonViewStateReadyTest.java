package com.fasten.executor_driver.presentation.smsbutton;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SmsButtonViewStateReadyTest {

  private SmsButtonViewStateReady viewState;

  @Mock
  private SmsButtonViewActions smsButtonViewActions;

  @Before
  public void setUp() throws Exception {
    viewState = new SmsButtonViewStateReady();
  }

  @Test
  public void testActions() throws Exception {
    // Действие:
    viewState.apply(smsButtonViewActions);

    // Результат:
    verify(smsButtonViewActions).showSmsButtonTimer(null);
    verify(smsButtonViewActions).setSmsButtonResponsive(true);
    verify(smsButtonViewActions).showSmsSendError(null);
    verify(smsButtonViewActions).showSmsSendPending(false);
    verifyNoMoreInteractions(smsButtonViewActions);
  }
}
