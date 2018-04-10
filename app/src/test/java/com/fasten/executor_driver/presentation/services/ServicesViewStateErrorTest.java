package com.fasten.executor_driver.presentation.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ServicesViewStateErrorTest {

  private ServicesViewStateError viewState;

  @Mock
  private ServicesViewActions codeViewActions;

  @Before
  public void setUp() {
    viewState = new ServicesViewStateError(123);
  }

  @Test
  public void testActions() {
    // Действие:
    viewState.apply(codeViewActions);

    // Результат:
    verify(codeViewActions).enableReadyButton(false);
    verify(codeViewActions).showServicesList(false);
    verify(codeViewActions).showServicesPending(false);
    verify(codeViewActions).showServicesListErrorMessage(true, 123);
    verify(codeViewActions).showServicesListResolvableErrorMessage(false, 0);
    verifyNoMoreInteractions(codeViewActions);
  }

  @Test
  public void testEquals() {
    assertEquals(viewState, new ServicesViewStateError(123));
    assertNotEquals(viewState, new ServicesViewStateError(0));
  }
}