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
public class SmsButtonViewStateErrorTest {

  private SmsButtonViewStateError viewState;

  @Mock
  private SmsButtonViewActions codeViewActions;

  @Before
  public void setUp() throws Exception {
    viewState = new SmsButtonViewStateError();
  }

  @Test
  public void testActions() throws Exception {
    // Действие:
    viewState.apply(codeViewActions);

    // Результат:
    verify(codeViewActions).showSmsSendPending(false);
    verify(codeViewActions).setSmsSendNetworkErrorMessage(true);
    verify(codeViewActions).enableSmsButton(true);
    verify(codeViewActions).setSmsButtonText(R.string.repeat_code_from_sms, null);
    verifyNoMoreInteractions(codeViewActions);
  }
}
