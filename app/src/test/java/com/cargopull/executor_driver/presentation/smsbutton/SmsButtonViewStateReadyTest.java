package com.cargopull.executor_driver.presentation.smsbutton;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.cargopull.executor_driver.R;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SmsButtonViewStateReadyTest {

  private SmsButtonViewStateReady viewState;

  @Mock
  private SmsButtonViewActions viewActions;

  @Before
  public void setUp() {
    viewState = new SmsButtonViewStateReady();
  }

  @Test
  public void testActions() {
    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).setSmsButtonText(R.string.repeat_code_from_sms, null);
    verify(viewActions).enableSmsButton(true);
    verify(viewActions).showSmsSendNetworkErrorMessage(false);
    verify(viewActions).showSmsSendPending(false);
    verifyNoMoreInteractions(viewActions);
  }
}
