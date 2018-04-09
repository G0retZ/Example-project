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
  private ServicesViewActions codeViewActions;

  @Before
  public void setUp() {
    viewState = new ServicesViewStateReady(new ArrayList<>());
  }

  @Test
  public void testActions() {
    // Действие:
    viewState.apply(codeViewActions);

    // Результат:
    verify(codeViewActions).enableReadyButton(true);
    verify(codeViewActions).showServicesList(true);
    verify(codeViewActions).showServicesPending(false);
    verify(codeViewActions).showServicesListErrorMessage(false, 0);
    verify(codeViewActions).showServicesListResolvableErrorMessage(false, 0);
    verify(codeViewActions).setServicesListItems(new ArrayList<>());
    verifyNoMoreInteractions(codeViewActions);
  }
}