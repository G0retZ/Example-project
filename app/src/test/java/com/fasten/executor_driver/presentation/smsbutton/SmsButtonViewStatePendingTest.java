package com.fasten.executor_driver.presentation.smsbutton;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SmsButtonViewStatePendingTest {

  private SmsButtonViewStatePending viewState;

  @Mock
  private SmsButtonViewActions smsButtonViewActions;

  @Before
  public void setUp() throws Exception {
    viewState = new SmsButtonViewStatePending();
  }

  @Test
  public void testActions() throws Exception {
    // Действие:
    viewState.apply(smsButtonViewActions);

    // Результат:
    verify(smsButtonViewActions).showSmsButtonTimer(null);
    verify(smsButtonViewActions).setSmsButtonResponsive(false);
    verify(smsButtonViewActions).showSmsSendError(null);
    verify(smsButtonViewActions).showSmsSendPending(true);
    verifyNoMoreInteractions(smsButtonViewActions);
  }
}
