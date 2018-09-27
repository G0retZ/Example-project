package com.cargopull.executor_driver.presentation.preorder;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PreOrderViewStateUnAvailableTest {

  @Mock
  private PreOrderViewActions viewActions;

  @Test
  public void testActions() {
    // Дано:
    PreOrderViewStateUnAvailable viewState = new PreOrderViewStateUnAvailable();

    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).showPreOrderAvailable(false);
    verifyNoMoreInteractions(viewActions);
  }
}