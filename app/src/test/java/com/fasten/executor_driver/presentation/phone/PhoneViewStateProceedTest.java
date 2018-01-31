package com.fasten.executor_driver.presentation.phone;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(MockitoJUnitRunner.class)
public class PhoneViewStateProceedTest {

  private PhoneViewStateProceed viewState;

  @Mock
  private PhoneViewActions phoneViewActions;

  @Before
  public void setUp() throws Exception {
    viewState = new PhoneViewStateProceed("login");
  }

  @Test
  public void testActions() throws Exception {
    // Действие:
    viewState.apply(phoneViewActions);

    // Результат:
    verify(phoneViewActions).showPending(false);
    verify(phoneViewActions).showError(null);
    verify(phoneViewActions).setInputEditable(false);
    verify(phoneViewActions).enableButton(false);
    verify(phoneViewActions).proceedNext("login");
    verifyNoMoreInteractions(phoneViewActions);
  }

  @Test
  public void testEquals() throws Exception {
    assertEquals(viewState, new PhoneViewStateProceed("login"));
    assertNotEquals(viewState, new PhoneViewStateProceed("logi"));
  }
}