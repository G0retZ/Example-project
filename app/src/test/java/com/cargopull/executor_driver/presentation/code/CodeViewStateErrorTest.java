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
public class CodeViewStateErrorTest {

  private CodeViewStateError viewState;

  @Mock
  private CodeViewActions viewActions;

  @Before
  public void setUp() {
    viewState = new CodeViewStateError();
  }

  @Test
  public void testActions() {
    // Action:
    viewState.apply(viewActions);

      // Effect:
    verify(viewActions).setEnabled(R.id.codeInput, true);
    verify(viewActions).unblockWithPending("password");
    verify(viewActions).setBackground(R.id.codeInput, R.drawable.ic_code_input_error);
    verify(viewActions).animateError();
    verifyNoMoreInteractions(viewActions);
  }
}
