package com.cargopull.executor_driver.presentation.upcomingpreorder;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class UpcomingPreOrderViewStateUnAvailableTest {

  @Mock
  private UpcomingPreOrderViewActions viewActions;

  @Test
  public void testActions() {
    // Дано:
    UpcomingPreOrderViewStateUnAvailable viewState = new UpcomingPreOrderViewStateUnAvailable();

    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).showUpcomingPreOrderAvailable(false);
    verifyNoMoreInteractions(viewActions);
  }
}