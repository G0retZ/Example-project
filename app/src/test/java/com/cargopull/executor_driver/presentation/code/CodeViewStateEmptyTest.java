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
public class CodeViewStateEmptyTest {

  private CodeViewStateEmpty viewState;

  @Mock
  private CodeViewActions viewActions;

  @Before
  public void setUp() {
    viewState = new CodeViewStateEmpty();
  }

  @Test
  public void testActions() {
    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).setEnabled(R.id.codeInput, true);
    verify(viewActions).unblockWithPending("password");
    verify(viewActions).setBackground(R.id.codeInput, R.drawable.ic_code_input_default);
    verifyNoMoreInteractions(viewActions);
  }
}
