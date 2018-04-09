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
public class ServicesViewStateInitialTest {

  private ServicesViewStateInitial viewState;

  @Mock
  private ServicesViewActions codeViewActions;

  @Before
  public void setUp() {
    viewState = new ServicesViewStateInitial();
  }

  @Test
  public void testActions() {
    // Действие:
    viewState.apply(codeViewActions);

    // Результат:
    verify(codeViewActions).enableReadyButton(false);
    verify(codeViewActions).showServicesList(true);
    verify(codeViewActions).showServicesPending(false);
    verify(codeViewActions).showServicesListErrorMessage(false);
    verify(codeViewActions).setServicesListItems(new ArrayList<>());
    verifyNoMoreInteractions(codeViewActions);
  }
}