package com.cargopull.executor_driver.presentation.movingtoclienttimer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.presentation.FragmentViewActions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MovingToClientTimerViewStateCountingTest {

  private MovingToClientTimerViewStateCounting viewState;

  @Mock
  private FragmentViewActions viewActions;

  @Test
  public void testPositive() {
    // Дано:
    viewState = new MovingToClientTimerViewStateCounting(4289080);

    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).setText(R.id.timerText, "01:11:29");
    verify(viewActions).setTextColor(R.id.timerText, R.color.textColorPrimary);
    verify(viewActions).unblockWithPending("MovingToClientTimerViewState");
    verifyNoMoreInteractions(viewActions);
  }

  @Test
  public void testZero() {
    // Дано:
    viewState = new MovingToClientTimerViewStateCounting(0);

    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).setText(R.id.timerText, "00:00:00");
    verify(viewActions).setTextColor(R.id.timerText, R.color.colorError);
    verify(viewActions).unblockWithPending("MovingToClientTimerViewState");
    verifyNoMoreInteractions(viewActions);
  }

  @Test
  public void testNegative() {
    // Дано:
    viewState = new MovingToClientTimerViewStateCounting(-7323089);

    // Действие:
    viewState.apply(viewActions);

    // Результат:
    verify(viewActions).setText(R.id.timerText, "-02:02:03");
    verify(viewActions).setTextColor(R.id.timerText, R.color.colorError);
    verify(viewActions).unblockWithPending("MovingToClientTimerViewState");
    verifyNoMoreInteractions(viewActions);
  }

  @Test
  public void testEquals() {
    viewState = new MovingToClientTimerViewStateCounting(123);
    assertEquals(viewState, new MovingToClientTimerViewStateCounting(123));
    assertNotEquals(viewState, new MovingToClientTimerViewStateCounting(321));
  }
}