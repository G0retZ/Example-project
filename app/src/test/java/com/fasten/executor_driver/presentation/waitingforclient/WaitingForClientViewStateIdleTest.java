package com.fasten.executor_driver.presentation.waitingforclient;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class WaitingForClientViewStateIdleTest {

  private WaitingForClientViewStateIdle viewState;

  @Mock
  private WaitingForClientViewActions driverOrderConfirmationViewActions;

  @Before
  public void setUp() {
    viewState = new WaitingForClientViewStateIdle();
  }

  @Test
  public void testActions() {
    // Действие:
    viewState.apply(driverOrderConfirmationViewActions);

    // Результат:
    verify(driverOrderConfirmationViewActions, only()).showWaitingForClientPending(false);
  }
}