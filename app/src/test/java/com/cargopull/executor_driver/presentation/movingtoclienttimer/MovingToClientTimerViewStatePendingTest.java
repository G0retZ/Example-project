package com.cargopull.executor_driver.presentation.movingtoclienttimer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.cargopull.executor_driver.presentation.FragmentViewActions;
import com.cargopull.executor_driver.presentation.ViewState;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MovingToClientTimerViewStatePendingTest {

  private MovingToClientTimerViewStatePending viewState;

  @Mock
  private FragmentViewActions viewActions;

  @Mock
  private ViewState<FragmentViewActions> parentViewState;
  @Mock
  private ViewState<FragmentViewActions> parentViewState1;

  @Before
  public void setUp() {
    viewState = new MovingToClientTimerViewStatePending(parentViewState);
  }

  @Test
  public void testActions() {
    // Action:
    viewState.apply(viewActions);

      // Effect:
    verify(viewActions).blockWithPending("MovingToClientTimerViewState");
    verifyNoMoreInteractions(viewActions);
    verify(parentViewState, only()).apply(viewActions);
  }

  @Test
  public void testNoActions() {
      // Given:
    viewState = new MovingToClientTimerViewStatePending(null);

      // Action:
    viewState.apply(viewActions);

      // Effect:
    verify(viewActions).blockWithPending("MovingToClientTimerViewState");
    verifyNoMoreInteractions(viewActions);
    verifyNoInteractions(parentViewState);
  }

  @Test
  public void testEquals() {
    viewState = new MovingToClientTimerViewStatePending(parentViewState);
    assertEquals(viewState, new MovingToClientTimerViewStatePending(parentViewState));
    assertNotEquals(viewState, new MovingToClientTimerViewStatePending(parentViewState1));
    assertNotEquals(viewState, new MovingToClientTimerViewStatePending(null));
  }

}