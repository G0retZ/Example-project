package com.fasten.executor_driver.presentation.smsbutton;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(MockitoJUnitRunner.class)
public class SmsButtonViewStateHoldTest {

  private SmsButtonViewStateHold viewState;

  @Mock
  private SmsButtonViewActions smsButtonViewActions;

  @Before
  public void setUp() throws Exception {
    viewState = new SmsButtonViewStateHold(12);
  }

  @Test
  public void testActions() throws Exception {
    // Действие:
    viewState.apply(smsButtonViewActions);

    // Результат:
    verify(smsButtonViewActions).showSmsButtonTimer(12L);
    verify(smsButtonViewActions).setSmsButtonResponsive(false);
    verifyNoMoreInteractions(smsButtonViewActions);
  }

  @Test
  public void testEquals() throws Exception {
    assertEquals(viewState, new SmsButtonViewStateHold(12));
    assertNotEquals(viewState, new SmsButtonViewStateHold(13));
    assertNotEquals(viewState, new SmsButtonViewStateHold(11));
  }
}
