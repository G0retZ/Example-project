package com.cargopull.executor_driver.presentation.phone;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PhoneViewStateInitialTest {

  private PhoneViewStateInitial viewState;

  @Mock
  private PhoneViewActions viewActions;

  @Before
  public void setUp() {
    viewState = new PhoneViewStateInitial();
  }

  @Test
  public void testActions() {
    // Action:
    viewState.apply(viewActions);

      // Effect:
    verify(viewActions).enableButton(false);
    verifyNoMoreInteractions(viewActions);
  }
}