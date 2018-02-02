package com.fasten.executor_driver.presentation.code;

import com.fasten.executor_driver.utils.ThrowableUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(MockitoJUnitRunner.class)
public class CodeViewStateErrorTest {

  private CodeViewStateError viewState;

  @Mock
  private CodeViewActions codeViewActions;

  @Captor
  private ArgumentCaptor<Throwable> throwableCaptor;

  @Before
  public void setUp() throws Exception {
    viewState = new CodeViewStateError(new IllegalArgumentException("mess"));
  }

  @Test
  public void testActions() throws Exception {
    // Действие:
    viewState.apply(codeViewActions);

    // Результат:
    verify(codeViewActions).showCodeCheckPending(false);
    verify(codeViewActions).showCodeCheckError(throwableCaptor.capture());
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
    assertEquals(viewState, new CodeViewStateError(new IllegalArgumentException("mess")));
    assertNotEquals(viewState, new CodeViewStateError(new IllegalArgumentException("mes")));
    assertNotEquals(viewState, new CodeViewStateError(new NullPointerException("mess")));
  }
}
