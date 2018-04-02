package com.fasten.executor_driver.presentation.onlinebutton;

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
public class OnlineButtonViewStateErrorTest {

  private OnlineButtonViewStateError viewState;

  @Mock
  private OnlineButtonViewActions codeViewActions;

  @Captor
  private ArgumentCaptor<Throwable> throwableCaptor;

  @Before
  public void setUp() {
    viewState = new OnlineButtonViewStateError(new IllegalArgumentException("mess"));
  }

  @Test
  public void testActions() {
    // Действие:
    viewState.apply(codeViewActions);

    // Результат:
    verify(codeViewActions).showGoOnlineError(throwableCaptor.capture());
    verify(codeViewActions).enableGoOnlineButton(false);
    verifyNoMoreInteractions(codeViewActions);
    assertTrue(
        ThrowableUtils.throwableEquals(
            throwableCaptor.getValue(),
            new IllegalArgumentException("mess")
        )
    );
  }

  @Test
  public void testEquals() {
    assertEquals(viewState, new OnlineButtonViewStateError(new IllegalArgumentException("mess")));
    assertNotEquals(viewState, new OnlineButtonViewStateError(new IllegalArgumentException("mes")));
    assertNotEquals(viewState, new OnlineButtonViewStateError(new NullPointerException("mess")));
  }
}