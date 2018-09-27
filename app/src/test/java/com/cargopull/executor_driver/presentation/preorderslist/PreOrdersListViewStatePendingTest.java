package com.cargopull.executor_driver.presentation.preorderslist;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;

import com.cargopull.executor_driver.presentation.ViewState;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PreOrdersListViewStatePendingTest {

  private PreOrdersListViewStatePending viewState;

  @Mock
  private PreOrdersListViewActions viewActions;

  @Mock
  private ViewState<PreOrdersListViewActions> parentViewState;
  @Mock
  private ViewState<PreOrdersListViewActions> parentViewState1;

  @Test
  public void testActions() {
    // Дано:
    viewState = new PreOrdersListViewStatePending(parentViewState);

    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).showPreOrdersListPending(true);
    verifyNoMoreInteractions(viewActions);
    verify(parentViewState, only()).apply(viewActions);
  }

  @Test
  public void testNoActions() {
    // Дано:
    viewState = new PreOrdersListViewStatePending(null);

    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).showPreOrdersListPending(true);
    verifyNoMoreInteractions(viewActions);
    verifyZeroInteractions(parentViewState);
  }

  @Test
  public void testEquals() {
    viewState = new PreOrdersListViewStatePending(parentViewState);
    assertEquals(viewState, viewState);
    assertEquals(viewState, new PreOrdersListViewStatePending(parentViewState));
    assertNotEquals(viewState, new PreOrdersListViewStatePending(parentViewState1));
    assertNotEquals(viewState, new PreOrdersListViewStatePending(null));
  }
}