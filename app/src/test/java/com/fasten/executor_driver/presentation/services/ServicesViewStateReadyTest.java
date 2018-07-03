package com.fasten.executor_driver.presentation.services;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.ArrayList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ServicesViewStateReadyTest {

  private ServicesViewStateReady viewState;

  @Mock
  private ServicesViewActions viewActions;

  @Before
  public void setUp() {
    viewState = new ServicesViewStateReady(new ArrayList<>());
  }

  @Test
  public void testActions() {
    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).enableReadyButton(true);
    verify(viewActions).showServicesList(true);
    verify(viewActions).showServicesPending(false);
    verify(viewActions).showServicesListErrorMessage(false, 0);
    verify(viewActions).showServicesListResolvableErrorMessage(false, 0);
    verify(viewActions).setServicesListItems(new ArrayList<>());
    verifyNoMoreInteractions(viewActions);
  }
}