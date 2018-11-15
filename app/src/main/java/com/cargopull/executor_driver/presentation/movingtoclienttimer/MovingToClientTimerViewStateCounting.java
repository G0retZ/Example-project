package com.cargopull.executor_driver.presentation.movingtoclienttimer;

import androidx.annotation.NonNull;
import com.cargopull.executor_driver.R;
import com.cargopull.executor_driver.presentation.FragmentViewActions;
import com.cargopull.executor_driver.presentation.ViewState;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;

/**
 * Состояние счетчика таймера движения к клиенту.
 */
final class MovingToClientTimerViewStateCounting implements ViewState<FragmentViewActions> {

  private final long timeStamp;

  MovingToClientTimerViewStateCounting(long timeStamp) {
    this.timeStamp = timeStamp;
  }

  @Override
  public void apply(@NonNull FragmentViewActions stateActions) {
    stateActions.setText(R.id.timerText,
        DateTimeFormat.forPattern((timeStamp < 0 ? "-" : "") + "HH:mm:ss")
            .print(LocalTime.fromMillisOfDay(Math.abs(timeStamp)))
    );
    stateActions.setTextColor(R.id.timerText,
        timeStamp > 0 ? R.color.textColorPrimary : R.color.colorError);
    stateActions.unblockWithPending("MovingToClientTimerViewState");
  }

  @Override
  public String toString() {
    return "MovingToClientTimerViewStateCounting{" +
        "timeStamp=" + timeStamp +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    MovingToClientTimerViewStateCounting that = (MovingToClientTimerViewStateCounting) o;

    return timeStamp == that.timeStamp;
  }

  @Override
  public int hashCode() {
    return (int) (timeStamp ^ (timeStamp >>> 32));
  }
}
