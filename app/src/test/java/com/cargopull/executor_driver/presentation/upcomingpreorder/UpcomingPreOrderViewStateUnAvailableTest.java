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
    // Given:
    UpcomingPreOrderViewStateUnAvailable viewState = new UpcomingPreOrderViewStateUnAvailable();

      // Action:
    viewState.apply(viewActions);

      // Effect:
    verify(viewActions).showUpcomingPreOrderAvailable(false);
    verifyNoMoreInteractions(viewActions);
  }
}