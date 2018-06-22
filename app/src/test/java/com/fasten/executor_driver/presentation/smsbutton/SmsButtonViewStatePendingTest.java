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
  private SmsButtonViewActions viewActions;

  @Before
  public void setUp() {
    viewState = new SmsButtonViewStatePending();
  }

  @Test
  public void testActions() {
    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).setSmsButtonText(R.string.repeat_code_from_sms, null);
    verify(viewActions).enableSmsButton(false);
    verify(viewActions).showSmsSendNetworkErrorMessage(false);
    verify(viewActions).showSmsSendPending(true);
    verifyNoMoreInteractions(viewActions);
  }
}
