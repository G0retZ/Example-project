package com.cargopull.executor_driver.presentation.preorderslist;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PreOrdersListViewStateReadyTest {

  private PreOrdersListViewStateReady viewState;

  @Mock
  private PreOrdersListViewActions viewActions;
  @Mock
  private List<PreOrdersListItem> preOrdersListItems;
  @Mock
  private List<PreOrdersListItem> preOrdersListItems1;

  @Before
  public void setUp() {
    viewState = new PreOrdersListViewStateReady(preOrdersListItems);
  }

  @Test
  public void testActions() {
    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).showPreOrdersList(true);
    verify(viewActions).showEmptyPreOrdersList(false);
    verify(viewActions).showPreOrdersListPending(false);
    verify(viewActions).setPreOrdersListItems(preOrdersListItems);
    verifyNoMoreInteractions(viewActions);
  }

  @Test
  public void testEquals() {
    assertEquals(viewState, viewState);
    assertEquals(viewState, new PreOrdersListViewStateReady(preOrdersListItems));
    assertNotEquals(viewState, new PreOrdersListViewStateReady(preOrdersListItems1));
    assertNotEquals(viewState, "");
    assertNotEquals(viewState, null);
  }
}