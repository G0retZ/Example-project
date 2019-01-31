package com.cargopull.executor_driver.presentation.smsbutton;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
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
public class SmsButtonViewStateHoldTest {

  private SmsButtonViewStateHold viewState;

  @Mock
  private FragmentViewActions viewActions;

  @Before
  public void setUp() {
    viewState = new SmsButtonViewStateHold(12);
  }

  @Test
  public void testActions() {
    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).setEnabled(R.id.sendSms, false);
    verify(viewActions).setFormattedText(R.id.sendSms, R.string.repeat_code_from_sms_delayed, 12L);
    verify(viewActions).unblockWithPending("sms");
    verifyNoMoreInteractions(viewActions);
  }

  @Test
  public void testEquals() {
    assertEquals(viewState, new SmsButtonViewStateHold(12));
    assertNotEquals(viewState, new SmsButtonViewStateHold(13));
    assertNotEquals(viewState, new SmsButtonViewStateHold(11));
  }
}
