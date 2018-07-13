package com.cargopull.executor_driver.presentation.clientorderconfirmationtime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.cargopull.executor_driver.R;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ClientOrderConfirmationTimeViewStateCountingTest {

  private ClientOrderConfirmationTimeViewStateCounting viewState;

  @Mock
  private ClientOrderConfirmationTimeViewActions viewActions;

  @Before
  public void setUp() {
    viewState = new ClientOrderConfirmationTimeViewStateCounting(12345);
  }

  @Test
  public void testActions() {
    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).setWaitingForClientTime(12345);
    verify(viewActions).setWaitingForClientTimeText(R.string.client_confirmation);
    verify(viewActions).showWaitingForClientTimer(true);
    verifyNoMoreInteractions(viewActions);
  }

  @Test
  public void testEquals() {
    assertEquals(viewState, new ClientOrderConfirmationTimeViewStateCounting(12345));
    assertNotEquals(viewState, new ClientOrderConfirmationTimeViewStateCounting(54321));
  }
}