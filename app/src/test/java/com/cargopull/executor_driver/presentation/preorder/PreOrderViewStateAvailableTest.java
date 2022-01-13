package com.cargopull.executor_driver.presentation.preorder;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PreOrderViewStateAvailableTest {

  @Mock
  private PreOrderViewActions viewActions;

  @Test
  public void testActions() {
    // Given:
    PreOrderViewStateAvailable viewState = new PreOrderViewStateAvailable();

      // Action:
    viewState.apply(viewActions);

      // Effect:
    verify(viewActions).showPreOrderAvailable(true);
    verifyNoMoreInteractions(viewActions);
  }
}