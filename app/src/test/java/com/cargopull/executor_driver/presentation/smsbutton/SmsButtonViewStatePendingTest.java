package com.cargopull.executor_driver.presentation.smsbutton;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.presentation.FragmentViewActions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SmsButtonViewStatePendingTest {

  private SmsButtonViewStatePending viewState;

  @Mock
  private FragmentViewActions viewActions;

  @Before
  public void setUp() {
    viewState = new SmsButtonViewStatePending();
  }

  @Test
  public void testActions() {
    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).setEnabled(R.id.sendSms, false);
    verify(viewActions).setText(R.id.sendSms, R.string.repeat_code_from_sms);
    verify(viewActions).blockWithPending("sms");
    verifyNoMoreInteractions(viewActions);
  }
}
