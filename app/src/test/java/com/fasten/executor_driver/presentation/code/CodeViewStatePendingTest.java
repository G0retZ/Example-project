package com.fasten.executor_driver.presentation.code;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(MockitoJUnitRunner.class)
public class CodeViewStatePendingTest {

  private CodeViewStatePending viewState;

  @Mock
  private CodeViewActions codeViewActions;

  @Before
  public void setUp() throws Exception {
    viewState = new CodeViewStatePending();
  }

  @Test
  public void testActions() throws Exception {
    // Действие:
    viewState.apply(codeViewActions);

    // Результат:
    verify(codeViewActions).showPending(true);
    verify(codeViewActions).showError(null);
    verifyNoMoreInteractions(codeViewActions);
  }
}
