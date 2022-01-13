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
public class CodeViewStateNetworkErrorTest {


  private CodeViewStateNetworkError viewState;

  @Mock
  private CodeViewActions viewActions;

  @Before
  public void setUp() {
    viewState = new CodeViewStateNetworkError();
  }

  @Test
  public void testActions() {
    // Action:
    viewState.apply(viewActions);

      // Effect:
    verify(viewActions).setEnabled(R.id.codeInput, true);
    verify(viewActions).unblockWithPending("password");
    verify(viewActions).setBackground(R.id.codeInput, R.drawable.ic_code_input_activated);
    verify(viewActions).showPersistentDialog(R.string.code_network_error, null);
    verifyNoMoreInteractions(viewActions);
  }
}