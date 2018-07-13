package com.cargopull.executor_driver.presentation.clientorderconfirmationtime;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.cargopull.executor_driver.R;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ClientOrderConfirmationTimeViewStateNotCountingTest {

  private ClientOrderConfirmationTimeViewStateNotCounting viewState;

  @Mock
  private ClientOrderConfirmationTimeViewActions viewActions;

  @Before
  public void setUp() {
    viewState = new ClientOrderConfirmationTimeViewStateNotCounting();
  }

  @Test
  public void testActions() {
    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).setWaitingForClientTimeText(R.string.client_confirmation_problem);
    verify(viewActions).showWaitingForClientTimer(false);
    verifyNoMoreInteractions(viewActions);
  }
}