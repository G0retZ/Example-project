package com.cargopull.executor_driver.presentation.preorderslist;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;

@RunWith(MockitoJUnitRunner.class)
public class PreOrdersListViewStateEmptyTest {

  private PreOrdersListViewStateEmpty viewState;

  @Mock
  private PreOrdersListViewActions viewActions;

  @Before
  public void setUp() {
    viewState = new PreOrdersListViewStateEmpty();
  }

  @Test
  public void testActions() {
    // Action:
    viewState.apply(viewActions);

    // Effect:
    verify(viewActions).showPreOrdersList(false);
    verify(viewActions).showEmptyPreOrdersList(true);
    verify(viewActions).showPreOrdersListPending(false);
    verify(viewActions).setPreOrdersListItems(new ArrayList<>());
    verifyNoMoreInteractions(viewActions);
  }
}