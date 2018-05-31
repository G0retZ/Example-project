package com.fasten.executor_driver.presentation.waitingforclient;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class WaitingForClientViewStateErrorTest {

  private WaitingForClientViewStateError viewState;

  @Mock
  private WaitingForClientViewActions driverOrderConfirmationViewActions;

  @Before
  public void setUp() {
    viewState = new WaitingForClientViewStateError();
  }

  @Test
  public void testActions() {
    // Действие:
    viewState.apply(driverOrderConfirmationViewActions);

    // Результат:
    verify(driverOrderConfirmationViewActions).showWaitingForClientPending(false);
    verify(driverOrderConfirmationViewActions).showNetworkErrorMessage(true);
    verifyNoMoreInteractions(driverOrderConfirmationViewActions);
  }
}