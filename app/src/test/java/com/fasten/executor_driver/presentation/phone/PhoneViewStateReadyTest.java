package com.fasten.executor_driver.presentation.phone;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PhoneViewStateReadyTest {

  private PhoneViewStateReady viewState;

  @Mock
  private PhoneViewActions phoneViewActions;

  @Before
  public void setUp() {
    viewState = new PhoneViewStateReady();
  }

  @Test
  public void testActions() {
    // Действие:
    viewState.apply(phoneViewActions);

    // Результат:
    verify(phoneViewActions).enableButton(true);
    verifyNoMoreInteractions(phoneViewActions);
  }
}