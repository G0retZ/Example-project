package com.cargopull.executor_driver.presentation.movingtoclienttimer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;

import com.cargopull.executor_driver.presentation.ViewActions;
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
  private ViewActions viewActions;

  @Mock
  private ViewState<ViewActions> parentViewState;
  @Mock
  private ViewState<ViewActions> parentViewState1;

  @Before
  public void setUp() {
    viewState = new MovingToClientTimerViewStatePending(parentViewState);
  }

  @Test
  public void testActions() {
    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).blockWithPending("MovingToClientTimerViewState");
    verifyNoMoreInteractions(viewActions);
    verify(parentViewState, only()).apply(viewActions);
  }

  @Test
  public void testNoActions() {
    // Дано:
    viewState = new MovingToClientTimerViewStatePending(null);

    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).blockWithPending("MovingToClientTimerViewState");
    verifyNoMoreInteractions(viewActions);
    verifyZeroInteractions(parentViewState);
  }

  @Test
  public void testEquals() {
    viewState = new MovingToClientTimerViewStatePending(parentViewState);
    assertEquals(viewState, new MovingToClientTimerViewStatePending(parentViewState));
    assertNotEquals(viewState, new MovingToClientTimerViewStatePending(parentViewState1));
    assertNotEquals(viewState, new MovingToClientTimerViewStatePending(null));
  }

}