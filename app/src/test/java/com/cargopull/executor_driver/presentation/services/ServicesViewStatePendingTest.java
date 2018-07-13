package com.cargopull.executor_driver.presentation.services;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ServicesViewStatePendingTest {

  private ServicesViewStatePending viewState;

  @Mock
  private ServicesViewActions viewActions;

  @Before
  public void setUp() {
    viewState = new ServicesViewStatePending();
  }

  @Test
  public void testActions() {
    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).enableReadyButton(false);
    verify(viewActions).showServicesList(true);
    verify(viewActions).showServicesPending(true);
    verify(viewActions).showServicesListErrorMessage(false, 0);
    verify(viewActions).showServicesListResolvableErrorMessage(false, 0);
    verifyNoMoreInteractions(viewActions);
  }
}