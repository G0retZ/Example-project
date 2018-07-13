package com.cargopull.executor_driver.presentation.code;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.cargopull.executor_driver.R;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CodeViewStateInitialTest {

  private CodeViewStateInitial viewState;

  @Mock
  private CodeViewActions viewActions;

  @Before
  public void setUp() {
    viewState = new CodeViewStateInitial();
  }

  @Test
  public void testActions() {
    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).enableInputField(true);
    verify(viewActions).showCodeCheckPending(false);
    verify(viewActions).showCodeCheckError(false);
    verify(viewActions).showCodeCheckNetworkErrorMessage(false);
    verify(viewActions).setUnderlineImage(R.drawable.ic_code_input_activated);
    verifyNoMoreInteractions(viewActions);
  }
}
