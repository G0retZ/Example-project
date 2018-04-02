package com.fasten.executor_driver.presentation.smsbutton;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.fasten.executor_driver.R;
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
  public void setUp() {
    viewState = new SmsButtonViewStatePending();
  }

  @Test
  public void testActions() {
    // Действие:
    viewState.apply(smsButtonViewActions);

    // Результат:
    verify(smsButtonViewActions).setSmsButtonText(R.string.repeat_code_from_sms, null);
    verify(smsButtonViewActions).enableSmsButton(false);
    verify(smsButtonViewActions).showSmsSendNetworkErrorMessage(false);
    verify(smsButtonViewActions).showSmsSendPending(true);
    verifyNoMoreInteractions(smsButtonViewActions);
  }
}
