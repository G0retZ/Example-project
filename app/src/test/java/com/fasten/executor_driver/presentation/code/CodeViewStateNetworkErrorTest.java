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
public class CodeViewStateNetworkErrorTest {


  private CodeViewStateNetworkError viewState;

  @Mock
  private CodeViewActions codeViewActions;

  @Before
  public void setUp() throws Exception {
    viewState = new CodeViewStateNetworkError();
  }

  @Test
  public void testActions() throws Exception {
    // Действие:
    viewState.apply(codeViewActions);

    // Результат:
    verify(codeViewActions).enableInputField(false);
    verify(codeViewActions).showCodeCheckPending(false);
    verify(codeViewActions).showCodeCheckError(false);
    verify(codeViewActions).showCodeCheckNetworkErrorMessage(true);
    verify(codeViewActions).showDescriptiveHeader(false);
    verify(codeViewActions).showInputField(false);
    verify(codeViewActions).showUnderlineImage(false);
    verify(codeViewActions).setUnderlineImage(R.drawable.ic_code_input_default);
    verifyNoMoreInteractions(codeViewActions);
  }
}