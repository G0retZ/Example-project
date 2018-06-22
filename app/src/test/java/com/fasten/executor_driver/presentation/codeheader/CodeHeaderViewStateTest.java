package com.fasten.executor_driver.presentation.codeheader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;

import com.fasten.executor_driver.R;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CodeHeaderViewStateTest {

  private CodeHeaderViewState viewState;

  @Mock
  private CodeHeaderViewActions viewActions;

  @Before
  public void setUp() {
    viewState = new CodeHeaderViewState("+7 (999) 700-44-50");
  }

  @Test
  public void testActions() {
    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions, only())
        .setDescriptiveHeaderText(R.string.sms_code_message, "+7 (999) 700-44-50");
  }

  @Test
  public void testEquals() {
    assertEquals(viewState, new CodeHeaderViewState("+7 (999) 700-44-50"));
    assertNotEquals(viewState, new CodeHeaderViewState("+7 (999) 700-44-51"));
  }
}