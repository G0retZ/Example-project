package com.fasten.executor_driver.presentation.smsbutton;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.fasten.executor_driver.utils.ThrowableUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SmsButtonViewStateErrorTest {

  private SmsButtonViewStateError viewState;

  @Mock
  private SmsButtonViewActions codeViewActions;

  @Captor
  private ArgumentCaptor<Throwable> throwableCaptor;

  @Before
  public void setUp() throws Exception {
    viewState = new SmsButtonViewStateError(new IllegalArgumentException("mess"));
  }

  @Test
  public void testActions() throws Exception {
    // Действие:
    viewState.apply(codeViewActions);

    // Результат:
    verify(codeViewActions).showSmsSendPending(false);
    verify(codeViewActions).showSmsSendError(throwableCaptor.capture());
    verify(codeViewActions).setSmsButtonResponsive(false);
    verify(codeViewActions).showSmsButtonTimer(null);
    verifyNoMoreInteractions(codeViewActions);
    assertTrue(
        ThrowableUtils.throwableEquals(
            throwableCaptor.getValue(),
            new IllegalArgumentException("mess")
        )
    );
  }

  @Test
  public void testEquals() throws Exception {
    assertEquals(viewState, new SmsButtonViewStateError(new IllegalArgumentException("mess")));
    assertNotEquals(viewState, new SmsButtonViewStateError(new IllegalArgumentException("mes")));
    assertNotEquals(viewState, new SmsButtonViewStateError(new NullPointerException("mess")));
  }
}
