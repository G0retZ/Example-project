package com.fasten.executor_driver.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.fasten.executor_driver.R;
import com.fasten.executor_driver.di.AppComponent;
import com.fasten.executor_driver.presentation.clientorderconfirmationtime.ClientOrderConfirmationTimeViewActions;
import com.fasten.executor_driver.presentation.clientorderconfirmationtime.ClientOrderConfirmationTimeViewModel;
import javax.inject.Inject;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;

/**
 * Отображает индикатор звонка к оператору.
 */

public class ClientOrderConfirmationTimeFragment extends BaseFragment implements
    ClientOrderConfirmationTimeViewActions {

  private ClientOrderConfirmationTimeViewModel clientOrderConfirmationTimeViewModel;
  private TextView waitingForClientTimerText;
  private TextView waitingForClientTimer;

  @Inject
  public void setClientOrderConfirmationTimeViewModel(
      @NonNull ClientOrderConfirmationTimeViewModel clientOrderConfirmationTimeViewModel) {
    this.clientOrderConfirmationTimeViewModel = clientOrderConfirmationTimeViewModel;
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View view = inflater
        .inflate(R.layout.fragment_client_order_confirmation_time, container, false);
    waitingForClientTimerText = view.findViewById(R.id.clientTimerText);
    waitingForClientTimer = view.findViewById(R.id.clientTimer);
    return view;
  }

  @Override
  protected void onDependencyInject(AppComponent appComponent) {
    // Required by Dagger2 for field injection
    appComponent.inject(this);
  }

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    clientOrderConfirmationTimeViewModel.getNavigationLiveData().observe(this, destination -> {
      if (destination != null) {
        navigate(destination);
      }
    });
    clientOrderConfirmationTimeViewModel.getViewStateLiveData().observe(this, viewState -> {
      if (viewState != null) {
        viewState.apply(this);
      }
    });
  }

  @Override
  public void setWaitingForClientTime(long currentMillis) {
    waitingForClientTimer.setText(
        DateTimeFormat.forPattern((currentMillis < 0 ? "-" : "") + "mm:ss")
            .print(LocalTime.fromMillisOfDay(currentMillis))
    );
  }

  @Override
  public void setWaitingForClientTimeText(int stringId) {
    waitingForClientTimerText.setText(stringId);
  }

  @Override
  public void showWaitingForClientTimer(boolean show) {
    waitingForClientTimer.setVisibility(show ? View.VISIBLE : View.GONE);
  }
}
