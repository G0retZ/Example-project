package com.fasten.executor_driver.presentation.code;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.fasten.executor_driver.R;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

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
    verify(codeViewActions).enableInputField(false);
    verify(codeViewActions).showCodeCheckPending(true);
    verify(codeViewActions).showCodeCheckError(false);
    verify(codeViewActions).showCodeCheckNetworkErrorMessage(false);
    verify(codeViewActions).setUnderlineImage(R.drawable.ic_code_input_default);
    verifyNoMoreInteractions(codeViewActions);
  }
}
