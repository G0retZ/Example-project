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
public class CodeViewStateErrorTest {

  private CodeViewStateError viewState;

  @Mock
  private CodeViewActions codeViewActions;

  @Before
  public void setUp() {
    viewState = new CodeViewStateError();
  }

  @Test
  public void testActions() {
    // Действие:
    viewState.apply(codeViewActions);

    // Результат:
    verify(codeViewActions).enableInputField(true);
    verify(codeViewActions).showCodeCheckPending(false);
    verify(codeViewActions).showCodeCheckError(true);
    verify(codeViewActions).showCodeCheckNetworkErrorMessage(false);
    verify(codeViewActions).setUnderlineImage(R.drawable.ic_code_input_error);
    verifyNoMoreInteractions(codeViewActions);
  }
}
