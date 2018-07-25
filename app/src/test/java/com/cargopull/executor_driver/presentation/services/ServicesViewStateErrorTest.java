package com.cargopull.executor_driver.presentation.services;

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
  private ServicesViewActions viewActions;

  @Before
  public void setUp() {
    viewState = new ServicesViewStateError(123);
  }

  @Test
  public void testActions() {
    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).enableReadyButton(false);
    verify(viewActions).showServicesList(false);
    verify(viewActions).showServicesPending(false);
    verify(viewActions).showServicesListErrorMessage(true, 123);
    verify(viewActions).showServicesListResolvableErrorMessage(false, 0);
    verifyNoMoreInteractions(viewActions);
  }

  @Test
  public void testEquals() {
    assertEquals(viewState, new ServicesViewStateError(123));
    assertNotEquals(viewState, new ServicesViewStateError(0));
  }
}